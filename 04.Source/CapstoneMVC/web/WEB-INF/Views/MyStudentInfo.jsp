<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="passCredits" value="${student.passCredits}" />
<c:set var="passFailCredits" value="${student.passFailCredits}" />
<c:set var="passFailAverageMark" value="${student.passFailAverageMark}" />

<c:if test="${empty passCredits}">
    <c:set var="passCredits" value="0"/>
</c:if>

<c:if test="${empty passFailCredits}">
    <c:set var="passFailCredits" value="0"/>
</c:if>

<c:if test="${empty passFailAverageMark}">
    <c:set var="passFailAverageMark" value="0"/>
</c:if>

<style>
    .form-group .my-content .my-input-group .right-content {
        width: 76%;
    }

    .first-col {
        width: 12%;
        padding-right: 10px;
        text-align: right;
    }

    .other-col {
        width: 88%;
    }

    .mark-detail-wrapper {
        width: 100%;
        text-align: center;
        font-size: 16px;
    }

    .mark-detail-wrapper .mark-detail {
        width: 80%;
        background-color: #f9f9f9;
    }

    .mark-detail-wrapper .mark-detail .header {
        display: flex;

        background-color: #3c8dbc;
        color: white;
        border-bottom: 1px solid #333333;
    }

    .mark-detail-wrapper .mark-detail .header,
    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper {
        padding: 5px 0px;
    }

    .mark-detail-wrapper .mark-detail .header span {
        font-weight: 600;
        margin: 0px;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper {
        width: 100%;
        display: flex;
        flex-flow: row wrap;

        border-bottom: 1px solid #333333;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .term {
        padding-top: 5px;
        font-weight: 600;
        float: left;
        width: 12%;
        padding-right: 10px;
        text-align: right;
        color: chocolate;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .marks {
        width: 88%;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .marks .mark {
        display: flex;
        background: none;
        padding: 5px 0px;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .marks .mark:nth-child(odd) {
        background-color: white;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .average {
        width: 100%;
        font-weight: 600;
        padding: 5px 0px;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .average .title {
        width: 12%;
        float: left;
        margin: 0px;
        padding: 0px 10px;
        text-align: right;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .average .number {
        width: 88%;
        float: left;
        text-align: left;
        padding-left: 30px;
    }

    .small-col {
        width: 12%;
        padding: 0px 15px;
        float: left;
    }

    .medium-col {
        width: 15%;
        padding: 0px 15px;
        float: left;
    }

    .large-col {
        width: 34%;
        padding: 0px 15px;
        float: left;
    }

    .btn-icon {
        padding: 2px 0px;
        font-size: 14px;
    }

    .bottom-info-text {
        color: #444444;
        font-weight: 600;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-6 title">
                    <h1>Thông tin sinh viên</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <div class="col-md-6 title">
                            <h4 class="text-left m-r-10 m-t-5">Thông tin chi tiết</h4>
                            <button class="btn btn-primary text-left" id="btnEdit" onclick="onEdit()">
                                <i class="glyphicon glyphicon-pencil btn-icon"></i>
                            </button>
                            <button class="btn btn-success text-left m-r-5" id="btnSubmit" onclick="EditStudent()" style="display: none">
                                <i class="fa fa-check btn-icon"></i>
                            </button>
                            <button class="btn btn-danger text-left" id="btnCancel" onclick="onCancel()" style="display: none">
                                <i class="fa fa-times btn-icon"></i>
                            </button>
                            <div class="text-right">
                                <a onclick="ExportExcel()" class="btn btn-warning btn-with-icon text-right m-l-5">
                                    <i class="glyphicon glyphicon-open"></i>
                                    <div class="m-l-3">XUẤT BẢNG ĐIỂM QUÁ TRÌNH</div>
                                </a>
                            </div>
                        </div>

                    </div>
                    <div class="my-content">
                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Tên:</label>
                                </div>
                                <div class="right-content">
                                    <input id="studentName" disabled type="text" class="form-control"
                                           value="${student.fullName}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Giới tính:</label>
                                </div>
                                <div class="right-content">
                                    <input id="gender" disabled type="text" class="form-control"
                                           value="${gender}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">MSSV:</label>
                                </div>
                                <div class="right-content">
                                    <input id="rollNumber" disabled type="text" class="form-control"
                                           value="${student.rollNumber}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Ngày sinh:</label>
                                </div>
                                <div class="right-content">
                                    <input id="dateOfBirth" disabled type="text" class="form-control"
                                           value="${dateOfBirth}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Ngành học:</label>
                                </div>
                                <div class="right-content">
                                    <input id="program" disabled type="text" class="form-control"
                                           value="${program}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Khóa ngành:</label>
                                </div>
                                <div class="right-content">
                                    <input id="curriculum" disabled type="text" class="form-control"
                                           value="${curriculum}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Học kì:</label>
                                </div>
                                <div class="right-content">
                                    <input id="termNumber" disabled type="text" class="form-control"
                                           value="${student.term}"/>
                                </div>
                            </div>
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Trạng thái:</label>
                                </div>
                                <div class="right-content">
                                    <input id="status" disabled type="text" class="form-control"
                                           value="${status}"/>
                                </div>
                            </div>
                        </div>
                        <div class="row m-0">
                            <button style="display: none" id="change" type="button" class="btn btn-primary" onclick="return EditStudent($('#rollNumber').val(),$('#studentName').val()
                                    ,$('#gender').val(),$('#dateOfBirth').val(),$('#program').val(),$('#curriculum').val(),$('#termNumber').val())">
                                Thay đổi thông tin
                            </button>

                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4 class="text-left m-r-10 m-t-5">Thông tin điểm</h4>
                        <button class="btn btn-success text-left" style="margin-top: -3px" onclick="GetAllStudentMarks(${student.id})">Xem chi tiết điểm</button>
                    </div>

                    <div class="my-content">
                        <div hidden class="no-data col-md-12">
                            Không có dữ liệu
                        </div>
                        <div hidden class="mark-detail-wrapper col-md-12">
                            <div class="mark-detail">
                                <div class="header">
                                    <div class="first-col"></div>
                                    <div class="other-col">
                                        <div class="small-col"><span>Mã môn</span></div>
                                        <div class="large-col text-left"><span>Tên môn</span></div>
                                        <div class="medium-col"><span>Học kỳ</span></div>
                                        <div class="small-col"><span>Tín chỉ</span></div>
                                        <div class="small-col"><span>Điểm</span></div>
                                        <div class="medium-col"><span>Trạng thái</span></div>
                                    </div>
                                </div>

                                <div class="mark-detail-content">
                                </div>

                                <%--<div class="mark-detail-content">--%>
                                <%--<div class='term-wrapper'>--%>
                                <%--<div class='term'><span>Tổng tín chỉ và điểm trung bình</span></div>--%>
                                <%--<div class='marks'>--%>
                                <%--<div class='mark'>--%>
                                <%--<div class='small-col'><span> </span></div>--%>
                                <%--<div class='large-col text-left'><span></span></div>--%>
                                <%--<div class='medium-col'><span></span></div>--%>
                                <%--<div class='small-col'><span id="total"></span></div>--%>
                                <%--<div class='small-col'><span id="average"></span></div>--%>
                                <%--<div class='medium-col'><span></span></div>--%>
                                <%--</div>--%>
                                <%--</div>--%>
                                <%--</div>--%>
                                <%--</div>--%>
                            </div>
                        </div>
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

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="studentId"/>
</form>

<script>
    var oldFullName;
    var oldGender;
    var oldDoB;
    var oldTerm;
    var tableMarkDetail = null;

    $(document).ready(function () {
        LoadMarkList();
        CreateSelect();

        oldFullName = '${student.fullName}';
        oldGender = '${gender}';
        oldDoB = '${dateOfBirth}';
        oldTerm = '${student.term}';
    });

    function LoadMarkList() {
        var form = new FormData();
        form.append("studentId", '${student.id}');
        $.ajax({
            type: "POST",
            url: "/studentList/marks",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    RenderMarkList(result.detailList)
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });
    }

    function RenderMarkList(list) {
        if (list.length > 0) {
            $('.mark-detail-wrapper').show();
            $('.no-data').hide();
        } else {
            $('.mark-detail-wrapper').hide();
            $('.no-data').show();
        }

        var html = "";
        for (var i = 0; i < list.length; ++i) {
            var term = list[i].term;
            var markList = list[i].markList;

            var termStr = term != -1 ? "Học kỳ " + term : "Môn khác";

            html += "<div class='term-wrapper'>";
            html += "<div class='term'><span>" + termStr + "</span></div>";
            html += "<div class='marks'>";

            var sum = 0;

            for (var j = 0; j < markList.length; ++j) {
                var mark = markList[j];

                html += "<div class='mark'>"
                html += "<div class='small-col'><span>" + mark.subject + "</span></div>";
                html += "<div class='large-col text-left'><span>" + mark.subjectName + "</span></div>";
                html += "<div class='medium-col'><span>" + mark.semester + "</span></div>";
                html += "<div class='small-col'><span>" + mark.credits + "</span></div>";
                html += "<div class='small-col'><span>" + mark.averageMark + "</span></div>";
                html += "<div class='medium-col'><span>" + mark.status + "</span></div>";
                html += "</div>";

                sum += parseFloat(mark.averageMark);
            }
            html += "</div>";

            html += "<div class='term'><span>Trung bình</span></div>";
            html += "<div class='marks'>";
            html += "<div class='mark'>";
            html += "<div class='small-col'><span></span></div>";
            html += "<div class='large-col text-left'><span></span></div>";
            html += "<div class='medium-col'><span></span></div>";
            html += "<div class='small-col'><span></span></div>";
            html += "<div class='small-col'><span>" + (Math.round(sum / markList.length * 100) / 100) + "</span></div>";
            html += "<div class='medium-col'><span></span></div>";
            html += "</div>";
            html += "</div>";

            html += "</div>";

        }

        html += "<div class='term-wrapper'>";
        html += "<div class='term'><span>Thông tin</span></div>";

        html += "<div class='marks'>";
        html += "<div class='mark'>"
//        html += "<div class='small-col'><span></span></div>";
//        html += "<div class='large-col text-left'><span></span></div>";
//        html += "<div class='medium-col'><span></span></div>";
//        html += "<div class='small-col'><span id='total'></span></div>";
//        html += "<div class='small-col'><span id='average'></span></div>";
//        html += "<div class='medium-col'><span></span></div>";
        html += "<div class='width-5'></div>"
        html += "<div class='width-29'><span class='bottom-info-text'>Số tín chỉ đã học: </span>${passFailCredits}</div>";
        html += "<div class='width-29'><span class='bottom-info-text'>Số tín chỉ tích lũy: </span>${passCredits}</div>";
        html += "<div class='width-29'><span class='bottom-info-text'>Điểm trung bình: </span>${passFailAverageMark}</div>";
        html += "</div></div>";

        html += "</div>"

        $('.mark-detail-content').html(html);
    }

    function EditStudent() {
        swal({
            title: 'Xác nhận cập nhật sinh viên?',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            $.ajax({
                type: "POST",
                url: "/student/edit",
                data: {
                    "sRollNumber": $('#rollNumber').val(),
                    "sFullName": $('#studentName').val(),
                    "sGender": $('#gender').val(),
                    "sDOB": $('#dateOfBirth').val(),
                    "sProgram": $('#program').val(),
                    "sCurriculum": $('#curriculum').val(),
                    "sTermNumber": $('#termNumber').val(),
                },
                success: function (result) {
                    if (result.success) {
                        swal({
                            title: 'Thành công',
                            text: "Đã cập nhật sinh viên!",
                            type: 'success'
                        }).then(function () {
                            oldFullName = $('#studentName').val();
                            oldGender = $('#gender').val();
                            oldDoB = $('#dateOfBirth').val();
                            oldTerm = $('#termNumber').val();
                            onCancel();
                        });
                    } else {
                        swal('', result.message, 'error');
                    }
                }
            });
        });
    }

    function onEdit() {
        $('#btnEdit').hide();
        $('#btnSubmit').show();
        $('#btnCancel').show();
        $('#studentName').prop("disabled", false);
        $('#dateOfBirth').prop("disabled", false);
        $('#gender').prop("disabled", false);
        $('#termNumber').prop("disabled", false);
    }

    function onCancel() {
        $('#btnEdit').show();
        $('#btnSubmit').hide();
        $('#btnCancel').hide();
        $('#studentName').prop("disabled", true);
        $('#dateOfBirth').prop("disabled", true);
        $('#gender').prop("disabled", true);
        $('#termNumber').prop("disabled", true);

        $('#studentName').val(oldFullName);
        $('#dateOfBirth').val(oldDoB);
        $('#gender').val(oldGender);
        $('#termNumber').val(oldTerm);
    }

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

    function ExportExcel() {
        $("input[name='objectType']").val(18);
        $("input[name='studentId']").val('${student.id}');

        $("#export-excel").submit();
    }

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
    }
    $('#cb-student').on('change', function(){
       var stuId = $('#cb-student').val();
       if(stuId > 0){
           $(location).attr('href', '/studentList/' + stuId);
       }
    });

</script>

