<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 1/17/2018
  Time: 10:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/excel-sub-menu.css">

<style>
    .form-group .my-content .my-input-group .left-content {
        min-width: 70px;
    }

</style>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-7 title">
                    <h1>Danh sách điểm sinh viên theo kỳ</h1>
                </div>
                <div class="col-md-5 text-right">
                    <button type="button" class="btn btn-success" onclick="ExportExcel()">Xuất điểm sinh viên theo kỳ
                    </button>
                    <button type="button" class="btn btn-success" onclick="ExportExcelInfo()">Xuất thông tin sinh viên theo kỳ
                    </button>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Thông tin bộ lọc</h4>
                    </div>
                    <div class="my-content p-l-10">
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Học kỳ:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="semester" class="select form-control">
                                    <c:forEach var="semester" items="${semesterList}">
                                        <option value="${semester.id}">${semester.semester}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<form id="export-excel" action="/exportExcel" hidden>
    <input name="semesterId"/>
    <input name="objectType"/>

</form>

<form id="export-excel-info" action="/exportExcel" hidden>
    <input name="semesterId"/>
    <input name="objectType"/>

</form>


<script>
    $(document).ready(function () {
        $('.select').select2();
    })

    function ExportExcel() {
        $("input[name='semesterId']").val($("#semester").val());
        $("input[name='objectType']").val(20);
        $('#export-excel').submit();
    }

    function ExportExcelInfo() {
        $("input[name='semesterId']").val($("#semester").val());
        $("input[name='objectType']").val(21);
        $('#export-excel-info').submit();
    }
</script>