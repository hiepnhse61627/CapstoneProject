<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Nhập danh sách lịch dạy của GV</h1>
            <hr>
        </div>
        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        Bấm vào <a class="link" href="/Resources/FileTemplates/Schedule.xlsx">Template</a>
                        để tải
                        về bản mẫu
                    </div>
                    <div class="title">
                        <label>Chọn file:</label>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <label for="file" hidden></label>
                            <input type="file" accept=".xlsx, .xls" id="file" name="file"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="left-content m-r-5">
                    <label>Chọn học kỳ:</label>
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
                <button type="button" onclick="DeacttiveAll()" class="btn btn-danger">Xóa tất cả TKB</button>
            </div>
        </div>
    </div>
</section>

<script>
    var isRunning = true;

    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
        form.append('semesterId', $('#semester').val());

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!</div><div id='progress' class='form-group'></div><div id='progress2' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                $.ajax({
                    type: "POST",
                    url: "/uploadSchedules",
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
                waitForExcelFinish();
                // waitForTaskFinish(isRunning);
            },
            allowOutsideClick: false
        });
    }

    function waitForTaskFinish(running) {
        $.ajax({
            type: "GET",
            url: "/getLineScheduleStatus",
            processData: false,
            contentType: false,
            success: function (result) {
                $('#progress2').html("<h4>Thêm vào database</h4><div>(" + result.current + "/" + result.total + ")</div>");
                if (running) {
                    setTimeout("waitForTaskFinish(isRunning)", 50);
                }
            }
        });
    }

    function waitForExcelFinish() {
        $.ajax({
            type: "GET",
            url: "/getExcelCurrentLineStatus",
            processData: false,
            contentType: false,
            success: function (result) {
                    $('#progress').html("<h4>Đang thêm dữ liệu</h4><div>(" + result.excelCurrent + "/" + result.excelTotal + ")</div>");
                    setTimeout("waitForExcelFinish()", 500);
            }
        });
    }

    function DeacttiveAll() {
        swal({
            title: 'Xóa toàn bộ TKB cũ?',
            text: "Bạn có chắc muốn xóa tất cả TKB cũ?",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Xóa tất cả'
        }).then(function () {
                swal(
                    'Deleted!',
                    'Your file has been deleted.',
                    'success'
                )

            swal({
                title: 'Đang xử lý',
                html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!</div>",
                type: 'info',
                onOpen: function () {
                    swal.showLoading();
                    $.ajax({
                        type: "POST",
                        url: "/deacttiveAllSchedule",
                        processData: false,
                        contentType: false,
                        success: function (result) {
                            if (result.success) {
                                swal({
                                    title: 'Thành công',
                                    text: "Đã xóa "+ result.countUpdated + " dữ liệu",
                                    type: 'success'
                                }).then(function () {
                                    location.reload();
                                });
                            } else {
                                swal('Đã xảy ra lỗi!', result.message, 'error');
                            }
                        }
                    });
                },
                allowOutsideClick: false
            });
        });
    }
</script>