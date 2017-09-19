<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<style>
    table.formatHTML5 tr.selectedRow {
        background-color: #e92929 !important;
        color:#fff;
        vertical-align: middle;
        padding: 1.5em;
    }
</style>

<section class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</section>
<section class="content">
    <h1>
        Import Subjects
    </h1>
    <form id="form" enctype=”multipart/form-data”>
        <div class="col-md-12">
            <c:if test="${not empty files}">
                <h4>Các file đã sử dựng</h4>
                <table id="table" class="table">
                    <c:forEach var="file" items="${files}">
                        <tr><td>${file.name}</td></tr>
                    </c:forEach>
                </table>
            </c:if>
            <div class="form-group">
                <label for="file">File</label>
                <input type="file" accept=".xls" id="file" name="file" placeholder="Roll Number"/>
            </div>
            <div class="form-group">
                Bấm vào <a class="link" href="/Resources/FileTemplates/SubjectList_Upload_Template.xls">Template</a> để tải về bản mẫu
            </div>
            <div class="form-group">
                <button type="button" onclick="Add()" class="btn btn-success">Upload</button>
            </div>
        </div>
    </form>
</section>

<script>
    $(document).ready(function() {
        $("#table tbody tr").click(function () {
            $('.selectedRow').removeClass('selectedRow');
            $(this).addClass("selectedRow");
//            var product = $('.p',this).html();
            var file = $(this).html();
//            var note =$('.n',this).html();
            alert(file);
        });
    });

    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
//        console.log(form);


        swal({
            title: 'Đang xử lý',
            text: 'Tiến trình có thể kéo dài vài phút!',
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/subject",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function(result) {
                        console.log(result);
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