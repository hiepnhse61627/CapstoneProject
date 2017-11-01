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
                    <div class="title">
                        <h4>Chọn sinh viên</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="select" class="select form-control">
                                <c:forEach var="student" items="${students}">
                                    <option value="${student.id}">${student.rollNumber} - ${student.fullName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="my-content">
                        <div class="col-md-12">
                            <div id="prequisite" class="m-b-7">
                                <b>Thông tin</b>
                            </div>
                        </div>
                        <div class="col-md-12">
                            Ngành hiện tại: <span id="info"></span>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="my-content">
                        <div class="col-md-4">
                            <div class="title">
                                <h4>Danh sách document</h4>
                            </div>
                            <table id="table">
                                <thead>
                                <tr>
                                    <th>Ngành</th>
                                    <th>Quyết định</th>
                                    <th>Ngày tạo</th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                        <div class="col-md-8">
                            <div class="form-group">
                                <div class="title">
                                    Chọn ngành/khung
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="row">
                                    <div class="col-md-8">
                                        <select id="curriculum" class="select form-control">
                                            <c:forEach var="cur" items="${curs}">
                                                <option value="${cur.id}">${cur.programId.name}_${cur.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-6 text-center">
                                        <p>Các môn chung</p>
                                        <table id="yes"></table>
                                    </div>
                                    <div class="col-md-6 text-center">
                                        <p>Các môn không chung</p>
                                        <table id="no"></table>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<input type="hidden" id="curid" value=""/>

<script>
    var table = null;

    $(document).ready(function () {
        $('.select').select2();

        $('#select').on('change', function () {
            RefreshTable();
            Get();
        });

        $('#curriculum').on('change', function () {
            GetCurrent();
            GetNew();
        });

        RefreshTable();
        Get();
    });

    function GetCurrent() {
        $.ajax({
            type: "GET",
            url: "/managerrole/getcurrent",
            data: {"curId": $('#curid').val(), "newId": $('#curriculum').val()},
            success: function (result) {
                console.log(result.data);
                var data = result.data;
                var html = "";
                for (var i = 0; i < data.length; i++) {
                    html += "<tr>";
                    for (var j = 0; j < data[i].length; j++) {
                        html += "<td>";
                        html += data[i][j];
                        html += "</td>";
                    }
                    html += "</tr>";
                }
                $('#yes').html(html);
            }
        });
    }

    function GetNew() {
        $.ajax({
            type: "GET",
            url: "/managerrole/getnew",
            data: {"curId": $('#curid').val(), "newId": $('#curriculum').val()},
            success: function (result) {
                console.log(result.data);
                var data = result.data;
                var html = "";
                for (var i = 0; i < data.length; i++) {
                    html += "<tr>";
                    for (var j = 0; j < data[i].length; j++) {
                        html += "<td>";
                        html += data[i][j];
                        html += "</td>";
                    }
                    html += "</tr>";
                }
                $('#no').html(html);
            }
        });
    }

    function Get() {
        $.ajax({
            type: "GET",
            url: "/managerrole/getinfo",
            data: {"stuId": $('#select').val()},
            success: function (result) {
                console.log(result);
                $('#info').html(result.info);
                $('#curid').val(result.curriculum);
            }
        });
    }

    function RefreshTable() {
        if (table == null) {
            table = $('#table').dataTable({
                "bServerSide": true,
                "bFilter": true,
                "bRetrieve": true,
                "sScrollX": "100%",
                "bScrollCollapse": true,
                "bProcessing": true,
                "bSort": false,
                "bFilter": false,
                "bPaginate": false,
                "sAjaxSource": "/managerrole/getdocuments", // url getData.php etc
                "fnServerParams": function (aoData) {
                    aoData.push({"name": "stuId", "value": $('#select').val()})
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
                        "aTargets": [0, 1, 2],
                        "bSortable": false,
                        "sClass": "text-center",
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
