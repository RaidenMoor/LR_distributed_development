package client;

import Vector.Vector;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Client {
    public static void main(String[] args){
        String host = "127.0.0.1";
        int port = 8000;

        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        double[][] vectors = {
                {5.0, 2.0, 3.0},
                {11.0, 5.0},
                {0.0, 0.0, 0.0},
                {11.5, 2.5, 3.5, 4.5},
                {17.4,18.6,75.2,40.1,1.0,3.4}
        };

        for (double[] coords : vectors) {
            Vector request = new Vector(coords);
            System.out.println("Отправка вектора: " + java.util.Arrays.toString(coords));

            try {

                out.writeObject(request);
                out.flush();
                out.reset();

                Vector response = (Vector) in.readObject();

                if (response.getLength() == 0.0) {
                    System.out.println("Ошибка: нулевой вектор");
                } else {
                    System.out.println("Длина = " + response.getLength());
                }

            } catch (SocketTimeoutException e) {
                System.err.println("Таймаут ожидания ответа");
                break;
            } catch (IOException e) {
                System.err.println("Сервер закрыл соединение");
                break;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }


        }

        try {
            out.writeObject(new Vector(true));
            out.flush();

            out.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
