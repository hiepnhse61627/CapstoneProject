<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

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

    .table-condensed > tbody > tr > td {
        padding: 3px;
    }
</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách khóa học</h1>
                </div>
                <div class="col-md-3 text-right">
                    <input class="btn btn-primary" type="button" value="Tạo khóa học" onclick="CreateCourse()"/>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-course">
                        <thead>
                        <th>Lớp</th>
                        <th>Mã môn</th>
                        <th>Ngày bắt đầu</th>
                        <th>Ngày kết thúc</th>
                        <th>Chỉnh sửa</th>
                        <th>Xóa</th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>

    </div>
</section>

<div id="courseModal" class="modal fade" role="dialog">
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
                            <label for="class">Lớp:</label>
                            <input id="class" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="subject">Môn học:</label>
                            <%--<input id="subjectCode" name="subjectCode" type="text" class="form-control"/>--%>
                            <select id="subject" class="select form-control">
                                <option value="-1">- Chọn môn học -</option>
                                <c:forEach var="sub" items="${subjects}">
                                    <option value="${sub.id}">${sub.id} - ${sub.name} - ${sub.abbreviation}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group form-date-range">
                            <label for="courseDate">Thời gian:</label>
                            <input id="courseDate" type="text" class="form-control"/>
                            <i class="fa fa-calendar"></i>
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
    var tblCourse;
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
        LoadCourse();

        // Show daterangepicker when click on icon
        $('.form-date-range i').click(function () {
            $(this).parent().find('input').click();
        });

        startDate = endDate = moment().format('DD/MM/YYYY');
        $('#courseDate').daterangepicker({
            startDate: moment(),
            endDate: moment(),
//            drops: "up",
            locale: {
                format: 'DD/MM/YYYY'
            },
        }, function (start, end) {
            startDate = start.format('DD/MM/YYYY');
            endDate = end.format('DD/MM/YYYY');
            $('#startDate span').html(startDate + ' - ' + endDate);
        });

        $('#subject').select2({width: '100%'});

    });


    function LoadCourse() {
        tblCourse = $('#tbl-course').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/course/loadTable",
            "oLanguage": {
                "sSearchPlaceholder": "Lớp hoặc mã môn",
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
                    "aTargets": [4],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-success tbl-btn' onclick='EditCourse(" + data + ",\""
                            + row[0] + "\",\"" + row[1] + "\",\"" + row[2] + "\",\"" + row[3] + "\")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                    }
                },
                {
                    "aTargets": [5],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-danger tbl-btn' onclick='DeleteCourse(" + row[4] + ")'>" +
                            "<i class='glyphicon glyphicon-trash'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }

    $("#btnSubmit").on("click", function () {
        if (ValidateCourseDetail()) {
            var successMsg = "";
            var url = "";
            if ($(this).data("type") == "create") {
                successMsg = "Tạo thành công";
                url = "/course/create";
            } else {
                successMsg = "Cập nhật thành công";
                url = "/course/edit";
            }

            $.ajax({
                type: "POST",
                url: url,
                data: {
                    courseId: $("#btnSubmit").data("course-id"),
                    clazz: $("#class").val(),
                    subjectCode: $("#subject").val(),
                    sStartDate: startDate,
                    sEndDate: endDate,
                },
                success: function (result) {
                    if (result.success) {
                        RefreshTable();
                        swal(successMsg, '', 'success');
                        $("#courseModal").modal('toggle');
                    } else {
                        swal('Đã xảy ra lỗi!', result.message, 'error');
                    }
                }
            });
        }
    });

    function CreateCourse() {
        ClearModal();
        $("#btnSubmit").html("Tạo");
        $('#btnSubmit').data("type", "create");
        $("#btnSubmit").attr("data-course-id", 0);

        $("#courseModal").modal('toggle');
    }

    function EditCourse(courseId, clazz, subCode, sDate, eDate) {
        ClearModal();
        $("#btnSubmit").html("Cập nhật");
        $('#btnSubmit').data("type", "edit")
        $("#btnSubmit").attr("data-course-id", courseId);

        $("#class").val(clazz);
        $("#subject").val(subCode).trigger("change");
        $('#courseDate').data('daterangepicker').setStartDate(ToDate(sDate));
        $('#courseDate').data('daterangepicker').setEndDate(ToDate(eDate));

        $("#courseModal").modal('toggle');
    }

    function DeleteCourse(courseId) {
        swal({
            title: 'Bạn muốn xóa khóa học này?',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            $.ajax({
                type: "POST",
                url: "/course/delete",
                data: {
                    courseId: courseId,
                },
                success: function (result) {
                    if (result.success) {
                        swal('Xóa thành công!', '', 'success');
                        RefreshTable();
                    } else {
                        swal('Đã xảy ra lỗi!', result.message, 'error');
                    }
                }
            });
        });
    }

    function ValidateCourseDetail() {
        var isError = false;
        var sClass = $("#class").val().trim().replace(/\s+/g, " ");
        var subCode = $('#subject').val().trim().replace(/\s+/g, " ");

        if (sClass == "") {
            alert("Tên lớp không được bỏ trống!");
            isError = true;
        } else if (subCode == -1) {
            alert("Xin hãy chọn môn học!");
            isError = true;
        }

        return !isError;
    }

    function ClearModal() {
        $("#class").val("");
        $('#subject').val(-1).trigger('change');
        $('#courseDate').data('daterangepicker').setStartDate(moment());
        $('#courseDate').data('daterangepicker').setEndDate(moment());
    }

    function RefreshTable() {
        if (tblCourse != null) {
            tblCourse._fnPageChange(0);
            tblCourse._fnAjaxUpdate();
        }
    }

    function ToDate(str) {
        var data = str.split("/");
        return new Date(data[2], data[1] - 1, data[0]);
    }

</script>