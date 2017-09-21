<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: hiepnhse61627
  Date: 17/09/2017
  Time: 04:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content-header">
    <h1>Import danh sách điểm</h1>
</section>

<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <div class="form-group">
                    <div class="box-header">
                        <h4 class="box-title">Chọn file</h4>
                    </div>
                    <label for="file" hidden></label>
                    <input type="file" accept=".xls, .xlsx" id="file" name="file"/>
                </div>
                <div class="form-group">
                    <button type="button" onclick="Add()" class="btn btn-success">Upload</button>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    // Import file process
    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);

        swal({
            title: 'Đang xử lý',
            html: '<div class="form-group">Tiến trình có thể kéo dài vài phút</div><div id="progress" class="form-group"></div>',
            type: info,
            onOpen: function() {
                swal.showLoading();
                $.ajax({
                   type: 'POST',
                   url: '/uploadStudentMarks',
                   processData: false,
                   contentType: false,
                   data: form,
                   success: function (result) {
                       if (result.success) {
                           swal(
                               'Thành công!',
                               'Đã import các môn học!',
                               'success'
                           );
                       } else {
                           swal('Đã xảy ra lỗi!', result.message, 'error');
                       }
                   }
                });
            },
            allowOutsideClick: false
        });
    }
</script>

<%--<html>--%>
<%--<body>--%>
<%--<div class="col-md-12">--%>
    <%--<form:form id="uploadStudentMarks" action="/uploadStudentMarks" enctype="multipart/form-data">--%>
        <%--<input type="file" name="file"/><br/>--%>
        <%--<input type="submit" value="Upload File"/>--%>
    <%--</form:form>--%>
<%--</div>--%>
<%--</body>--%>
<%--</html>--%>
