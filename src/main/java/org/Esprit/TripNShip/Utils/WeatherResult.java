package org.Esprit.TripNShip.Utils;

public class WeatherResult {

    private final String condition;       // Ex: "ciel dégagé"
    private final double temperature;     // En degrés Celsius
    private final String iconCode;        // Ex: "01d"

    public WeatherResult(String condition, double temperature, String iconCode) {
        this.condition = condition;
        this.temperature = temperature;
        this.iconCode = iconCode;
    }

    public String getCondition() {
        return condition;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getIconCode() {
        return iconCode;
    }

    /**
     * Formatage lisible pour affichage à l'utilisateur.
     */
    public String getDisplayText() {
        return String.format("Conditions : %s\nTempérature : %.1f°C", condition, temperature);
    }

    @Override
    public String toString() {
        return String.format("WeatherResult{condition='%s', temp=%.1f°C, icon='%s'}",
                condition, temperature, iconCode);
    }
}