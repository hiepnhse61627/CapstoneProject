<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>


<style>
    .form-group .my-content .my-input-group .right-content {
        width: 76%;
    }

    .btn-icon {
        padding: 2px 0px;
        font-size: 14px;
    }

    .form-date-range {
        position: relative;
    }

    .form-date-range i {
        position: absolute;
        bottom: 10px;
        right: 10px;
        top: auto;
        cursor: pointer;
    }

    .select2-selection--single {
        border-radius: 0px !important;

        box-shadow: none;
        border-color: #d2d6de !important;
        padding: 6px 12px !important;
        height: 34px !important;
    }

    .select2-selection--single .select2-selection__rendered {
        padding-left: 0px !important;
    }

    .select2-selection--single .select2-selection__arrow {
        height: 30px !important;
    }

    .select2-search__field {
        width: 100% !important;
    }

    .select2-selection__choice {
        color: black !important;;
    }

    .table-condensed > tbody > tr > td {
        padding: 3px;
    }

    .select2-selection__rendered[title]:hover:after {
        color: red;
    }


</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Thông tin giảng viên</h1>
                </div>
                <div class="col-md-3 text-right">
                    <a href="/employeeList" class="btn btn-danger btn-with-icon text-right">
                        <i class="fa fa-arrow-left"></i>
                        <div class="m-l-3">QUAY LẠI</div>
                    </a>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4 class="text-left m-r-10 m-t-5">Thông tin chi tiết</h4>
                        <button class="btn btn-primary text-left" id="btnEdit" onclick="onEdit()">
                            <i class="glyphicon glyphicon-pencil btn-icon"></i>
                        </button>
                        <button class="btn btn-success text-left m-r-5" id="btnEditSubmit" onclick="EditEmployee()"
                                style="display: none">
                            <i class="fa fa-check btn-icon"></i>
                        </button>
                        <button class="btn btn-danger text-left" id="btnEditCancel" onclick="onCancel()"
                                style="display: none">
                            <i class="fa fa-times btn-icon"></i>
                        </button>
                    </div>
                    <div class="my-content">
                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Tên:</label>
                                </div>
                                <div class="right-content">
                                    <input id="employeeName" disabled type="text" class="form-control"
                                           value="${employee.fullName}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Giới tính:</label>
                                </div>
                                <div class="right-content">
                                    <input id="gender" disabled type="text" class="form-control"
                                           value="${(employee.gender == true) ? "Nam" : "Nữ"}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Mã GV:</label>
                                </div>
                                <div class="right-content">
                                    <input id="code" disabled type="text" class="form-control"
                                           value="${employee.code}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Ngày sinh:</label>
                                </div>
                                <div class="right-content">
                                    <input id="dateOfBirth" disabled type="text" class="form-control"
                                           value="${employee.dateOfBirth}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Vị trí:</label>
                                </div>
                                <div class="right-content">
                                    <input id="position" disabled type="text" class="form-control"
                                           value="${employee.position}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Loại hợp đồng:</label>
                                </div>
                                <div class="right-content">
                                    <input id="contract" disabled type="text" class="form-control"
                                           value="${employee.contract}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Email FE:</label>
                                </div>
                                <div class="right-content">
                                    <input id="emailFE" disabled type="text" class="form-control"
                                           value="${employee.emailFE}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Email EDU:</label>
                                </div>
                                <div class="right-content">
                                    <input id="emailEDU" disabled type="text" class="form-control"
                                           value="${employee.emailEDU}"/>
                                </div>
                            </div>
                        </div>


                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Email cá nhân:</label>
                                </div>
                                <div class="right-content">
                                    <input id="emailPersonal" disabled type="text" class="form-control"
                                           value="${employee.personalEmail}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">SĐT:</label>
                                </div>
                                <div class="right-content">
                                    <input id="phone" disabled type="text" class="form-control"
                                           value="${employee.phone}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group p-l-30 text-left" style="width: 93%;">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Địa chỉ:</label>
                                </div>
                                <div class="right-content">
                                    <input id="address" disabled type="text" class="form-control"
                                           value="${employee.address}"/>
                                </div>
                            </div>

                        </div>
                        <div class="row m-0">
                            <div class="my-input-group p-l-30 text-left" style="width: 93%;">
                                <div class="left-content" style="width: 85px">
                                    <label class="p-t-8">Môn phụ trách:</label>
                                </div>
                                <div class="right-content">
                                    <select id="empCompetence" class="select slot-select" multiple="multiple" disabled>
                                        <c:forEach var="aSubject" items="${subjects}">
                                            <option value="${aSubject.id}">${aSubject.id}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4 class="text-left m-r-10 m-t-5">Lịch dạy</h4>
                    </div>

                    <div class="my-content">
                        <div class="b-body">
                            <div class="row" style="display: flex; position: relative;">
                                <div class="col-md-3">
                                    <div class="form-group form-date-range">
                                        <label for="scheduleDate2">Ngày bắt đầu - kết thúc:</label>
                                        <input id="scheduleDate2" type="text" class="form-control"/>
                                        <i class="fa fa-calendar"></i>
                                    </div>
                                </div>
                                <div class="col-md-9">
                                    <div class="form-group" style="width: 100%; bottom: 0; position: absolute;">
                                        <button type="button" class="btn btn-success" onclick="RefreshTable()"
                                                id="searchBtn">Tìm kiếm
                                        </button>
                                        <button type="button" class="btn btn-primary" onclick="resetFilter()"
                                                id="removeFilterBtn">Xóa bộ lọc
                                        </button>
                                        <button class="btn btn-success text-right" onclick="CreateSchedule()"
                                                style="margin-right: 30px;">
                                            Thêm lịch dạy
                                        </button>
                                    </div>
                                </div>
                                <%--<div class="col-md-3">--%>
                                    <%--<div class="form-group">--%>
                                        <%--<label for="addBtn"> &nbsp;</label>--%>
                                        <%----%>
                                    <%--</div>--%>
                                <%--</div>--%>

                            </div>


                            <div class="row">
                                <div class="col-md-12">
                                    <table id="tbl-schedule">
                                        <thead>
                                        <th>ID</th>
                                        <th>Mã môn</th>
                                        <th>Lớp</th>
                                        <th>Ngày</th>
                                        <th>Slot</th>
                                        <th>Phòng</th>
                                        <th>Giảng viên</th>
                                        <th>Capacity</th>
                                        <th>IsPast</th>
                                        <th>Chi tiết</th>
                                        </thead>
                                        <tbody></tbody>
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

