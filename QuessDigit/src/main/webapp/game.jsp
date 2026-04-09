<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ page errorPage="error.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>Игра</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .info {
            background: #f0f0f0;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .attempt {
            font-size: 1.2em;
            color: #666;
            margin-bottom: 10px;
        }

        .guess {
            font-size: 2.5em;
            font-weight: bold;
            color: #f5576c;
            margin: 20px 0;
            padding: 20px;
            background: #fffafa;
            border-radius: 10px;
        }

        .button-group {
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }

        button {
            background: #667eea;
            color: white;
            border: none;
            padding: 12px 25px;
            font-size: 16px;
            border-radius: 8px;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
            flex: 1;
            min-width: 120px;
        }

        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }

        button:active {
            transform: translateY(0);
        }

        .btn-less {
            background: #ff6b6b;
        }

        .btn-more {
            background:#4ecdc4;
        }

        .btn-equal {
            background: #4169e1;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Муся думает...</h1>
        <img class="cat-image" src="/images/game.png" alt="игра" width="150" height="150"><br>

        <div class="info">
            <div class="attempt">Попытка: <strong>${step}</strong></div>
            <div class="guess">Предположение Муси: <strong>${guess}</strong></div>
        </div>

        <form method="post" action="/guess">
            <div class="button-group">
                <button type="submit" name="action" value="less" class="btn-less">Мое число меньше</button>
                <button type="submit" name="action" value="more" class="btn-more">Мое число больше</button>
                <button type="submit" name="action" value="equal" class="btn-equal">Число угадано!</button>
            </div>
        </form>
    </div>
</body>
</html>