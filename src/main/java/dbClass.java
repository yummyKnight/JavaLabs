import java.sql.*;

public class dbClass {
    public static Connection conn;
    public static Statement statmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException
    {
        conn = null;
//        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("../myFirstBD.db");
        System.out.println("База Подключена!");
    }

    // --------Заполнение таблицы--------
    public static void insertDriver(Driver driver, int driver_id) throws SQLException
    {
        String sql = "INSERT INTO 'Drivers' ('id','exp', 'class', 'FIO')" + " VALUES (?,?,?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setDouble(1, driver_id);
        pstmt.setDouble(2, driver.getExperience());
        pstmt.setString(3, driver.getClassification());
        pstmt.setString(4, driver.getFIO());
        pstmt.executeUpdate();
        if (driver.getViolations().isEmpty()) {
            String updSQL = "UPDATE Drivers SET viol = ? WHERE id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(updSQL);
            pstmt1.setString(1,driver.getViolations());
            pstmt1.setInt(2,driver_id);
            pstmt1.executeUpdate();
        }
        System.out.println("Таблица заполнена");
    }
    public static void insertRoute(Route route, int route_id) throws SQLException
    {
        String sql = "INSERT INTO 'Route' ('id','stops' )" + " VALUES (?,?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, route_id);
        pstmt.setString(2, route.stopsToString());
        pstmt.executeUpdate();

        // update drivers
        for (int i : route.getDrivers_ids()){
            String updSQL = "UPDATE Drivers SET route_id = ? WHERE id = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(updSQL);
            pstmt1.setInt(1, route_id);
            pstmt1.setInt(2, i);
            pstmt1.execute();
        }
        System.out.println("Таблица заполнена");
    }
    // -------- Вывод таблицы--------
    public static void ReadDB() throws ClassNotFoundException, SQLException
    {
        resSet = statmt.executeQuery("SELECT * FROM users");

        while(resSet.next())
        {
            int id = resSet.getInt("id");
            String  name = resSet.getString("name");
            String  phone = resSet.getString("phone");
            System.out.println( "ID = " + id );
            System.out.println( "name = " + name );
            System.out.println( "phone = " + phone );
            System.out.println();
        }

        System.out.println("Таблица выведена");
    }

    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException
    {
        conn.close();
        statmt.close();
        resSet.close();

        System.out.println("Соединения закрыты");
    }
}
