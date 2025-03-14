package org.esprit.tripnship.Tests;

import org.esprit.tripnship.Entities.*;
import org.esprit.tripnship.Services.UserService;

import java.time.LocalDateTime;

public class TestUserCRUD {
    public static void main(String[] args) {
        User user = new Client("Hamdi","Kamel", Gender.HOMME,"samah@gmail.com","haysy267h","localhost://user.jpeg", LocalDateTime.of(2002,4,12,0,0),"94567342");
        Client user2 = new Client("Hamza","Kamel", Gender.HOMME,"hamza@gmail.com","haysy267h","localhost://user.jpeg", LocalDateTime.of(2002,4,12,0,0),"94567342");
        User user3 = new TourCoordinator("kamilia","betoni", Gender.FEMME,"kamilia@gmail.com","haysy267h","localhost://user.jpeg", LocalDateTime.of(2002,4,12,0,0),"94567342","Paris",3250.800,LocalDateTime.now());
        Employee user4 = new ShippingCoordinator("taha","regued", Gender.HOMME,"taha@gmail.com","haysy267h","localhost://user.jpeg", LocalDateTime.of(2002,4,12,0,0),"94567342","Paris",3250.800,LocalDateTime.now());
          UserService us = new UserService();
//        // adding
        us.add(user);
        us.add(user2);
        us.add(user3);
        us.add(user4);
        us.getAll().forEach(u -> System.out.println(u.toString()));
        // updating
        user.setPhoneNumber("2734688");
        user.setIdUser(1);
        user2.setEmail("yyrawahd@gmail.com");
        user2.setIdUser(2);
        user3.setLastName("lastname");
        user3.setIdUser(3);
        user4.setRole(Role.TRAVEL_ORGANIZER);
        user4.setIdUser(4);
        us.update(user);
        us.update(user2);
        us.update(user3);
        us.update(user4);
        us.getAll().forEach(u -> System.out.println(u.toString()));
        // deleting
        us.delete(user);
        us.delete(user2);
        us.delete(user3);
        us.delete(user4);


    }
}
