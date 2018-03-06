<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .my-tbl-wrapper {
        width: 100%;
        padding: 10px;
    }

    .dataTables_filter, .dataTables_info {
        display: none;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-5 title">
                    <h1>Thông tin chi tiết sinh viên</h1>
                </div>
                <div class="col-md-7 text-right">
                    <button type="button" class="btn btn-warning btn-with-icon" onclick="ExportExcelForOneStudent()">
                        <i class="glyphicon glyphicon-open"></i>
                        <div>Xuất dữ liêu cho sinh viên đang được chọn</div>
                    </button>
                    <button type="button" class="btn btn-warning btn-with-icon" onclick="ExportExcel2()">
                        <i class="glyphicon glyphicon-open"></i>
                        <div>Xuất dữ liêu chỉ học đi</div>
                    </button>
                    <button type="button" style="margin-top: 4px;" class="btn btn-success btn-with-icon"
                            onclick="ExportExcel()">
                        <i class="glyphicon glyphicon-open"></i>
                        <div>XUẤT DỮ LIỆU</div>
                    </button>
                </div>
            </div>
            <hr>
        </div>
        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="my-content">
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Chọn sinh viên:</label>
                            </div>

                            <div class="col-md-6 right-content width-m-70">
                                <div class="width-40 float-left m-r-5">
                                    <select id="cb-student" class="select"> </select>
                                </div>
                                <button id="find" type="button" class="btn btn-primary float-left m-r-5">Tìm kiếm
                                </button>
                                <button id="detail" type="button" class="btn btn-success float-left"
                                        style="display: none" onclick="GetAllStudentMarks()">Xem chi tiết điểm
                                </button>
                            </div>
                        </div>
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Số môn đề xuất:</label>
                            </div>
                            <div class="right-content width-60 width-m-70">
                                <div class="col-md-2">
                                    <input id="total" style="margin-left: -6px;" type="number" min="0" value="7" max="9" class="form-control"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <%--<div class="form-group">--%>
            <%--<div class="row">--%>
            <%--<div class="my-content">--%>
            <%--<div class="my-input-group">--%>
            <%--<div class="left-content m-r-5">--%>
            <%--<label class="p-t-8">Chọn kì</label>--%>
            <%--</div>--%>
            <%--<div class="right-content width-30 width-m-70">--%>
            <%--<select id="semester" class="select">--%>
            <%--<c:forEach var="sem" items="${semesters}">--%>
            <%--<option value="${sem.semester}">${sem.semester}</option>--%>
            <%--</c:forEach>--%>
            <%--</select>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-6 p-l-5 p-r-5 m-b-10">
                        <div class="my-tbl-wrapper bg-gray-light">
                            <div class="title text-center">
                                <h4>Danh sách môn chưa đạt</h4>
                            </div>
                            <div class="my-content">
                                <table id="table">
                                    <thead>
                                    <tr>
                                        <th>Mã môn</th>
                                        <th>Lớp</th>
                                        <th>Khóa</th>
                                        <th>Điểm</th>
                                        <th>Trạng thái</th>
                                        <th>Môn bị thay thế</th>
                                    </tr>
                                    </thead>
                                    <tbody></tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 p-l-5 p-r-5">
                        <div class="my-tbl-wrapper bg-gray-light overflow">
                            <div class="title text-center">
                                <h4>Danh sách môn học theo tiến độ</h4>
                            </div>
                            <div class="my-content">
                                <table id="nextCourseTable">
                                    <thead>
                                    <tr>
                                        <th>Mã môn</th>
                                        <th>Tên môn</th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-6 p-l-5 p-r-5">
                        <div class="my-tbl-wrapper bg-gray-light overflow">
                            <div class="title text-center">
                                <h4>Danh sách môn đang được học trong kỳ</h4>
                            </div>
                            <div class="my-content">
                                <div class="col-md-12">
                                    <table id="curCourseTable">
                                        <thead>
                                        <tr>
                                            <th>Mã môn</th>
                                            <th>Tên môn</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 p-l-5 p-r-5">
                        <div class="my-tbl-wrapper bg-gray-light overflow">
                            <div class="title text-center">
                                <h4>Danh sách môn học được đề xuất tiếp theo</h4>
                            </div>
                            <div class="my-content">
                                <div class="col-md-12">
                                    <table id="suggestCourseTable">
                                        <thead>
                                        <tr>
                                            <th>Mã môn</th>
                                            <th>Tên môn</th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-6 p-l-5 p-r-5">
                        <div class="my-tbl-wrapper bg-gray-light overflow">
                            <div class="title text-center">
                                <h4>Danh sách môn đang bị chậm tiến độ</h4>
                            </div>
                            <div class="my-content">
                                <div class="col-md-12">
                                    <table id="notStart">
                                        <thead>
                                        <tr>
                                            <th>Mã môn</th>
                                            <th>Tên môn</th>
                                            <th>Trạng thái</th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 p-l-5 p-r-5">
                        <div class="my-tbl-wrapper bg-gray-light overflow">
                            <div class="title text-center">
                                <h4>Danh sách môn không được học</h4>
                            </div>
                            <div class="my-content">
                                <div class="col-md-12">
                                    <table id="cantStudy">
                                        <thead>
                                        <tr>
                                            <th>Mã môn</th>
                                            <th>Tên môn</th>
                                        </tr>
                                        </thead>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="studentId"/>
    <input name="semesterId"/>
    <input name="total"/>
</form>

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
                            <th>Số lần học</th>
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
    var tableMarkDetail = null;

    var table = null;
    var nextCourseTable = null;
    var curCourseTable = null;
    var suggestCourseTable = null;
    var notStartTable = null;
    var cantStudyTable = null;

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

    function GetAllStudentMarks() {
        var form = new FormData();
        form.append("studentId", $('#cb-student').val());

        $.ajax({
            type: "POST",
            url: "/student/getAllLatestMarks",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {

                if (result.success) {
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

    $(document).ready(function () {
        CreateSelect();

        $('#find').on("click", function () {
            RefreshTable();
            CreateSelect();
        });

        CreateEmptyDataTable('#table');
        CreateEmptyDataTable('#nextCourseTable');
        CreateEmptyDataTable('#curCourseTable');
        CreateEmptyDataTable('#suggestCourseTable');
        CreateEmptyDataTable('#notStart');
        CreateEmptyDataTable('#cantStudy');
    });

    function CreateSelect() {
        $('#cb-student').select2({
            width: 'resolve',
            minimumInputLength: 2,
            ajax: {
                url: '/getStudentList',
                data: function (params) {
                    var queryParameters = {
                        searchValue: params.term
                    }
                    return queryParameters;
                },
                processResults: function (result) {
                    if (result.success) {
                        return {
                            results: $.map(result.items, function (item) {
                                return {
                                    id: item.value,
                                    text: item.text,
                                }
                            })
                        };
                    } else {
                        swal('', 'Có lỗi xảy ra, vui lòng thử lại', 'warning');
                    }
                }
            }
        });
        $("#semester").select2();
    }

    function CreateDebtTable() {
        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": false,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentDetail",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#cb-student').val()})
//                    aoData.push({"name": "semesterId", "value": $('#semester').val()})
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
                    "aTargets": [0, 1, 2, 3, 4, 5],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function CreateNextCourseTable() {
        nextCourseTable = $('#nextCourseTable').dataTable({
            "bServerSide": true,
            "bFilter": false,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentNextCourse",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#cb-student').val()})
//                    aoData.push({"name": "semesterId", "value": $('#semester').val()})
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
                    "aTargets": [0, 1],
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function CreateCantStudyTable() {
        cantStudyTable = $('#cantStudy').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentNotNextCourse",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#cb-student').val()})
                aoData.push({"name": "total", "value": $('#total').val()})
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
                    "aTargets": [0, 1],
                    "mRender": function (data, type, row) {
                        if (row[3] == '1') {
                            return "<span style='text-decoration: line-through'>" + data + "</span>";
                        } else {
                            return data;
                        }
                    }
                },
                {
                    "aTargets": [0],
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function CreateCurrentCourseTable() {
        curCourseTable = $('#curCourseTable').dataTable({
            "bServerSide": true,
            "bFilter": false,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentCurrentCourse",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#cb-student').val()})
//                    aoData.push({"name": "semesterId", "value": $('#semester').val()})
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
                    "mRender": function (data, type, row) {
                        if (row[3] == '1') {
                            return "<span style='text-decoration: line-through'>" + data + "</span>";
                        } else {
                            return data;
                        }
                    }
                },
                {
                    "aTargets": [0],
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function CreateSuggestCourseTable() {
        suggestCourseTable = $('#suggestCourseTable').dataTable({
            "bServerSide": true,
            "bFilter": false,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentNextCourseSuggestion",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#cb-student').val()})
                aoData.push({"name": "total", "value": $('#total').val()})
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
                    "aTargets": [0, 1],
                    "mRender": function (data, type, row) {
                        if (row[3] == '1') {
                            return "<span style='text-decoration: line-through'>" + data + "</span>";
                        } else {
                            return data;
                        }
                    }
                },
                {
                    "aTargets": [0],
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function CreateNotStartTable() {
        notStartTable = $('#notStart').dataTable({
            "bServerSide": true,
            "bFilter": false,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentNotStart",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#cb-student').val()})
//                    aoData.push({"name": "semesterId", "value": $('#semester').val()})
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
                    "mRender": function (data, type, row) {
                        if (row[3] == '1') {
                            return "<span style='text-decoration: line-through'>" + data + "</span>";
                        } else {
                            return data;
                        }
                    }
                },
                {
                    "aTargets": [0, 1, 2],
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function ExportExcel() {
        $("input[name='objectType']").val(2);
        $("input[name='studentId']").val("-1");
        $("input[name='total']").val($('#total').val());
        $("#export-excel").submit();

        Call();
    }

    function ExportExcel2() {
        $("input[name='objectType']").val(13);
        $("input[name='studentId']").val("-1");
        $("input[name='total']").val($('#total').val());
        $("#export-excel").submit();

        Call();
    }


    function ExportExcelForOneStudent() {
        $("input[name='objectType']").val(2);
        $("input[name='studentId']").val($('#cb-student').val());
        $("input[name='total']").val($('#total').val());
        $("#export-excel").submit();

        Call();
    }

    function Call() {
        swal({
            title: 'Đang xử lý',
            html: '<div class="form-group">Tiến trình có thể kéo dài vài phút</div>' +
            '<div class="form-group" id="progress"></div>' +
            '<div><button id="stop">Stop</button></div>',
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $('#stop').click(function () {
                    $.ajax({
                        type: "GET",
                        url: "/pauseexportStudentDetail",
                        processData: false,
                        contentType: false,
                        success: function (result) {
                            swal('', 'Dừng thành công', 'success');
                        }
                    });
                });
                Run();
            },
            allowOutsideClick: false
        });
    }

    function Run() {
        $.ajax({
            type: "GET",
            url: "/getStatusExport",
            processData: false,
            contentType: false,
            success: function (result) {
                $('#progress').html("<div>" + result.status + "</div>");
                if (result.running) {
                    setTimeout("Run()", 50);
                } else {
                    swal('', 'Download file thành công!', 'success');
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
        $('#detail').css('display', 'block');

        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        } else {
            // Delete empty table
            $('#table').dataTable().fnDestroy();
            CreateDebtTable();
        }

        if (nextCourseTable != null) {
            nextCourseTable._fnPageChange(0);
            nextCourseTable._fnAjaxUpdate();
        } else {
            // Delete empty table
            $('#nextCourseTable').dataTable().fnDestroy();
            CreateNextCourseTable();
        }


        if (curCourseTable != null) {
            curCourseTable._fnPageChange(0);
            curCourseTable._fnAjaxUpdate();
        } else {
            // Delete empty table
            $('#curCourseTable').dataTable().fnDestroy();
            CreateCurrentCourseTable();
        }

        if (suggestCourseTable != null) {
            suggestCourseTable._fnPageChange(0);
            suggestCourseTable._fnAjaxUpdate();
        } else {
            // Delete empty table
            $('#suggestCourseTable').dataTable().fnDestroy();
            CreateSuggestCourseTable();
        }

        if (notStartTable != null) {
            notStartTable._fnPageChange(0);
            notStartTable._fnAjaxUpdate();
        } else {
            // Delete empty table
            $('#notStart').dataTable().fnDestroy();
            CreateNotStartTable();
        }

        if (cantStudyTable != null) {
            cantStudyTable._fnPageChange(0);
            cantStudyTable._fnAjaxUpdate();
        } else {
            // Delete empty table
            $('#cantStudy').dataTable().fnDestroy();
            CreateCantStudyTable();
        }
    }
</script>