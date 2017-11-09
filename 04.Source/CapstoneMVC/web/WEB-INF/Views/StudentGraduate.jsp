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
                    <button type="button" class="btn btn-warning" onclick="ExportExcelPDF()">Xuất dữ liệu (PDF)
                    <%--<div class="export-content">--%>
                        <%--<div class="btn btn-success btn-with-icon btn-excel">--%>
                            <%--<i class="glyphicon glyphicon-open"></i>--%>
                            <%--<div>XUẤT DỮ LIỆU</div>--%>
                        <%--</div>--%>
                        <%--<div class="excel-modal">--%>
                            <%--<div class="arrow-up"></div>--%>
                            <%--<div class="my-content-wrap">--%>
                                <%--<div class="content">--%>
                                    <%--<div class="item">--%>
                                        <%--<i class="fa fa-angle-left"></i>--%>
                                        <%--<span>In tất cả sinh viên</span>--%>
                                        <%--<div class="sub-item-wrapper">--%>
                                            <%--&lt;%&ndash;<div class="item"><span>Word</span></div>&ndash;%&gt;--%>
                                            <%--<div class="item" onclick="Test(1)"><span>PDF</span></div>--%>
                                            <%--<div class="item" onclick="Test(2)"><span>Excel</span></div>--%>
                                        <%--</div>--%>
                                    <%--</div>--%>
                                    <%--<div class="item">--%>
                                        <%--<i class="fa fa-angle-left"></i>--%>
                                        <%--<span>In một sinh viên</span>--%>
                                        <%--<div class="sub-item-wrapper">--%>
                                            <%--&lt;%&ndash;<div class="item"><span>Word</span></div>&ndash;%&gt;--%>
                                            <%--<div class="item" onclick="Test(3)"><span>PDF</span></div>--%>
                                            <%--<div class="item" onclick="Test(4)"><span>Excel</span></div>--%>
                                        <%--</div>--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        <%--</div>--%>
                    <%--</div>--%>
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
                                    <option value="0" selected>Tất cả</option>
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
                                    <option value="0" selected>Tất cả</option>
                                    <c:forEach var="semester" items="${semesterList}">
                                        <option value="${semester.id}">${semester.semester}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <%--<div class="col-md-12">--%>
                        <%--&lt;%&ndash;<button class="btn btn-success" onclick="RefreshTable()">Tìm kiếm</button>&ndash;%&gt;--%>
                        <%--<button type="button" class="btn btn-primary" onclick="ExportExcel()">Xuất dữ liệu</button>--%>
                        <%--<button type="button" class="btn btn-warning" onclick="ExportExcelPDF()">Xuất dữ liệu (PDF)--%>
                        <%--</button>--%>
                    <%--</div>--%>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên</th>
                                <th>Tín chỉ tích lũy</th>
                                <th>Tín chỉ dư</th>
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

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="credit"/>
    <input name="sCredit"/>
    <input name="programId"/>
    <input name="semesterId"/>
    <input name="sSearch"/>
</form>

<script>
    var table = null;
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

    $(document).ready(function () {
        $('.select').select2();

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

        $('#program').on('change', function() {
            RefreshTable();
        });

        $('#semester').on('change', function() {
            RefreshTable();
        });

        $('#type').on('change', function() {
            RefreshTable();
        });

        table = $('#table').dataTable({
            "bServerSide": true,
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
                    aoData.push({"name": "type", "value": $("#type").val()})
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
                    "aTargets": [0, 1, 2, 3],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    });

    function ExportExcel() {
        $("input[name='objectType']").val(4);
        $("input[name='credit']").val($('#credit').val());
        $("input[name='sCredit']").val($('#sCredit').val());
        $("input[name='programId']").val($('#program').val());
        $("input[name='semesterId']").val($('#semester').val());
        $("input[name='sSearch']").val(table.api().context[0].oPreviousSearch.sSearch);

        $("#export-excel").submit();
    }

    function ExportExcelPDF() {
        $("input[name='objectType']").val(5);
        $("input[name='credit']").val($('#credit').val());
        $("input[name='sCredit']").val($('#sCredit').val());
        $("input[name='programId']").val($('#program').val());
        $("input[name='semesterId']").val($('#semester').val());
        $("input[name='sSearch']").val(table.api().context[0].oPreviousSearch.sSearch);

        $("#export-excel").submit();
    }

    function RefreshTable() {
        table._fnPageChange(0);
        table._fnAjaxUpdate();
    }

    function Test(param) {
        alert("yasss" + param);
    }
</script>
