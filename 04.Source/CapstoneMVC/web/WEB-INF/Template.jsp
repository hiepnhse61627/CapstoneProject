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

<c:set var="admin" value="ROLE_ADMIN"/>
<c:set var="student" value="ROLE_STUDENT"/>

<html>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <title>${title} | Uniap 2.0</title>

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

</head>
<body class="hold-transition skin-black-light sidebar-mini">
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
        </nav>
    </header>
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="main-sidebar">

        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">

            <!-- Sidebar user panel (optional) -->
            <div class="user-panel">
                <div class="pull-left image">
                    <security:authentication var="imageUrl" property="principal.picture"/>
                    <c:choose>
                        <c:when test="${imageUrl} != ''">
                            <img src="${imageUrl}" class="img-circle" alt="User Image">
                        </c:when>
                        <c:otherwise>
                            <img src="/Resources/plugins/dist/img/anonymous.jpg" class="img-circle" alt="User Image">
                        </c:otherwise>
                    </c:choose>

                </div>
                <div class="pull-left info">
                    <p>
                        <security:authentication var="username" property="principal.fullname"/>
                        <c:choose>
                            <c:when test="${username} != ''">
                                ${username}
                            </c:when>
                            <c:otherwise>
                                Ẩn danh
                            </c:otherwise>
                        </c:choose>

                    </p>
                    <!-- Status -->
                    <a href="#">
                        <i class="fa fa-circle text-success"></i>
                        <security:authorize access="hasRole('${admin}')">
                            <span> Admin</span>
                        </security:authorize>
                        <security:authorize access="hasRole('${student}')">
                            <span> Student</span>
                        </security:authorize>
                    </a>
                </div>
            </div>
            <!-- Sidebar Menu -->
            <ul class="sidebar-menu">
                <%--<li class="header">HEADER</li>--%>
                <!-- Optionally, you can add icons to the links -->
                <security:authorize access="hasRole('${admin}')">
                    <li class="treeview">
                        <a href="#">
                            <i class="fa fa-users"></i>
                            <span>Thống kê</span>
                            <i class="fa fa-angle-left pull-right"></i>
                        </a>
                        <ul class="treeview-menu">
                            <li>
                                <a href="/dashboard">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Dashboard
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/percent/index">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Tỷ lệ rớt môn
                                        </div>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="/goSQLQueryPage"><i class="fa fa-search"></i> <span>Truy vấn dữ liệu</span></a>
                    </li>
                    <li class="treeview">
                        <a href="#">
                            <i class="fa fa-users"></i>
                            <span>Quản lý sinh viên</span>
                            <i class="fa fa-angle-left pull-right"></i>
                        </a>
                        <ul class="treeview-menu">
                            <li>
                                <a href="/studentList">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách sinh viên
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="#">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Trạng thái sinh viên
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/create">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Enroll sinh viên
                                        </div>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </li>
                    <li>
                        <a href="/course"><i class="fa fa-list"></i> <span>Quản lý khóa học</span></a>
                    </li>

                    <li class="treeview">
                        <a href="#">
                            <i class="glyphicon glyphicon-save"></i>
                            <span>Nhập dữ liệu</span>
                            <i class="fa fa-angle-left pull-right"></i>
                        </a>
                        <ul class="treeview-menu">
                            <li>
                                <a href="/goUploadStudentList">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách sinh viên
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/goUploadStudentMarks">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách điểm
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/subject">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách môn học
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/goUploadCoursePage">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách khóa học
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/subcurriculum">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Khung chương trình
                                        </div>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </li>
                    <li class="treeview">
                        <a href="#">
                            <i class="glyphicon glyphicon-open"></i>
                            <span>Xuất dữ liệu</span>
                            <i class="fa fa-angle-left pull-right"></i>
                        </a>
                        <ul class="treeview-menu">
                            <li>
                                <a href="/graduate">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách sinh viên được xét tốt nghiệp
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/studentDetail">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách nợ môn
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/studentsOJT">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách đi OJT
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="#">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách làm đồ án
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="#">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách tốt nghiệp
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="#">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách đóng học phí
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/checkPrequisite">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách pass môn nhưng chưa pass môn tiên quyết
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="/display">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách học lại
                                        </div>
                                    </div>
                                </a>
                            </li>
                            <li>
                                <a href="#">
                                    <div class="menu-child-wrapper">
                                        <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                        <div class="child-content col-md-11">
                                            Danh sách chậm tiến độ
                                        </div>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </li>
                </security:authorize>

                <security:authorize access="hasRole('${student}')">
                    <li>
                        <a href="/studentDetail">
                            <div class="menu-child-wrapper">
                                <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                <div class="child-content col-md-11">
                                    Xem sinh viên nợ môn
                                </div>
                            </div>
                        </a>
                    </li>
                </security:authorize>
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

<script>
    $(document).ready(function() {
        $(".select2-selection span").attr('title', '');
    });
</script>

</body>

</html>