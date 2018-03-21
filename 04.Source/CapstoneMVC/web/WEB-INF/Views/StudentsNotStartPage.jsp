<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 11/03/18
  Time: 11:49 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<script src="/Resources/plugins/export-DataTable/dataTables.buttons.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.flash.min.js"></script>
<script src="/Resources/plugins/export-DataTable/jszip.min.js"></script>
<script src="/Resources/plugins/export-DataTable/pdfmake.min.js"></script>
<script src="/Resources/plugins/export-DataTable/vfs_fonts.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.html5.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.print.min.js"></script>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-7 title">
                    <h1>Danh sách môn chậm tiến độ của sinh viên</h1>
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
                                <label class="p-t-8">Ngành:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="program" class="select form-control">
                                    <c:forEach var="program" items="${programList}">
                                        <option value="${program.id}">${program.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
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
                    <div class="col-md-12">
                        <button class="btn btn-success" onclick="RefreshTable()">Tìm kiếm</button>
                    </div>
                </div>
            </div>


            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên</th>
                                <th>Kỳ</th>
                                <th>Môn chậm tiến độ</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    var table = null;
    $(document).ready(function () {
        $('.select').select2();

        CreateEmptyDataTable('#table');
    });

    function CreateMainTable() {
        table = $('#table').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/notStartStudentsData",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "programId", "value": $("#program").val()}),
                    aoData.push({"name": "semesterId", "value": $("#semester").val()})
            },
            "oLanguage": {
                "sSearchPlaceholder": "Tìm kiếm theo MSSV, Tên",
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
                    "aTargets": [0, 1, 2, 3],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
            dom: 'Bfrtip',
            buttons: [
                'copy', 'excel', 'pdf', 'print'
            ],
        });
    }

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        } else {
            //destroy empty table
            $('#table').dataTable().fnDestroy();
            CreateMainTable();
        }
    }
</script>