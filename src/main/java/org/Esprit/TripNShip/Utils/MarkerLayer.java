package org.Esprit.TripNShip.Utils;

import com.gluonhq.maps.MapLayer;
import com.gluonhq.maps.MapPoint;
import javafx.geometry.Point2D;
import javafx.scene.Node;

public class MarkerLayer extends MapLayer {

    @Override
    protected void layoutLayer() {
        for (Node node : getChildren()) {
            if (node instanceof CustomMarker marker && marker.getUserData() instanceof CustomMarker.MarkerInfo info) {
                Point2D screenPoint = getMapPoint(info.lat, info.lng);
                node.setTranslateX(screenPoint.getX() - node.getLayoutBounds().getWidth() / 2);
                node.setTranslateY(screenPoint.getY() - node.getLayoutBounds().getHeight());
            }
        }
    }

    public void addMarker(CustomMarker marker) {
        getChildren().clear(); // clear old marker
        getChildren().add(marker);
    }

    public void clearMarkers() {
        getChildren().clear();
    }

    public int getMarkerCount() {
        return getChildren().size();
    }
}
