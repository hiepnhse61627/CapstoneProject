<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/12/2017
  Time: 11:13 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>AdminLTE 2 | Registration Page</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <link rel="stylesheet" href="/Resources/plugins/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/font-awesome-4.7.0/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/ionicons-2.0.1/css/ionicons.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/AdminLTE.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/skin-custom.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/_all-skins.css"/>
    <link rel="stylesheet" href="/Resources/plugins/sweetalert2/sweetalert2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/datatables/datatables.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/select2-4.0.4/css/select2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/jQueryUI/jquery-ui.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/iCheck/square/blue.css"/>
    <link rel="stylesheet" href="/Resources/plugins/daterangepicker-2.1.25/daterangepicker.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/template.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/custom-scrollbar.css"/>

    <!-- REQUIRED JS SCRIPTS -->

    <!-- jQuery 2.1.4 -->
    <script src="/Resources/plugins/jQuery/jquery-1.12.4.min.js"></script>
    <script src="/Resources/plugins/serializeToJson/jquery.serializejson.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="/Resources/plugins/bootstrap/js/bootstrap.min.js"></script>
    <!-- AdminLTE App -->
    <script src="/Resources/plugins/dist/js/app.min.js"></script>
    <script src="/Resources/plugins/sweetalert2/sweetalert2.min.js"></script>
    <script src="/Resources/plugins/datatables/datatables.min.js"></script>
    <script src="/Resources/plugins/select2-4.0.4/js/select2.min.js"></script>
    <script src="/Resources/plugins/jQueryUI/jquery-ui.min.js"></script>
    <script src="/Resources/plugins/tablednd/jquery.tablednd.min.js"></script>
    <script src="/Resources/plugins/iCheck/icheck.min.js"></script>
    <script src="/Resources/plugins/daterangepicker-2.1.25/moment.min.js"></script>
    <script src="/Resources/plugins/daterangepicker-2.1.25/daterangepicker.js"></script>
    <script src="/Resources/plugins/dist/js/template.js"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>
<body class="hold-transition register-page">
<div class="register-box">
    <div class="register-logo">
        <a href="#"><b>Admin</b>LTE</a>
    </div>

    <div class="register-box-body">
        <p class="login-box-msg">Register a new membership</p>

        <form id="form">
            <div class="col-md-12">
                <div class="form-group has-feedback">
                    <label for="username">Username</label>
                    <input type="text" id="username" name="username" placeholder="Username"/>
                </div>
                <div class="form-group has-feedback">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" placeholder="Password"/>
                </div>
                <div class="form-group has-feedback">
                    <label for="password">Email</label>
                    <c:if var="disable" test="${param.disable eq true}">
                        <input type="email" id="email" name="email" placeholder="Email" value="${param.email}" readonly/>
                    </c:if>
                    <c:if var="disable" test="${empty param.disable}">
                        <input type="email" id="email" name="email" placeholder="Email" value="${param.email}"/>
                    </c:if>
                </div>
                <div class="form-group">
                    <sec:authorize access="hasRole('ROLE_ADMIN')">
                        <label for="role">Role</label>
                        <select id="role" name="role">
                            <option value="ROLE_STUDENT">Student</option>
                            <option value="ROLE_ADMIN">Admin</option>
                            <option value="ROLE_STAFF">Staff</option>
                            <option value="ROLE_MANAGER">Manager</option>
                        </select>
                    </sec:authorize>
                    <sec:authorize access="isAnonymous()">
                        <input type="hidden" name="role" value="ROLE_MANAGER"/>
                    </sec:authorize>
                </div>
                <div class="row">
                    <div class="col-xs-8">
                        <div class="checkbox icheck">
                            <label>
                                <input type="checkbox"> I agree to the <a href="#">terms</a>
                            </label>
                        </div>
                    </div>
                    <!-- /.col -->
                    <div class="col-xs-4">
                        <button type="button" class="btn btn-primary btn-block btn-flat" onclick="Add()">Register
                        </button>
                    </div>
                    <!-- /.col -->
                </div>
            </div>
        </form>

        <a href="/login" class="text-center">I already have a membership</a>
    </div>
    <!-- /.form-box -->
</div>
</body>
</html>

<script>
    $(function () {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' // optional
        });
    });

    function Add() {
        var form = JSON.stringify($('#form').serializeJSON());
        $.ajax({
            type: "POST",
            url: "/register",
            contentType: 'application/json',
            data: form,
            success: function (result) {
                if (result.success) {
                    swal({
                        title: 'Thành công?',
                        text: "Bạn đã tạo tài khoản",
                        type: 'success'
//                        showCancelButton: false,
//                        confirmButtonColor: '#3085d6',
//                        cancelButtonColor: '#d33',
//                        confirmButtonText: 'Ok'
                    }).then(function () {
                        var url = window.location.hostname;
                        if (url.indexOf("localhost") == -1 && url.indexOf("xip.io") == -1) {
                            url += ".xip.io";
                        }
                        url += ":" + (location.port == '' ? "80" : location.port);
                        window.location.href = "https://accounts.google.com/o/oauth2/auth?client_id=415843400023-vlpk1t8gu558gmt597aqtumvkco0lmme.apps.googleusercontent.com&redirect_uri=http://" + url + "/auth/google&scope=openid%20email%20profile&&response_type=code&approval_prompt=auto&login_hint=" + $('#email').val();
                    });
                } else {
                    swal("Lỗi", result.msg, "error");
                }
            }
        });
    }
</script>