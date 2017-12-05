<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: hiepnhse61627
  Date: 17/09/2017
  Time: 10:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content-header">
    <h1>Nhập chương trình đào tạo</h1>
</section>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <c:if test="${not empty files}">
                    <div class="box-header">
                        <h4 class="box-title">Các file gần đây</h4>
                    </div>
                    <div class="form-group">
                        <table id="table" class="table">
                            <c:forEach var="file" items="${files}">
                                <tr class="table-row">
                                    <td>${file.name}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <div class="form-group">
                        <button type="button" class="btn btn-info" onclick="UseFile()">Sử dụng</button>
                    </div>
                </c:if>
                <div class="form-group">
                    <div class="box-header">
                        <h4 class="box-title">Chọn file</h4>
                    </div>
                    <label for="file" hidden></label>
                    <input type="file" accept=".xlsx, .xls" id="file" name="file" placeholder="DS khóa"/>
                </div>
                <div class="form-group">
                    Bấm vào <a class="link" href="/Resources/FileTemplates/AllCurriculum-Template.xlsx">Template</a> để tải
                    về bản mẫu
                </div>
                <div class="form-group">
                    <button type="button" onclick="Add()" class="btn btn-success">Import</button>
                </div>
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
            //            var file = $('td', this).html();
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
                            url: "/uploadCurriculumExistFile",
                            processData: false,
                            contentType: false,
                            data: form,
                            success: function (result) {
                                isRunning = false;
                                if (result.success) {
                                    swal({
                                        title: 'Thành công',
                                        text: "Đã import chương trình đào tạo!",
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
                    url: "/uploadCurriculum",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        isRunning = false;
                        if (result.success) {
                            swal({
                                title: 'Thành công',
                                text: "Đã import chương trình đào tạo!",
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
            url: "/getCuriculumStatus",
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
