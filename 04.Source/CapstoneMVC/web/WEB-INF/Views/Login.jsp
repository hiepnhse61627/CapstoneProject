<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/12/2017
  Time: 11:26 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Trang đăng nhập</title>
    <script>

        if(window.location.href.indexOf(".xip.io") === -1){
            window.location.replace("http://127.0.0.1.xip.io:8780");
            console.log(window.location);
        }

    </script>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.7 -->
    <link rel="stylesheet" href="/Resources/plugins/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/font-awesome-4.7.0/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/ionicons-2.0.1/css/ionicons.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/AdminLTE.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/skin-custom.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/_all-skins.css"/>
    <link rel="stylesheet" href="/Resources/plugins/sweetalert2/sweetalert2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/datatables/datatables.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/select2/select2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/jQueryUI/jquery-ui.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/iCheck/square/blue.css"/>
    <link rel="stylesheet" href="/Resources/plugins/daterangepicker-2.1.25/daterangepicker.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/template.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/custom-scrollbar.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/login-page.css"/>

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
    <script src="/Resources/plugins/select2/select2.min.js"></script>
    <script src="/Resources/plugins/jQueryUI/jquery-ui.min.js"></script>
    <script src="/Resources/plugins/tablednd/jquery.tablednd.min.js"></script>
    <script src="/Resources/plugins/iCheck/icheck.min.js"></script>
    <script src="/Resources/plugins/daterangepicker-2.1.25/moment.min.js"></script>
    <script src="/Resources/plugins/daterangepicker-2.1.25/daterangepicker.js"></script>
</head>

<body class="hold-transition login-page">
<div class="login-box">
    <div class="overlay">
        <div class="login-logo">
            <div class="my-logo">
                <%--<img src="/Resources/plugins/dist/img/logo/logo-fpt-1.png">--%>
                <img src="/Resources/plugins/dist/img/logo/STPM.png">
            </div>
        </div>
        <!-- /.login-logo -->
        <div class="login-box-body">
            <p class="login-box-msg">Đăng nhập</p>
            <c:if test="${param.error != null}">
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger">
                        <p>${param.error}</p>
                    </div>
                </c:if>
                <c:if test="${empty param.error}">
                    <div class="alert alert-danger">
                        <p>Tên đăng nhập hoặc mật khẩu sai.</p>
                    </div>
                </c:if>
            </c:if>

            <form action="/processlogin" method="post">
                <div class="form-group has-feedback">
                    <input type="text" class="form-control" name="username" placeholder="Tên đăng nhập">
                    <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
                </div>
                <div class="form-group has-feedback">
                    <input type="password" class="form-control" name="password" placeholder="Mật khẩu">
                    <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                </div>
                <div class="row">
                    <div class="col-xs-7">
                        <div class="checkbox icheck">
                            <label>
                                <input id="rememberme" name="remember-me" type="checkbox"> Lưu mật khẩu
                            </label>
                        </div>
                    </div>
                    <div class="col-xs-5">
                        <button type="submit" class="btn btn-primary btn-block btn-flat">Đăng nhập</button>
                    </div>
                </div>
            </form>

            <div class="text-center link-red">
                <a href="/register">Đăng ký tài khoản mới</a>
            </div>

            <div class="social-auth-links text-center">
                <p>- Hoặc -</p>
                <a id="goog" href="/googleSignIn" class="btn btn-block btn-social btn-google btn-flat">
                    <i class="fa fa-google-plus"></i> Đăng nhập bằng Google+
                </a>
            </div>
        </div>
    </div>
</div>

<script>
    // $(function () {
    //     var href = document.getElementById("goog");
    //     var url = window.location.hostname;
    //     if (url.indexOf("localhost") == -1 && url.indexOf("xip.io") == -1) {
    //         url += ".xip.io";
    //     }
    //     url += ":" + (location.port == '' ? "80" : location.port);
    //     href.href = "https://accounts.google.com/o/oauth2/auth?client_id=633838326707-anulcphc8kqt0k2hib34r42or6ikgcv8.apps.googleusercontent.com&redirect_uri=http://" + url + "/auth/google&scope=openid%20email%20profile&&response_type=code&approval_prompt=auto";
    //
    //     console.log(url);
    //
    // });
    $(document).ready(function() {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' // optional
        });
    })
</script>

</body>
</html>
