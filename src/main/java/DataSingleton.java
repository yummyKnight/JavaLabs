import java.lang.reflect.Array;
import java.util.*;

class DataSingleton {

    private static DataSingleton instance = null;
    private HashMap<Integer, Driver> allDrivers = new HashMap<>();
    private HashMap<Integer, Route> allRouts = new HashMap<>();

    private DataSingleton() {
    }

    static DataSingleton getInstance() {
        if (instance == null) {
            instance = new DataSingleton();
        }
        return instance;
    }

    void addDriver(Driver driver, int driver_id) {
        allDrivers.put(driver_id, driver);
    }

    void addRoute(Route route, int route_id) {
        allRouts.put(route_id, route);
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

    int getRoutesSize() {
        return allRouts.size();
    }

    Set<Integer> getAllDriversID() {
        return allDrivers.keySet();
    }

    Set<Integer> getAllRoutesID() {
        return allRouts.keySet();
    }

    void setAllDrivers(HashMap<Integer, Driver> allDrivers) {
        this.allDrivers = allDrivers;
    }

     void setAllRouts(HashMap<Integer, Route> allRouts) {
        this.allRouts = allRouts;
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
