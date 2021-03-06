<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 27/03/18
  Time: 2:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x" %>

<style>
    custom-body {
        font-family:'Source Sans Pro','Helvetica Neue',Helvetica,Arial,sans-serif;
    }
    table {
        border-collapse: collapse;
        width: 95%;
    }
    tr.odd{
        background-color: #eef1f6;
    }

    table.custom-tbl thead th {
        background-color: #3c8dbc;
        color: white;
        border-bottom: none;
        padding: 10px 18px;
    }
    table.custom-tbl, table.custom-tbl th, table.custom-tbl td{
        box-sizing: content-box;
    }

    .txt-center {
        text-align: center;
    }

    table, th, td {
        border: 1px solid black;
    }

    .inline {
        display: inline;
        float: left;
    }

    .txt-left {
        text-align: left;
    }

    .txt-right {
        text-align: right;
    }

    body {
        /*font-family: "Roboto", Sans-Serif !important;*/
        font-size: 15px;
    }

    input {
        font-size: inherit !important;
    }

    .btn {
        font-size: 0.99em;
    }

    .btn .fa {
        font-size: 14px;
    }

    .btn-default {
        border-color: #d0d0d0;
    }

    th {
        font-size: 15.5px;
        font-weight: 500;
    }

    td {
        font-size: 15px !important;
    }

    h1, h2, h3, h4, h5, h6, .h1, .h2, .h3, .h4, .h5, .h6 {
        font-family: inherit !important;
        font-weight: 500 !important;
    }

    a {
        white-space: normal;
        cursor: pointer;
    }

    .table-scroll {
        width: 100%;
        overflow-x: auto;
    }

    .select, .select2 {
        width: 100% !important;
    }

    .select2-container--default .select2-selection--single {
        border-color: #d2d6de;
        border-radius: 0px;
        height: 34px;
    }

    .select2-container .select2-selection--single .select2-selection__rendered {
        /*margin-top: -7px;*/
        padding-left: 0px;
    }

    .select2-container--default .select2-selection--single .select2-selection__arrow {
        top: 4px;
    }

    .dataTables_scroll .dataTables_scrollHead .dataTables_scrollHeadInner,
    .dataTables_scroll .dataTables_scrollHead .dataTables_scrollHeadInner .datatable {
        width: 100% !important;
    }

    .dataTables_processing {
        z-index: 100;
    }

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

    .user-panel > .image > img {
        max-width: 40px;
    }

    .user-panel > .info {
        padding: 5px;
        left: 52px;
    }

    .user-panel > .info > p {
        font-weight: 500;
    }

    .user-panel > .info > p {
        white-space: normal;
        margin-bottom: 5px;
        width: 165px;

        line-height: 15px;
        max-height: 30px;
        overflow: hidden;
        text-overflow: ellipsis;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        display: -webkit-box;
    }

    .main-header .logo {
        padding: 0px;
    }

    .main-sidebar {
        /*background: linear-gradient(to bottom, white, white, white, white, white, orange);*/
        background: linear-gradient(to bottom, white, white, #e6e9ec);
    }

    .sidebar-form {
        margin: 0px 10px 5px 10px !important;
    }

    .box {
        border: none;
        padding: 12px 15px 20px 15px;
    }

    .box-header {
        padding: 10px 0px;
    }

    .box-header .box-title {
        font-weight: 600;
    }

    .menu-child-wrapper {
        display: inline-block;
        width: 100%;
    }

    .menu-child-wrapper .child-icon {
        margin-top: 1%;
        float: left;
    }

    .menu-child-wrapper .child-content {
        padding-left: 6%;
        padding-right: 1%;
    }

    @media (max-width: 500px) {
        .menu-child-wrapper .child-content {
            padding-left: 10%;
            padding-right: 2%;
        }
    }

    .tbl-btn {
        padding: 7px 10px;
    }

    .btn-with-icon {
        min-width: 80px;
    }

    .btn-with-icon div,
    .btn-with-icon i {
        vertical-align: middle;
        display: inline-block;
    }

    .btn-with-icon i.glyphicon {
        margin-bottom: 3px;
        margin-right: 1px;
    }

    /* Content header */
    .b-header > h1,
    .b-header .title > h1 {
        margin: 5px 0px 0px 0px;
        /*font-size: 24px;*/
        font-size: 22px;
        /*font-weight: 600;*/
        color: darkgreen;
        min-height: 28px;
    }

    .b-header .title > h1 {
    }

    .b-header > hr {
        margin: 8px 0px 20px;
        border-top: 1px solid #d3d3d3
    }

    .b-body {
        padding: 0px 5px;
    }

    /* Form */
    .form-group .title {
        width: 100%;
        padding: 0px 15px;
        margin-bottom: 10px;
        display: inline-block;
    }

    .form-group .title > h4 {
        font-size: 18px;
        font-weight: 600 !important;
        margin: 0px;
        color: #444444;
    }

    .form-group .title > h4 > i {
        font-size: 16px;
    }

    .form-group .my-content {
        width: 100%;
        position: relative;
        display: inline-block;
    }

    .form-group .my-content .my-input-group {
        width: 100%;
        min-height: 30px;
        padding: 0px 15px 0px 15px;
        margin-bottom: 10px;
    }

    .form-group .my-content .my-input-group .left-content {
        min-width: 60px;
        text-align: left;
        vertical-align: middle;
        float: left;
    }

    .form-group .my-content .my-input-group .left-content label {
        font-weight: normal;
    }

    .form-group .my-content .my-input-group .right-content {
        width: 50%;
        float: left;
    }

    /*------------------- Datatable -------------------*/

    .dataTables_length label,
    .dataTables_filter label {
        font-weight: normal !important;
        font-size: 15px !important;
    }

    .dataTables_length label select,
    .dataTables_filter label input[type=search] {
        height: 24px;
        font-size: 14px;
        border: 1px #999999 solid;
    }

    .dataTables_length label select {
        margin: 0px 3px;
        padding-bottom: 1px;
    }

    .dataTables_filter label input[type=search] {
        padding: 0px 3px;
        -webkit-appearance: searchfield !important;
    }

    .dataTables_filter label input[type=search]::placeholder {
        color: #999999;
    }

    .dataTables_wrapper .dataTable {
        padding-top: 4px;
    }

    .dataTables_wrapper .dataTables_scrollHead {
        margin-top: 10px;
    }

    .dataTables_wrapper .dataTables_scrollBody .dataTable {
        padding-top: 0px;
    }

    .dataTables_wrapper .dataTable > thead {
        /*background-color: goldenrod;*/
        /*background-color: #47a741;*/
        background-color: #3c8dbc;
        color: white;
        border-bottom: none;
    }

    .dataTables_wrapper .dataTable > thead > tr > th {
        border-bottom: none;
        text-align: center;
    }

    .dataTables_wrapper .dataTable > tbody > tr:nth-child(odd) {
        background-color: #eef1f6;
    }

    .dataTables_wrapper .dataTables_paginate .paginate_button:hover:not(.current) {
        background: linear-gradient(to bottom, #f4f4f4, #dfdfdf) !important;
        color: #111 !important;
        border: 1px solid #979797 !important;
    }

    /*.treeview-menu > li > a > i {*/
    /*position: absolute;*/
    /*}*/

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
    /*background-image: url('/Resources/plugins/dist/img/logo/logo-fpt-2.png');*/
    /*}*/

    /*.main-header .logo .logo-lg {*/
    /*background-image: url('/Resources/plugins/dist/img/logo/logo-fpt-1.png');*/
    /*}*/

    /*.main-header > .navbar {*/
    /*margin-left: 231px;*/
    /*}*/

    /*.sidebar-mini.sidebar-collapse .main-header .navbar {*/
    /*margin-left: 51px;*/
    /*}*/

    .text-left {
        float: left !important;
    }

    .text-right {
        float: right !important;
    }

    .text-center {
        text-align: center;
    }

    .float-left {
        float: left;
    }

    .float-right {
        float: right;
    }

    /* -------------- Margin ---------------- */
    .m-0 {
        margin: 0px !important;
    }

    /* ---- Left ---- */
    .m-l-0 {
        margin-left: 0px !important;
    }

    .m-l-1 {
        margin-left: 1px !important;
    }

    .m-l-2 {
        margin-left: 2px !important;
    }

    .m-l-3 {
        margin-left: 3px !important;
    }

    .m-l-4 {
        margin-left: 4px !important;
    }

    .m-l-5 {
        margin-left: 5px !important;
    }

    .m-l-6 {
        margin-left: 6px !important;
    }

    .m-l-7 {
        margin-left: 7px !important;
    }

    .m-l-8 {
        margin-left: 8px !important;
    }

    .m-l-9 {
        margin-left: 9px !important;
    }

    .m-l-10 {
        margin-left: 10px !important;
    }

    /* ---- Right ---- */
    .m-r-0 {
        margin-right: 0px !important;
    }

    .m-r-1 {
        margin-right: 1px !important;
    }

    .m-r-2 {
        margin-right: 2px !important;
    }

    .m-r-3 {
        margin-right: 3px !important;
    }

    .m-r-4 {
        margin-right: 4px !important;
    }

    .m-r-5 {
        margin-right: 5px !important;
    }

    .m-r-6 {
        margin-right: 6px !important;
    }

    .m-r-7 {
        margin-right: 7px !important;
    }

    .m-r-8 {
        margin-right: 8px !important;
    }

    .m-r-9 {
        margin-right: 9px !important;
    }

    .m-r-10 {
        margin-right: 10px !important;
    }

    .m-r-15 {
        margin-right: 15px !important;
    }

    /* ---- Top ---- */
    .m-t-0 {
        margin-top: 0px !important;
    }

    .m-t-1 {
        margin-top: 1px !important;
    }

    .m-t-2 {
        margin-top: 2px !important;
    }

    .m-t-3 {
        margin-top: 3px !important;
    }

    .m-t-4 {
        margin-top: 4px !important;
    }

    .m-t-5 {
        margin-top: 5px !important;
    }

    .m-t-10 {
        margin-top: 10px !important;
    }

    .m-t-15 {
        margin-top: 15px !important;
    }

    /* ---- Bottom ---- */
    .m-b-0 {
        margin-bottom: 0px !important;
    }

    .m-b-1 {
        margin-bottom: 1px !important;
    }

    .m-b-2 {
        margin-bottom: 2px !important;
    }

    .m-b-3 {
        margin-bottom: 3px !important;
    }

    .m-b-4 {
        margin-bottom: 4px !important;
    }

    .m-b-5 {
        margin-bottom: 5px !important;
    }

    .m-b-7 {
        margin-bottom: 7px !important;
    }

    .m-b-10 {
        margin-bottom: 10px !important;
    }

    .m-b-15 {
        margin-bottom: 15px !important;
    }

    /* -------------- Padding ---------------- */
    .p-0 {
        padding: 0px !important;
    }

    /* ---- Left ---- */
    .p-l-0 {
        padding-left: 0px !important;
    }

    .p-l-1 {
        padding-left: 1px !important;
    }

    .p-l-2 {
        padding-left: 2px !important;
    }

    .p-l-3 {
        padding-left: 3px !important;
    }

    .p-l-4 {
        padding-left: 4px !important;
    }

    .p-l-5 {
        padding-left: 5px !important;
    }

    .p-l-8 {
        padding-left: 8px !important;
    }

    .p-l-10 {
        padding-left: 10px !important;
    }

    .p-l-15 {
        padding-left: 15px !important;
    }

    .p-l-30 {
        padding-left: 30px !important;
    }

    /* ---- Right ---- */
    .p-r-0 {
        padding-right: 0px !important;
    }

    .p-r-1 {
        padding-right: 1px !important;
    }

    .p-r-2 {
        padding-right: 2px !important;
    }

    .p-r-3 {
        padding-right: 3px !important;
    }

    .p-r-4 {
        padding-right: 4px !important;
    }

    .p-r-5 {
        padding-right: 5px !important;
    }

    .p-r-8 {
        padding-right: 8px !important;
    }

    .p-r-10 {
        padding-right: 10px !important;
    }

    .p-r-15 {
        padding-right: 15px !important;
    }

    .p-r-30 {
        padding-right: 30px !important;
    }

    /* ---- Top ---- */
    .p-t-0 {
        padding-top: 0px !important;
    }

    .p-t-1 {
        padding-top: 1px !important;
    }

    .p-t-2 {
        padding-top: 2px !important;
    }

    .p-t-3 {
        padding-top: 3px !important;
    }

    .p-t-4 {
        padding-top: 4px !important;
    }

    .p-t-5 {
        padding-top: 5px !important;
    }

    .p-t-8 {
        padding-top: 8px !important;
    }

    .p-t-10 {
        padding-top: 10px !important;
    }

    .p-t-15 {
        padding-top: 15px !important;
    }

    .p-t-30 {
        padding-top: 30px !important;
    }

    /* ---- Bottom ---- */
    .p-b-0 {
        padding-bottom: 0px !important;
    }

    .p-b-1 {
        padding-bottom: 1px !important;
    }

    .p-b-2 {
        padding-bottom: 2px !important;
    }

    .p-b-3 {
        padding-bottom: 3px !important;
    }

    .p-b-4 {
        padding-bottom: 4px !important;
    }

    .p-b-5 {
        padding-bottom: 5px !important;
    }

    .p-b-8 {
        padding-bottom: 8px !important;
    }

    .p-b-10 {
        padding-bottom: 10px !important;
    }

    .p-b-15 {
        padding-bottom: 15px !important;
    }

    .p-b-30 {
        padding-bottom: 30px !important;
    }

    /*  ------------- Width -------------*/
    .width-5 {
        width: 5% !important;
    }

    .width-10 {
        width: 10% !important;
    }

    .width-20 {
        width: 20% !important;
    }

    .width-29 {
        width: 29% !important;
    }

    .width-30 {
        width: 30% !important;
    }

    .width-40 {
        width: 40% !important;
    }

    .width-50 {
        width: 50% !important;
    }

    .width-60 {
        width: 60% !important;
    }

    .width-70 {
        width: 70% !important;
    }

    .width-80 {
        width: 80% !important;
    }

    .width-90 {
        width: 90% !important;
    }

    .width-100 {
        width: 100% !important;
    }

    @media (max-width: 768px) {
        .width-m-10 {
            width: 10% !important;
        }

        .width-m-20 {
            width: 20% !important;
        }

        .width-m-30 {
            width: 30% !important;
        }

        .width-m-40 {
            width: 40% !important;
        }

        .width-m-50 {
            width: 50% !important;
        }

        .width-60 {
            width: 60% !important;
        }

        .width-m-70 {
            width: 70% !important;
        }

        .width-m-80 {
            width: 80% !important;
        }

        .width-m-90 {
            width: 90% !important;
        }

        .width-m-100 {
            width: 100% !important;
        }
    }
</style>

<section class="content custom-body">
    <div class="box">

        <div>
            <div class="inline txt-left width-50 width-m-50">Họ và tên: Tô Thanh Huyền</div>
            <div class="inline txt-left width-50 width-m-50">MSSV: SE61437</div>
        </div>
        <div>
            <div class="inline txt-left width-50 width-m-50">Ngày sinh: 12/11/1995</div>
            <div class="inline txt-left width-50 width-m-50">Hình thức đào tạo: Chính quy</div>
        </div>
        <div>
            <div class="inline txt-left width-50 width-m-50">Ngành: An toàn thông tin</div>
            <div class="inline txt-left width-50 width-m-50">Chuyên ngành: An toàn thông tin</div>
        </div>
        <table class="custom-tbl">
            <thead class="">
            <th class="txt-center"><b>No.</b></th>
            <th class="txt-center"><b>Subject</b></th>
            <th class="txt-center"><b>Môn học</b></th>
            <th class="txt-center"><b>Tín chỉ</b></th>
            <th class="txt-center"><b>Điểm số</b></th>
            <th class="txt-center"><b>Điểm chữ</b></th>
            </thead>
            <tbody>
            <tr>
                <td class="txt-center">1</td>
                <td class="txt-center">Hehe</td>
                <td class="txt-center">nanahah</td>
                <td class="txt-center">3</td>
                <td class="txt-center">10.0</td>
                <td class="txt-center">A</td>
            </tr>
            <tr>
                <td class="txt-center">2</td>
                <td class="txt-center">Hehe</td>
                <td class="txt-center">nanahah</td>
                <td class="txt-center">5</td>
                <td class="txt-center">9.0</td>
                <td class="txt-center">A</td>
            </tr>
            </tbody>
        </table>

        <br/>
        <div>
            <div class="txt-left">
                Tên đồ án: PHÁT TRIỂN HỆ THỐNG PHÒNG CHỐNG TẤN CÔNG DDOS (DISTRIBUTED DENIAL OF SERVICE)
            </div>
        </div>

        <div>
            <div class="txt-left">
                Capstone Project: DDOS ATTACK PREVENTION SYSTEM
            </div>
        </div>
    </div>
</section>
