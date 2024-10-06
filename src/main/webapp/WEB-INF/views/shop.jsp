<%@ page contentType="text/html;charset=UTF-8" %>
<% String contextPath = request.getContextPath(); %>
<html>
<head>
    <title>Крамниця</title>
</head>
<body>
    <div data-context-path="<%=contextPath%>" id="app-container"></div>

    <script src="https://unpkg.com/react@18/umd/react.development.js" crossorigin></script>
    <script src="https://unpkg.com/react-dom@18/umd/react-dom.development.js" crossorigin></script>
    <script src="https://unpkg.com/babel-standalone@6/babel.min.js"></script>
    <script src="<%=contextPath%>/js/shop.js"></script>
</body>
</html>
