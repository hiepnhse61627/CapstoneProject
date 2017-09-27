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
            <div class="form-group">
                <label>Chọn môn</label>
                <select id="select" class="select form-control">
                    <c:forEach var="sub" items="${subs}">
                        <option value="${sub.id}">${sub.id} - ${sub.name} - ${sub.abbreviation}</option>
                    </c:forEach>
                </select>
                <div id="comment"></div>
            </div>
            <div class="form-group">
                <label>Chọn môn tiên quyết</label>
                <select id="prequisite" class="select form-control"></select>
            </div>
            <div class="form-group">
                <button id="but" type="button" class="btn btn-primary" onclick="GetStudents()">Lấy thông tin</button>
            </div>
        </div>
        <div class="form-group">
            <label>Danh sách sinh viên đậu môn nhưng fail prequisites</label>
            <table id="table"></table>
        </div>
    </div>
</section>

<script>
    var table;

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
                    $("#prequisite").find('option').remove();

                    var options;

                    if (result.data.length == 0) {
                        $('#comment').html("<font color='red'>Môn này không có môn tiên quyết</font>");
                    } else {
                        $.each(result.data, function(i, item) {
                            options += "<option value='" + item.value + "'>" + item.name + "</option>";
                        });

                        $("#prequisite").append(options);
                        $('#prequisite').removeAttr('selected').find('option:first').attr('selected', 'selected');
                        $('#comment').html("");
                    }
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'error');
                }
            }
        });
    }

    function GetStudents() {
        if (table == null || table == 'undefined') {
            table = $('#table').dataTable({
                "bServerSide": true,
                "bFilter": true,
                "bRetrieve": true,
                "bScrollCollapse": true,
                "bProcessing": true,
                "bSort": false,
                "sAjaxSource": "/getFailStudents", // url getData.php etc
                "fnServerParams": function (aoData) {
                    aoData.push({"name": "subId", "value": $('#select').val()}),
                        aoData.push({"name": "prequisiteId", "value": $('#prequisite').val()})
                },
                "oLanguage": {
                    "sSearchPlaceholder": "",
                    "sSearch": "Tìm kiếm:",
                    "sZeroRecords": "Không có dữ liệu phù hợp",
                    "sInfo": "Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng",
                    "sEmptyTable": "Không có dữ liệu",
                    "sInfoFiltered": " - lọc ra từ _MAX_ dòng",
                    "sLengthMenu": "Hiển thị _MENU_ dòng",
                    "sProcessing": "Đang xử lý...",
                    "oPaginate": {
                        "sNext": "<i class='fa fa-chevron-right'></i>",
                        "sPrevious": "<i class='fa fa-chevron-left'></i>"
                    }

                },
                "aoColumnDefs": [
                    {
                        "aTargets": [0, 1, 2, 3, 4, 5, 6, 7],
                        "bSortable": false,
                    },
                ],
                "bAutoWidth": false,
            });
        } else {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }
</script>

