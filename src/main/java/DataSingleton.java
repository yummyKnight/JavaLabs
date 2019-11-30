import java.util.*;

class DataSingleton {

    private static DataSingleton instance = null;
    private int driver_id;
    private int route_id;
    private HashMap<Integer, Driver> allDrivers = new HashMap<>();
    private HashMap<Integer, Route> allRouts = new HashMap<>();

    private DataSingleton() {
        driver_id = 0;
        route_id = 0;
    }

    static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }

    int addDriver(Driver driver) {
        allDrivers.put(driver_id, driver);
        // TODO: db sync
        return driver_id++;
    }

    int addRoute(Route route) {
        allRouts.put(route_id, route);
        return route_id++;
    }

    Driver getDriverByKey(Integer key) {
        return allDrivers.get(key);
    }

    Route getRouteByKey(Integer key) {
        return allRouts.get(key);
    }

    boolean isTwin(String FIO) {
        boolean twin = false;
        for (Driver driver : allDrivers.values()) {
            if (driver.getFIO().equals(FIO)) {
                twin = true;
                break;
            }
        }
        return twin;
    }

    String driversToString(Route route) {
        StringJoiner joiner = new StringJoiner(",");
        for (int i : route.getDrivers_ids()) {
            joiner.add(getDriverByKey(i).getFIO());
        }
        return joiner.toString();
    }

    int getDriverSize() {
        return allDrivers.size();
    }

    Set<Integer> getAllDriversID() {
        return allDrivers.keySet();
    }

    Set<Integer> getAllRoutesID() {
        return allRouts.keySet();
    }


    HashSet<String> getAllStops() {
        HashSet<String> tmp = new HashSet<>();
        for (Route route : allRouts.values()) {
            Object[][] t = route.getStopsArrays();
            for (Object[] objects : t) {
                tmp.add((String) objects[0]);
            }
        }
        return tmp;
    }
}
