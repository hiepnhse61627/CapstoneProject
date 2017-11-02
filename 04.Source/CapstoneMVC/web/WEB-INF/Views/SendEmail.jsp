<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Gửi email cho sinh viên</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Chọn file:</h4>
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
                <button type="button" onclick="Add()" class="btn btn-success">Nhập danh sách</button>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Danh sách sinh viên được gửi</h4>
                    </div>
                    <div class="my-content">
                        <table id="table">
                        </table>
                    </div>
                </div>
            </div>

            <button type="button" onclick="Send()">Gửi</button>
        </div>

    </div>
</section>

<script>
    var array = null;

    $(document).ready(function () {

    });

    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);

        swal({
            title: 'Đang xử lý',
            html: "Đợi giây lát!",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                $.ajax({
                    type: "POST",
                    url: "/email/uploadEmail",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        isRunning = false;
                        if (result.success) {
                            array = result.data;
                            $('#table').DataTable({
                                data: array,
                                columns: [
                                    { title: "MSSV" },
                                    { title: "họ tên" },
                                    { title: "môn nợ" },
                                    { title: "môn tiếp theo" },
                                    { title: "môn đang học trong kỳ" },
                                    { title: "Danh sách môn học dự kiến tiếp theo" }
                                ]
                            });
                            swal.close();
//                            swal({
//                                title: 'Thành công',
//                                text: "Đã import các khóa học!",
//                                type: 'success'
//                            }).then(function () {
//                                location.reload();
//                            });
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                    }
                });
            },
            allowOutsideClick: false
        });
    }

    function Send() {
        $.ajax({
            type: "POST",
            url: "/email/send",
            data: { "params" : JSON.stringify(array)},
            success: function (result) {
                console.log(result);
            }
        });
    }
</script>
