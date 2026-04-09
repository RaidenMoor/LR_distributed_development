<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Обнаружен жулик!</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
          .message {
            background: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 20px;
        }

        .message p {
            color: #856404;
            font-size: 1.1em;
        }

        .warning {
            font-size: 3em;
            margin-bottom: 10px;
        }

        a {
            display: inline-block;
            background: linear-gradient(135deg, #dc3545, #c82333);
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
            box-shadow: 0 5px 15px rgba(220, 53, 69, 0.4);
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Муся видит, что вы жульничаете!</h1>
        <img class="cat-image" src="/images/cheater.png" alt="жульничество" width="150" height="150"><br>

        <div class="message">
            <p>Проверьте, что вы честно указываете «больше», «меньше» или «равно».</p>
            <p>Муся очень расстроена вашим поведением!</p>
        </div>

        <a href="welcome.jsp">Сыграть ещё раз (честно)</a>
    </div>
</body>
</html>