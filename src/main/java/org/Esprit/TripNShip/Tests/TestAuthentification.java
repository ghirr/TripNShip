package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Services.AuthService;

public class TestAuthentification {
    public static void main(String[] args) {
        AuthService authService = new AuthService();
        authService.login("kamilia@gmail.com","123456");
    }
}
