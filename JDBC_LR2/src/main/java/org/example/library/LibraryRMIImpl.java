package org.example.library;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;

public class LibraryRMIImpl extends UnicastRemoteObject implements LibraryRemote {

    private Connection connection;

    private static final String URL = "jdbc:postgresql://localhost:5432/library";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";


    public LibraryRMIImpl() throws RemoteException {
        super();
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Сервер: соединение с БД установлено");
        } catch (Exception e) {
            System.err.println("Сервер: ошибка подключения к БД");
            e.printStackTrace();
            throw new RemoteException("Не удалось подключиться к БД", e);
        }
    }

    @Override
    public String displayBooksWithAuthors() throws RemoteException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT b.idbook, b.book_name, b.book_year, " +
                "w.first_name, w.last_name, w.patronymic " +
                "FROM book b " +
                "JOIN writer w ON b.writer_idwriter = w.idwriter " +
                "ORDER BY b.idbook";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            result.append(String.format("%-5s %-50s %-10s %-20s\n",
                    "ID", "Название", "Год", "Автор"));

            while (rs.next()) {
                int id = rs.getInt("idbook");
                String title = rs.getString("book_name");
                int year = rs.getInt("book_year");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String patronymic = rs.getString("patronymic");

                String author = lastName + " " + firstName +
                        (patronymic != null ? " " + patronymic : "");

                result.append(String.format("%-5d %-50s %-10d %-20s\n",
                        id, title, year, author));
            }
        } catch (SQLException e) {
            throw new RemoteException("Ошибка при получении данных", e);
        }
        return result.toString();
    }

    @Override
    public String displayAuthors() throws RemoteException {
        StringBuilder result = new StringBuilder();
        String query = "SELECT idwriter, first_name, last_name FROM writer ORDER BY idwriter";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            result.append("\nДоступные авторы:");
            result.append(String.format("%-5s %-30s %-10s\n",
                    "ID", "Имя", "Фамилия"));

            while (rs.next()) {
                int id = rs.getInt("idwriter");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");

                result.append(String.format("%-5d %-30s %-10s\n",
                        id, firstName, lastName));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка авторов!");
        }

        return result.toString();
    }

    @Override
    public String addWriter(String firstName, String lastName, String patronymic,
                            String birthDate, String country, String description) throws RemoteException {
        String query = "INSERT INTO writer (first_name, last_name, patronymic, " +
                "birth_date, country, writer_description) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, patronymic);
            pstmt.setString(4, birthDate);
            pstmt.setString(5, country);
            pstmt.setString(6, description);

            int rowsAffected = pstmt.executeUpdate();
            return "Писатель добавлен! (строк добавлено: " + rowsAffected + ")";

        } catch (SQLException e) {
            throw new RemoteException("Ошибка при добавлении писателя", e);
        }
    }

    @Override
    public String addBook(int writerId, String bookName, int bookYear,
                          int pagesCount, String description, String coverPath) throws RemoteException, SQLException {

        if (!writerExists(writerId)) {
            return "Автор с ID " + writerId + " не существует";
        }

        int bookFileId = createBookFileStub();

        String query = "INSERT INTO book (writer_idwriter, book_file_idbook_file, " +
                "book_name, book_year, book_cover, pages_count, book_description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);
            pstmt.setInt(2, bookFileId);
            pstmt.setString(3, bookName);
            pstmt.setInt(4, bookYear);
            pstmt.setString(5, coverPath);
            pstmt.setInt(6, pagesCount > 0 ? pagesCount : 0);
            pstmt.setString(7, description);

            int rowsAffected = pstmt.executeUpdate();
            return "Книга добавлена! (строк добавлено: " + rowsAffected + ")";

        } catch (SQLException e) {
            throw new RemoteException("Ошибка при добавлении книги", e);
        }
    }



    private int createBookFileStub() throws SQLException {
        String query = "INSERT INTO book_file (file_path, file_size) VALUES (?, ?) RETURNING idbook_file";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "/covers/default.jpg");
            pstmt.setString(2, "0");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 1;
    }

    private boolean writerExists(int writerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM writer WHERE idwriter = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    @Override
    public String checkWriterBeforeDelete(int writerId) throws RemoteException {
        try {
            if (hasBooks(writerId)) {
                String booksList = getBooksByWriterAsString(writerId);
                return "У писателя есть книги:\n" + booksList +
                        "\nДля удаления сначала удалите книги через deleteBook.";
            }
            else {
                deleteWriter(writerId);
                return null;
            }
        } catch (SQLException e) {
            throw new RemoteException("Ошибка при удалении писателя", e);
        }
    }
    @Override
    public String deleteWriter(int writerId) throws RemoteException {
        try {

            String query = "DELETE FROM writer WHERE idwriter = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, writerId);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    return "Писатель с ID " + writerId + " успешно удален";
                } else {
                    return "Писатель с ID " + writerId + " не найден.";
                }
            }
        } catch (SQLException e) {
            throw new RemoteException("Ошибка при удалении писателя", e);
        }
    }

    private boolean hasBooks(int writerId) throws SQLException {
        String query = "SELECT COUNT(*) FROM book WHERE writer_idwriter = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    private String getBooksByWriterAsString(int writerId) throws SQLException {
        StringBuilder sb = new StringBuilder();
        String query = "SELECT idbook, book_name, book_year FROM book WHERE writer_idwriter = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("idbook");
                String title = rs.getString("book_name");
                int year = rs.getInt("book_year");
                sb.append("  ID: ").append(id).append(" - \"").append(title).append("\" (").append(year).append(")\n");
            }
        }
        return sb.toString();
    }

    @Override
    public String deleteBook(int bookId) throws RemoteException {
        String query = "DELETE FROM book WHERE idbook = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                return "Книга с ID " + bookId + " успешно удалена!";
            } else {
                return "Книга с ID " + bookId + " не найдена.";
            }

        } catch (SQLException e) {
            throw new RemoteException("Ошибка при удалении книги", e);
        }
    }

    @Override
    public String deleteBooksByWriter(int writerId) throws RemoteException {
        String query = "DELETE FROM book WHERE writer_idwriter = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);

            int rowsAffected = pstmt.executeUpdate();
           return "Удалено книг: " + rowsAffected;

        } catch (SQLException e) {

            e.printStackTrace();
            return "Ошибка при удалении книг!";
        }
    }

}