package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerParallel {

    private static final int PORT = 8000;
    private static final int MAX_THREADS = 10;
    public static void main(String[] args) throws IOException {

        ExecutorService threadPool = Executors.newFixedThreadPool(MAX_THREADS);

        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                //подключение нового клиента
                Socket clientSocket = serverSocket.accept();

                // передача обработки клиента в пул потоков
                threadPool.execute(new ClientHandler(clientSocket));

                System.out.println("Клиент подключён. Активных потоков: " +
                        ((ThreadPoolExecutor) threadPool).getActiveCount());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
