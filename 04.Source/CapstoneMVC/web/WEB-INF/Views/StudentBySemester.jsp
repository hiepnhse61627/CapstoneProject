<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 1/23/2018
  Time: 5:59 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<%--use for export data as excel or pdf for Datatable--%>
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
                <div class="col-md-9 title">
                    <h1>Danh sách thông tin sinh viên theo kỳ và ngành</h1>
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
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Ngành:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="program" class="select form-control">
                                    <option value="-1">Tất cả</option>
                                    <c:forEach var="program" items="${programList}">
                                        <option value="${program.id}">${program.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-student">
                        <thead>
                        <th>MSSV</th>
                        <th>Tên sinh viên</th>
                        <th>Ngành học</th>
                        <th>Kì học</th>
                        <th>Trạng thái</th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    $(document).ready(function () {
        $('.select').select2();

        loadData4Table();
    });

    function loadData4Table() {
        //use new Datatable not legacy datatable
        $('#tbl-student').DataTable({
            "serverSide": false,
            "filter": true,
            "retrieve": true,
            "scrollX": "100%",
            "scrollCollapse": true,
            "processing": true,
            "sort": false,
            "sAjaxSource": "/studentsBySemesterAndProgramData",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "semesterId", "value": $('#semester').val()});
                aoData.push({"name": "programId", "value": $('#program').val()});
            },
            "language": {
                "searchPlaceholder": "Tên hoặc MSSV",
                "search": "Tìm kiếm:",
                "zeroRecords": "Không có dữ liệu phù hợp",
                "info": "Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng",
                "emptyTable": "Không có dữ liệu",
                "infoFiltered": " - lọc ra từ _MAX_ dòng",
                "lengthMenu": "Hiển thị _MENU_ dòng",
                "processing": "Đang xử lý...",
                "paginate": {
                    "next": "<i class='fa fa-chevron-right'></i>",
                    "previous": "<i class='fa fa-chevron-left'></i>"
                }
            },
            "columnDefs": [
                {
                    "targets": [0, 1, 2, 3, 4],
                    "bSortable": false,
                    "className": "text-center",
                },
            ],
            "autoWidth": false,
             dom: 'Bfrtip',
            lengthMenu: [[10,25,50],[ '10 dòng', '25 dòng', '50 dòng', 'Tất cả' ]],
            buttons: [
                {
                    extend:    'excel',
                    text:      'Xuất Excel',
                },
                {
                    extend:    'pdf',
                    text:      'Xuất PDF',
                },
            ]
        });
    }

    $('.select').on('change', function(){
        $('#tbl-student').DataTable().ajax.reload();
    });

</script>