package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/start")
public class StartServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int min = Integer.parseInt(request.getParameter("min"));
            int max = Integer.parseInt(request.getParameter("max"));

            if (min >= max) {
                throw new IllegalArgumentException("Минимум должен быть меньше максимума");
            }

            int guess = (min + max) / 2;
            HttpSession session = request.getSession();

            session.setAttribute("min", min);
            session.setAttribute("max", max);
            session.setAttribute("guess", guess);
            session.setAttribute("step", 1);

            request.getRequestDispatcher("/game.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("exception", new Exception("Пожалуйста, введите корректные числа"));
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("exception", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }
    }
}