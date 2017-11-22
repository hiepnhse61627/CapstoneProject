<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 11/1/2017
  Time: 3:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Đổi ngành sinh viên</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="my-content">
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="title">
                                    Chọn khung
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="form-group">
                                            <select id="cur1" class="select form-control">
                                                <c:forEach var="cur" items="${curs}">
                                                    <option value="${cur.id}">${cur.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="form-group">
                                <div class="title">
                                    Chọn khung
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-12">
                                        <div class="form-group">
                                            <select id="cur2" class="select form-control">
                                                <c:forEach var="cur" items="${curs}">
                                                    <option value="${cur.id}">${cur.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-12">
                            <button type="button" class="btn btn-success" onclick="Get()">So sánh</button>
                        </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 text-center">
                        <p><b>Các môn chung</b></p>
                        <table id="current" class="table"></table>
                    </div>
                    <div class="col-md-6 text-center">
                        <p><b>Các môn không chung</b></p>
                        <table id="no" class="table"></table>
                    </div>
                </div>
            </div>
        </div>


    </div>
</section>

<input type="hidden" id="curid" value=""/>

<script>
    $(document).ready(function () {
        $('.select').select2();
    });

    function Get() {
        GetCurrent();
        GetNew();
    }

    function GetCurrent() {
        $.ajax({
            type: "GET",
            url: "/managerrole/curriculumcompare/getcurrent",
            data: {"curId": $('#cur1').val(), "newId": $('#cur2').val()},
            success: function (result) {
                var data = result.data;
//                console.log(data);
                var html = "";
                $.each(data, function (index, value) {
                    html += "<tr>";
                    html += "<td>";
                    html += value.subjectID;
                    html += "</td>";
                    html += "<td>";
                    html += value.subjectName;
                    html += "</td>";
                    html += "</tr>";
                });
                $('#current').html(html);
//                $("input[type='checkbox']").bootstrapSwitch();
            }
        });
    }

    function GetNew() {
        $.ajax({
            type: "GET",
            url: "/managerrole/curriculumcompare/getnew",
            data: {"curId": $('#cur1').val(), "newId": $('#cur2').val()},
            success: function (result) {
                var data = result.data;
//                console.log(data);
                var html = "";
                $.each(data, function (index, value) {
                    html += "<tr>";
                    html += "<td>";
                    html += value.subjectID;
                    html += "</td>";
                    html += "<td>";
                    html += value.subjectName;
                    html += "</td>";
                    html += "</tr>";
                });
                $('#no').html(html);
//                $("input[type='checkbox']").bootstrapSwitch();
            }
        });
    }
</script>
