<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    a {
        cursor: pointer;
    }
</style>

<section class="content-header">
    <h1>
        Danh sách sinh viên nợ môn
    </h1>
</section>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <form id="form">
                    <div class="form-group">
                        <div class="box-header">
                            <h4 class="box-title">Học kỳ</h4>
                        </div>
                        <select id="semester" class="select form-control">
                            <option value="0">All</option>
                            <c:forEach var="semester" items="${semesters}">
                                <option value="${semester.id}">${semester.semester}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-group">
                        <div class="box-header">
                            <h4 class="box-title">Môn học</h4>
                        </div>
                        <select id="subject" class="select form-control">
                            <option value="0">All</option>
                            <c:forEach var="sub" items="${subjects}">
                                <option value="${sub.id}">${sub.id} - ${sub.name} - ${sub.abbreviation}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <button type="button" onclick="RefreshTable()" class="btn btn-success">Tìm kiếm</button>
                </form>

                <div class="col-md-12">
                    <table id="table">
                        <thead>
                        <tr>
                            <th>MSSV</th>
                            <th>Tên SV</th>
                            <th>Môn học</th>
                            <th>Lớp</th>
                            <th>Học kỳ</th>
                            <th>Điểm TB</th>
                            <th>Status</th>
                        </tr>
                        </thead>
                    </table>
                </div>
                <div class="col-md-12">
                    <a href="/exportExcel?objectType=1" class="btn btn-success">Export File</a>
                </div>
                </form>
            </div>
        </div>
    </div>
</section>

<div id="markDetail" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Chi tiết điểm</h4>
            </div>
            <div class="modal-body">
                <div class="col-md-12">
                    <table id="table-mark-detail">
                        <thead>
                        <tr>
                            <th>Môn học</th>
                            <th>Học kỳ</th>
                            <th>Số lần học lại</th>
                            <th>Điểm trung bình</th>
                            <th>Trạng thái</th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>

<script>
    var table = null;
    var tableMarkDetail = null;

    jQuery.fn.dataTableExt.oApi.fnSetFilteringDelay = function (oSettings, iDelay) {
        var _that = this;

        if (iDelay === undefined) {
            iDelay = 250;
        }

        this.each(function (i) {
            $.fn.dataTableExt.iApiIndex = i;
            var
                $this = this,
                oTimerId = null,
                sPreviousSearch = null,
                anControl = $('input', _that.fnSettings().aanFeatures.f);

            anControl.off('keyup search input').on('keyup search input', function () {
                var $$this = $this;

                if ((anControl.val().length == 0 || anControl.val().length >= 3) && (sPreviousSearch === null || sPreviousSearch != anControl.val())) {
                    window.clearTimeout(oTimerId);
                    sPreviousSearch = anControl.val();
                    oTimerId = window.setTimeout(function () {
                        $.fn.dataTableExt.iApiIndex = i;
                        _that.fnFilter(anControl.val());
                    }, iDelay);
                }
            });

            return this;
        });
        return this;
    };

    $(document).ready(function () {
        $('.select').select2();

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getstudents", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "semesterId", "value": $('#semester').val()}),
                    aoData.push({"name": "subjectId", "value": $('#subject').val()})
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
                {
                    "aTargets": [0, 2, 3, 4, 5, 6],
                    "sClass": "text-center",
                },
                {
                    "aTargets": [1],
                    "mRender": function (data, type, row) {
                        return "<a onclick='GetAllStudentMarks(" + row[7] + ")'>" + data + "</a>";
                    }
                },
                {
                    "aTargets": [7],
                    "bVisible": false
                }
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    });

    function GetAllStudentMarks(studentId) {
        var form = new FormData();
        form.append("studentId", studentId);

        $.ajax({
            type: "POST",
            url: "/student/getAllMarks",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {

                if (result.success) {
                    debugger
                    result.studentMarkDetail = JSON.parse(result.studentMarkDetail);

                    $("#markDetail").find(".modal-title").html("Chi tiết điểm - " + result.studentMarkDetail.studentName);
                    CreateMarkDetailTable(result.studentMarkDetail.markList);
                    $("#markDetail").modal();
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'warning');
                }
            }
        });
    }

    function CreateMarkDetailTable(dataSet) {
        if (tableMarkDetail != null) {
            tableMarkDetail.fnDestroy();
        }

        tableMarkDetail = $('#table-mark-detail').dataTable({
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "data": dataSet,
            "aoColumns": [
                {"mData": "subject"},
                {"mData": "semester"},
                {"mData": "repeatingNumber"},
                {"mData": "averageMark"},
                {"mData": "status"},
            ],
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
                    "aTargets": [0, 1, 2, 3, 4],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        });
        $("#markDetail").modal();
    }

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }
</script>