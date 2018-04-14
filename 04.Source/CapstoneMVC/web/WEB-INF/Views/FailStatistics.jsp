<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Thống kê học lại</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Học kỳ</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="semester" class="select form-control">
                                <c:forEach var="semester" items="${semesters}">
                                    <option value="${semester.semester}">${semester.semester}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-default" onclick="RefreshTable()">Tìm kiếm</button>
                <button type="button" class="btn btn-success" onclick="ExportExcel()">Xuất dữ liệu</button>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                                <tr>
                                    <th>Số nợ đầu kỳ</th>
                                    <th>Số nợ đầu kỳ đã trả được trong kỳ</th>
                                    <th>Số nợ phát sinh trong kỳ</th>
                                    <th>Số lượt trả nợ trong kỳ</th>
                                    <th>Số lượt đăng ký học lại trong kỳ</th>
                                    <th>Số nợ cuối kỳ</th>
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
    <input name="semesterId"/>
</form>

<script>
    var table = null;

    $(document).ready(function(){
        CreateEmptyDataTable('#table');
    });

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

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }else {
            //destroy empty table
            $('#table').dataTable().fnDestroy();
            CreateMainTable();
        }
    }

//    $(document).ready(function () {
//        $('.select').select2();

    function CreateMainTable() {
        if (table != null) {
            table.fnDestroy();
        }

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/failStatistics/details", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "semester", "value": $('#semester').val()})
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
                },
                {
                    "aTargets": [0, 1, 2, 3, 4, 5],
                    "sClass": "text-center",
                }
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
//    });
    }
    function ExportExcel() {
        $("input[name='objectType']").val(10);
        $("input[name='semesterId']").val($('#semester').val());

        $("#export-excel").submit();
    }
</script>