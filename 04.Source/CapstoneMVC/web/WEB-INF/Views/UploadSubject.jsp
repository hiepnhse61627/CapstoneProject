<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content-header">
    <h1>Nhập môn học</h1>
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
                        <h4 class="box-title">Chọn file</h4>
                    </div>
                    <label for="file" hidden></label>
                    <input type="file" accept=".xls" id="file" name="file" placeholder="Roll Number"/>
                </div>
                <div class="form-group">
                    Bấm vào <a class="link" href="/Resources/FileTemplates/SubjectList_Upload_Template.xls">Template</a>
                    để tải
                    về bản mẫu
                </div>
                <div class="form-group">
                    <button type="button" onclick="Add()" class="btn btn-success">Upload</button>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    var isRunning = false;

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
                            url: "/subject/upload-exist-file",
                            processData: false,
                            contentType: false,
                            data: form,
                            success: function (result) {
                                isRunning = false;
                                if (result.success) {
                                    swal({
                                        title: 'Thành công',
                                        text: "Đã import các môn học!",
                                        type: 'success'
                                    }).then(function () {
                                        location.reload();
                                    });
                                } else {
                                    swal('Đã xảy ra lỗi!', result.message, 'error');
                                }
                            }
                        });
                        updateLineStatus(isRunning);
                    },
                    allowOutsideClick: false
                });
            });
        }
    }

    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
        isRunning = true;

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/subject/upload",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        console.log(result);
                        if (result.success) {
                            isRunning = false;
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
                updateLineStatus(isRunning);
            },
            allowOutsideClick: false
        });
    }

    function updateLineStatus(running) {
        $.ajax({
            type: "GET",
            url: "/subject/getlinestatus",
            processData: false,
            contentType: false,
            success: function (result) {
                $('#progress').html("<div>(" + result.currentLine + "/" + result.totalLine + ")</div>");
                if (running) {
                    setTimeout("updateLineStatus(isRunning)", 50);
                }
            }
        });
    }
</script>