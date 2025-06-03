package org.Esprit.TripNShip.Tests;


import org.Esprit.TripNShip.Services.AssetsService;

public class TestAssetCRUD {

    public static void main(String[] args) {
        AssetsService service = new AssetsService();
//        Asset asset = new Asset(new Employee(7,"http://localhost/tripNship/user.png"), TypeAsset.COMPUTER,"Dell","24SKJHDFU77DJS",2340.50,"http://localhost/tripNship/default.jpg");
//        service.add(asset);
//        Asset asset = new Asset(1,new Employee(7,"http://localhost/tripNship/user.png"), TypeAsset.PHONE,"Apple","24SKJHDFU77DJS",2340.50,"http://localhost/tripNship/default.jpg");
//       service.update(asset);
        service.getAll().forEach(asset -> {
            System.out.println(asset);
        });
    }
}
