<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 9/13/2017
  Time: 2:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dec" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<security:authentication var="imageUrl" property="principal.user.picture"/>
<security:authentication var="username" property="principal.user.fullname"/>
<security:authentication var="role" property="principal.user.role"/>

<c:if test="${empty username}">
    <c:set var="username" value="Vô danh"/>
</c:if>

<c:if test="${empty imageUrl}">
    <c:set var="imageUrl" value="/Resources/plugins/dist/img/anonymous.jpg"/>
</c:if>

<html>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <title>${title} | ExperimentalAP 2.0</title>

    <link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet">
    <link rel="stylesheet" href="/Resources/plugins/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/font-awesome-4.7.0/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/ionicons-2.0.1/css/ionicons.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/AdminLTE.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/skin-custom.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/_all-skins.css"/>
    <link rel="stylesheet" href="/Resources/plugins/sweetalert2/sweetalert2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/DataTables/datatables.css"/>
    <link rel="stylesheet" href="/Resources/plugins/select2-4.0.6/css/select2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/jQueryUI/jquery-ui.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/iCheck/square/blue.css"/>
    <link rel="stylesheet" href="/Resources/plugins/daterangepicker-2.1.25/daterangepicker.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/custom-scrollbar.css"/>
    <link rel="stylesheet" href="/Resources/plugins/bootstrap-switch/css/bootstrap3/bootstrap-switch.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/Scroller-1.4.3/css/scroller.dataTables.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/template.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/_all-skins.css"/>

    <!-- REQUIRED JS SCRIPTS -->
    <!-- jQuery 2.1.4 -->
    <script src="/Resources/plugins/jQuery/jquery-1.12.4.min.js"></script>
    <script src="/Resources/plugins/serializeToJson/jquery.serializejson.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="/Resources/plugins/bootstrap/js/bootstrap.min.js"></script>
    <!-- AdminLTE App -->
    <script src="/Resources/plugins/dist/js/app.min.js"></script>
    <script src="/Resources/plugins/sweetalert2/sweetalert2.min.js"></script>
    <script src="/Resources/plugins/DataTables/datatables.js"></script>
    <script src="/Resources/plugins/select2-4.0.6/js/select2.min.js"></script>
    <script src="/Resources/plugins/jQueryUI/jquery-ui.min.js"></script>
    <script src="/Resources/plugins/tablednd/jquery.tablednd.min.js"></script>
    <script src="/Resources/plugins/iCheck/icheck.min.js"></script>
    <script src="/Resources/plugins/daterangepicker-2.1.25/moment.min.js"></script>
    <script src="/Resources/plugins/daterangepicker-2.1.25/daterangepicker.js"></script>
    <script src="/Resources/plugins/dist/js/template.js"></script>
    <script src="/Resources/plugins/bootstrap-switch/js/bootstrap-switch.js"></script>
    <script src="/Resources/plugins/Scroller-1.4.3/js/dataTables.scroller.js"></script>
    <script src="/Resources/plugins/jQuery/jquery.hotkeys.js"></script>
    <%--<script src="/Resources/plugins/ckeditor/ckeditor.js"></script>--%>
    <%--<script src="/Resources/plugins/ckeditor/adapters/jquery.js"></script>--%>
