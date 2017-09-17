<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</div>
<div class="content">
    <h1>
        Page Header
        <small>Optional description</small>
    </h1>
    <div class="col-md-12">
        <form:form method="post" action="/uploadMark" enctype="multipart/form-data">
            <input type="file" name="file" />
            <input type="submit" value="Upload File" />
        </form:form>
    </div>
</div>