import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class dbClass {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws SQLException, ClassNotFoundException {
        conn = null;
        conn = DriverManager.getConnection("jdbc:sqlite:../myFirstBD.db");
        statmt = conn.createStatement();
        conn.setAutoCommit(false);
        System.out.println("База Подключена!");
    }

    // --------Заполнение таблицы--------
    public static int insertDriver(Driver driver) throws SQLException
    {
        String sql = "INSERT INTO 'Drivers' ('exp', 'class', 'FIO')" + " VALUES (?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        int driver_id;
        pstmt.setDouble(1, driver.getExperience());
        pstmt.setString(2, driver.getClassification());
        pstmt.setString(3, driver.getFIO());
        pstmt.executeUpdate();
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                driver_id =  generatedKeys.getInt(1);
                if (driver.getViolations() != null) {
                    String updSQL = "UPDATE Drivers SET viol = ? WHERE id = ?";
                    PreparedStatement pstmt1 = conn.prepareStatement(updSQL);
                    pstmt1.setString(1,driver.getViolations());
                    pstmt1.setInt(2,driver_id);
                    pstmt1.executeUpdate();
                    System.out.println("Таблица заполнена");
                }
                return driver_id;
            }
            else {
                throw new SQLException("Creating driver failed, no ID obtained.");
            }
        }
    }
    static void insertRoute(Route route, int route_id) throws SQLException
    {
        String sql = "INSERT INTO 'Route' ('id','stops','time')" + " VALUES (?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, route_id);
        pstmt.setString(2, route.stopsToString());
        pstmt.setString(3, route.getTime());
        for (int id : route.getDrivers_ids()){
            String updSQL = "UPDATE Drivers SET route_id = ? WHERE id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(updSQL);
            pstmt1.setInt(1,route_id);
            pstmt1.setInt(2,id);
            pstmt1.executeUpdate();
        }
        pstmt.executeUpdate();
        System.out.println("Таблица заполнена");
    }
    static ArrayList<Integer> getFreeDrivers() {
        ArrayList<Integer> res = null;
        try {
        resSet = statmt.executeQuery("SELECT Drivers.id FROM Drivers WHERE route_id is null");
         res = new ArrayList<>();
        while(resSet.next())
        {
            res.add(resSet.getInt("id"));
        }
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return res;
    }
    // -------- Вывод таблицы--------



    public static HashMap<Integer, Driver> ReadDriversBD() throws SQLException
    {   HashMap<Integer, Driver> res = new HashMap<>();
        resSet = statmt.executeQuery("SELECT * FROM Drivers");
        while(resSet.next())
        {
            int id = resSet.getInt("id");
            double exp = resSet.getDouble("exp");
            String FIO = resSet.getString("FIO");
            String d_class = resSet.getString("class");
            String viol = resSet.getString("viol"); //NULL check?
            res.put(id, new Driver(FIO, exp, d_class,viol));
        }
        System.out.println("Таблица выведена");
        return res;
    }

    public static HashMap<Integer, Route> ReadRouteBD() throws SQLException
    {   HashMap<Integer, Route> res = new HashMap<>();
        resSet = statmt.executeQuery("SELECT * FROM Route");
        while(resSet.next())
        {
            int r_id = resSet.getInt("id");
            String stops = resSet.getString("stops");
            String time = resSet.getString("time");
            String idSQL = "SELECT Drivers.id FROM Drivers WHERE route_id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(idSQL);
            pstmt1.setInt(1, r_id);
            ResultSet idsResSet = pstmt1.executeQuery();
            HashSet<Integer> ids = new HashSet<>();
            while (idsResSet.next()){
                ids.add(idsResSet.getInt("id"));
            }
            ArrayList<String> stops_arr = new ArrayList<>();
            for (String s : stops.split(",")){
                stops_arr.add(s.strip());
            }
            res.put(r_id, new Route(ids,stops_arr,time));
        }
//        System.out.println("Таблица выведена");
        return res;
    }

    static void commitChanges() throws SQLException {
        conn.commit();
    }
    // --------Закрытие--------
    public static void CloseDB() throws SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();

        System.out.println("Соединения закрыты");
    }
}
