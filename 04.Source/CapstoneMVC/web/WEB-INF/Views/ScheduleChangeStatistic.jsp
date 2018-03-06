<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
            <h1>Thống kê lịch dạy thay đổi </h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group form-date-range">
                <label for="scheduleDate">Ngày bắt đầu - kết thúc:</label>
                <input id="scheduleDate" type="text" class="form-control"/>
                <i class="fa fa-calendar"></i>
            </div>

            <div class="form-group">
                <label for="lecture">Giảng viên:</label>
                <select id="lecture" class="select lecture-select">
                    <c:forEach var="emp" items="${employees}">
                        <option value="${emp.id}">${emp.fullName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="RefreshTable()">Tìm kiếm</button>
                <button type="button" class="btn btn-primary" onclick="resetFilter()">Xóa bộ lọc</button>
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
                                <th>Ngày</th>
                                <th>Slot</th>
                                <th>Phòng</th>
                                <th>Giảng viên</th>
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


<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="subjectId"/>
    <input name="semesterId"/>
    <input name="sSearch"/>
</form>

<script>
    var table = null;

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
            placeholder: '- Chọn giáo viên -'
        });

        $('select').on('change', function (evt) {
            $('#select2-lecture-container').removeAttr('title');
        });

        $("#lecture").val('').trigger('change');

        // Show daterangepicker when click on icon
        $('.form-date-range i').click(function () {
            $(this).parent().find('input').click();
        });

        startDate = endDate = moment().format('DD/MM/YYYY');
        $('#scheduleDate').daterangepicker({
            autoUpdateInput: false,
            locale: {
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        });

        $('#scheduleDate').on('apply.daterangepicker', function(ev, picker) {
            startDate = picker.startDate.format('DD/MM/YYYY');
            endDate = picker.endDate.format('DD/MM/YYYY');
            $(this).val(picker.startDate.format('DD/MM/YYYY') + ' - ' + picker.endDate.format('DD/MM/YYYY'));
        });

        $('#scheduleDate').on('cancel.daterangepicker', function(ev, picker) {
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
                aoData.push({"name": "startDate", "value":  $('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY')}),
                    aoData.push({"name": "endDate", "value":  $('#scheduleDate').data('daterangepicker').endDate.format('DD/MM/YYYY')}),
                    aoData.push({"name": "lecture", "value": $('#lecture').val()})
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
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }

    function resetFilter(){
        $("#lecture").val('').trigger('change');
        $('#scheduleDate').data('daterangepicker').setStartDate(moment());
        $('#scheduleDate').data('daterangepicker').setEndDate(moment());
    }
</script>