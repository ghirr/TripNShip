package org.Esprit.TripNShip.Services;

import org.Esprit.TripNShip.Entities.RentalAgency;
import org.Esprit.TripNShip.Entities.ReviewAgency;
import org.Esprit.TripNShip.Utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReviewAgencyService implements IService<ReviewAgency> {

    private final Connection connection;

    public ReviewAgencyService() {
        connection = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void add(ReviewAgency reviewAgency) {

    }

    @Override
    public void update(ReviewAgency reviewAgency) {

    }

    @Override
    public void delete(ReviewAgency reviewAgency) {

    }

    @Override
    public List<ReviewAgency> getAll() {
        return List.of();
    }
}
