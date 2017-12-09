<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>

<style>
    .info-box-icon i {
        margin-top: 25%;
    }

    .content-wrapper {
        text-align: center;
        vertical-align: middle;

        /*background-color: #fbfbfb;*/
        background: radial-gradient(white 25%, #fdfdfd 75%);
    }

    .stpm-wrapper {
        width: 100%;
        height: 100%;
    }

    .stpm-triangle {
        width: 100%;
        display: inline-block;
    }

    .triangle-top-left {
        width: 0;
        height: 0;
        border-top: 120px solid #49b848;
        border-right: 120px solid transparent;
        float: left;
    }

    .triangle-bottom-right {
        width: 0;
        height: 0;
        border-bottom: 120px solid #49b848;
        border-left: 120px solid transparent;
        float: right;
    }

    .stpm-logo {
        margin-top: 8%;
        margin-bottom: 12%;
        width: 60%;
        height: auto;
    }


</style>

<div class="stpm-wrapper">
    <div class="stpm-triangle">
        <div class="triangle-top-left"></div>
    </div>
    <img class="stpm-logo" src="/Resources/plugins/dist/img/logo/STPM.png">
    <div class="stpm-triangle">
        <div class="triangle-bottom-right"></div>
    </div>
</div>


<%--<section class="content-header">--%>
<%--<h1>Thống kê</h1>--%>
<%--</section>--%>

<%--<section class="content">--%>
<%--<div class="col-md-12">--%>
<%--<div class="row">--%>
<%--<div class="col-md-4">--%>
<%--<div class="info-box">--%>
<%--<div class="info-box-icon bg-red">--%>
<%--<i class="fa fa-users"></i>--%>
<%--</div>--%>
<%--<div class="info-box-content">--%>
<%--<div class="info-box-text">Số sinh viên đang học</div>--%>
<%--<div class="info-box-number">0</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--<div class="col-md-4">--%>
<%--<div class="info-box">--%>
<%--<div class="info-box-icon bg-green">--%>
<%--<i class="fa fa-users"></i>--%>
<%--</div>--%>
<%--<div class="info-box-content">--%>
<%--<div class="info-box-text">Số sinh viên nợ môn</div>--%>
<%--<div class="info-box-number">0</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--<div class="col-md-4">--%>
<%--<div class="info-box">--%>
<%--<div class="info-box-icon bg-yellow">--%>
<%--<i class="fa fa-users"></i>--%>
<%--</div>--%>
<%--<div class="info-box-content">--%>
<%--<div class="info-box-text">Tổng số môn nợ</div>--%>
<%--<div class="info-box-number">0</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</div>--%>
<%--</section>--%>
