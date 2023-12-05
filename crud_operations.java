import java.sql.*;
import java.util.Scanner;

public class crud_operations {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/FaridASS2";
    private static final String USER = "postgres";
    private static final String PASS = "1234";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {

                boolean keepRunning = true;
            while (keepRunning) {
                System.out.println("Choose an option: \n1 - Add Author\n2 - Add Book\n3 - Add Customer\n4 - Add Order\n5 - View Books\n6 - Update Book\n7 - Delete Book\n8 - Get MetaData\n9 - Exit");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        addAuthor(scanner, connection);
                        break;
                     case "2":
                        addBook(scanner, connection);
                        break;    

                    default:
                        System.out.println("Invalid choice, please try again.");
                }
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void addAuthor(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Adding a new author.");
        System.out.print("Author ID: ");
        int authorId = Integer.parseInt(scanner.nextLine());
        System.out.print("Author's Name: ");
        String authorName = scanner.nextLine();

        String sql = "INSERT INTO Authors (author_id, author_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            pstmt.setString(2, authorName);
            pstmt.executeUpdate();
            System.out.println("Author added successfully.");
        }
    }
    private static void addBook(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Adding a new book.");
        System.out.print("Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine());
        System.out.print("Book Name: ");
        String bookName = scanner.nextLine();
        System.out.print("Author ID: ");
        int authorId = Integer.parseInt(scanner.nextLine());
        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO Books (book_id, book_name, author_id, stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            pstmt.setString(2, bookName);
            pstmt.setInt(3, authorId);
            pstmt.setInt(4, stock);
            pstmt.executeUpdate();
            System.out.println("Book added successfully.");
        }
    } 


}