<div id="scheduleModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Lịch dạy cá nhân</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label class="form-check-label" for="subject">Môn học:</label>
                            <select id="subject" class="select semester-select">
                                <c:forEach var="aSubject" items="${subjects}">
                                    <option value="${aSubject.id}">${aSubject.id} - ${aSubject.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group" id="semester-container">
                            <label class="form-check-label" for="semester">Học kỳ:</label>
                            <select id="semester" class="select semester-select">
                                <c:forEach var="semester" items="${semesters}">
                                    <option value="${semester.semester}">${semester.semester}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="class">Lớp:</label>
                            <input id="class" type="text" class="form-control"/>
                        </div>

                        <div class="form-group form-date-range">
                            <label for="scheduleDate">Ngày bắt đầu - kết thúc:</label>
                            <input id="scheduleDate" type="text" class="form-control"/>
                            <i class="fa fa-calendar"></i>
                        </div>

                        <div class="form-group row">
                            <div class="col-md-6" id="dayOfWeek-container">
                                <label class="form-check-label" for="dayOfWeek">Thứ:</label>
                                <select id="dayOfWeek" class="select dayOfWeek-select">
                                    <option value="Mon">Thứ 2</option>
                                    <option value="Tue">Thứ 3</option>
                                    <option value="Wed">Thứ 4</option>
                                    <option value="Thu">Thứ 5</option>
                                    <option value="Fri">Thứ 6</option>
                                    <option value="Sat">Thứ 7</option>
                                </select>
                            </div>

                            <div class="col-md-6" id="slot-container">
                                <label for="slot">Slot:</label>
                                <select id="slot" class="select slot-select" multiple="multiple">
                                    <c:forEach var="aSlot" items="${slots}">
                                        <option value="${aSlot.slotName}">${aSlot.slotName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="input_fields_wrap">
                                <button class="btn btn-primary add_field_button">Thêm ngày học</button>
                            </div>
                        </div>

                        <div class="form-group field_array">
                        </div>

                        <div class="form-group" id="room-container">
                            <label for="room">Phòng:</label>
                            <select id="room" class="select room-select">
                                <c:forEach var="room" items="${rooms}">
                                    <option value="${room.name}">${room.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group" id="capacity-container">
                            <label for="capacity">Số lượng sinh viên tối đa:</label>
                            <select id="capacity" class="select room-select">
                                <c:forEach var="aCapacity" items="${capacity}">
                                    <option value="${aCapacity}">${aCapacity}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-check" id="changeRoom-container">
                            <input class="form-check-input" type="checkbox" value="" id="changeRoom">
                            <label class="form-check-label" for="changeRoom">
                                Tự tìm phòng trống
                            </label>
                        </div>

                        <div class="form-check" id="all-container">
                            <input class="form-check-input" type="checkbox" value="" id="all">
                            <label class="form-check-label" for="all">
                                Áp dụng cho tất cả slot tương tự
                            </label>
                        </div>

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Đóng</button>
                <button id="btnSubmit" type="button" class="btn btn-primary">Tạo</button>
            </div>
        </div>

    </div>
</div>

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="studentId"/>
</form>

<script>
    var tblSchedule;
    var startDate;
    var endDate;

    var startDate2;
    var endDate2;

    var oldPosition;
    var oldContract;
    var oldEmailFE;
    var oldEmailEDU;
    var oldEmailPersonal;
    var oldPhone;
    var oldAddress;
    var oldName;
    var oldCode;
    var oldCompetence = new Array();


    jQuery.fn.dataTableExt.oApi.fnSetFilteringDelay = function (oSettings, iDelay) {
        var _that = this;

        if (iDelay === undefined) {
            iDelay = 250;
        }

        this.each(function (i) {
            $.fn.dataTableExt.iApiIndex = i;
            var
                oTimerId = null,
                sPreviousSearch = null,
                anControl = $('input', _that.fnSettings().aanFeatures.f);

            anControl.off('keyup search input').on('keyup search input', function () {
                if ((anControl.val().length == 0 || anControl.val().length >= 2) && (sPreviousSearch === null || sPreviousSearch != anControl.val())) {
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

    function resetSelect2(x) {
        $('#dayOfWeek' + x).select2({
            placeholder: '- Chọn thứ -'
        });
        $('#dayOfWeek' + x).val('').trigger('change');

        $('#slot' + x).select2({
            placeholder: '- Chọn slot học -'
        });
        $('#slot' + x).val('').trigger('change');

        $('select').on('change', function (evt) {
            $("span[class*='select2-selection__rendered']").each(function (i, el) {
                $(el).removeAttr('title');
            });

            $("select[id*='slot']").each(function (j, el) {
                var slotArr = $(el).val();

                if (slotArr !== null && slotArr !== undefined && slotArr instanceof Array) {
                    for (var i = 0; i < slotArr.length; i++) {
                        slotArr[i] = parseInt(slotArr[i].replace("Slot ", ""));
                    }

                    var isConsecutive = Consecutive(slotArr);
                    if (isConsecutive > 0) {
                        slotArr.pop();
                        alert("Slot trong cùng ngày phải kế nhau");
                        for (var t = 0; t < slotArr.length; t++) {
                            slotArr[t] = "Slot " + slotArr[t];
                        }
                        $(el).val(slotArr).trigger("change");
                        return;
                    }
                }

            });


            $("li[class*='select2-selection__choice']").each(function (i, el) {
                $(el).removeAttr('title');
            });
        });
    }

    function Consecutive(arr) {
        arr.sort(function (a, b) {
            return a - b;
        });
        return arr[arr.length - 1] - arr[0] - arr.length + 1;
    }

    $(document).ready(function () {
        oldPosition = '${employee.position}';
        oldContract = '${employee.contract}';
        oldEmailFE = '${employee.emailFE}';
        oldEmailEDU = '${employee.emailEDU}';
        oldEmailPersonal = '${employee.personalEmail}';
        oldPhone = '${employee.phone}';
        oldAddress = '${employee.address}';
        oldName = '${employee.fullName}';
        oldCode = '${employee.code}';
        oldCompetence = JSON.parse('${empCompetence}');

        $('#empCompetence').select2({
            placeholder: '- Chọn môn -'
        });

        $('#empCompetence').val(oldCompetence).trigger('change');

        $("li[class*='select2-selection__choice']").each(function (i, el) {
            $(el).removeAttr('title');
        });


        $('#subject').select2({
            placeholder: '- Chọn môn -'
        });

        $('#semester').select2({
            placeholder: '- Chọn kì học -'
        });

        $('#dayOfWeek').select2({
            placeholder: '- Chọn thứ -'
        });

        $('#slot').select2({
            placeholder: '- Chọn slot học -'
        });

        $('#capacity').select2({
            placeholder: '- Chọn số lượng -'
        });

        $('#room').select2();
        // $("#room").attr("disabled", true);

        $('#lecture').select2({
            placeholder: '- Chọn giảng viên -'
        });

        $('select').on('change', function (evt) {
            $('.select2-selection__choice').removeAttr('title');
            $('#select2-dayOfWeek-container').removeAttr('title');
        });
        var max_fields = 10; //maximum input boxes allowed
        var wrapper = $(".field_array"); //Fields wrapper
        var add_button = $(".add_field_button"); //Add button ID


        $(add_button).click(function (e) { //on add input button click
            e.preventDefault();
            if (x < max_fields) { //max input box allowed
                x++; //text box increment
                $(wrapper).append('<div class="form-group row addMore">\n' +
                    '                            <div class="col-md-4">\n' +
                    '                                <select id="dayOfWeek' + x + '" class="select slot-select">\n' +
                    '                                    <option value="Mon">Thứ 2</option>\n' +
                    '                                    <option value="Tue">Thứ 3</option>\n' +
                    '                                    <option value="Wed">Thứ 4</option>\n' +
                    '                                    <option value="Thu">Thứ 5</option>\n' +
                    '                                    <option value="Fri">Thứ 6</option>\n' +
                    '                                    <option value="Sat">Thứ 7</option>\n' +
                    '                                </select>\n' +
                    '                            </div>\n' +
                    '                            <div class="col-md-6">\n' +
                    '                                <select id="slot' + x + '" class="select slot-select" multiple="multiple">\n' +
                    '                                    <c:forEach var="aSlot" items="${slots}">\n' +
                    '                                        <option value="${aSlot.slotName}">${aSlot.slotName}</option>\n' +
                    '                                    </c:forEach>\n' +
                    '                                </select>\n' +
                    '                            </div>\n' +
                    '                            <div class="col-md-2"><button type="button" class="btn btn-danger remove_field">Xóa</button></div>\n' +
                    '                 </div>'); //add input box

                resetSelect2(x);
            }
        });

        $(wrapper).on("click", ".remove_field", function (e) { //user click on remove text
            e.preventDefault();
            $(this).parent('div').parent('div').remove();
            x--;
        });

        LoadScheduleList();

        // Show daterangepicker when click on icon
        $('.form-date-range i').click(function () {
            $(this).parent().find('input').click();
        });

        startDate = endDate = moment().format('DD/MM/YYYY');
        $('#scheduleDate').daterangepicker({
            startDate: moment(),
            endDate: moment(),
            minDate: moment(),
//            drops: "up",
            locale: {
                applyLabel: "Chọn",
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        }, function (start, end) {
            startDate = start.format('DD/MM/YYYY');
            endDate = end.format('DD/MM/YYYY');
            $('#startDate span').html(startDate + ' - ' + endDate);
        });

    });


    function LoadScheduleList() {
        startDate2 = endDate2 = moment().format('DD/MM/YYYY');
        $('#scheduleDate2').daterangepicker({
            autoUpdateInput: false,
            locale: {
                applyLabel: "Chọn",
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        });

        $('#scheduleDate2').on('apply.daterangepicker', function (ev, picker) {
            startDate = picker.startDate.format('DD/MM/YYYY');
            endDate = picker.endDate.format('DD/MM/YYYY');
            $(this).val(picker.startDate.format('DD/MM/YYYY') + ' - ' + picker.endDate.format('DD/MM/YYYY'));
        });

        $('#scheduleDate2').on('cancel.daterangepicker', function (ev, picker) {
            $(this).val('');
        });


        tblSchedule = $('#tbl-schedule').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/loadScheduleList/${employee.id}", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({
                    "name": "startDate",
                    "value": $('#scheduleDate2').data('daterangepicker').startDate.format('DD/MM/YYYY')
                }),
                    aoData.push({
                        "name": "endDate",
                        "value": $('#scheduleDate2').data('daterangepicker').endDate.format('DD/MM/YYYY')
                    })
            },
            <%--"sAjaxSource": "/loadScheduleList/${employee.id}",--%>
            "oLanguage": {
                "sSearchPlaceholder": "Nhập từ khóa",
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
                    "aTargets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9],
                    "bSortable": false,
                    "sClass": "text-center",
                },
                {
                    "aTargets": [0],
                    "bVisible": false,
                },
                {
                    "aTargets": [7],
                    "bVisible": false,
                },
                {
                    "aTargets": [8],
                    "bVisible": false,
                },
                {
                    "aTargets": [9],
                    "mRender": function (data, type, row) {
                        var isPast = row[8];
                        if (isPast === "true") {
                            return "<div></div>";
                        } else {
                            return "<a class='btn btn-success tbl-btn' onclick='EditSchedule(" + row[0] + ",\""
                                + row[1] + "\",\"" + row[2] + "\",\"" + row[3] + "\",\"" + row[4] + "\",\"" + row[5] + "\",\"" + row[6] + "\",\"" + row[7] + "\")'>" +
                                "<i class='glyphicon glyphicon-pencil'></i></a>";
                        }

                    }
                },

            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }


    $("#btnSubmit").on("click", function () {
        if (ValidateScheduleDetail($(this).data("type"))) {
            var successMsg = "";
            var url = "";
            if ($(this).data("type") == "create") {
                successMsg = "Tạo thành công";
                url = "/schedule/create";
            } else {
                successMsg = "Cập nhật thành công";
                url = "/schedule/edit";
            }

            var dayOfWeekArr = [];

            $("select[id*='dayOfWeek']").each(function (i, el) {
                dayOfWeekArr.push($(el).val());
            });

            var slots = [];

            $("select[id*='slot']").each(function (i, el) {
                slots.push($(el).val());
            });


            swal({
                title: 'Đang xử lý',
                html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                type: 'info',
                onOpen: function () {
                    swal.showLoading();
                    isRunning = true;
                    $.ajax({
                        type: "POST",
                        url: url,
                        data: {
                            scheduleId: $("#btnSubmit").attr("data-schedule-id"),
                            clazz: $("#class").val(),
                            subject: $("#subject").val(),
                            semester: $("#semester").val(),
                            startDate: $('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY'),
                            endDate: $('#scheduleDate').data('daterangepicker').endDate.format('DD/MM/YYYY'),
                            dayOfWeekList: JSON.stringify(dayOfWeekArr),
                            slots: JSON.stringify(slots),
                            room: $("#room").val(),
                            capacity: $("#capacity").val(),
                            lecture: $("#employeeName").val(),
                            changeRoom: $("#changeRoom").is(":checked"),
                            all: $("#all").is(":checked"),
                        },
                        success: function (result) {
                            isRunning = false;
                            if (result.success) {
                                swal({
                                    title: 'Thành công',
                                    text: successMsg,
                                    type: 'success'
                                }).then(function () {
                                    RefreshTable();
                                    $("#scheduleModal").modal('toggle');

                                });
                            }

                            if (result.warning) {
                                var el = document.createElement("h4");
                                el.innerText = result.message;
                                swal({
                                    title: 'Thành công một phần',
                                    html: "<h4>Xin thực hiện lại với các slot bị lỗi</h4>" + result.message,
                                    type: 'warning'
                                }).then(function () {
                                    RefreshTable();
                                    $("#scheduleModal").modal('toggle');

                                });
                            }

                            if (result.fail) {
                                swal('Đã xảy ra lỗi!', result.message, 'error');
                            }
                        }
                    });
                },
                allowOutsideClick: false
            });

        }
    });

    function CreateSchedule() {
        ClearModal();
        $("#btnSubmit").html("Tạo");
        $('#btnSubmit').data("type", "create");
        $("#btnSubmit").attr("data-schedule-id", 0);
        $("#subject").attr("disabled", false);
        $("#semester-container").show();
        $("#dayOfWeek-container").show();
        $("#room-container").hide();
        $("#changeRoom-container").hide();
        $("#all-container").hide();
        $("#class").attr("disabled", false);
        $("#dayOfWeek").attr("disabled", false);
        $("#scheduleDate").attr("disabled", false);
        $("#slot").attr("multiple", "multiple");
        resetSelect2(0);
        $(".add_field_button").show();
        $("#capacity-container").show();

        $("#scheduleModal").modal('toggle');
    }

    function EditSchedule(scheduleId, subCode, clazz, sDate, slot, room, lecture, capacity) {
        ClearModal();

        startDate = endDate = moment().format('DD/MM/YYYY');
        $('#scheduleDate').daterangepicker({
            startDate: moment(),
            endDate: moment(),
            minDate: moment(),
            singleDatePicker: true,
            locale: {
                applyLabel: "Chọn",
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        }, function (start, end) {
            startDate = start.format('DD/MM/YYYY');
            endDate = end.format('DD/MM/YYYY');
            $('#startDate span').html(startDate + ' - ' + endDate);
        });

        $("#btnSubmit").html("Cập nhật");
        $('#btnSubmit').data("type", "edit")
        $("#btnSubmit").attr("data-schedule-id", scheduleId);
        $("#subject").attr("disabled", true);
        $("#semester-container").hide();
        $("#dayOfWeek-container").hide();
        $("#room-container").show();
        $("#changeRoom-container").show();
        $("#all-container").show();
        $("#class").attr("disabled", true);
        $("#dayOfWeek").attr("disabled", true);
        $("#slot").removeAttr("multiple");
        resetSelect2(0);
        $(".add_field_button").hide();

        $("#subject").val(subCode).trigger("change");
        $("#semester").val('N/A').trigger("change");
        $("#class").val(clazz);
        $('#scheduleDate').data('daterangepicker').setStartDate(ToDate(sDate));
        $('#scheduleDate').data('daterangepicker').setEndDate(ToDate(sDate));
        $("#slot").val(slot).trigger("change");

        const weekDays = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
        var day = $('#scheduleDate').data('daterangepicker').endDate.format('E');
        $("#dayOfWeek").val(weekDays[day]).trigger("change");

        $("#room").val(room).trigger("change");
        $("#capacity").val(capacity).trigger("change");
        $("#capacity-container").hide();

        $("#lecture").val(lecture).trigger("change");

        $("#scheduleModal").modal('toggle');
    }

    function ValidateScheduleDetail(type) {
        var isError = false;

        if ($("#subject").val() === "" || $("#subject").val() === null) {
            alert("Môn học không được bỏ trống");
            isError = true;
        } else if ($("#class").val() === "") {
            alert("Tên lớp không được bỏ trống");
            isError = true;
        } else if (type === "create") {
            $("select[id*='dayOfWeek']").each(function (i, el) {
                if ($(el).val() === "" || $(el).val() === null) {
                    alert("Thứ trong tuần không được bỏ trống");
                    isError = true;
                }
            });

            $("select[id*='slot']").each(function (i, el) {
                if ($(el).val() === "" || $(el).val() === null) {
                    alert("Slot không được bỏ trống");
                    isError = true;
                }
            });

            if (isError) {
                return !isError;
            } else if ($("#capacity").val() === "" || $("#capacity").val() === null) {
                alert("Số lượng không được bỏ trống");
                isError = true;
            } else if ($("#lecture").val() === "" || $("#lecture").val() === null) {
                alert("Giảng viên không được bỏ trống");
                isError = true;
            }
        } else if (type !== "create") {
            if ($("#dayOfWeek").val() === "" || $("#dayOfWeek").val() === null) {
                alert("Thứ trong tuần không được bỏ trống");
                isError = true;
            } else if ($("#slot").val() === "" || $("#slot").val() === null) {
                alert("Slot không được bỏ trống");
                isError = true;
            } else if ($("#capacity").val() === "" || $("#capacity").val() === null) {
                alert("Số lượng không được bỏ trống");
                isError = true;
            } else if ($("#lecture").val() === "" || $("#lecture").val() === null) {
                alert("Giảng viên không được bỏ trống");
                isError = true;
            }
        }

        return !isError;
    }

    function ClearModal() {
        $('#subject').val('').trigger('change');
        $('#semester').val('N/A').trigger('change');
        $("#class").val("");
        $('#scheduleDate').data('daterangepicker').setStartDate(moment());
        $('#scheduleDate').data('daterangepicker').setEndDate(moment());
        $("#dayOfWeek").val('').trigger('change');
        $("#slot").val('').trigger('change');
        $("#lecture").val('').trigger('change');
        $('#room').val('').trigger('change');
        $('#capacity').val('').trigger('change');
        $('#changeRoom').prop('checked', false);
        $('#all').prop('checked', false);

        startDate = endDate = moment().format('DD/MM/YYYY');
        $('#scheduleDate').daterangepicker({
            startDate: moment(),
            endDate: moment(),
            minDate: moment(),
//            drops: "up",
            locale: {
                applyLabel: "Chọn",
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        }, function (start, end) {
            startDate = start.format('DD/MM/YYYY');
            endDate = end.format('DD/MM/YYYY');
            $('#startDate span').html(startDate + ' - ' + endDate);
        });

        $("div[class*='addMore']").each(function (i, el) {
            $(el).remove();
        });
        x = 0;
    }

    // function RefreshTable() {
    //     if (tblSchedule != null) {
    //         // tblSchedule._fnPageChange(0);
    //         tblSchedule._fnAjaxUpdate();
    //     }
    // }

    function ToDate(str) {
        var data = str.split("/");
        return new Date(data[2], data[1] - 1, data[0]);
    }

    function EditEmployee() {
        console.log($('#empCompetence').val());
        swal({
            title: 'Xác nhận cập nhật giảng viên?',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            $.ajax({
                type: "POST",
                url: "/employee/edit/${employee.id}",
                data: {
                    "position": $('#position').val(),
                    "emailPersonal": $('#emailPersonal').val(),
                    "emailFE": $('#emailFE').val(),
                    "emailEDU": $('#emailEDU').val(),
                    "phone": $('#phone').val(),
                    "address": $('#address').val(),
                    "contract": $('#contract').val(),
                    "code": $('#code').val(),
                    "empCompetence": JSON.stringify($('#empCompetence').val())
                },
                success: function (result) {
                    if (result.success) {
                        swal({
                            title: 'Thành công',
                            text: "Đã cập nhật giảng viên!",
                            type: 'success'
                        }).then(function () {
                            oldPosition = $('#position').val();
                            oldContract = $('#contract').val();
                            oldEmailFE = $('#emailFE').val();
                            oldEmailEDU = $('#emailEDU').val();
                            oldEmailPersonal = $('#emailPersonal').val();
                            oldPhone = $('#phone').val();
                            oldAddress = $('#address').val();
                            oldCode = $('#code').val();
                            oldCompetence = $('#empCompetence').val();
                            onCancel();
                        });
                    } else {
                        swal('', result.message, 'error');
                    }
                }
            })
            ;
        });
    }

    function onEdit() {
        $('#btnEdit').hide();
        $('#btnEditSubmit').show();
        $('#btnEditCancel').show();
        $('#position').prop("disabled", false);
        $('#emailPersonal').prop("disabled", false);
        $('#emailFE').prop("disabled", false);
        $('#emailEDU').prop("disabled", false);
        $('#phone').prop("disabled", false);
        $('#address').prop("disabled", false);
        $('#contract').prop("disabled", false);
        // $('#code').prop("disabled", false);
        $('#empCompetence').prop("disabled", false);

    }

    function onCancel() {
        $('#btnEdit').show();
        $('#btnEditSubmit').hide();
        $('#btnEditCancel').hide();
        $('#position').prop("disabled", true);
        $('#emailPersonal').prop("disabled", true);
        $('#emailFE').prop("disabled", true);
        $('#emailEDU').prop("disabled", true);
        $('#phone').prop("disabled", true);
        $('#address').prop("disabled", true);
        $('#contract').prop("disabled", true);
        $('#code').prop("disabled", true);
        $('#empCompetence').prop("disabled", true);

        $('#position').val(oldPosition);
        $('#emailPersonal').val(oldEmailPersonal);
        $('#emailFE').val(oldEmailFE);
        $('#emailEDU').val(oldEmailEDU);
        $('#phone').val(oldPhone);
        $('#address').val(oldAddress);
        $('#contract').val(oldContract);
        $('#code').val(oldCode);

        // var selectedValues = new Array();
        // selectedValues[0] = "ITE302";
        // selectedValues[1] = "SWP490";
        console.log(oldCompetence);

        $('#empCompetence').val(oldCompetence).trigger('change');
    }

    function RefreshTable() {
        if (tblSchedule != null) {
            tblSchedule._fnPageChange(0);
            tblSchedule._fnAjaxUpdate();
        }
        $('#removeFilterBtn').removeAttr('disabled');

    }

    function resetFilter() {
        $("#lecture").val('').trigger('change');
        $('#scheduleDate').data('daterangepicker').setStartDate(moment());
        $('#scheduleDate').data('daterangepicker').setEndDate(moment());
        $('#scheduleDate').val('');
        $("#department").val('').trigger('change');

        $('#removeFilterBtn').attr('disabled', 'disabled');

    }

</script>

