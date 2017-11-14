<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<style>
    .min-width-50px {
        min-width: 50px;
    }

    .min-width-75px {
        min-width: 75px;
    }

    .min-width-100px {
        min-width: 100px;
    }
</style>

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
                            <button type="button" class="btn btn-warning" onclick="SelectOnlyFail()">Chỉ chọn sv có rớt
                                môn
                            </button>
                            <%--<button type="button" class="btn btn-success pull-right" onclick="Send()">Gửi</button>--%>
                            <%--<a href="https://accounts.google.com/o/oauth2/auth?client_id=154261814473-m5o6qqmt4768ij676ore7280qbpgf03u.apps.googleusercontent.com&redirect_uri=http://localhost:8080/auth/google&scope=openid%20email%20profile&&response_type=code&approval_prompt=auto" class="btn btn-block btn-social btn-google btn-flat">--%>
                            <%--<i class="fa fa-google-plus"></i> Đăng nhập bằng Google+--%>
                            <%--</a>--%>
                            <button type="button" onclick="Authenticate()">test</button>
                        </div>
                        <div class="col-md-12">
                            <table id="table">
                                <thead>
                                <tr>
                                    <th>Chọn</th>
                                    <th>MSSV</th>
                                    <th>Họ tên</th>
                                    <th>Email</th>
                                    <th>Tín chỉ tích lũy</th>
                                    <th>Môn nợ</th>
                                    <th>Môn tiếp theo</th>
                                    <th>Môn đang học trong kỳ</th>
                                    <th>Môn chậm tiến độ</th>
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

    var OAUTHURL = 'https://accounts.google.com/o/oauth2/auth?';
    var VALIDURL = 'https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=';
    var SCOPE = 'https://mail.google.com/ https://www.googleapis.com/auth/userinfo.email';
    var CLIENTID = '415843400023-daksefsdrol9o9b5b79o9mhk8cskc9k4.apps.googleusercontent.com';
    var REDIRECT = 'http://localhost:8080/email/google';
    var TYPE = 'token';
    var url = OAUTHURL + 'scope=' + SCOPE + '&client_id=' + CLIENTID + '&redirect_uri=' + REDIRECT + '&response_type=' + TYPE;

    function Authenticate() {
        var win = window.open(url, "Choose a Email", 'width=800, height=600');

        var pollTimer = window.setInterval(function () {
            try {
                console.log(win.document.URL);
                if (win.document.URL.indexOf(REDIRECT) != -1) {
                    window.clearInterval(pollTimer);
                    var url = win.document.URL;
                    var acToken = url.match(/#(?:access_token)=([\S\s]*?)&/)[1];
                    var tokenType = gup(url, 'token_type');
                    var expiresIn = gup(url, 'expires_in');
                    win.close();
//                    Send(acToken);
                    console.log(acToken + ", " + tokenType + ", " + expiresIn);
                    validateToken(acToken);
                }
            } catch (e) {
//                swal('', e.message, 'error');
            }
        }, 100);
    }

    function validateToken(token) {
        $.ajax({
            url: VALIDURL + token,
            data: null,
            success: function (responseText) {
                console.log(responseText);
                getUserInfo(token);
            },
            dataType: "jsonp"
        });
    }

    function getUserInfo(token) {
        $.ajax({
            url: 'https://www.googleapis.com/oauth2/v1/userinfo?access_token=' + token,
            data: null,
            success: function (resp) {
                var user = resp;
                console.log(user);
                Send(token, user.email, user.name);
            },
            dataType: "jsonp"
        });
    }

    function gup(url, name) {
        name = name.replace(/[[]/, "\[").replace(/[]]/, "\]");
        var regexS = "[\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(url);
        if (results == null)
            return "";
        else
            return results[1];
    }

    $(document).ready(function () {
        CreateEmptyDataTable("#table");
        $('#table').wrap("<div class='table-scroll'></div>");
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
                            $('#table').dataTable().fnDestroy();
                            table = $('#table').dataTable({
                                "bFilter": true,
                                "bRetrieve": true,
                                "bScrollCollapse": true,
                                "bProcessing": true,
                                "bSort": false,
                                "data": array,
                                "oLanguage": {
                                    "sSearchPlaceholder": "",
                                    "sSearch": "Tìm kiếm:",
                                    "sZeroRecords": "Không có dữ liệu phù hợp",
                                    "sInfo": "Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng",
                                    "sEmptyTable": "Không có dữ liệu",
                                    "sInfoFiltered": " - lọc ra từ _MAX_ dòng",
                                    "sLengthMenu": "Hiển thị _MENU_ dòng",
                                    "sProcessing": "Đang xử lý...",
                                    "oPaginate": {
                                        "sNext": "<i class='fa fa-chevron-right'></i>",
                                        "sPrevious": "<i class='fa fa-chevron-left'></i>"
                                    }

                                },
                                "aoColumnDefs": [
                                    {
                                        "aTargets": [0, 1, 4, 5, 6, 7, 8, 9],
                                        "bSortable": false,
                                        "sClass": "text-center",
                                    },
                                    {"aTargets": [0], "sClass": "min-width-50px",},
                                    {"aTargets": [1], "sClass": "min-width-50px",},
                                    {"aTargets": [2], "sClass": "min-width-100px",},
                                    {"aTargets": [3], "sClass": "min-width-100px",},
                                    {"aTargets": [4], "sClass": "min-width-75px",},
                                    {"aTargets": [5], "sClass": "min-width-100px",},
                                    {"aTargets": [6], "sClass": "min-width-100px",},
                                    {"aTargets": [7], "sClass": "min-width-100px",},
                                    {"aTargets": [8], "sClass": "min-width-100px",},
                                    {"aTargets": [9], "sClass": "min-width-100px",},
                                    {
                                        "aTargets": [0],
                                        "mRender": function (data, type, row) {
                                            return "<input name='send' type='checkbox'/>";
                                        },
                                    },
                                    {
                                        "aTargets": [5],
                                        "mRender": function (data, type, row) {
                                            return FormatText(data) + "<input type='hidden' name='data' value='" + data + "'/>";
                                        }
                                    },
                                    {
                                        "aTargets": [6, 7, 8, 9],
                                        "mRender": function (data, type, row) {
                                            return FormatText(data);
                                        }
                                    },
                                ],
                                "bAutoWidth": false,
                            });
                            $('#table').wrap("<div class='table-scroll'></div>");
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
            $("input[name='send']", table.rows({search: 'applied'}).nodes()).prop('checked', true);
            select = false;
        } else {
            $("input[name='send']", table.rows({search: 'applied'}).nodes()).prop('checked', false);
            select = true;
        }
    }

    function SelectOnlyFail() {
        $.each($("input[name='data']", table.rows({search: 'applied'}).nodes()), function () {
            var data = $(this).val();
//            console.log(data);
            if (data != 'N/A') {
                $(this).closest("tr").find("input[name='send']").prop('checked', true);
            }
        });
    }

    function Send(token, username, name) {
        var array = [];
        $.each($("input[name='send']:checked", table.rows({search: 'applied'}).nodes()), function () {
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
                    data: {"params": JSON.stringify(array), "token": token, "username": username, "name": name},
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

    function FormatText(str) {
        var data = str.split(",");
        var result = data[0];
        for (var i = 1; i < data.length; ++i) {
            if (data[i] != '') {
                result += ", " + data[i];
            }
        }
        return result;
    }
</script>
