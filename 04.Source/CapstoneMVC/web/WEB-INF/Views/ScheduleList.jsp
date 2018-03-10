<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
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
                    <h1>Danh sách lịch học</h1>
                </div>
                <div class="col-md-3 text-right">
                    <button type="button" class="btn btn-success btn-with-icon" onclick="CreateSchedule()">
                        <i class="glyphicon glyphicon-plus"></i>
                        <div>Thêm lịch học</div>
                    </button>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
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
</section>

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
</form>

<div id="scheduleModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Tiêu đề</h4>
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
                                Yêu cầu chuyển phòng
                            </label>
                        </div>

                        <div class="form-group">
                            <label for="lecture">Giáo viên:</label>
                            <select id="lecture" class="select lecture-select">
                                <c:forEach var="emp" items="${employees}">
                                    <option value="${emp.fullName}">${emp.fullName}</option>
                                </c:forEach>
                            </select>
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
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="btnSubmit" type="button" class="btn btn-primary">Tạo</button>
            </div>
        </div>

    </div>
</div>

<script>
    var tblSchedule;
    var startDate;
    var endDate;
    var x = 0; //initlal text box count

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
        $("#room").attr("disabled", true);

        $('#lecture').select2({
            placeholder: '- Chọn giáo viên -'
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
                format: 'DD/MM/YYYY'
            }
        }, function (start, end) {
            startDate = start.format('DD/MM/YYYY');
            endDate = end.format('DD/MM/YYYY');
            $('#startDate span').html(startDate + ' - ' + endDate);
        });

    });

    function ExportExcel() {
        $("input[name='objectType']").val(7);
        $("#export-excel").submit();
    }

    function LoadScheduleList() {
        tblSchedule = $('#tbl-schedule').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/loadScheduleList",
            "oLanguage": {
                "sSearchPlaceholder": "Tên lớp, Ngày, Mã môn",
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
                            lecture: $("#lecture").val(),
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
                alert("Giáo viên không được bỏ trống");
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
                alert("Giáo viên không được bỏ trống");
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

    function RefreshTable() {
        if (tblSchedule != null) {
            // tblSchedule._fnPageChange(0);
            tblSchedule._fnAjaxUpdate();
        }
    }

    function ToDate(str) {
        var data = str.split("/");
        return new Date(data[2], data[1] - 1, data[0]);
    }


</script>