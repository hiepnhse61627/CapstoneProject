<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 9/13/2017
  Time: 2:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="dec" uri="http://www.opensymphony.com/sitemesh/decorator" %>

<html>
<head>
    <meta charset="utf-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">

    <title>${title}</title>

    <link rel="stylesheet" href="/Resources/plugins/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/font-awesome-4.7.0/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/ionicons-2.0.1/css/ionicons.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/AdminLTE.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/skin-custom.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/skins/_all-skins.css"/>
    <link rel="stylesheet" href="/Resources/plugins/sweetalert2/sweetalert2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/datatables/datatables.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/select2/select2.min.css"/>
    <link rel="stylesheet" href="/Resources/plugins/dist/css/template.css"/>
    <%--<link rel="stylesheet" href="/Resources/plugins/jQueryUI/jquery-ui.min.css"/>--%>

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
    <%--<script src="/Resources/plugins/jQueryUI/jquery-ui.min.js"></script>--%>

</head>
<body class="hold-transition skin-black-light sidebar-mini">
<div class="wrapper">

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
                    <img src="/Resources/plugins/dist/img/user2-160x160.jpg" class="img-circle" alt="User Image">
                </div>
                <div class="pull-left info">
                    <p>Alexander Pierce</p>
                    <!-- Status -->
                    <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
                </div>
            </div>

            <!-- Sidebar Menu -->
            <ul class="sidebar-menu">
                <%--<li class="header">HEADER</li>--%>
                <!-- Optionally, you can add icons to the links -->
                <li class="active">
                    <a href="#"><i class="fa fa-dashboard"></i> <span>Thống kê</span></a>
                </li>
                <li class="active">
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
                            <a href="/studentDetail">
                                <div class="menu-child-wrapper">
                                    <div class="child-icon"><i class="fa fa-circle-o"></i></div>
                                    <div class="child-content col-md-11">
                                        Xem sinh viên nợ môn
                                    </div>
                                </div>
                            </a>
                        </li>
                        <li>
                            <a href="#">
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
            </ul><!-- /.sidebar-menu -->
        </section>
        <!-- /.sidebar -->
    </aside>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <dec:body/>
    </div><!-- /.content-wrapper -->


    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Create the tabs -->
        <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
            <li class="active"><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
            <li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
        </ul>
        <!-- Tab panes -->
        <div class="tab-content">
            <!-- Home tab content -->
            <div class="tab-pane active" id="control-sidebar-home-tab">
                <h3 class="control-sidebar-heading">Recent Activity</h3>
                <ul class="control-sidebar-menu">
                    <li>
                        <a href="#">
                            <i class="menu-icon fa fa-birthday-cake bg-red"></i>
                            <div class="menu-info">
                                <h4 class="control-sidebar-subheading">Langdon's Birthday</h4>
                                <p>Will be 23 on April 24th</p>
                            </div>
                        </a>
                    </li>
                </ul><!-- /.control-sidebar-menu -->

                <h3 class="control-sidebar-heading">Tasks Progress</h3>
                <ul class="control-sidebar-menu">
                    <li>
                        <a href="#">
                            <h4 class="control-sidebar-subheading">
                                Custom Template Design
                                <span class="label label-danger pull-right">70%</span>
                            </h4>
                            <div class="progress progress-xxs">
                                <div class="progress-bar progress-bar-danger" style="width: 70%"></div>
                            </div>
                        </a>
                    </li>
                </ul><!-- /.control-sidebar-menu -->

            </div><!-- /.tab-pane -->
            <!-- Stats tab content -->
            <div class="tab-pane" id="control-sidebar-stats-tab">Stats Tab Content</div><!-- /.tab-pane -->
            <!-- Settings tab content -->
            <div class="tab-pane" id="control-sidebar-settings-tab">
                <form method="post">
                    <h3 class="control-sidebar-heading">General Settings</h3>
                    <div class="form-group">
                        <label class="control-sidebar-subheading">
                            Report panel usage
                            <input type="checkbox" class="pull-right" checked>
                        </label>
                        <p>
                            Some information about this general settings option
                        </p>
                    </div><!-- /.form-group -->
                </form>
            </div><!-- /.tab-pane -->
        </div>
    </aside><!-- /.control-sidebar -->
    <!-- Add the sidebar's background. This div must be placed
         immediately after the control sidebar -->
    <div class="control-sidebar-bg"></div>
</div><!-- ./wrapper -->
</body>

</html>
