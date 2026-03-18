package org.example.client;

import org.example.library.LibraryRemote;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class LibraryClient {

    public static void main(String[] args) {
        String host = (args.length < 1) ? "localhost" : args[0];
        Scanner scanner = new Scanner(System.in);

        try {
            Registry registry = LocateRegistry.getRegistry(host);

            LibraryRemote library = (LibraryRemote) registry.lookup("LibraryService");

            System.out.println("Клиент: подключен к серверу " + host);
            System.out.println("Клиент: удаленный объект 'LibraryService' найден");

            while (true) {
                System.out.println("\n--- Меню клиента библиотеки ---");
                System.out.println("1. Показать все книги");
                System.out.println("2. Добавить писателя");
                System.out.println("3. Добавить книгу");
                System.out.println("4. Удалить писателя");
                System.out.println("5. Удалить книгу");
                System.out.println("0. Выход");
                System.out.print("Выберите действие: ");

                String choice = scanner.nextLine();
                String writers = library.displayAuthors();

                try {
                    switch (choice) {
                        case "1":
                            String books = library.displayBooksWithAuthors();
                            System.out.println("\n" + books);
                            break;

                        case "2":
                            System.out.print("Имя: ");
                            String firstName = scanner.nextLine();
                            System.out.print("Фамилия: ");
                            String lastName = scanner.nextLine();
                            System.out.print("Отчество (enter если нет): ");
                            String patronymic = scanner.nextLine();
                            if (patronymic.isEmpty()) patronymic = null;
                            System.out.print("Дата рождения (enter если нет): ");
                            String birthDate = scanner.nextLine();
                            if (birthDate.isEmpty()) birthDate = null;
                            System.out.print("Страна (enter если нет): ");
                            String country = scanner.nextLine();
                            if (country.isEmpty()) country = null;
                            System.out.print("Описание (enter если нет): ");
                            String desc = scanner.nextLine();
                            if (desc.isEmpty()) desc = null;

                            String addWriterResult = library.addWriter(firstName, lastName, patronymic,
                                    birthDate, country, desc);
                            System.out.println(addWriterResult);
                            break;

                        case "3":

                            System.out.println("Список авторов:");
                            System.out.println("\n"+writers);
                            System.out.print("ID автора: ");
                            int wId = Integer.parseInt(scanner.nextLine());
                            System.out.print("Название книги: ");
                            String bName = scanner.nextLine();
                            System.out.print("Год издания: ");
                            int bYear = Integer.parseInt(scanner.nextLine());
                            System.out.print("Кол-во страниц (0 если неизвестно): ");
                            int pages = Integer.parseInt(scanner.nextLine());
                            System.out.print("Описание (enter если нет): ");
                            String bDesc = scanner.nextLine();
                            if (bDesc.isEmpty()) bDesc = null;
                            System.out.print("Путь к обложке (enter если нет): ");
                            String cover = scanner.nextLine();
                            if (cover.isEmpty()) cover = null;

                            String addBookResult = library.addBook(wId, bName, bYear, pages, bDesc, cover);
                            System.out.println(addBookResult);
                            break;

                        case "4":
                            System.out.println("Список авторов:");
                            System.out.println("\n"+writers);
                            System.out.print("ID писателя для удаления: ");
                            int delWriterId = Integer.parseInt(scanner.nextLine());
                            String writerCheck = library.checkWriterBeforeDelete(delWriterId);
                            if(writerCheck != null) {
                                System.out.println(writerCheck);
                                System.out.print("\nУдалить все книги этого писателя перед удалением? (yes/no): ");
                                String answer = scanner.nextLine().toLowerCase();
                                if (answer.equals("yes") || answer.equals("y")) {
                                    String delBookResult = library.deleteBooksByWriter(delWriterId);
                                    System.out.println(delBookResult);
                                    String delWriterResult = library.deleteWriter(delWriterId);
                                    System.out.println(delWriterResult);

                                } else {
                                    System.out.println("Удаление отменено. Сначала удалите книги вручную.");

                                }
                            }

                            break;

                        case "5":
                            System.out.print("ID книги для удаления: ");
                            int delBookId = Integer.parseInt(scanner.nextLine());
                            String delBookResult = library.deleteBook(delBookId);
                            System.out.println(delBookResult);
                            break;

                        case "0":
                            System.out.println("Клиент завершает работу.");
                            return;

                        default:
                            System.out.println("Неверный выбор.");
                    }
                } catch (Exception e) {
                    System.err.println("Ошибка при вызове удаленного метода: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.err.println("Клиент: не удалось подключиться к серверу");
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
