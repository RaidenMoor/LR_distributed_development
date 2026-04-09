<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Муся угадала! - Победа</title>
    <link rel="stylesheet" href="css/style.css">
    <style>

        .result {
            background: #a8e6cf;
            padding: 20px;
            border-radius: 15px;
            margin-bottom: 20px;
        }

        .result p {
            color: white;
            font-size: 1.2em;
            margin: 10px 0;
        }

        .number {
            font-size: 2.5em;
            font-weight: bold;
            margin: 10px 0;
        }

        .attempts {
            font-size: 1.2em;
        }

        a {
            display: inline-block;
            background:  #667eea;
            color: white;
            text-decoration: none;
            padding: 14px 40px;
            font-size: 16px;
            border-radius: 8px;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
            margin-top: 10px;
        }

        a:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .confetti {
            font-size: 3em;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Ура! Муся угадала ваше число!</h1>
        <img class="cat-image" src="/images/victory.png" alt="победа" width="150" height="150"><br>

        <div class="result">
            <p>Ваше число:</p>
            <div class="number">${min}</div>
            <p class="attempts">Попыток потребовалось: <strong>${step}</strong></p>
        </div>
        <a href="welcome.jsp">Сыграть ещё раз</a>
    </div>
</body>
</html>