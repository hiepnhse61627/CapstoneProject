<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách giảng viên</h1>
                </div>
                <div class="col-md-3 text-right">
                    <%--<button type="button" class="btn btn-success btn-with-icon" onclick="ExportExcel()">--%>
                        <%--<i class="glyphicon glyphicon-open"></i>--%>
                        <%--<div>XUẤT DỮ LIỆU</div>--%>
                    <%--</button>--%>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-employee">
                        <thead>
                        <th>Mã GV</th>
                        <th>Tên giảng viên</th>
                        <th>Ngày sinh</th>
                        <th>Chức vụ</th>
                        <th>Điện thoại</th>
                        <th>Email FE</th>
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


<script>
    var tblStudent;

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


    $(document).ready(function () {
        LoadEmployeeList();
    });

    function ExportExcel() {
        $("input[name='objectType']").val(7);
        $("#export-excel").submit();
    }

    function LoadEmployeeList() {
        tblStudent = $('#tbl-employee').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/loadEmployeeList",
            "oLanguage": {
                "sSearchPlaceholder": "Tên hoặc Mã GV",
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
                    "sClass": "text-center",
                },
                {
                    "aTargets": [6],
                    "mRender": function (data, type, row) {
                        var href = "/employeeList/" + data;
                        return "<a href='" + href + "' class='btn btn-success tbl-btn'>" +
                            "<i class='fa fa-eye'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }



</script>