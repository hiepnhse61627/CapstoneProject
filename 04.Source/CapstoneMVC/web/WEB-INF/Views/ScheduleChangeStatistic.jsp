<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    .dataTables_filter input {
        width: 250px;
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

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-6 title">
                    <h1>Thống kê lịch dạy thay đổi</h1>
                </div>
                <div class="col-md-2 text-right">
                    <button type="button" class="btn btn-primary btn-with-icon" onclick="SyncChangedSchedule()">
                        <i class="glyphicon glyphicon-retweet"></i>
                        <div>Đồng bộ lịch học thay đổi</div>
                    </button>
                </div>
                <div class="col-md-3 text-right">
                    <button type="button" class="btn btn-success btn-with-icon" onclick="ExportExcel()">
                        <i class="glyphicon glyphicon-open"></i>
                        <div>XUẤT DỮ LIỆU</div>
                    </button>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-6">
                    <div class="form-group form-date-range">
                        <label for="scheduleDate">Ngày bắt đầu - kết thúc:</label>
                        <input id="scheduleDate" type="text" class="form-control"/>
                        <i class="fa fa-calendar"></i>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group">
                        <label for="lecture">Giảng viên yêu cầu đổi lịch:</label>
                        <select id="lecture" class="select lecture-select">
                            <option value="-1">Tất cả</option>
                            <c:forEach var="emp" items="${employees}">
                                <option value="${emp.id}">${fn:substring(emp.emailEDU, 0, fn:indexOf(emp.emailEDU, "@"))}
                                    - ${emp.fullName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>


            <div class="row" style="display: flex; position: relative;">
                <div class="col-md-6">
                    <div class="form-group">
                        <label for="department">Bộ môn:</label>
                        <select id="department" class="select department-select">
                            <option value="-1">Tất cả</option>
                            <c:forEach var="department" items="${departments}">
                                <option value="${department.deptId}">${department.deptName}</option>
                            </c:forEach>
                        </select>
                    </div>
                </div>

                <div class="col-md-6">
                    <div class="form-group" style="width: 100%; bottom: 0; position: absolute;">
                        <button type="button" class="btn btn-success" onclick="RefreshTable()" id="searchBtn">Tìm kiếm
                        </button>
                        <button type="button" class="btn btn-primary" onclick="resetFilter()" id="removeFilterBtn">Xóa
                            bộ lọc
                        </button>
                    </div>
                </div>
            </div>


            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Mã môn</th>
                                <th>Lớp</th>
                                <th>Ngày thực dạy</th>
                                <th>Slot</th>
                                <th>Phòng</th>
                                <th>GV đứng lớp</th>
                                <th>Slot ban đầu</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


<form id="export-excel" action="/exportExcelWithoutCallable" hidden>
    <input name="objectType"/>
    <input name="lecture"/>
    <input name="department"/>
    <input name="startDate"/>
    <input name="endDate"/>
    <input name="dateTextbox"/>
</form>

<script>
    var table = null;
    var startDate;
    var endDate;

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

        $('#lecture').select2({
            placeholder: '- Chọn giảng viên -'
        });

        $('#department').select2({
            placeholder: '- Chọn bộ môn -'
        });

        $('select').on('change', function (evt) {
            $('#select2-lecture-container').removeAttr('title');
            $('#select2-department-container').removeAttr('title');
        });

        $("#lecture").val('').trigger('change');

        $("#department").val('').trigger('change');

        // Show daterangepicker when click on icon
        $('.form-date-range i').click(function () {
            $(this).parent().find('input').click();
        });

        startDate = endDate = "";
        $('#scheduleDate').daterangepicker({
            autoUpdateInput: false,
            locale: {
                applyLabel: "Chọn",
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        });

        $('#scheduleDate').on('apply.daterangepicker', function (ev, picker) {
            startDate = picker.startDate.format('DD/MM/YYYY');
            endDate = picker.endDate.format('DD/MM/YYYY');
            $(this).val(picker.startDate.format('DD/MM/YYYY') + ' - ' + picker.endDate.format('DD/MM/YYYY'));
        });

        $('#scheduleDate').on('cancel.daterangepicker', function (ev, picker) {
            $(this).val('');
        });

        table = $('#table').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/scheduleChangeStatistic/get", // url getData.php etc
            "fnServerParams": function (aoData) {
                // aoData.push({"name": "startDate", "value":  $('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY')}),
                //     aoData.push({"name": "endDate", "value":  $('#scheduleDate').data('daterangepicker').endDate.format('DD/MM/YYYY')}),
                aoData.push({"name": "startDate", "value": startDate}),
                    aoData.push({"name": "endDate", "value": endDate}),
                    aoData.push({"name": "department", "value": $('#department').val()}),
                    aoData.push({"name": "lecture", "value": $('#lecture').val()}),
                    aoData.push({"name": "dateTextbox", "value": $('#scheduleDate').val()})
            },
            "oLanguage": {
                "sSearchPlaceholder": "Môn học, Lớp, Ngày...",
                "sSearch": "Tìm kiếm:",
                "sZeroRecords": "Không có dữ liệu phù hợp",
                "sInfo": 'Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng',
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
                    "sClass": "text-center",
                    "bSortable": false
                },
                {
                    "aTargets": [0],
                    "bVisible": false,
                },
                // {
                //     "aTargets": [1],
                //     "mRender": function (data, type, row) {
                //         return "<a onclick='GetAllStudentMarks(" + row[5] + ")'>" + data + "</a>";
                //     }
                // }
            ],
            "bAutoWidth": false
        }).fnSetFilteringDelay(1000);
    });


    function RefreshTable() {

        if ($('#scheduleDate').val() !== "") {
            if (table != null) {
                table._fnPageChange(0);
                table._fnAjaxUpdate();
            }
            $('#removeFilterBtn').removeAttr('disabled');
        } else {
            alert("Xin chọn khoảng thời gian muốn tìm kiếm.");
        }

    }

    function resetFilter() {
        $("#lecture").val('').trigger('change');
        $('#scheduleDate').data('daterangepicker').setStartDate(moment());
        $('#scheduleDate').data('daterangepicker').setEndDate(moment());
        $('#scheduleDate').val('');
        $("#department").val('').trigger('change');

        $('#removeFilterBtn').attr('disabled', 'disabled');

    }

    function SyncChangedSchedule() {

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/syncFAPChangedSchedule",
                    // url: "/countAttendanceOfClass",
                    processData: false,
                    contentType: false,
                    success: function (result) {
                        if (result.success) {
                            swal({
                                title: 'Thành công',
                                text: "Đã đồng bộ lịch thay đổi!",
                                type: 'success'
                            }).then(function () {
                                location.reload();
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

    function ExportExcel() {
        $("input[name='objectType']").val(24);
        $("input[name='lecture']").val($('#lecture').val());
        $("input[name='department']").val($('#department').val());
        $("input[name='startDate']").val($('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY'));
        $("input[name='endDate']").val($('#scheduleDate').data('daterangepicker').endDate.format('DD/MM/YYYY'));
        $("input[name='dateTextbox']").val($('#scheduleDate').val());


        $("#export-excel").submit();
        Call();
    }


    function Call() {
        swal({
            title: 'Đang xử lý',
            html: '<div class="form-group">Tiến trình có thể kéo dài vài phút</div>',
            type: 'info',
            onOpen: function () {
                swal.showLoading();
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
                    swal('', 'Download file thành công!', 'success').then(function () {
                        location.reload();
                    });
                }
            }
        });
    }
</script>