package org.example.server;

import org.example.library.LibraryRMIImpl;
import org.example.library.LibraryRemote;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class LibraryServer {

    public static void main(String[] args) {
        try {
            LibraryRemote remoteLibrary = new LibraryRMIImpl();
            System.out.println("Сервер: удаленный объект создан");

            Registry registry;
            try {

                registry = LocateRegistry.getRegistry("localhost", 1099);
                registry.list();
                System.out.println("Сервер: подключились к существующему реестру");
            } catch (RemoteException e) {
                System.out.println("Сервер: реестр не найден, создаем новый на порту 1099");
                registry = LocateRegistry.createRegistry(1099);
            }

            registry.rebind("LibraryService", remoteLibrary);

            System.out.println("Сервер готов. Объект 'LibraryService' зарегистрирован в RMI-реестре.");
            System.out.println("Ожидание вызовов от клиентов...");

        } catch (Exception e) {
            System.err.println("Сервер: критическая ошибка");
            e.printStackTrace();
        }
    }
}
