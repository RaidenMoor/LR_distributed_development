package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/guess")
public class GuesserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        String action = request.getParameter("action");

        Integer currentMin = (Integer) session.getAttribute("min");
        Integer currentMax = (Integer) session.getAttribute("max");
        Integer currentGuess = (Integer) session.getAttribute("guess");
        Integer step = (Integer) session.getAttribute("step");


        try {
            if ("less".equals(action)) {

                if (currentMin == null || currentMax == null || currentGuess == null) {
                    response.sendRedirect("welcome.jsp");
                    return;
                }

                int newMax = currentGuess;
                session.setAttribute("max", newMax);

                if (currentMin > newMax || currentMin == newMax) {
                    response.sendRedirect("cheater.jsp");
                    return;
                }

                int newGuess = (currentMin + newMax) / 2;
                session.setAttribute("guess", newGuess);
                session.setAttribute("step", step + 1);

                request.getRequestDispatcher("/game.jsp").forward(request, response);

            } else if ("more".equals(action)) {

                if (currentMin == null || currentMax == null || currentGuess == null) {
                    response.sendRedirect("welcome.jsp");
                    return;
                }
                int newMin = currentGuess;
                session.setAttribute("min", newMin);

                if (currentMax < newMin || currentMax == newMin) {
                    response.sendRedirect("cheater.jsp");
                    return;
                }

                int newGuess = (newMin + currentMax) / 2;
                session.setAttribute("guess", newGuess);
                session.setAttribute("step", step + 1);

                request.getRequestDispatcher("/game.jsp").forward(request, response);

            } else if ("equal".equals(action)) {
                request.setAttribute("min", currentGuess);
                request.setAttribute("step", step);
                request.getRequestDispatcher("/victory.jsp").forward(request, response);
            }

        } catch (Exception e) {
            request.setAttribute("exception", e);
            request.getRequestDispatcher("/error.jsp").forward(request, response);
        }


    }
}