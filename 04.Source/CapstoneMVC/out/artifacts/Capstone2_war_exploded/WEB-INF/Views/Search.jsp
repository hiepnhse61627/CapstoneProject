<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</section>
<section class="content">
    <h1>
        Page Header
        <small>Optional description</small>
    </h1>
    <div class="col-md-12">
        <form action="/search" method="post">
            <div class="form-group">
                <label for="ss">Search</label>
                <input type="text" id="ss" name="txtSearch" placeholder="Enter student rollnumber"/>
                <button type="submit">Search</button>
            </div>
        </form>
    </div>
    <div class="col-md-12">
        <c:if test="${not empty student and not empty student.firstName}">
            <h2>Student information</h2>
            <div class="text text-primary">
                ${student.firstName}
            </div>
        </c:if>
    </div>
</section>