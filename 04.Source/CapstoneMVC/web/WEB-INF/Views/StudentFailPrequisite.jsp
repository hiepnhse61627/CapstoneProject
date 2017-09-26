<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 9/26/2017
  Time: 4:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="content-header">
    <h1>
        Danh sách sinh viên fail môn tiên quyết
    </h1>
</section>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <label>Chọn môn</label>
            <select id="select" class="select form-control">
                <c:forEach var="sub" items="${subs}">
                    <option value="${sub.id}">${sub.id} - ${sub.name} - ${sub.abbreviation}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-12">
            <label>Chọn môn tiên quyết</label>
            <select id="prequisite" class="select form-control"></select>
        </div>
        <%--<div class="col-md-12">--%>
            <%--<button type="button" onclick="Get()">Test</button>--%>
        <%--</div>--%>
    </div>
</section>

<script>
    $(document).ready(function () {
        $('.select').select2();

        $('#select').on('change', function() {
            Get();
        })
    });

    function Get() {
        var form = new FormData();
        form.append("subId", $('#select').val());

        $.ajax({
            type: "POST",
            url: "/getAllPrequisites",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    console.log(result.data);
                    var options;

                    $.each(result.data, function(i, item) {
                        options += "<option value='" + item.value + "'>" + item.name + "</option>";
                    });

                    $("#prequisite").find('option').remove();
                    $("#prequisite").append(options);
                    $('#prequisite').removeAttr('selected').find('option:first').attr('selected', 'selected');
//                    $("#prequisite").selectmenu({ style: 'dropdown' });
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'error');
                }
            }
        });
    }
</script>

