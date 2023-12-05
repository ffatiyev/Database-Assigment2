import java.sql.*;
import java.util.Scanner;

public class crud_operations {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/FaridASS2";
    private static final String USER = "postgres";
    private static final String PASS = "1234";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {

        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}