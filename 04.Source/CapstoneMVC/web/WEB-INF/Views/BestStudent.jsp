<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
                    <h1>Danh sách sinh viên giỏi nhất môn</h1>
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
                    <div class="my-content">
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Chọn kỳ:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="cb-semester" class="select">
                                    <option value="0">All</option>
                                    <c:forEach var="s" items="${semesterList}">
                                        <option value="${s.id}">${s.semester}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="tbl-student">
                            <thead>
                            <tr>
                                <%--<th>MSSV</th>--%>
                                <%--<th>Tên sinh viên</th>--%>
                                <%--<th>Điểm trung bình</th>--%>
                                <th>MSSV</th>
                                <th>Tên sinh viên</th>
                                <th>Học kỳ</th>
                                <th>Khóa</th>
                                <th>Kỳ</th>
                                <th>Mã môn</th>
                                <th>Tên môn</th>
                                <th>Điểm trung bình</th>
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
    <input name="semesterId" value="0"/>
    <input name="sSearch" value=""/>
</form>

<script>
    var tblStudent = null;

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
        $(".select").select2();
        $("#cb-semester").on("change", function () {
            RefreshTable();
        });

        LoadGoodStudent();
    });

    function LoadGoodStudent() {
        tblStudent = $('#tbl-student').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/bestStudent/getStudentList",
            "fnServerParams": function (aoData) {
                aoData.push({
                    "name": "semesterId",
                    "value": $('#cb-semester').val() != null ? $('#cb-semester').val() : 0
                })
            },
            "oLanguage": {
                "sSearchPlaceholder": "Tìm theo MSSV, TênSV, Khóa, Kỳ, Mã môn, Tên môn",
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
                    "aTargets": [0, 1, 2, 3, 4, 5, 6, 7],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function RefreshTable() {
        if (tblStudent != null) {
            tblStudent._fnPageChange(0);
            tblStudent._fnAjaxUpdate();
        }
    }

    function ExportExcel() {
        $("input[name='objectType']").val(23);
        $("input[name='semesterId']").val($('#cb-semester').val() != null ? $('#cb-semester').val() : 0);
        $("#export-excel").submit();
    }

</script>