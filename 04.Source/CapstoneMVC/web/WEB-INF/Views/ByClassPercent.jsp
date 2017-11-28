<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .dataTables_filter input {
        width: 250px;
    }
</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Tỉ lệ môn đạt theo học kỳ</h1>
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
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Môn học</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="subject" class="select form-control">
                                <option value="0">All</option>
                                <c:forEach var="s" items="${subjects}">
                                    <option value="${s.id}">${s.id} - ${s.abbreviation} - ${s.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Học Kỳ</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="semester" class="select form-control">
                                <option value="0">All</option>
                                <c:forEach var="s" items="${semesters}">
                                    <option value="${s.id}">${s.semester}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="RefreshTable()">Tìm kiếm</button>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <table id="table">
                        <thead>
                        <tr>
                            <th>Môn</th>
                            <th>Lớp</th>
                            <th>Tỉ lệ</th>
                        </tr>
                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <form id="export-excel" action="/exportExcel" hidden>
        <input name="objectType"/>
        <input name="subject"/>
        <input name="course"/>
    </form>
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

    function ExportExcel() {
        $("input[name='objectType']").val(8);
        $("input[name='subject']").val($('#subject').val());
        $("input[name='semester']").val($('#semester').val());
        $("#export-excel").submit();
    }

    $(document).ready(function () {
        $('.select').select2();

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/percent/query", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "subject", "value": $('#subject').val()}),
                    aoData.push({"name": "semester", "value": $('#semester').val()})
            },
            "oLanguage": {
                "sSearchPlaceholder": "Tìm theo Môn, Lớp",
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
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    });

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }
</script>
