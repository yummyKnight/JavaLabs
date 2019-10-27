import java.util.ArrayList;
import java.util.StringJoiner;

public class Route {
    private ArrayList<Driver> drivers;
    private ArrayList<String> stops;
    private String time;

    public Route() {
    }

    public Route(ArrayList<Driver> drivers, ArrayList<String> stops, String time) {
        this.drivers = drivers;
        this.stops = stops;
        this.time = time;
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
    }

    public void addStop(String stop) {
        stops.add(stop);
    }

    public void removeDriver(Driver driver) {
        drivers.remove(driver);
    }

    public void removeStop(String stop) {
        stops.remove(stop);
    }

    public String getTime() {
        return time;
    }

    public String driversToString() {
        StringJoiner joiner = new StringJoiner(",");
        for (Driver driver : drivers) {
            joiner.add(driver.getFIO());
        }
        return joiner.toString();
    }

    public String stopsToString() {
        StringJoiner joiner = new StringJoiner(",");
        for (String stop : stops) {
            joiner.add(stop);
        }
        return joiner.toString();
    }

    public Object[][] getDriversFIOAsArrays() {
        Object[][] result = new Object[drivers.size()][1];
        int i = 0;
        for (Driver driver : drivers) {
            result[i][0] = driver.getFIO();
        }
        return result;
    }

    public Object[][] getStopsArrays() {
        Object[][] result = new Object[stops.size()][1];
        int i = 0;
        for (String stop : stops) {
            result[i][0] = stop;
        }
        return result;
    }

    public String displayShortRoute() {
        return stops.get(0) + " -- " + stops.get(stops.size() - 1);
    }

    public void setTime(String time) {
        this.time = time;
    }
}
