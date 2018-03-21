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
                    <h1>GV dạy thế theo môn</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group form-date-range">
                <label for="scheduleDate">Ngày dạy:</label>
                <input id="scheduleDate" type="text" class="form-control"/>
                <i class="fa fa-calendar"></i>
            </div>

            <div class="form-group">
                <label for="subject2">Môn học:</label>
                <select id="subject2" class="select department2-select">
                    <c:forEach var="aSubject" items="${subjects}">
                        <option value="${aSubject.id}">${aSubject.id} - ${aSubject.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="aTime">Slot:</label>
                <select id="aTime" class="select aTime-select">
                    <c:forEach var="aSlot" items="${slots}">
                        <option value="${aSlot.slotName}">${aSlot.slotName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="RefreshTable()" id="searchBtn">Tìm kiếm</button>
                <button type="button" class="btn btn-primary" onclick="resetFilter()" id="removeFilterBtn">Xóa bộ lọc</button>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên</th>
                                <th>SĐT</th>
                                <th>Email</th>
                                <th>Gửi mail</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


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

        $('#subject2').select2({
            placeholder: '- Chọn bộ môn -'
        });


        $('#aTime').select2({
            placeholder: '- Chọn slot -'
        });

        $('select').on('change', function (evt) {
            $('#select2-subject2-container').removeAttr('title');
            $('#select2-aTime-container').removeAttr('title');
        });

        $("#subject2").val('').trigger('change');

        $("#aTime").val('').trigger('change');

        // Show daterangepicker when click on icon
        $('.form-date-range i').click(function () {
            $(this).parent().find('input').click();
        });

        startDate = endDate ="";

        $('#scheduleDate').daterangepicker({
            autoUpdateInput: false,
            singleDatePicker: true,
            locale: {
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        });

        $('#scheduleDate').on('apply.daterangepicker', function (ev, picker) {
            startDate2 = picker.startDate.format('DD/MM/YYYY');
            endDate2 = picker.endDate.format('DD/MM/YYYY');
            $(this).val(picker.startDate.format('DD/MM/YYYY'));
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
            "sAjaxSource": "/requestLecture/get", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "startDate", "value":  $('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY')}),
                    aoData.push({"name": "endDate", "value":  $('#scheduleDate').data('daterangepicker').endDate.format('DD/MM/YYYY')}),
                    aoData.push({"name": "subject", "value": $('#subject2').val()}),
                    aoData.push({"name": "slot", "value": $('#aTime').val()})
            },
            "oLanguage": {
                "sSearchPlaceholder": "Nhập từ khóa",
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
                    "aTargets": [0, 1, 2, 3, 4],
                    "sClass": "text-center",
                    "bSortable": false
                },
                {
                    "aTargets": [0],
                    "bVisible": false,
                },
                {
                    "aTargets": [4],
                    "mRender": function (data, type, row) {
                        var isPast = row[8];
                        if (isPast === "true") {
                            return "<div></div>";
                        } else {
                            return "<a class='btn btn-success tbl-btn' onclick='EditSchedule(" + row[0] + ",\""
                                + row[1] + "\",\"" + row[2] + "\",\"" + row[3] + "\",\"" + row[4] +"\")'>" +
                                "<i class='glyphicon glyphicon-mail'></i></a>";
                        }

                    }
                },
            ],
            "bAutoWidth": false
        }).fnSetFilteringDelay(1000);
    });


    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
        $('#removeFilterBtn').removeAttr('disabled');

    }

    function resetFilter(){
        $("#lecture").val('').trigger('change');
        $('#scheduleDate').data('daterangepicker').setStartDate(moment());
        $('#scheduleDate').data('daterangepicker').setEndDate(moment());
        $('#scheduleDate').val('');
        $("#aTime").val('').trigger('change');
        $("#subject2").val('').trigger('change');

        $('#removeFilterBtn').attr('disabled','disabled');

    }


</script>