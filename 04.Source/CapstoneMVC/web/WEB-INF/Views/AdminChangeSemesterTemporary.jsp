<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .table {
        width: 60%;
        border-bottom: 1px #666 solid;
    }

    .table > thead > tr > th  {
        font-weight: 600;
        border-bottom: 1px #666 solid;
    }

    .table > tbody > tr > td {
        vertical-align: middle;
    }

    .table > thead > tr > th:first-child,
    .table > tbody > tr > td:first-child,
    .table > thead > tr > th:last-child,
    .table > tbody > tr > td:last-child {
        text-align: center;
    }

    .table input[type="radio"] {
        display: none;
    }

    .table input[type="radio"] + label {
        width: 120px;
        padding: 5px;
        color: #333;
        background-color: #fff;
        border: 1px #ccc solid;
        font-weight: normal;
        cursor: pointer;
        margin: 0px;
    }

    .table input[type="radio"] + label:hover {
        background-color: #e6e6e6;
        border-color: #adadad;
    }

    .table input[type="radio"]:checked + label {
        color: #fff;
        background-color: #5cb85c;
        border-color: #4cae4c;
    }

    .table input[type="radio"]:checked + label:hover {
        background-color: #449d44;
        border-color: #398439;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Giả lập học kỳ hiện hành</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-semester" class="table">
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Học kỳ</th>
                            <th>Thiết lập tạm thời</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="semester" items="${semesters}" varStatus="count">
                            <tr>
                                <td>${count.count}</td>
                                <td>${semester.semester}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${semester.id eq temporarySemester}">
                                            <input type="radio" id="rd-${semester.id}" name="radio" value="${semester.id}" checked/>
                                        </c:when>
                                        <c:otherwise>
                                            <input type="radio" id="rd-${semester.id}" name="radio" value="${semester.id}"/>
                                        </c:otherwise>
                                    </c:choose>
                                    <label for="rd-${semester.id}">Chọn</label>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    $(document).ready(function () {
        $('input[type="radio"]').click(function () {
            if ($(this).is(':checked')) {
                $.ajax({
                    type: "POST",
                    url: "/admin/changesemster",
                    data: {"semesterId": $(this).val()},
                    success: function (result) {
//                        console.log(result);
                    }
                });
            }
        });
    });

</script>