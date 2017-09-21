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
<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content-header">
    <h1>Nhập danh sách điểm</h1>
</section>

<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <c:if test="${not empty files}">
                    <div class="box-header">
                        <h4 class="box-title">Các file đã sử dụng</h4>
                    </div>
                    <div class="form-group">
                        <table id="table" class="table">
                            <c:forEach var="file" items="${files}">
                                <tr class="table-row">
                                    <td>${file.name}</td>
                                </tr>
                            </c:forEach>
                        </table>
                        <button type="button" class="btn btn-primary" onclick="UseFile()">Sử dụng</button>
                    </div>
                </c:if>
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
    var isrunning = false;

    $(document).ready(function () {
        $("#table tbody tr").click(function () {
            $('.selectedRow').removeClass('selectedRow');
            $('#selected').removeAttr('id', 'selected');
            $(this).addClass("selectedRow");
            $('.selectedRow').attr('id', 'selected');
        });
    });

    function UseFile() {
        alert($('#selected td').html());
    }

    // Import file process
    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
        isrunning = true;

        swal({
            title: 'Đang xử lý',
            html: '<div class="form-group">Tiến trình có thể kéo dài vài phút</div><div id="progress" class="form-group"></div>',
            type: 'info',
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
                           isrunning = false;
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
                updateSuccessSavedMarks(isrunning);
            },
            allowOutsideClick: false
        });
    }

    function updateSuccessSavedMarks(running) {
        $.ajax({
            type: "GET",
            url: "/marks/getStatus",
            processData: false,
            contentType: false,
            success: function (result) {
                $('#progress').html("<div>Tổng số dòng trong file excel: " + result.totalLine + "</div>" +
                                    "<div>(" + result.successSavedMark + "/" + result.totalExistMarks + ")</div>");
                if (running) {
                    setTimeout("updateSuccessSavedMarks(isrunning)", 50);
                }
            }
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
