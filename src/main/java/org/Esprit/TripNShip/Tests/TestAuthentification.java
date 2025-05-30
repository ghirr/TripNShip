package org.Esprit.TripNShip.Tests;

import org.Esprit.TripNShip.Entities.Client;
import org.Esprit.TripNShip.Services.AuthService;

public class TestAuthentification {
    public static void main(String[] args) {
        AuthService authService = new AuthService();
//        authService.login("mood@gmail.com","mood02#@");
        Client client = new Client("hamouda", "mood", "mood@gmail.com", "AAAA");
        authService.signUp(client);
    }
}
