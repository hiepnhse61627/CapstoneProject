<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%--
  Created by IntelliJ IDEA.
  User: hiepnhse61627
  Date: 18/09/2017
  Time: 09:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<div class="col-md-12">
    <form:form id="uploadSubjects" action="/uploadSubjects" enctype="multipart/form-data">
        <input type="file" name="file"/><br/>
        <input type="submit" value="Upload File"/>
    </form:form>
</div>
</body>
</html>
