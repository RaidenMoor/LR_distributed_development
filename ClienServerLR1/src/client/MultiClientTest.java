package client;

import Vector.Vector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MultiClientTest {
    private static final String SERVER_HOST = "127.0.0.1";
    private static final int SERVER_PORT = 8000;
    private static final int NUMBER_OF_CLIENTS = 5;
    private static final int VECTORS_PER_CLIENT = 3;

    public static void main(String[] args) {
        System.out.println("Работают " + NUMBER_OF_CLIENTS + " клиентов одновременно");
        System.out.println("Каждый клиент отправляет " + VECTORS_PER_CLIENT + " вектора\n");

        ExecutorService clientPool = Executors.newFixedThreadPool(NUMBER_OF_CLIENTS);

        // счётчик для ожидания завершения всех клиентов
        CountDownLatch latch = new CountDownLatch(NUMBER_OF_CLIENTS);

        List<ClientResult> results = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= NUMBER_OF_CLIENTS; i++) {
            final int clientId = i;
            clientPool.execute(() -> {
                ClientResult result = runClient(clientId);
                synchronized (results) {
                    results.add(result);
                }
                latch.countDown();
            });

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            latch.await(30, TimeUnit.SECONDS);

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            System.out.println("\nОбщее время выполнения: " + totalTime + " мс");
            System.out.println("Среднее время на клиента: " + (totalTime / NUMBER_OF_CLIENTS) + " мс");
            System.out.println("\nДетализация по клиентам:");

            for (ClientResult result : results) {
                System.out.println(result);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientPool.shutdown();
        }
    }


    private static ClientResult runClient(int clientId) {
        ClientResult result = new ClientResult(clientId);
        long clientStartTime = System.currentTimeMillis();

        System.out.println("Клиент " + clientId + " запущен");

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            Random random = new Random(clientId);

            for (int i = 1; i <= MultiClientTest.VECTORS_PER_CLIENT; i++) {
                int dimensions = 2 + random.nextInt(4);
                double[] coordinates = new double[dimensions];

                for (int j = 0; j < dimensions; j++) {
                        coordinates[j] = 1 + random.nextDouble() * 10;
                }

                Vector request = new Vector(coordinates);
                System.out.println("Клиент " + clientId + " [" + i + "/" + MultiClientTest.VECTORS_PER_CLIENT +
                        "] отправляет: " + formatCoordinates(coordinates));

                out.writeObject(request);
                out.flush();

                Vector response = (Vector) in.readObject();

                if (response.getLength() == 0.0) {
                    System.out.println("Клиент " + clientId + " получил: ОШИБКА (нулевой вектор)");
                    result.addError();
                } else {
                    System.out.println("Клиент " + clientId + " получил: длина = " +
                            String.format("%.4f", response.getLength()));
                    result.addSuccess(response.getLength());
                }

                Thread.sleep(200);
            }

            out.writeObject(new Vector(true));
            out.flush();

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            System.err.println("Клиент " + clientId + " ошибка: " + e.getMessage());
            result.setError(e.getMessage());
        }

        long clientTime = System.currentTimeMillis() - clientStartTime;
        result.setTime(clientTime);

        System.out.println("Клиент " + clientId + " завершил работу за " + clientTime + " мс");
        return result;
    }


    private static String formatCoordinates(double[] coords) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < coords.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(String.format("%.2f", coords[i]));
        }
        sb.append("]");
        return sb.toString();
    }

    static class ClientResult {
        private final int clientId;
        private int successCount = 0;
        private int errorCount = 0;
        private double totalLength = 0.0;
        private long timeMs;
        private String errorMessage;

        public ClientResult(int clientId) {
            this.clientId = clientId;
        }

        public void addSuccess(double length) {
            successCount++;
            totalLength += length;
        }

        public void addError() {
            errorCount++;
        }

        public void setTime(long timeMs) {
            this.timeMs = timeMs;
        }

        public void setError(String message) {
            this.errorMessage = message;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Клиент ").append(clientId).append(": ");
            sb.append("успешно: ").append(successCount);
            sb.append(", ошибок: ").append(errorCount);
            if (successCount > 0) {
                sb.append(", ср.длина=").append(String.format("%.2f", totalLength / successCount));
            }
            sb.append(", время=").append(timeMs).append(" мс");
            if (errorMessage != null) {
                sb.append(", ошибка: ").append(errorMessage);
            }
            return sb.toString();
        }
    }
}