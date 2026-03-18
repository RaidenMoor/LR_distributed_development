package org.example.library;

import java.sql.*;
import java.util.Scanner;

public class LibraryJDBC {
    private static final String URL = "jdbc:postgresql://localhost:5432/library";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";

    private Connection connection;
    private final Scanner scanner;

    public LibraryJDBC() {
        this.scanner = new Scanner(System.in);
        connect();
    }

    private void connect() {
        try {
            Class.forName("org.postgresql.Driver");

            // Установка соединения
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Соединение с БД установлено успешно!");
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер PostgreSQL не найден!");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к БД!");
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение закрыто.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void displayBooksWithAuthors() {
        String query = "SELECT b.idbook, b.book_name, b.book_year, " +
                "w.first_name, w.last_name, w.patronymic " +
                "FROM book b " +
                "JOIN writer w ON b.writer_idwriter = w.idwriter " +
                "ORDER BY b.idbook";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nСписок книг с авторами");
            System.out.printf("%-5s %-30s %-10s %-20s%n",
                    "ID", "Название", "Год", "Автор");

            while (rs.next()) {
                int id = rs.getInt("idbook");
                String title = rs.getString("book_name");
                int year = rs.getInt("book_year");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String patronymic = rs.getString("patronymic");

                String author = lastName + " " + firstName +
                        (patronymic != null ? " " + patronymic : "");

                System.out.printf("%-5d %-30s %-10d %-20s%n",
                        id, title, year, author);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении данных!");
            e.printStackTrace();
        }
    }


    public void addWriter() {
        System.out.println("\nДобавить нового писателя");

        System.out.print("Введите имя: ");
        String firstName = scanner.nextLine();

        System.out.print("Введите фамилию: ");
        String lastName = scanner.nextLine();

        System.out.print("Введите отчество (или оставьте пустым): ");
        String patronymic = scanner.nextLine();
        if (patronymic.isEmpty()) patronymic = null;

        System.out.print("Введите дату рождения (или оставьте пустым): ");
        String birthDate = scanner.nextLine();
        if (birthDate.isEmpty()) birthDate = null;

        System.out.print("Введите страну (или оставьте пустым): ");
        String country = scanner.nextLine();
        if (country.isEmpty()) country = null;

        System.out.print("Введите описание (или оставьте пустым): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = null;

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
            System.out.println("Писатель добавлен! (строк добавлено: " + rowsAffected + ")");

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении писателя!");
            e.printStackTrace();
        }
    }


    public void addBook() {
        System.out.println("\nДобавить новую книгу");

        displayWriters();

        System.out.print("Введите ID автора: ");
        int writerId;
        try {
            writerId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ID!");
            return;
        }

        if (!writerExists(writerId)) {
            System.out.println("Автор с ID " + writerId + " не существует!");
            return;
        }

        System.out.print("Введите название книги: ");
        String bookName = scanner.nextLine();

        System.out.print("Введите год издания: ");
        int bookYear;
        try {
            bookYear = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Некорректный год!");
            return;
        }

        System.out.print("Введите количество страниц (или 0, если неизвестно): ");
        int pagesCount;
        try {
            pagesCount = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            pagesCount = 0;
        }

        System.out.print("Введите описание книги (или оставьте пустым): ");
        String description = scanner.nextLine();
        if (description.isEmpty()) description = null;

        System.out.print("Введите путь к файлу обложки (или оставьте пустым): ");
        String coverPath = scanner.nextLine();
        if (coverPath.isEmpty()) coverPath = null;

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
            System.out.println("Книга добавлена! (строк добавлено: " + rowsAffected + ")");

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении книги!");
            e.printStackTrace();
        }
    }

    private int createBookFileStub() {
        String query = "INSERT INTO book_file (file_path, file_size) VALUES (?, ?) RETURNING idbook_file";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "/covers/default");
            pstmt.setString(2, "0");

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при создании записи в book_file!");
        }
        return 1;
    }

    private void displayWriters() {
        String query = "SELECT idwriter, first_name, last_name FROM writer ORDER BY idwriter";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nДоступные авторы:");
            while (rs.next()) {
                int id = rs.getInt("idwriter");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                System.out.println("ID: " + id + " - " + lastName + " " + firstName);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка авторов!");
        }
    }

    private boolean writerExists(int writerId) {
        String query = "SELECT COUNT(*) FROM writer WHERE idwriter = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void deleteWriterWithCheck() {
        System.out.println("\nУдалить писателя");

        displayWriters();

        System.out.print("Введите ID писателя для удаления: ");
        int writerId;
        try {
            writerId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ID!");
            return;
        }

        if (hasBooks(writerId)) {
            System.out.println("\nУ этого писателя есть книги в библиотеке:");
            displayBooksByWriter(writerId);

            System.out.print("\nУдалить все книги этого писателя перед удалением? (yes/no): ");
            String answer = scanner.nextLine().toLowerCase();

            if (answer.equals("yes") || answer.equals("y")) {
                deleteBooksByWriter(writerId);
            } else {
                System.out.println("Удаление отменено. Сначала удалите книги вручную.");
                return;
            }
        }

        String query = "DELETE FROM writer WHERE idwriter = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Писатель с ID " + writerId + " успешно удален");
            } else {
                System.out.println("Писатель с ID " + writerId + " не найден.");
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении писателя!");
            e.printStackTrace();
        }
    }


    private boolean hasBooks(int writerId) {
        String query = "SELECT COUNT(*) FROM book WHERE writer_idwriter = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void displayBooksByWriter(int writerId) {
        String query = "SELECT idbook, book_name, book_year FROM book WHERE writer_idwriter = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("Книги писателя:");
            while (rs.next()) {
                int id = rs.getInt("idbook");
                String title = rs.getString("book_name");
                int year = rs.getInt("book_year");
                System.out.println("  ID: " + id + " - \"" + title + "\" (" + year + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBooksByWriter(int writerId) {
        String query = "DELETE FROM book WHERE writer_idwriter = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, writerId);

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Удалено книг: " + rowsAffected);

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении книг!");
            e.printStackTrace();
        }
    }

    public void deleteBook() {
        System.out.println("\nУдалить книгу");

        displayBooksWithAuthors();

        System.out.print("Введите ID книги для удаления: ");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Некорректный ID!");
            return;
        }

        String query = "DELETE FROM book WHERE idbook = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, bookId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Книга с ID " + bookId + " успешно удалена!");
            } else {
                System.out.println("Книга с ID " + bookId + " не найдена.");
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении книги!");
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            System.out.println("   Добро пожаловать в библиотеку!  ");
            System.out.println("-------------------------------------");
            System.out.println("1. Показать все книги с авторами");
            System.out.println("2. Добавить нового писателя");
            System.out.println("3. Добавить новую книгу");
            System.out.println("4. Удалить писателя (с проверкой целостности)");
            System.out.println("5. Удалить книгу");
            System.out.println("0. Выход");
            System.out.print("Выберите действие: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    displayBooksWithAuthors();
                    break;
                case "2":
                    addWriter();
                    break;
                case "3":
                    addBook();
                    break;
                case "4":
                    deleteWriterWithCheck();
                    break;
                case "5":
                    deleteBook();
                    break;
                case "0":
                    System.out.println("Выход из программы...");
                    close();
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }
}