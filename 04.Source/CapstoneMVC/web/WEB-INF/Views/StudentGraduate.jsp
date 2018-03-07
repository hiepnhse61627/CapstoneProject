<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/excel-sub-menu.css">

<style>
    .form-group .my-content .my-input-group .left-content {
        min-width: 70px;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-7 title">
                    <h1>Danh sách sinh viên được xét tốt nghiệp</h1>
                </div>
                <div class="col-md-5 text-right">
                    <button type="button" class="btn btn-success" onclick="ExportExcel()">Xuất dữ liệu</button>
                    <button type="button" class="btn btn-warning" onclick="ExportExcelPDF()">Xuất danh sách sinh viên
                        tốt nghiệp (PDF)
                    </button>
                    <button type="button" class="btn btn-success btn-lg" data-toggle="modal"
                            data-target="#preExportExcel">Xuất danh sách sinh viên tốt nghiệp (Excel)
                    </button>
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
                                <label class="p-t-8">Loại xét</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="pass" class="select form-control">
                                    <option value="true">Sinh viên đạt điều kiện</option>
                                    <option value="false">Sinh viên không đạt điều kiện</option>
                                </select>
                            </div>
                        </div>
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Loại xét</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="type" class="select form-control">
                                    <option value="OJT">OJT</option>
                                    <option value="SWP">Đồ Án</option>
                                    <option value="Graduate">Tốt Nghiệp</option>
                                </select>
                            </div>
                        </div>
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


            <%--dùng update mấy data bị sai--%>
            <%--<button class="btn btn-success" onclick="UpdateOjtTerm()">Update Ojt term</button>--%>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên</th>
                                <th>Kỳ</th>
                                <th>Tín chỉ tích lũy</th>
                                <th>Tín chỉ yêu cầu</th>
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

<div id="preExportExcel" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Xuất kèm tên đề tài tốt nghiệp</h4>
            </div>
            <div class="modal-body">
                <div class="title">
                    <label>Chọn file:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <label for="thesisFile" hidden></label>
                            <input type="file" accept=".xlsx, .xls" id="thesisFile" name="file"/>
                        </div>
                        <div class="">
                            Bấm vào <a class="link" href="/Resources/FileTemplates/Ten_De_Tai.xlsx">Template</a> để tải
                            về bản mẫu
                        </div>
                        <button type="button" onclick="UploadThesisName()" class="btn btn-success"
                                title="dùng để upload, gán tên đề tài vào bảng điểm cho học sinh tốt nghiệp">
                            Upload Tên đề tài
                        </button>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <div class="form-group">
                    <button type="button" onclick="ExportExcelPDF2()"
                            title="Xuất ra danh sách học sinh tốt nghiệp của kì được chọn" class="btn btn-success">
                        Export Excel
                    </button>
                </div>
            </div>
        </div>

    </div>
</div>

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="programId"/>
    <input name="semesterId"/>
    <input name="boolean"/>
    <input name="type"/>
</form>

<form id="export-pdf" action="/exportExcelWithoutCallable" hidden>
    <input name="objectType"/>
    <input name="programId"/>
    <input name="semesterId"/>
</form>

<form id="export-excel-2" action="/exportExcelWithoutCallable" hidden>
    <input name="objectType"/>
    <input name="programId"/>
    <input name="semesterId"/>
</form>

<script>
    var table = null;
    var tableMarkDetail = null;
    var timeOut = 0;

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

    function UploadThesisName() {
        var form = new FormData();
        form.append('file', $('#thesisFile')[0].files[0]);

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/uploadThesisName",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        if (result.success) {
                            swal({
                                title: 'Thành công',
                                text: result.message,
                                type: 'success'
                            });
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                    }
                });
            },
            allowOutsideClick: false
        });
    }

    $(document).ready(function () {
        $('.select').select2();

        CreateEmptyDataTable('#table');

//        $('#credit').on("input", function () {
//            this.value = this.value.replace(/[^0-9]/g, '');
//            clearTimeout(timeOut);
//            timeOut = setTimeout(RefreshTable, 500);
//        });
//
//        $('#credit').on("blur", function() {
//            if (this.value == '') {
//                this.value = '0';
//            }
//        });
//
//        $('#sCredit').on("input", function () {
//            this.value = this.value.replace(/[^0-9]/g, '');
//            clearTimeout(timeOut);
//            timeOut = setTimeout(RefreshTable, 500);
//        });
//
//        $('#sCredit').on("blur", function() {
//            if (this.value == '') {
//                this.value = '0';
//            }
//        });

//        $('#program').on('change', function() {
//            RefreshTable();
//        });
//
//        $('#semester').on('change', function() {
//            RefreshTable();
//        });
//
//        $('#type').on('change', function() {
//            RefreshTable();
//        });


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
            "sAjaxSource": "/processgraduate",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "programId", "value": $("#program").val()}),
                    aoData.push({"name": "semesterId", "value": $("#semester").val()}),
                    aoData.push({"name": "type", "value": $("#type").val()}),
                    aoData.push({"name": "boolean", "value": $("#pass").val()})
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
                    "aTargets": [0, 1, 2, 3, 4],
                    "bSortable": false,
                    "sClass": "text-center",
                },
                {
                    "aTargets": [1],
                    "mRender": function (data, type, row) {
                        return "<a onclick='GetAllStudentMarks(" + row[4] + ")'>" + data + "</a>";
                    }
                },
                {
                    "aTargets": [5],
                    "bVisible": false
                }
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function ExportExcel() {
        $("input[name='objectType']").val(4);
        $("input[name='programId']").val($("#program").val());
        $("input[name='semesterId']").val($("#semester").val());
        $("input[name='type']").val($("#type").val());
        $("input[name='boolean']").val($("#pass").val());

        $("#export-excel").submit();
    }

    function ExportExcelPDF() {
        $("input[name='objectType']").val(16);
        $("input[name='programId']").val($("#program").val());
        $("input[name='semesterId']").val($("#semester").val());

        $('#export-pdf').submit();
    }

    function ExportExcelPDF2() {
        $("input[name='objectType']").val(17);
        $("input[name='programId']").val($("#program").val());
        $("input[name='semesterId']").val($("#semester").val());

        $('#export-excel-2').submit();
    }

    //    function ExportExcelPDF() {
    //        $("input[name='objectType']").val(5);
    //        $("input[name='credit']").val($('#credit').val());
    //        $("input[name='sCredit']").val($('#sCredit').val());
    //        $("input[name='programId']").val($('#program').val());
    //        $("input[name='semesterId']").val($('#semester').val());
    //        $("input[name='sSearch']").val(table.api().context[0].oPreviousSearch.sSearch);
    //
    //        $("#export-excel").submit();
    //    }

    function GetAllStudentMarks(studentId) {
        var form = new FormData();
        form.append("studentId", studentId);

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
        } else {
            //destroy empty table
            $('#table').dataTable().fnDestroy();
            CreateMainTable();
        }
    }

    function UpdateOjtTerm() {

        $.ajax({
            type: "POST",
            url: "/updateStudentTerm",
            processData: false,
            contentType: false,
            success: function (result) {

                if (result.success) {
                    swal('Success',
                        result.message,
                        'success')
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'warning');
                }
            }
        });
    }

</script>
