package org.example.library;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;


public interface LibraryRemote extends Remote {


    String displayBooksWithAuthors() throws RemoteException;

    String displayAuthors() throws RemoteException;

    String addWriter(String firstName, String lastName, String patronymic,
                     String birthDate, String country, String description) throws RemoteException;

    String addBook(int writerId, String bookName, int bookYear,
                   int pagesCount, String description, String coverPath) throws RemoteException, SQLException;


    String checkWriterBeforeDelete(int writerId) throws RemoteException;
    String deleteWriter(int writerId) throws RemoteException;


    String deleteBook(int bookId) throws RemoteException;

    String deleteBooksByWriter(int writerId) throws RemoteException;



}