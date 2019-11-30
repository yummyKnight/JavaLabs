import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class Route {
    private HashSet<Integer> drivers_ids;
    private ArrayList<String> stops;
    private String time;

    public Route() {
    }

    public Route(HashSet<Integer> drivers_ids, ArrayList<String> stops, String time) {
        this.drivers_ids = drivers_ids;
        this.stops = stops;
        this.time = time;
    }

    public void addDriver(int driver_id) {
        drivers_ids.add(driver_id);
    }

    public void addStop(String stop) {
        stops.add(stop);
    }

//    public void removeDriver(Driver driver) {
//        drivers_ids.remove(driver);
//    }

    public void removeStop(String stop) {
        stops.remove(stop);
    }

     String getTime() {
        return time;
    }


    public String stopsToString() {
        StringJoiner joiner = new StringJoiner(",");
        for (String stop : stops) {
            joiner.add(stop);
        }
        return joiner.toString();
    }

     HashSet<Integer> getDrivers_ids() {
        return drivers_ids;
    }

    //    public Object[][] getDriversFIOAsArrays() {
//        Object[][] result = new Object[drivers_ids.size()][1];
//        int i = 0;
//        for (Driver driver : drivers_ids) {
//            result[i][0] = driver.getFIO();
//        }
//        return result;
//    }
    String driversToString(Route route) {
        StringJoiner joiner = new StringJoiner(",");
        for (String i : stops) {
            joiner.add(i);
        }
        return joiner.toString();
    }
     Object[][] getStopsArrays() {
        Object[][] result = new Object[stops.size()][1];
        int i = 0;
        for (String stop : stops) {
            result[i][0] = stop;
            i++;
        }
        return result;
    }

     String displayShortRoute() {
        return stops.get(0) + " -- " + stops.get(stops.size() - 1);
    }

    public void setTime(String time) {
        this.time = time;
    }
}
