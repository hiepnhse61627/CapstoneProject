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
                        <div class="col-md-12">
                            <button type="button" class="btn btn-warning" onclick="SelectAll()">Chọn tất cả</button>
                            <button type="button" class="btn btn-warning" onclick="SelectOnlyFail()">Chỉ chọn sv có rớt môn</button>
                            <button type="button" class="btn btn-success pull-right" onclick="Send()">Gửi</button>
                        </div>
                        <div class="col-md-12">
                            <table id="table">
                                <thead>
                                <tr>
                                    <th>Chọn</th>
                                    <th>MSSV</th>
                                    <th>Họ tên</th>
                                    <th>Email</th>
                                    <th>tín chỉ tích lũySV</th>
                                    <th>môn nợ</th>
                                    <th>môn tiếp theo</th>
                                    <th>môn đang học trong kỳ</th>
                                    <th>môn chậm tiến độ</th>
                                    <th>Danh sách môn học dự kiến tiếp theo</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    var table = null;

    $(document).ready(function () {
        $('#table').DataTable();
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
                            $('#table').DataTable().destroy();
                            table = $('#table').DataTable({
                                "data": array,
                                "deferRender": false,
                                "scrollY": 600,
                                "scrollX": true,
                                "scrollCollapse": true,
                                "scroller": true,
                                "columnDefs": [
                                    {
                                        "targets": [1, 2, 3, 4, 6, 7, 8],
                                        "sortable": false,
                                        "class": "text-center",
                                    },
                                    {
                                        "targets": [0],
                                        "render": function (data, type, row) {
                                            return "<input name='send' type='checkbox'/>";
                                        },
                                        "sortable": false,
                                    },
                                    {
                                        "targets": [5],
                                        "render": function (data, type, row) {
                                            return data + "<input type='hidden' name='data' value='" + data + "'/>";
                                        },
                                        "sortable": false,
                                    }
                                ],
//                                "search": true,
//                                "filter": false,
                            });
                            swal.close();
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                    }
                });
            },
            allowOutsideClick: false
        });
    }

    var select = true;
    function SelectAll() {
        if (select) {
            $("input[name='send']", table.rows({search:'applied'}).nodes()).prop('checked', true);
            select = false;
        } else {
            $("input[name='send']", table.rows({search:'applied'}).nodes()).prop('checked', false);
            select = true;
        }
    }

    function SelectOnlyFail() {
        $.each($("input[name='data']", table.rows({search:'applied'}).nodes()), function () {
            var data = $(this).val();
//            console.log(data);
            if (data != 'N/A') {
                $(this).closest("tr").find("input[name='send']").prop('checked', true);
            }
        });
    }

    function Send() {
        var array = [];
        $.each($("input[name='send']:checked", table.rows({search:'applied'}).nodes()), function () {
            var values = [];
            $.each($(this).closest("tr").find("td"), function (i) {
                if (i > 0) {
                    values.push($(this).html());
                }
            });
            array.push(values);
        });
//        console.log(array);

        swal({
            title: 'Đang xử lý',
            html: '<div class="form-group">Tiến trình có thể kéo dài vài phút</div>' +
            '<div class="form-group" id="progress"></div>' +
            '<div><button id="stop">Stop</button></div>',
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/email/send",
                    data: {"params": JSON.stringify(array)},
                    success: function (result) {
                        if (result.success) {
                            swal('', 'Đã gửi thành công', 'success');
                        } else {
                            swal('', result.msg, 'error');
                        }
                    }
                });
                $('#stop').click(function () {
                    $.ajax({
                        type: "GET",
                        url: "/email/stop",
                        processData: false,
                        contentType: false,
                        success: function (result) {
                            console.log(result);
                        }
                    });
                });
                Run();
            },
            allowOutsideClick: false
        });
    }

    function Run() {
        $.ajax({
            type: "GET",
            url: "/email/status",
            processData: false,
            contentType: false,
            success: function (result) {
                $('#progress').html("<div>" + result.status + "</div>");
                if (result.run) {
                    setTimeout("Run()", 50);
                }
            }
        });
    }
</script>
