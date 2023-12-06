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
                    case "3":
                        addCustomer(scanner, connection);
                        break;   
                    case "4":
                        addOrder(scanner, connection);
                        break;    
                    case "5":
                        viewBooks(connection);
                        break; 
                    case "6":
                        updateBook(scanner, connection);
                        break;     
                     case "7":
                        deleteBook(scanner, connection);
                        break; 
                    case "8":

                        System.out.print("Input the name of the table to get the metadata for: ");

                        String name_of_table = scanner.nextLine();

                        get_metadata(connection, name_of_table);    

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
     private static void addCustomer(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Adding a new customer.");
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        System.out.print("Customer's Name: ");
        String customerName = scanner.nextLine();

        String sql = "INSERT INTO Customers (customer_id, customer_name) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            pstmt.setString(2, customerName);
            pstmt.executeUpdate();
            System.out.println("Customer added successfully.");
        }
    }
    private static void addOrder(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Creating a new order.");
        System.out.print("Order ID: ");
        int orderId = Integer.parseInt(scanner.nextLine());
        System.out.print("Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine());
        System.out.print("Book ID: ");
        int bookId = Integer.parseInt(scanner.nextLine());
        System.out.print("Order Amount: ");
        int orderAmount = Integer.parseInt(scanner.nextLine());
        System.out.print("Order Date (YYYY-MM-DD): ");
        String orderDate = scanner.nextLine();

        // Check if the stock is sufficient for the order
        String checkStockSql = "SELECT stock FROM Books WHERE book_id = ?";
        try (PreparedStatement checkStockStmt = connection.prepareStatement(checkStockSql)) {
            checkStockStmt.setInt(1, bookId);
            ResultSet rs = checkStockStmt.executeQuery();
            if (rs.next()) {
                int stock = rs.getInt("stock");
                if (orderAmount > stock) {
                    System.out.println("Order amount exceeds current stock. Order cannot be placed.");
                    return;
                }
            } else {
                System.out.println("Book not found.");
                return;
            }
        }

        String insertOrderSql = "INSERT INTO Orders (order_id, customer_id, book_id, order_amount, order_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderSql)) {
            insertOrderStmt.setInt(1, orderId);
            insertOrderStmt.setInt(2, customerId);
            insertOrderStmt.setInt(3, bookId);
            insertOrderStmt.setInt(4, orderAmount);
            insertOrderStmt.setString(5, orderDate);
            insertOrderStmt.executeUpdate();
            System.out.println("Order created successfully.");
        }

        String updateStock = "UPDATE Books SET stock = stock - ? WHERE book_id = ?";
        try (PreparedStatement updateBookVolumeStmt = connection.prepareStatement(updateStock)) {
            updateBookVolumeStmt.setInt(1, orderAmount);
            updateBookVolumeStmt.setInt(2, bookId);
            updateBookVolumeStmt.executeUpdate();
        }
    }
    private static void viewBooks(Connection connection) throws SQLException {
        String sql = "SELECT b.book_id, b.stock, b.book_name, a.author_name, COALESCE(SUM(o.order_amount), 0) as total_order_amount " +
                "FROM Books b " +
                "LEFT JOIN Authors a ON b.author_id = a.author_id " +
                "LEFT JOIN Orders o ON b.book_id = o.book_id " +
                "GROUP BY b.book_id, b.book_name, a.author_name";

        System.out.println("Books and their details:");
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Book ID: " + rs.getInt("book_id") +
                        "Current stock: " + rs.getInt("stock") +
                        ", Book name: " + rs.getString("book_name") +
                        ", Author Name: " + rs.getString("author_name") +
                        ", Order Amount: " + rs.getInt("total_order_amount"));
            }
        }
    }
    private static void updateBook(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Updating a book.");
        System.out.print("Enter Book ID to update: ");
        int bookId = Integer.parseInt(scanner.nextLine());
        System.out.print("New Book Name (leave blank to keep same): ");
        String bookName = scanner.nextLine();
        System.out.print("New Stock (enter -1 to keep same): ");
        int stock = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE Books SET book_name = COALESCE(NULLIF(?, ''), book_name), " +
                "stock = COALESCE(NULLIF(?, -1), stock) WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bookName);
            pstmt.setInt(2, stock);
            pstmt.setInt(3, bookId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " book(s) updated.");
        }
    }
     private static void deleteBook(Scanner scanner, Connection connection) throws SQLException {
        System.out.println("Deleting a book.");
        System.out.print("Enter Book ID to delete: ");
        int bookId = Integer.parseInt(scanner.nextLine());

        String sql = "DELETE FROM Books WHERE book_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, bookId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " book(s) deleted.");
        }
    }
    private static void get_metadata(Connection conn, String name_of_table) {
        try {

            table_structures(conn);
            columns(conn, name_of_table);
            primary_keys(conn, name_of_table);
            foreign_keys(conn, name_of_table);
        } catch (SQLException e) {
            System.out.println("Error happened for the table " + name_of_table + " .");
            e.printStackTrace();
        }
    }


}