<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Nhập danh sách khóa học</h1>
            <hr>
        </div>

        <div class="b-body">
            <c:if test="${not empty files}">
                <div class="form-group">
                    <div class="row">
                        <div class="title">
                            <h4>Các file gần đây:</h4>
                        </div>
                        <div class="my-content">
                            <div class="col-md-12">
                                <table id="table" class="table">
                                    <c:forEach var="file" items="${files}">
                                        <tr class="table-row">
                                            <td>${file.name}</td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </div>
                            <div class="col-md-12">
                                <button type="button" class="btn btn-primary" onclick="UseFile()">Sử dụng</button>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Chọn file:</h4>
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
                Bấm vào <a class="link" href="/Resources/FileTemplates/DSSV_Template.xlsx">Template</a> để tải
                về bản mẫu
            </div>
            <div class="form-group">
                <button type="button" onclick="Add()" class="btn btn-success">Import</button>
            </div>
        </div>

    </div>
</section>

<script>
    // global variables
    var isRunning = true;

    $(document).ready(function () {
        $("#table tbody tr").click(function () {
            $('.selectedRow').removeClass('selectedRow');
            $('#selected').removeAttr('id', 'selected');
            $(this).addClass("selectedRow");
            $('.selectedRow').attr('id', 'selected');
        });
    });

    function UseFile() {
        if ($('#selected td').length == 0) {
            swal('', 'Hãy chọn file trước', 'error');
        } else {
            swal({
                title: 'Bạn có chắc là sử dụng file này?',
                type: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Tiếp tục',
                cancelButtonText: 'Đóng'
            }).then(function () {
                var form = new FormData();
                form.append('file', $('#selected td').html());

                swal({
                    title: 'Đang xử lý',
                    html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                    type: 'info',
                    onOpen: function () {
                        swal.showLoading();
                        isRunning = true;
                        $.ajax({
                            type: "POST",
                            url: "/uploadCourseExistFile",
                            processData: false,
                            contentType: false,
                            data: form,
                            success: function (result) {
                                isRunning = false;
                                if (result.success) {
                                    swal({
                                        title: 'Thành công',
                                        text: "Đã import các khóa học!",
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
            });

        }
    }

    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                $.ajax({
                    type: "POST",
                    url: "/uploadCourse",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        isRunning = false;
                        if (result.success) {
                            swal({
                                title: 'Thành công',
                                text: "Đã import các khóa học!",
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
            url: "/getCourseStatus",
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
