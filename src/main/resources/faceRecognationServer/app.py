from flask import Flask, request, jsonify
from service import FaceRecognitionService
import logging

app = Flask(__name__)
logging.basicConfig(level=logging.INFO)

# Database configuration
DB_CONFIG = {
    'host': 'localhost',
    'user': 'root',
    'password': '',
    'database': 'tripnship'
}

face_service = FaceRecognitionService(DB_CONFIG)

@app.route('/register_faces', methods=['POST'])
def register_faces():
    """Register multiple face samples for a user"""
    try:
        data = request.get_json()
        user_id = data.get('user_id')
        face_images = data.get('face_images')  # List of base64 encoded images
        
        if not user_id or not face_images:
            return jsonify({'success': False, 'message': 'Missing user_id or face_images'}), 400
        
        success = face_service.register_user_faces(user_id, face_images)
        
        return jsonify({
            'success': success,
            'message': 'Faces registered successfully' if success else 'Registration failed'
        })
        
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 500

@app.route('/authenticate', methods=['POST'])
def authenticate():
    """Authenticate user by face"""
    try:
        data = request.get_json()
        face_image = data.get('face_image')  # Base64 encoded image
        
        if not face_image:
            return jsonify({'success': False, 'message': 'Missing face_image'}), 400
        
        user_id = face_service.authenticate_user(face_image)
        
        return jsonify({
            'success': user_id is not None,
            'user_id': user_id,
            'message': f'User {user_id} authenticated' if user_id else 'Authentication failed'
        })
        
    except Exception as e:
        return jsonify({'success': False, 'message': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    """Health check endpoint"""
    return jsonify({'status': 'healthy'})

if __name__ == '__main__':
    # Create table if needed
    face_service.create_face_data_table()
    
    # Run the server
    app.run(host='0.0.0.0', port=5000, debug=False)