</head>
<body class="hold-transition skin-custom sidebar-mini">
<div class="wrapper" style="position: relative">

    <!-- Main Header -->
    <header class="main-header">

        <!-- Logo -->
        <a href="#" class="logo">
            <!-- mini logo for sidebar mini 50x50 pixels -->
            <div class="logo-mini">
                <img src="/Resources/plugins/dist/img/logo/logo-fpt-2.png">
            </div>
            <!-- logo for regular state and mobile devices -->
            <div class="logo-lg">
                <img src="/Resources/plugins/dist/img/logo/logo-fpt-1.png">
            </div>
        </a>

        <!-- Header Navbar -->
        <nav class="navbar navbar-static-top" role="navigation">
            <!-- Sidebar toggle button-->
            <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
                <span class="sr-only">Toggle navigation</span>
            </a>

            <!-- Navbar Right Menu -->
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <img src="${imageUrl}" class="user-image">
                            <span class="hidden-xs">
                                ${username}
                            </span>
                        </a>
                        <ul class="dropdown-menu">
                            <li class="user-header">
                                <img src="${imageUrl}"
                                     class="img-circle" alt="User Image">
                                <p>
                                    ${username}
                                    <small>${role}</small>
                                </p>
                            </li>

                            <li class="user-footer">
                                <div class="pull-left">
                                    <a href="/profile/" class="btn btn-default btn-flat">Profile</a>
                                </div>
                                <div class="pull-right">
                                    <a href="/logout" class="btn btn-default btn-flat">Đăng xuất</a>
                                </div>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>

        </nav>
    </header>
    <!-- Left side column. contains the logo and sidebar -->

    <aside class="main-sidebar">

        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">

            <!-- Sidebar user panel (optional) -->
            <div class="user-panel">
                <a href="/profile/" class="pull-left image">
                    <img src="${imageUrl}" class="img-circle" alt="User Image">
                </a>
                <div class="pull-left info">
                    <p>
                        <a href="/profile/">
                            ${username}
                        </a>
                    </p>
                    <!-- Status -->
                    <a>
                        <i class="fa fa-circle text-success"></i>
                        <span> Online</span>
                    </a>
                </div>
            </div>

            <div class="sidebar-form">
                <div class="input-group">
                    <input id="menu-filter" type="text" class="form-control" placeholder="Search...">
                    <span class="input-group-btn">
                    <button type="button" class="btn btn-flat"><i class="fa fa-search"></i></button>
                    </span>
                </div>
            </div>

            <!-- Sidebar Menu -->
            <ul class="sidebar-menu">
                <%--<li class="header">HEADER</li>--%>
                <!-- Optionally, you can add icons to the links -->
                <c:forEach var="items" items="${applicationScope['menuNoFunctionGroup']}">
                    <li>
                        <a href="${items.link}"><i class="fa fa-dashboard"></i>
                            <span class="name">${items.functionName}</span></a>
                    </li>
                </c:forEach>
                <c:forEach var="groupName" items="${applicationScope['functionGroup']}">
                    <li class="treeview">
                        <a href="#">
                            <i class="glyphicon glyphicon-save"></i>
                            <span>${groupName}</span>
                            <i class="fa fa-angle-left pull-right"></i>
                            <ul class="treeview-menu">
                                <c:forEach var="item" items="${applicationScope['menu']}">
                                    <c:if test="${item.groupName.contains(groupName)}">
                                        <li>
                                            <a href="${item.link}">
                                                <div class="menu-child-wrapper">
                                                    <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                                    <div class="child-content col-md-11">
                                                        <span class="name">${item.functionName}</span>
                                                    </div>
                                                </div>
                                            </a>
                                        </li>
                                    </c:if>
                                </c:forEach>
                            </ul>
                        </a>
                    </li>
                </c:forEach>
            </ul><!-- /.sidebar-menu -->
        </section>
        <!-- /.sidebar -->
    </aside>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <dec:body/>
    </div><!-- /.content-wrapper -->
</div><!-- ./wrapper -->
<footer>
    <p class="text-center">&#169;FPT University</p>
</footer>
<script>
    var list;

    var input = document.getElementById('menu-filter');
    input.onkeyup = function () {
        var filter = input.value.toLowerCase();
        list = document.querySelectorAll('.sidebar-menu li');
        for (var i = 0; i < list.length; i++) {
            var name = list[i].getElementsByClassName('name')[0].innerHTML;
            if (name.toLowerCase().search(filter) != -1)
                list[i].style.display = 'list-item';
            else
                list[i].style.display = 'none';
        }
    }

</script>

</body>

</html>