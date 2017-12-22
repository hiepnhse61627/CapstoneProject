<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách môn học</h1>
                </div>
                <div class="col-md-3 text-right">
                    <button type="button" class="btn btn-success btn-with-icon" onclick="CreateNewSubject()">
                        <i class="fa fa-plus"></i>
                        <div style="margin-top: -3px">TẠO MÔN HỌC</div>
                    </button>
                </div>
            </div>
            <hr>
        </div>
        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-subject">
                        <thead>
                        <th>Mã môn</th>
                        <th>Tên môn</th>
                        <th>Chi tiết</th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<div id="subjectDetailModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Chi tiết môn học</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="subjectId">Mã môn:</label>
                            <input disabled id="subjectId" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="subjectName">Tên môn:</label>
                            <input id="subjectName" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="prerequisiteSubs">Tiên quyết:</label>
                            <input id="prerequisiteSubs" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="credits">Tín chỉ:</label>
                            <input id="credits" type="text" maxlength="2" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="replacementSubject">Môn thay thế:</label>
                            <input id="replacementSubject" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="effectionSemester">Học kì bắt đầu áp dụng tiên quyết:</label>
                            <select id="effectionSemester" class="select form-control">
                                <option value="0"></option>
                                <c:forEach var="effectionSemester" items="${effectionSemester}">
                                    <option value="${effectionSemester.semester}">${effectionSemester.semester}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="failMark">Điểm tiên quyết môn</label>
                            <input id="failMark" type="text" class="form-control"/>
                        </div>

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="btnSubmit" type="button" class="btn btn-primary" onclick="EditSubject()">Cập nhật</button>
            </div>
        </div>

    </div>
</div>

<div id="subjectNewDetailModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Tạo môn học</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="subjectNewId">Mã môn:</label>
                            <input id="subjectNewId" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="subjectNewName">Tên môn:</label>
                            <input id="subjectNewName" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="prerequisiteNewSubs">Tiên quyết:</label>
                            <input id="prerequisiteNewSubs" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="newCredits">Tín chỉ:</label>
                            <input id="newCredits" type="text" maxlength="2" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="replacementNewSubject">Môn thay thế:</label>
                            <input id="replacementNewSubject" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="effectionNewSemester">Học kì bắt đầu áp dụng tiên quyết:</label>
                            <div id="selector" style="">
                                <select id="effectionNewSemester" class="select form-control">
                                    <option value="0"></option>
                                    <c:forEach var="effectionNewSemester" items="${effectionSemester}">
                                        <option value="${effectionNewSemester.semester}">${effectionNewSemester.semester}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="failNewMark">Điểm tiên quyết:</label>
                            <input id="failNewMark" type="text" class="form-control"/>
                        </div>

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="btnNewSubmit" type="button" class="btn btn-primary" onclick="CreateSubject()">Tạo mới</button>
            </div>
        </div>

    </div>
</div>


<script>
    var tblSubject;
    var tableMarkDetail;

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
        $('[id^=credits]').keypress(validateNumber);
        $('[id^=newCredits]').keypress(validateNumber);
    });


    function validateNumber(event) {
        var key = window.event ? event.keyCode : event.which;
        if (event.keyCode === 8 || event.keyCode === 46) {
            return true;
        } else if (key < 48 || key > 57) {
            return false;
        } else {
            return true;
        }
    }

    $(document).ready(function () {
        LoadSubjectList();
    });

    function CreateNewSubject() {
        $("#subjectNewDetailModal").modal('toggle');
    }

    function LoadSubjectList() {
        tblSubject = $('#tbl-subject').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/loadSubjectList",
            "oLanguage": {
                "sSearchPlaceholder": "Mã Môn",
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
                },
                {
                    "aTargets": [0, 2],
                    "bSortable": false,
                    "sClass": "text-center",
                },
                {
                    "aTargets": [2],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-success tbl-btn' onclick='ShowModal(\"" + row[0] + "\")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }

    function CreateSubject() {

        swal({
            title: 'Bấm tiếp tục để tạo môn ' + $('#subjectNewId').val(),
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            $.ajax({
                type: "POST",
                url: "/subject/create",
                data: {
                    "sNewSubjectId": $('#subjectNewId').val(),
                    "sNewSubjectName": $('#subjectNewName').val(),
                    "sNewCredits": $('#newCredits').val(),
                    "sNewReplacement": $('#replacementNewSubject').val(),
                    "sNewPrerequisite": $('#prerequisiteNewSubs').val(),
                    "sNewEffectionSemester": $('#effectionNewSemester').val(),
                    "sNewFailMark": $('#failNewMark').val(),
                },
                success: function (result) {
                    if (result.success) {
                        swal({
                            title: 'Thành công',
                            text: "Đã tạo môn học!",
                            type: 'success'
                        }).then(function () {
                            RefreshTable();
                        });
                        $("#subjectNewDetailModal").modal('toggle');
                    } else {
                        swal('', result.message, 'error');
                    }
                }
            });
        });

    }

    function EditSubject() {

        swal({
            title: 'Bấm tiếp tục để cập nhật môn ' + $('#subjectId').val(),
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            $.ajax({
                type: "POST",
                url: "/subject/edit",
                data: {
                    "sSubjectId": $('#subjectId').val(),
                    "sSubjectName": $('#subjectName').val(),
                    "sCredits": $('#credits').val(),
                    "sReplacement": $('#replacementSubject').val(),
                    "sPrerequisite": $('#prerequisiteSubs').val(),
                    "sEffectionSemester": $('#effectionSemester').val(),
                    "sFailMark": $('#failMark').val(),
                },
                success: function (result) {
                    if (result.success) {
                        swal({
                            title: 'Thành công',
                            text: "Đã cập nhật môn học!",
                            type: 'success'
                        }).then(function () {
                            RefreshTable();
                        });
                        $("#subjectDetailModal").modal('toggle');
                    } else {
                        swal('', result.message, 'error');
                    }
                }
            });
        });

    }

    function ClearModal() {
        $("#class").val("");
        $('#subject').val(-1).trigger('change');
    }

    function ShowModal(subjectId) {
        LoadSubjectList();
        var form = new FormData();
        form.append("subjectId", subjectId)

        $.ajax({
            type: "POST",
            url: "/getSubject",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                debugger
                if (result.success) {
                    var subject = JSON.parse(result.subject);

                    $("#subjectId").val(subject.subjectID);
                    $("#subjectName").val(subject.subjectName);
                    $("#prerequisiteSubs").val(subject.prerequisiteSubject);
                    $("#credits").val(subject.credits);
                    $("#replacementSubject").val(subject.replacementSubject);
                    $('#effectionSemester').val(subject.effectionSemester);
                    $("#failMark").val(subject.failMark);

                    $("#subjectDetailModal").modal('toggle');
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });
    }

    function RefreshTable() {
        if (tblSubject != null) {
            tblSubject._fnPageChange(0);
            tblSubject._fnAjaxUpdate();
        }
    }

</script>