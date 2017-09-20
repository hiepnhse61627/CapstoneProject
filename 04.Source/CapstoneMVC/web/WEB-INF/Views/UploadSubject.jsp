<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<style>

    #table {
        width: 60%;
        margin-bottom: 7px;
    }

    @media only screen and (max-width: 768px) {
        #table {
            width: 100%;
        }
    }

    #table tr td {
        border-radius: 5px;
    }

    .table-row {
        cursor: pointer;
    }

    .table-row:hover:not(.selectedRow) {
        /*background-color: #c8ecff;*/
        background-color: #f4f4f5;
    }

    .selectedRow {
        background-color: #f4b745;
        color: white;
    }

</style>

<section class="content-header">
    <h1>Import Subjects</h1>
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
//            var file = $('td', this).html();
        });
    });

    function UseFile() {
        alert($('#selected td').html());
    }

    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
        isRunning = true;

        swal({
            title: 'Đang xử lý',
            text: "yassss",
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/subject",
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