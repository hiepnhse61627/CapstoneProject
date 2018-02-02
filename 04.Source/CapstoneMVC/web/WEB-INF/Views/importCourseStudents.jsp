<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Nhập danh sách lịch học của SV</h1>
            <hr>
        </div>
        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        Bấm vào <a class="link" href="/Resources/FileTemplates/CourseStudent.xlsx">Template</a>
                        để tải
                        về bản mẫu
                    </div>
                    <div class="title">
                        <label>Chọn file:</label>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <label for="file" hidden></label>
                            <input type="file" accept=".xlsx, .xls" id="file" name="file" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="left-content m-r-5">
                    <label>Chọn học kỳ để nhập thời khóa biểu:</label>
                </div>
                <div class="right-content width-30 width-m-70">
                    <select id="semester" class="select form-control">
                        <c:forEach var="semester" items="${semesters}">
                            <option value="${semester.id}">${semester.semester}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <button type="button" onclick="Add()" class="btn btn-success">Import</button>
            </div>
        </div>
    </div>
</section>

<script>
    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
        form.append('semesterId', $('#semester').val());

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                $.ajax({
                    type: "POST",
                    url: "/uploadCourseStudents",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        isRunning = false;
                        if (result.success) {
                            swal({
                                title: 'Thành công',
                                text: "Đã import lịch học!",
                                type: 'success'
                            }).then(function () {
                                location.reload();
                            });
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                    }
                });
                waitForTaskFinish(isRunning);
            },
            allowOutsideClick: false
        });
    }

    function waitForTaskFinish(running) {
        $.ajax({
            type: "GET",
            url: "/getlinestatus",
            processData: false,
            contentType: false,
            success: function (result) {
                $('#progress').html("<div>(" + result.current + "/" + result.total + ")</div>");
                console.log("task running");
                if (running) {
                    setTimeout("waitForTaskFinish(isRunning)", 50);
                }
            }
        });
    }
</script>