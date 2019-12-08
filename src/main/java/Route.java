import java.util.ArrayList;
import java.util.HashSet;

public class Route {
    private HashSet<Integer> drivers_ids;
    private ArrayList<String> stops;
    private String time;

    public Route(HashSet<Integer> drivers_ids, ArrayList<String> stops, String time) {
        this.drivers_ids = drivers_ids;
        this.stops = stops;
        this.time = time;
    }

     String getTime() {
        return time;
    }

     HashSet<Integer> getDrivers_ids() {
        return drivers_ids;
    }

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
