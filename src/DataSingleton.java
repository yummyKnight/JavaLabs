import java.util.ArrayList;

public class DataSingleton {
    private static DataSingleton instance = null;
    ArrayList<Driver> allDrivers = new ArrayList<>();
    ArrayList<Route> allRouts = new ArrayList<>();

    private DataSingleton() {
    }

    static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }

    public Driver getDriverByFIO (String name){
        for(Driver driver : allDrivers) {
            if (driver.getFIO().equals(name)) {
                return driver;
            }
        }
        return null;
    }

}
