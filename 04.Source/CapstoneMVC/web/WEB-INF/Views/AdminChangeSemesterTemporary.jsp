<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Set học kỳ tạm thời</h1>
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
                            <%--<th>Đóng mở</th>--%>
                            <th>Chọn tạm thời</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="semester" items="${semesters}" varStatus="count">
                            <tr>
                                <t>${count.count}</t>
                                <td>${semester.semester}</td>
                                <td>
                                    <input type="radio" name="radio" value="${semester.id}"
                                            <c:if test="${count.last}" var="last">
                                                checked
                                            </c:if>
                                    /> Set tạm thời
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
//        $("input[type='checkbox']").bootstrapSwitch();
        $('input[type="radio"]').click(function () {
            if ($(this).is(':checked')) {
                $.ajax({
                    type: "POST",
                    url: "/admin/changesemster",
                    data: {"semesterId": $(this).val() },
                    success: function (result) {
                        console.log(result);
                    }
                });
//                alert($(this).val());
            }
        });
//            $.ajax({
//                type: "GET",
//                url: "/managerrole/semester/edit",
//                data: { "semesterId": $(this).val(), "onoff": $(this).is(':checked') },
//                success: function (result) {
//                    console.log(result);
//                }
//            });
//        $("#tbl-semester").DataTable();
    });

    //    function Create() {
    //        $.ajax({
    //            type: "GET",
    //            url: "/managerrole/semester/create",
    //            data: { "name": $("#s").val() },
    //            success: function (result) {
    //                console.log(result);
    //                location.reload();
    //            }
    //        });
    //    }
</script>