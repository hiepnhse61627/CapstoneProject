<%--
  Created by IntelliJ IDEA.
  User: hiepnhse61627
  Date: 17/09/2017
  Time: 04:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<body>
<div class="col-md-12">
    <form:form id="uploadStudentMarks" action="/uploadStudentMarks" enctype="multipart/form-data">
        <input type="file" name="file"/><br/>
        <input type="submit" value="Upload File"/>
    </form:form>
</div>
</body>
</html>
