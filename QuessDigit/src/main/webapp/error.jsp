<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Серверная ошибка</title>
    <link rel="stylesheet" href="css/style.css">
    <style>

  body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #ffc0cb;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }

        .container {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0,0,0,0.3);
            padding: 40px;
            text-align: center;
            max-width: 500px;
            width: 100%;
            animation: victory 0.8s ease-out;
        }

        h1 {
            color: #dc143c;
            margin-bottom: 20px;
            font-size: 1.8em;
        }

        .error-box {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 20px;
        }

        .error-box p {
            color: #721c24;
            font-family: monospace;
            font-size: 0.9em;
            word-wrap: break-word;
        }

        .error-icon {
            font-size: 3em;
            margin-bottom: 10px;
        }

        a {
            display: inline-block;
            background: #667eea;
            color: white;
            text-decoration: none;
            padding: 12px 30px;
            font-size: 14px;
            border-radius: 8px;
            cursor: pointer;
            transition: transform 0.2s, box-shadow 0.2s;
        }

        a:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .back-link {
            margin-top: 20px;
            display: inline-block;
        }
    </style>
</head>
<body>
    <div class="container">

        <h1>Муся не в ресурсе играть</h1>
        <img class="cat-image" src="/images/error.png" alt="ошибка" width="150" height="150"><br>

        <div class="error-box">
            <p><strong>Ошибка:</strong> ${exception.message}</p>
        </div>

        <p>Пожалуйста, проверьте введенные данные и попробуйте снова.</p>
        <a href="javascript:history.back()" class="back-link">Вернуться на предыдущую страницу</a>
    </div>
</body>
</html>