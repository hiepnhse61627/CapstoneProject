<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Quản lý điểm</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="my-content">
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Chọn sinh viên:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="cb-student" class="select">
                                    <option value="0">All</option>
                                    <c:forEach var="stu" items="${students}">
                                        <option value="${stu.rollNumber}">${stu.rollNumber} - ${stu.fullName}</option>
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
                        <table id="tbl-mark">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên sinh viên</th>
                                <th>Mã môn</th>
                                <th>Tên môn</th>
                                <th>Học kỳ</th>
                                <th>Điểm</th>
                                <th>Trạng thái</th>
                                <th>Chỉnh sửa</th>
                                <th>Xóa</th>
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


<div id="mark-detail" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 id="modal-header" class="modal-title">ABC (MSSV)</h4>
            </div>
            <div class="modal-body">
                <div class="col-md-12">
                    <div class="form-group">
                        <label for="subjectCode">Mã môn:</label>
                        <input disabled id="subjectCode" type="text" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label for="subjectName">Tên môn:</label>
                        <input disabled id="subjectName" type="text" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label for="semester">Học kỳ:</label>
                        <input disabled id="semester" type="text" class="form-control"/>
                    </div>
                    <div class="form-group">
                        <label for="mark">Điểm:</label>
                        <input id="mark" type="text" class="form-control" maxlength="5"/>
                    </div>
                    <div class="form-group">
                        <label for="status">Trạng thái:</label>
                        <select id="status" class="form-control select">
                            <option value="Passed">Passed</option>
                            <option value="Fail">Fail</option>
                            <option value="">NotStart</option>
                            <option value="Studying">Studying</option>
                            <option value="IsAttendanceFail">IsAttendanceFail</option>
                            <option value="IsSuspended">IsSuspended</option>
                            <option value="IsExempt">IsExempt</option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="btnEdit" data-id="" type="button" class="btn btn-primary">Cập nhật</button>
            </div>
        </div>

    </div>
</div>

<script>
    $(document).ready(function() {
        CreateSelect();
        LoadMarkTable();

        $('#cb-student').on("change", function() {
           RefreshTable();
            if ($('#cb-student').val() == "0") {
                ShowTableColumn("#tbl-mark", 0);
                ShowTableColumn("#tbl-mark", 1);
            } else {
                HideTableColumn("#tbl-mark", 0);
                HideTableColumn("#tbl-mark", 1);
            }
        });
    });

    $('#mark').on("input", function () {
        // Accept only float number
        if (!this.value.match(/^-?\d{1,2}\.?(\.\d)?$/)) {
            this.value = this.value.substring(0, this.value.length - 1);
        }
    });

    $('#btnEdit').on("click", function () {
        if ($('#mark').val() == '') {
            swal('Điểm không được bỏ trống', '', 'warning');
            return;
        } else {
            var mark = parseFloat($('#mark').val())
            if (mark < 0 || mark > 10) {
                swal('Điểm phải nằm trong khoảng từ 0 đến 10', '', 'warning');
                return;
            }
        }

        swal({
            title: 'Bấm tiếp tục để cập nhật điểm',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            $.ajax({
                type: "POST",
                url: "/markPage/edit",
                data: {
                    markId: $("#btnEdit").data("id"),
                    mark: $('#mark').val(),
                    status: $('#status').val(),
                },
                success: function (result) {
                    if (result.success) {
                        RefreshTable();
                        swal('Cập nhật thành công!', '', 'success');
                        $("#mark-detail").modal('toggle');
                    } else {
                        swal('Đã xảy ra lỗi, vui lòng thử lại', '', 'error');
                    }
                }
            });
        });


    });

    var tblMark = null;

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

    function CreateSelect() {
        $('#cb-student').select2({
            width: 'resolve',
            minimumInputLength: 2,
            ajax: {
                url: '/getStudentList',
                data: function (params) {
                    var queryParameters = {
                        searchValue: params.term
                    }
                    return queryParameters;
                },
                processResults: function (result) {
                    if (result.success) {
                        return {
                            results: $.map(result.items, function (item) {
                                return {
                                    id: item.value,
                                    text: item.text,
                                }
                            })
                        };
                    } else {
                        swal('', 'Có lỗi xảy ra, vui lòng thử lại', 'warning');
                    }
                }
            }
        });
    }

    function LoadMarkTable() {
        tblMark = $('#tbl-mark').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/markPage/getMarkList", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "studentId", "value": $('#cb-student').val()})
            },
            "oLanguage": {
                "sSearchPlaceholder": "Mã môn, Tên môn, Học kỳ, Trạng thái",
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
                    "aTargets": [0, 1, 2, 3, 4, 5, 6, 7, 8],
                    "sClass": "text-center",
                },
                {
                    "aTargets": [7],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-success tbl-btn' onclick='ShowEditModal(\""+ row +"\")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                    }
                },
                {
                    "aTargets": [8],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-danger tbl-btn' onclick='DeleteMark(\""+ row[7] +"\")'>" +
                            "<i class='glyphicon glyphicon-trash'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
        if ($('#cb-student').val() == "0") {
            ShowTableColumn("#tbl-mark", 0);
            ShowTableColumn("#tbl-mark", 1);
        } else {
            HideTableColumn("#tbl-mark", 0);
            HideTableColumn("#tbl-mark", 1);
        }
    }

    function ShowEditModal(row) {
        ClearModal();
        row = row.split(",");

        $('#modal-header').html(row[1] + " - " + row[0]);
        $('#subjectCode').val(row[2]);
        $('#subjectName').val(row[3]);
        $('#semester').val(row[4]);
        $('#mark').val(row[5]);
        $('#status').val(row[6]);
        $("#btnEdit").attr("data-id", row[7]);

        $('#mark-detail').modal('toggle');
    }

    function DeleteMark(markId) {
        swal({
            title: 'Bạn muốn xóa điểm này?',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            $.ajax({
                type: "POST",
                url: "/markPage/delete",
                data: {
                    markId: markId,
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

    function ClearModal() {
        $('#modal-header').html("");
        $('#subjectCode').val("");
        $('#subjectName').val("");
        $('#semester').val("");
        $('#mark').val("");
        $('#status').val("");
    }

    function RefreshTable() {
        if (tblMark != null) {
            tblMark._fnPageChange(0);
            tblMark._fnAjaxUpdate();
        }
    }

</script>
