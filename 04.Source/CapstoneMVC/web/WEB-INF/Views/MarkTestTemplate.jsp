<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 26/03/18
  Time: 10:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>


<body>
<div class="form-group">
    <label for="roles" class="col-sm-3 control-label">Chức vụ</label>
    <div class="col-sm-9">
        <select class="form-control select" id="roles" name="roles[]" multiple="multiple">
            <c:forEach var="role" items="${roleList}">
                <option value="${role.name}">${role.name}</option>
            </c:forEach>
        </select>
    </div>
</div>
</body>
</html>
