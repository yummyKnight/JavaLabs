import groovy.lang.Sequence;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class dbClass {
    public static Connection conn;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws SQLException {
        conn = null;
        conn = DriverManager.getConnection("jdbc:sqlite:../myFirstBD.db");
        conn.setAutoCommit(false);
        System.out.println("База Подключена!");
    }

    // --------Заполнение таблицы--------
    public static int insertDriver(Driver driver) throws SQLException {
        String sql = "INSERT INTO 'Drivers' ('exp', 'class', 'FIO')" + " VALUES (?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int driver_id;
        pstmt.setDouble(1, driver.getExperience());
        pstmt.setString(2, driver.getClassification());
        pstmt.setString(3, driver.getFIO());
        pstmt.executeUpdate();
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                driver_id = generatedKeys.getInt(1);
                if (driver.getViolations() != null) {
                    String updSQL = "UPDATE Drivers SET viol = ? WHERE id = ?";
                    PreparedStatement pstmt1 = conn.prepareStatement(updSQL);
                    pstmt1.setString(1, driver.getViolations());
                    pstmt1.setInt(2, driver_id);
                    pstmt1.executeUpdate();
                    System.out.println("Таблица заполнена");
                }
                pstmt.close();
                conn.commit();
                return driver_id;
            } else {
                pstmt.close();
                conn.rollback();
                throw new SQLException("Creating driver failed, no ID obtained.");
            }
        }
    }

    static void insertRoute(Route route, int route_id) throws SQLException {
        String sql = "INSERT INTO Route ('id','time')" + " VALUES (?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, route_id);
            pstmt.setString(2, route.getTime());
            for (int id : route.getDrivers_ids()) {
                String updSQL = "UPDATE Drivers SET route_id = ? WHERE id = ?";
                PreparedStatement pstmt1 = conn.prepareStatement(updSQL);
                pstmt1.setInt(1, route_id);
                pstmt1.setInt(2, id);
                pstmt1.executeUpdate();
                pstmt1.close();
            }
            pstmt.executeUpdate();
            linkStopsToRoute(route_id, route.getStops());
            System.out.println("Таблица заполнена");
            conn.commit();
        }
    }

    static void updateRoute(Route route, int oldID, int newID) throws SQLException {
        // null all prev
        try {
            String updSQL = "UPDATE Drivers SET route_id = null WHERE route_id = ?";
            try (PreparedStatement pstmt1 = conn.prepareStatement(updSQL)) {
                pstmt1.setInt(1, oldID);
                pstmt1.executeUpdate();
            }
            // set new id
            updSQL = "UPDATE Drivers SET route_id = ? WHERE id = ?";
            for (int id : route.getDrivers_ids()) {
                try (PreparedStatement pstmt2 = conn.prepareStatement(updSQL)) {
                    pstmt2.setInt(1, newID);
                    pstmt2.setInt(2, id);
                    pstmt2.executeUpdate();
                }
            }
            updSQL = "UPDATE Route SET id = ?, time = ? WHERE id = ?";
            try (PreparedStatement pstmt3 = conn.prepareStatement(updSQL)) {
                pstmt3.setInt(1, newID);
                pstmt3.setString(2, route.getTime());
                pstmt3.setInt(3, oldID);
                pstmt3.execute();
            }
            unLinkStopsToRoute(oldID);
            linkStopsToRoute(newID, route.getStops());
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }

    }

    static void updateDriver(Driver driver, int driver_id) throws SQLException {

        String sql = "UPDATE Drivers SET exp = ?, class = ?, FIO = ? WHERE id = ?";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, driver.getExperience());
            pstmt.setString(2, driver.getClassification());
            pstmt.setString(3, driver.getFIO());
            pstmt.setInt(4, driver_id);
            pstmt.executeUpdate();
            pstmt.close();
            sql = "UPDATE Drivers SET viol = ? WHERE id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(sql);
            pstmt1.setString(1, driver.getViolations());
            pstmt1.setInt(2, driver_id);
            pstmt1.execute();
            pstmt1.close();

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }

    }

    static void deleteRoute(int route_id) throws SQLException {
        try {
            String sql = "DELETE FROM Route WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, route_id);
            pstmt.execute();
            pstmt.close();
            String sqlUPD = "UPDATE Drivers SET route_id = null WHERE route_id = ?";
            PreparedStatement pstupd = conn.prepareStatement(sqlUPD);
            pstupd.setInt(1, route_id);
            pstupd.execute();
            pstupd.close();
            unLinkStopsToRoute(route_id);
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        }
    }

    static void deleteDriver(int driver_id) throws SQLException {
        String sql = "DELETE FROM Drivers WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, driver_id);
            pstmt.execute();
            conn.commit();
        }
    }

    static HashMap<Integer, String> getFreeDrivers() throws SQLException {
        HashMap<Integer, String> res = null;
        try (Statement statement = conn.createStatement()) {
            try (ResultSet resSet = statement.executeQuery("SELECT Drivers.id, Drivers.FIO FROM Drivers WHERE route_id is null")) {
                res = new HashMap<>();
                while (resSet.next()) {
                    res.put(resSet.getInt("id"), resSet.getString("FIO"));
                }
            }
        }
        return res;
    }
    // -------- Вывод таблицы--------


    static HashMap<Integer, Driver> ReadDriversBD() throws SQLException {
        HashMap<Integer, Driver> res = new HashMap<>();
        try (Statement statement = conn.createStatement()) {
            try (ResultSet resSet = statement.executeQuery("SELECT * FROM Drivers")) {
                while (resSet.next()) {
                    int id = resSet.getInt("id");
                    double exp = resSet.getDouble("exp");
                    String FIO = resSet.getString("FIO");
                    String d_class = resSet.getString("class");
                    String viol = resSet.getString("viol"); //NULL check?
                    res.put(id, new Driver(FIO, exp, d_class, viol));
                }
            }
        }
        System.out.println("Таблица выведена");
        return res;
    }

    static HashMap<Integer, Route> ReadRouteBD() throws SQLException {
        HashMap<Integer, Route> res = new HashMap<>();
        try (Statement statement = conn.createStatement()) {
            try (ResultSet resSet = statement.executeQuery("SELECT * FROM Route")) {
                while (resSet.next()) {
                    int r_id = resSet.getInt("id");
                    String time = resSet.getString("time");
                    String idSQL = "SELECT Drivers.id FROM Drivers WHERE route_id = ?";
                    PreparedStatement pstmt1 = conn.prepareStatement(idSQL);
                    pstmt1.setInt(1, r_id);
                    ResultSet idsResSet = pstmt1.executeQuery();
                    HashSet<Integer> ids = new HashSet<>();
                    while (idsResSet.next()) {
                        ids.add(idsResSet.getInt("id"));
                    }
                    ArrayList<String> stops_arr = getRouteStops(r_id);
                    res.put(r_id, new Route(ids, stops_arr, time));
                }
            }
        }
        return res;
    }

    public static void addNewStop(String formattedStop) throws SQLException {
        String sql = "INSERT INTO Stops(stop_name) VALUES (?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, formattedStop);
        pstmt.execute();
        pstmt.close();
    }

    public static void linkStopsToRoute(int route_id, ArrayList<String> stops) throws SQLException {
        String sql = "INSERT INTO StopsToRoute(route_id, stop) VALUES (?, ?)";
        for (String s : stops) {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, route_id);
                pstmt.setString(2, s);
                pstmt.execute();
            }
        }
    }

    public static ArrayList<Integer> getAllDriversID() throws SQLException {
        ArrayList<Integer> res = new ArrayList<>();
        String sql = " SELECT Drivers.id FROM Drivers";
        try (Statement statement = conn.createStatement()) {
            try (ResultSet set = statement.executeQuery(sql)) {
                while (set.next()) {
                    res.add(set.getInt("id"));
                }
                return res;
            }
        }
    }

    public static ArrayList<Integer> getAllRoutesID() {
        ArrayList<Integer> res = null;
        try {
            res = new ArrayList<>();
            String sql = " SELECT Route.id FROM Route";
            try (Statement statement = conn.createStatement()) {
                try (ResultSet set = statement.executeQuery(sql)) {
                    while (set.next()) {
                        res.add(set.getInt("id"));
                    }
                    return res;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public static void unLinkStopsToRoute(int route_id) throws SQLException {
        String sql = "DELETE FROM StopsToRoute WHERE route_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, route_id);
            pstmt.execute();
        }
    }

    public static ArrayList<String> getAllStops() throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        String sql = " SELECT Stops.stop_name FROM Stops";
        try (Statement statement = conn.createStatement()) {
            try (ResultSet set = statement.executeQuery(sql)) {
                while (set.next()) {
                    res.add(set.getString("stop_name"));
                }
                return res;
            }
        }
    }

    public static HashMap<Integer, String> getDriversOnRoute(int route_id) throws SQLException {
        HashMap<Integer, String> res = new HashMap<>();
        String sql = " SELECT Drivers.id,Drivers.FIO FROM Drivers WHERE route_id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, route_id);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    res.put(set.getInt("id"), set.getString("FIO"));
                }
                return res;
            }
        }


    }

    public static ArrayList<String> getRouteStops(int route_id) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        String sql = " SELECT stop FROM StopsToRoute WHERE route_id = ? ORDER BY ROWID";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, route_id);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    res.add(set.getString("stop"));
                }
                return res;
            }
        }


    }

    // --------Закрытие--------
    public static void CloseDB() throws SQLException {
        conn.close();
        System.out.println("Соединения закрыты");
    }

    static String getDriverFIOByKey(int tmpID) throws SQLException {
        String res = null;
        String sql = " SELECT FIO FROM Drivers WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, tmpID);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    res = set.getString("FIO");
                }
                return res;
            }
        }
    }

    static Driver getDriverByKey(int tmpID) throws SQLException {
        Driver res = null;
        String sql = " SELECT * FROM Drivers WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, tmpID);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    res = new Driver(set.getString("FIO"), set.getDouble("exp"),
                            set.getString("class"));
                    res.setViolations(set.getString("viol"));
                }
                return res;
            }
        }
    }

    static String getRouteTime(int route_id) throws SQLException {
        String res = null;
        String sql = " SELECT time FROM Route WHERE id = ?";
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, route_id);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    res = set.getString("time");
                }
                return res;
            }
        }

    }
}


