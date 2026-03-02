package server;

import Vector.Vector;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        String clientInfo = clientSocket.getInetAddress() + ":" + clientSocket.getPort();
        System.out.println("Начало обработки клиента: " + clientInfo);

        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
             ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {

            while (true) {
                Vector request = (Vector) in.readObject();

                if (request.isStopSignal()) {
                    System.out.println("Клиент " + clientInfo + " завершил работу");
                    break;
                }

                double[] coords = request.getCoordinates();
                System.out.println("Клиент " + clientInfo + " отправил вектор: " +
                        java.util.Arrays.toString(coords));


                    double length = calculateLength(request);
                    request.setLength(length);

                out.writeObject(request);
                out.flush();
            }

        } catch (ClassNotFoundException | IOException e) {
            System.err.println("Ошибка при обработке клиента " + clientInfo + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Соединение с клиентом " + clientInfo + " закрыто");

        }

    }

    private double calculateLength(Vector vector){
        double length = 0.0;
        for (double v : vector.getCoordinates()) {
            length += v * v;
        }
        length = Math.sqrt(length);
        return length;
    }
}