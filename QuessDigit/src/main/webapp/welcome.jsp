<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Старт</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .description {
            color: #555;
            margin-bottom: 30px;
            font-size: 1.1em;
        }

        .form-group {
            margin-bottom: 20px;
            text-align: left;
        }

        label {
            display: block;
            margin-bottom: 8px;
            color: #333;
            font-weight: bold;
        }

        input {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
            transition: border-color 0.3s;
        }

        input:focus {
            outline: none;
            border-color: #667eea;
        }

        button {
            background: #667eea;
            color: white;
            border: none;
            padding: 14px 40px;
            font-size: 16px;
            border-radius: 8px;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        button:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        button:active {
            transform: translateY(0);
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Кошка-предсказатель Муся</h1>
        <img class="cat-image" src="/images/welcome.png" alt="кошка-предсказатель" width="150" height="150"><br>
        <div class="description">Введите диапазон, в котором загадаете число, а Муся попробует его отгадать</div>
        <form method="post" action="/start">
            <div class="form-group">
                <label>Минимум:</label>
                <input type="number" name="min" required>
            </div>
            <div class="form-group">
                <label>Максимум:</label>
                <input type="number" name="max" required>
            </div>
            <button type="submit">Приступить к угадыванию</button>
        </form>
    </div>
</body>
</html>