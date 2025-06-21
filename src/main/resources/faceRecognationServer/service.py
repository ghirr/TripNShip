import cv2
import base64
import numpy as np
import mysql.connector
from deepface import DeepFace
import json
import os
from typing import Optional, List, Dict
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class FaceRecognitionService:
    def __init__(self, db_config: Dict[str, str]):
        """
        Initialize the Face Recognition Service
        
        Args:
            db_config: Dictionary containing database connection parameters
                      {'host': 'localhost', 'user': 'username', 'password': 'password', 'database': 'db_name'}
        """
        self.db_config = db_config
        self.model_name = "VGG-Face"  # You can change to Facenet, OpenFace, etc.
        self.detector_backend = "opencv"  # or 'mtcnn', 'retinaface' for better accuracy
        
    def get_db_connection(self):
        """Create and return database connection"""
        return mysql.connector.connect(**self.db_config)
    
    def register_user_faces(self, user_id: int, face_images: List[str]) -> bool:
        """
        Register multiple face encodings for a user
        
        Args:
            user_id: User ID from your system
            face_images: List of base64 encoded face images
            
        Returns:
            bool: True if successful, False otherwise
        """
        try:
            face_encodings = []
            
            for face_b64 in face_images:
                # Decode base64 image
                img_data = base64.b64decode(face_b64)
                nparr = np.frombuffer(img_data, np.uint8)
                img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
                
                # Get face embedding using DeepFace
                try:
                    embedding = DeepFace.represent(
                        img_path=img, 
                        model_name=self.model_name,
                        detector_backend=self.detector_backend,
                        enforce_detection=False
                    )
                    
                    if embedding:
                        face_encodings.append(embedding[0]["embedding"])
                        
                except Exception as e:
                    logger.warning(f"Could not process face image: {e}")
                    continue
            
            if not face_encodings:
                logger.error("No valid face encodings generated")
                return False
            
            # Store in database
            conn = self.get_db_connection()
            cursor = conn.cursor()
            
            # First, delete existing face data for this user
            cursor.execute("DELETE FROM user_face_data WHERE user_id = %s", (user_id,))
            
            # Insert new face encodings
            for i, encoding in enumerate(face_encodings):
                encoding_json = json.dumps(encoding)
                cursor.execute(
                    "INSERT INTO user_face_data (user_id, face_encoding, sample_number) VALUES (%s, %s, %s)",
                    (user_id, encoding_json, i + 1)
                )
            
            conn.commit()
            cursor.close()
            conn.close()
            
            logger.info(f"Successfully registered {len(face_encodings)} face samples for user {user_id}")
            return True
            
        except Exception as e:
            logger.error(f"Error registering user faces: {e}")
            return False
    
    
    def authenticate_user(self, face_image_b64: str, threshold: float = 0.35) -> Optional[int]:
        """
        Authenticate user by comparing face with stored encodings
        
        Args:
            face_image_b64: Base64 encoded face image
            threshold: Similarity threshold (lower = more strict, higher = more lenient)
            
        Returns:
            int: User ID if authenticated, None otherwise
        """
        try:
            # Decode and process input image
            img_data = base64.b64decode(face_image_b64)
            nparr = np.frombuffer(img_data, np.uint8)
            img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
            
            # Pre-process image for better face detection
            img = self._preprocess_image(img)
            
            # Get face embedding with multiple attempts
            input_embedding = self._get_face_embedding_robust(img)
            if input_embedding is None:
                return None
            
            # Get all stored face encodings from database
            conn = self.get_db_connection()
            cursor = conn.cursor()
            cursor.execute("SELECT user_id, face_encoding FROM user_face_data")
            stored_faces = cursor.fetchall()
            cursor.close()
            conn.close()
            
            # Track all matches for each user (in case multiple encodings per user)
            user_matches = {}
            
            # Compare with all stored faces
            for user_id, encoding_json in stored_faces:
                try:
                    stored_encoding = json.loads(encoding_json)
                    
                    # Calculate multiple distance metrics
                    cosine_dist = self._calculate_cosine_distance(input_embedding, stored_encoding)
                    euclidean_dist = self._calculate_euclidean_distance(input_embedding, stored_encoding)
                    
                    # Weighted combination of distances (cosine is primary)
                    combined_distance = (0.7 * cosine_dist) + (0.3 * self._normalize_euclidean(euclidean_dist))
                    
                    # Store best match for each user
                    if user_id not in user_matches or combined_distance < user_matches[user_id]['distance']:
                        user_matches[user_id] = {
                            'distance': combined_distance,
                            'cosine_dist': cosine_dist,
                            'euclidean_dist': euclidean_dist
                        }
                        
                except Exception as e:
                    logger.warning(f"Error comparing with stored face: {e}")
                    continue
            
            # Find best match across all users
            best_match_user = None
            best_distance = float('inf')
            
            for user_id, match_data in user_matches.items():
                if match_data['distance'] < threshold and match_data['distance'] < best_distance:
                    best_distance = match_data['distance']
                    best_match_user = user_id
            
            # Additional verification with relaxed threshold if no match found
            if not best_match_user and user_matches:
                relaxed_threshold = min(threshold * 1.3, 0.7)  # Increase threshold by 30% but cap at 0.7
                logger.info(f"No match found with threshold {threshold}, trying relaxed threshold {relaxed_threshold}")
                
                for user_id, match_data in user_matches.items():
                    if match_data['distance'] < relaxed_threshold and match_data['distance'] < best_distance:
                        best_distance = match_data['distance']
                        best_match_user = user_id
            
            if best_match_user:
                match_info = user_matches[best_match_user]
                logger.info(f"User {best_match_user} authenticated with combined distance {best_distance:.3f} "
                        f"(cosine: {match_info['cosine_dist']:.3f}, euclidean: {match_info['euclidean_dist']:.3f})")
                return best_match_user
            else:
                if user_matches:
                    best_failed = min(user_matches.items(), key=lambda x: x[1]['distance'])
                    logger.info(f"No matching user found. Best failed match: User {best_failed[0]} "
                            f"with distance {best_failed[1]['distance']:.3f}")
                else:
                    logger.info("No face comparisons could be performed")
                return None
                
        except Exception as e:
            logger.error(f"Error during authentication: {e}")
            return None

    def _preprocess_image(self, img):
        """Preprocess image for better face detection"""
        # Convert to RGB if needed
        if len(img.shape) == 3 and img.shape[2] == 3:
            img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        
        # Enhance contrast and brightness
        lab = cv2.cvtColor(img, cv2.COLOR_RGB2LAB)
        l, a, b = cv2.split(lab)
        clahe = cv2.createCLAHE(clipLimit=2.0, tileGridSize=(8,8))
        l = clahe.apply(l)
        img = cv2.merge([l, a, b])
        img = cv2.cvtColor(img, cv2.COLOR_LAB2RGB)
        
        return img

    def _get_face_embedding_robust(self, img):
        """Get face embedding with multiple fallback strategies"""
        detectors = [self.detector_backend, 'opencv', 'ssd', 'dlib']
        models = [self.model_name, 'VGG-Face', 'Facenet']
        
        for detector in detectors:
            for model in models:
                try:
                    result = DeepFace.represent(
                        img_path=img,
                        model_name=model,
                        detector_backend=detector,
                        enforce_detection=False
                    )
                    if result and len(result) > 0:
                        logger.info(f"Successfully extracted embedding using {model} with {detector}")
                        return result[0]["embedding"]
                except Exception as e:
                    logger.debug(f"Failed with {model}/{detector}: {e}")
                    continue
        
        logger.error("Could not extract face embedding with any model/detector combination")
        return None

    def _calculate_cosine_distance(self, embedding1: List[float], embedding2: List[float]) -> float:
        """Calculate cosine distance between two face embeddings"""
        embedding1 = np.array(embedding1)
        embedding2 = np.array(embedding2)
        
        # Normalize embeddings
        embedding1 = embedding1 / np.linalg.norm(embedding1)
        embedding2 = embedding2 / np.linalg.norm(embedding2)
        
        # Cosine similarity
        cos_sim = np.dot(embedding1, embedding2)
        # Convert to distance (0 = identical, 2 = opposite)
        distance = 1 - cos_sim
        return max(0, distance)  # Ensure non-negative

    def _calculate_euclidean_distance(self, embedding1: List[float], embedding2: List[float]) -> float:
        """Calculate euclidean distance between two face embeddings"""
        embedding1 = np.array(embedding1)
        embedding2 = np.array(embedding2)
        return np.linalg.norm(embedding1 - embedding2)

    def _normalize_euclidean(self, euclidean_dist: float) -> float:
        """Normalize euclidean distance to 0-1 range"""
        # Typical face embedding euclidean distances range from 0 to ~20
        # This normalization may need adjustment based on your model
        return min(euclidean_dist / 20.0, 1.0)

        
    def create_face_data_table(self):
        """Create the face data table if it doesn't exist"""
        try:
            conn = self.get_db_connection()
            cursor = conn.cursor()
            
            cursor.execute("""
                CREATE TABLE IF NOT EXISTS user_face_data (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    face_encoding TEXT NOT NULL,
                    sample_number INT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES user(id)                
                )
            """)
            
            conn.commit()
            cursor.close()
            conn.close()
            logger.info("Face data table created/verified successfully")
            
        except Exception as e:
            logger.error(f"Error creating face data table: {e}")

# Usage example and API endpoints
if __name__ == "__main__":
    # Database configuration
    DB_CONFIG = {
        'host': 'localhost',
        'user': 'root',
        'password': '',
        'database': 'tripnship'
    }
    
    # Initialize service
    face_service = FaceRecognitionService(DB_CONFIG)
    
    # Create table if needed
    face_service.create_face_data_table()
    
    # Example usage:
    # For registration (you would call this from your Java code):
    def register_faces_endpoint(user_id: int, face_images_b64: List[str]) -> Dict:
        """Endpoint to register user faces"""
        try:
            success = face_service.register_user_faces(user_id, face_images_b64)
            return {
                "success": success,
                "message": "Faces registered successfully" if success else "Registration failed"
            }
        except Exception as e:
            return {"success": False, "message": str(e)}
    
    # For authentication (you would call this from your Java code):
    def authenticate_face_endpoint(face_image_b64: str) -> Dict:
        """Endpoint to authenticate user by face"""
        try:
            user_id = face_service.authenticate_user(face_image_b64)
            return {
                "success": user_id is not None,
                "user_id": user_id,
                "message": f"User {user_id} authenticated" if user_id else "Authentication failed"
            }
        except Exception as e:
            return {"success": False, "message": str(e)}