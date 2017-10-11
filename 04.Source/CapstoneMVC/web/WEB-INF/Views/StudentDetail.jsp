<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .my-tbl-wrapper {
        width: 100%;
        padding: 10px;
    }

    .overflow {
        overflow: auto;
    }
</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Thông tin sinh viên</h1>
            <hr>
        </div>
        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="my-content">
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-5">Chọn sinh viên:</label>
                            </div>
                            <div class="right-content">
                                <select id="select" class="select">
                                    <c:forEach var="stu" items="${students}">
                                        <option value="${stu.id}">${stu.rollNumber} - ${stu.fullName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <%--<div class="form-group">--%>
                        <%--<div class="col-md-12">--%>
                        <%--<button type="button" class="btn btn-primary" onclick="RefreshTable()">Xem</button>--%>
                        <%--</div>--%>
                        <%--</div>--%>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-6 p-l-5 p-r-5 m-b-10">
                        <div class="my-tbl-wrapper bg-gray-light overflow">
                            <div class="title text-center">
                                <h4>Danh sách môn còn nợ</h4>
                            </div>
                            <div class="my-content">
                                <table id="table">
                                <thead>
                                <tr>
                                    <th>Mã môn</th>
                                    <th>Lớp</th>
                                    <th>Khóa</th>
                                    <th>Điểm</th>
                                    <th>Trạng thái</th>
                                </tr>
                                </thead>
                                <tbody></tbody>
                            </table>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6 p-l-5 p-r-5">
                        <div class="my-tbl-wrapper bg-gray-light overflow">
                            <div class="title text-center">
                                <h4>Danh sách môn học tiếp theo</h4>
                            </div>
                            <div class="my-content">
                                <table id="nextCourseTable">
                                    <thead>
                                    <tr>
                                        <th>Mã môn</th>
                                        <th>Tên môn</th>
                                    </tr>
                                    </thead>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

</section>


<script>
    var table = null;
    var nextCourseTable = null;

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
        $('#select').on("change", function () {
            RefreshTable();
        });

        $('.select').select2();

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentDetail", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#select').val()})
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
                    "aTargets": [0, 1, 2, 3, 4],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);

        nextCourseTable = $('#nextCourseTable').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentNextCourse", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#select').val()})
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
                    "aTargets": [0, 1],
                    "mRender": function (data, type, row) {
                        if (row[3] == '1') {
                            return "<span style='text-decoration: line-through'>" + data + "</span>";
                        } else {
                            return data;
                        }
                    }
                },
                {
                  "aTargets": [0],
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

        if (nextCourseTable != null) {
            nextCourseTable._fnPageChange(0);
            nextCourseTable._fnAjaxUpdate();
        }
    }
</script>