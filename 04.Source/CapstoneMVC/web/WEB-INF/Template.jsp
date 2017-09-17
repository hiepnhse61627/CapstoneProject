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

    <link rel="stylesheet" href="/Resources/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/Resources/font-awesome-4.7.0/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="/Resources/ionicons-2.0.1/css/ionicons.min.css"/>
    <link rel="stylesheet" href="/Resources/dist/css/AdminLTE.min.css"/>
    <link rel="stylesheet" href="/Resources/dist/css/skins/skin-custom.css"/>

    <style>
        .logo-mini img {
            width: 45px;
            height: auto;
            margin: 0 auto;
        }

        .logo-lg img {
            width: 210px;
            height: auto;
            margin: 0 auto;
        }

        .main-header .logo {
            padding: 0px;
        }

        /*.main-header .logo .logo-mini,*/
        /*.main-header .logo .logo-lg {*/
            /*width: 100%;*/
            /*height: 100%;*/

            /*background-size: cover;*/
            /*background-repeat: no-repeat;*/
            /*background-position: center center;*/
        /*}*/

        /*.main-header .logo .logo-mini {*/
            /*margin: 0px !important;*/
            /*background-image: url('/Resources/dist/img/logo/logo-fpt-2.png');*/
        /*}*/

        /*.main-header .logo .logo-lg {*/
            /*background-image: url('/Resources/dist/img/logo/logo-fpt-1.png');*/
        /*}*/

        /*.main-header > .navbar {*/
            /*margin-left: 231px;*/
        /*}*/

        /*.sidebar-mini.sidebar-collapse .main-header .navbar {*/
            /*margin-left: 51px;*/
        /*}*/
    </style>
</head>
<body class="hold-transition skin-custom sidebar-mini">
<div class="wrapper">

    <!-- Main Header -->
    <header class="main-header">

        <!-- Logo -->
        <a href="#" class="logo">
            <!-- mini logo for sidebar mini 50x50 pixels -->
            <div class="logo-mini">
                <img src="/Resources/dist/img/logo/logo-fpt-2.png">
            </div>
            <!-- logo for regular state and mobile devices -->
            <div class="logo-lg">
                <img src="/Resources/dist/img/logo/logo-fpt-1.png">
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
                    <img src="/Resources/dist/img/user2-160x160.jpg" class="img-circle" alt="User Image">
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
                    <a href="#"><i class="fa fa-link"></i> <span>Thống kê</span></a>
                </li>
                <li class="treeview">
                    <a href="#">
                        <i class="fa fa-link"></i>
                        <span>Quản lý sinh viên</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li><a href="#">Danh sách sinh viên</a></li>
                        <li><a href="#">Trạng thái sinh viên</a></li>
						<li><a href="/create"><i class="fa fa-link"></i> <span>Enroll sinh viên</span></a></li>
                    </ul>
                </li>
                <li class="treeview">
                    <a href="#">
                        <i class="fa fa-link"></i>
                        <span>Nhập dữ liệu</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li><a href="#">Danh sách sinh viên</a></li>
                        <li><a href="#">Danh sách điểm</a></li>
                    </ul>
                </li>
                <li class="treeview">
                    <a href="#">
                        <i class="fa fa-link"></i>
                        <span>Xuất dữ liệu</span>
                        <i class="fa fa-angle-left pull-right"></i>
                    </a>
                    <ul class="treeview-menu">
                        <li><a href="#">Danh sách đi OJT</a></li>
                        <li><a href="#">Danh sách làm đồ án</a></li>
                        <li><a href="#">Danh sách tốt nghiệp</a></li>
                        <li><a href="#">Danh sách đóng học phí</a></li>
                        <li><a href="#">Danh sách học lại</a></li>
                        <li><a href="#">Danh sách chậm tiến độ</a></li>
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

    <!-- Main Footer -->
    <footer class="main-footer">
        <!-- To the right -->
        <div class="pull-right hidden-xs">
            Anything you want
        </div>
        <!-- Default to the left -->
        <strong>Copyright &copy; 2015 <a href="#">Company</a>.</strong> All rights reserved.
    </footer>

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

<!-- REQUIRED JS SCRIPTS -->

<!-- jQuery 2.1.4 -->
<script src="/Resources/plugins/jQuery/jQuery-2.1.4.min.js"></script>
<script src="/Resources/plugins/serializeToJson/jquery.serializejson.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="/Resources/bootstrap/js/bootstrap.min.js"></script>
<!-- AdminLTE App -->
<script src="/Resources/dist/js/app.min.js"></script>

</html>
