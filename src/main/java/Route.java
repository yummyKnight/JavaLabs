import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class Route {
    private HashSet<Integer> drivers_ids;
    private ArrayList<String> stops;
    private String time;

    public Route(HashSet<Integer> drivers_ids, ArrayList<String> stops, String time) {
        this.drivers_ids = drivers_ids;
        this.stops = stops;
        this.time = time;
    }

//    public void removeDriver(Driver driver) {
//        drivers_ids.remove(driver);
//    }

     String getTime() {
        return time;
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
//    String driversToString(Route route) {
//        StringJoiner joiner = new StringJoiner(",");
//        for (String i : stops) {
//            joiner.add(i);
//        }
//        return joiner.toString();
//    }
//
//     Object[][] getStopsAs2DArray() {
//        Object[][] result = new Object[stops.size()][1];
//        int i = 0;
//        for (String stop : stops) {
//            result[i][0] = stop;
//            i++;
//        }
//        return result;
//    }


    public void setDrivers_ids(HashSet<Integer> drivers_ids) {
        this.drivers_ids = drivers_ids;
    }

    public void setStops(ArrayList<String> stops) {
        this.stops = stops;
    }

    public void setTime(String time) {
        this.time = time;
    }

    ArrayList<String> getStops() {
        return stops;
    }

    String displayShortRoute() {
        return stops.get(0) + " -- " + stops.get(stops.size() - 1);
    }

}
