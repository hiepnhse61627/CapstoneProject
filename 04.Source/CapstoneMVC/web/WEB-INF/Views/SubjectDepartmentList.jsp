<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách môn thuộc bộ môn </h1>
                </div>
                <%--<div class="col-md-3 text-right">--%>
                    <%--<button type="button" class="btn btn-success btn-with-icon" onclick="CreateModal()">--%>
                        <%--<i class="glyphicon glyphicon-plus"></i>--%>
                        <%--<div>Thêm môn - bộ môn</div>--%>
                    <%--</button>--%>
                <%--</div>--%>
            </div>
            <hr>
        </div>
        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-subjectDepartment">
                        <thead>
                        <th>Mã môn</th>
                        <th>Tên bộ môn</th>
                        <th>Thay đổi</th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<div id="scheduleModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Lịch dạy</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group" id="subject-create">
                            <label class="form-check-label" for="subject">Môn học:</label>
                            <select id="subject" class="select semester-select">
                                <c:forEach var="aSubject" items="${subjects}">
                                    <option value="${aSubject.id}">${aSubject.id} - ${aSubject.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group" id="subject-edit">
                            <label for="subjectEdit">Môn học:</label>
                            <input id="subjectEdit" type="text" class="form-control" disabled/>
                        </div>

                        <div class="form-group" id="room-container">
                            <label for="department">Bộ môn:</label>
                            <select id="department" class="select department-select">
                                <c:forEach var="aDepartment" items="${departments}">
                                    <option value="${aDepartment.deptName}">${aDepartment.deptName}</option>
                                </c:forEach>
                            </select>
                        </div>

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Đóng</button>
                <button id="btnSubmit" type="button" class="btn btn-primary">Tạo</button>
            </div>
        </div>

    </div>
</div>


<script>
    var tblsubjectDepartment;

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
        $('#subject').select2({
            placeholder: '- Chọn môn -'
        });

        $('#department').select2({
            placeholder: '- Chọn bộ môn -'
        });

        $('#select2-subject-container').removeAttr('title');
        $('#select2-department-container').removeAttr('title');
        $('select').on('change', function (evt) {
            $('#select2-subject-container').removeAttr('title');
            $('#select2-department-container').removeAttr('title');
        });

        LoadSubjectDepartmentList();

        $("#btnSubmit").on("click", function () {
            if (Validate($(this).data("type"))) {
                var successMsg = "";
                var subjectCode = "";
                var url = "";
                if ($(this).data("type") == "create") {
                    successMsg = "Tạo thành công";
                    url = "/subjectDepartment/create";
                    subjectCode = $("#subject").val();
                } else {
                    successMsg = "Cập nhật thành công";
                    url = "/subjectDepartment/edit";
                    subjectCode = $("#subjectEdit").val();

                }

                swal({
                    title: 'Đang xử lý',
                    html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                    type: 'info',
                    onOpen: function () {
                        swal.showLoading();
                        isRunning = true;
                        $.ajax({
                            type: "POST",
                            url: url,
                            data: {
                                subjectCode: subjectCode,
                                departmentName: $('#department').val()
                            },
                            success: function (result) {
                                if (result.success) {
                                    swal({
                                        title: 'Thành công',
                                        text: successMsg,
                                        type: 'success'
                                    }).then(function () {
                                        RefreshTable();
                                        $("#scheduleModal").modal('toggle');

                                    });
                                }

                                if (result.fail) {
                                    swal('Đã xảy ra lỗi!', result.message, 'error');
                                }
                            }
                        });
                    },
                    allowOutsideClick: false
                });
            }
        });
    });

    function Validate(type) {
        var isError = false;
        if (type == "create") {
            if ($("#subject").val() === "" || $("#subject").val() === null) {
                alert("Môn học không được bỏ trống");
                isError = true;
            } else if ($("#department").val() === "" || $("#department").val() === null) {
                alert("Bộ môn không được bỏ trống");
                isError = true;
            }
        }else{
            if ($("#department").val() === "" || $("#department").val() === null) {
                alert("Bộ môn không được bỏ trống");
                isError = true;
            }
        }
        return !isError;
    }

    function LoadSubjectDepartmentList() {
        tblsubjectDepartment = $('#tbl-subjectDepartment').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/loadSubjectDepartmentList",
            "oLanguage": {
                "sSearchPlaceholder": "Mã môn, tên bộ môn",
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
                {
                    "aTargets": [2],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-success tbl-btn' onclick='EditModal(\"" + row[0] + "\",\"" + row[1] + "\")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }


    function ClearModal() {
        $('#subject').val('').trigger('change');
        $('#subjectEdit').val('');
        $('#department').val('').trigger('change');
        $('#subject-create').hide();
        $('#subject-edit').hide();
    }

    function EditModal(subCode, departmentId) {
        ClearModal();
        $('#subject-create').hide();
        $('#subject-edit').show();
        $('#subjectEdit').val(subCode);
        $('#department').val(departmentId).trigger('change');
        $("#btnSubmit").html("Cập nhật");
        $('#btnSubmit').data("type", "edit")
        // $("#btnSubmit").attr("data-schedule-id", scheduleId);

        $("#scheduleModal").modal('toggle');
    }

    function CreateModal() {
        ClearModal();
        $('#subject-create').show();
        $('#subject-edit').hide();

        $("#btnSubmit").html("Tạo mới");
        $('#btnSubmit').data("type", "create")

        $("#scheduleModal").modal('toggle');
    }

    function RefreshTable() {
        if (tblsubjectDepartment != null) {
            // tblSchedule._fnPageChange(0);
            tblsubjectDepartment._fnAjaxUpdate();
        }

    }
</script>