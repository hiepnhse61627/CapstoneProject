<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 9/13/2017
  Time: 2:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="dec" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
        <h1>Header</h1>
        <dec:body/>
        <h2>Footer</h2>
</body>
</html>
