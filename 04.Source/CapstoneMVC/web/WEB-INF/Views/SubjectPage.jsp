<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<%--<section class="content-header">--%>
<%--<h1>Danh sách môn học</h1>--%>
<%--</section>--%>

<section class="content">
    <br class="box">
    <div class="b-header">
        <div class="col-md-9 title">
            <h1>Danh sách môn học</h1>
        </div>
        <div class="col-md-3 text-right">
            <input class="btn btn-primary" type="button" value="Tạo khóa học" onclick="CreateNewSubject()"/>
        </div>
    </div>
    </br>
    <div class="b-body">
        <div class="row">
            <div class="col-md-12">
                <table id="tbl-subject">
                    <thead>
                    <th>Mã Môn</th>
                    <th>Tên Môn</th>
                    <th>Xem Chi Tiết</th>
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
                <h4 class="modal-title">Chi Tiết Môn học</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="subjectId">Mã Môn:</label>
                            <input disabled id="subjectId" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="subjectName">Tên Môn:</label>
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
                <button id="btnSubmit" type="button" class="btn btn-primary" onclick="return confirmChange($('#subjectId').val(),$('#subjectName').val()
                ,$('#prerequisiteSubs').val(),$('#credits').val(),$('#replacementSubject').val(),
                $('#effectionSemester').val(),$('#failMark').val())">Thay đổi thông tin
                </button>
            </div>
        </div>

    </div>
</div>

<div id="subjectNewDetailModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Chi Tiết Môn học</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group">
                            <label for="subjectNewId">Mã Môn:</label>
                            <input id="subjectNewId" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="subjectNewName">Tên Môn:</label>
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
                <button id="btnNewSubmit" type="button" class="btn btn-primary" onclick="return confirmNew($('#subjectNewId').val()
                ,$('#subjectNewName').val(),$('#prerequisiteNewSubs').val(),$('#newCredits').val()
                ,$('#replacementNewSubject').val(),$('#effectionNewSemester').val()
                ,$('#failNewMark').val())">Tạo môn học
                </button>
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

    $(document).ready(function(){
        $('[id^=credits]').keypress(validateNumber);
        $('[id^=newCredits]').keypress(validateNumber);
    });


    function validateNumber(event) {
        var key = window.event ? event.keyCode : event.which;
        if (event.keyCode === 8 || event.keyCode === 46) {
            return true;
        } else if ( key < 48 || key > 57 ) {
            return false;
        } else {
            return true;
        }
    }

    $(document).ready(function () {
        LoadSubjectList();
    });


    function confirmChange(subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, effectionSemester, failMark) {

        if (confirm("Xác nhận thay đổi thông tin cho môn " + subjectId + "?")) {
            EditSubject(subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, effectionSemester, failMark);
        }
    }

    function confirmNew(subjectNewId, subjectNewName, prerequisiteNewSubs, newCredits, replacementNewSubject, effectionNewSemester, failNewMark) {

        if (confirm("Xác nhận tạo môn " + subjectNewId + "?")) {
            CreateSubject(subjectNewId, subjectNewName, prerequisiteNewSubs, newCredits, replacementNewSubject, effectionNewSemester, failNewMark);
        }
    }

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

    function CreateSubject(subjectNewId, subjectNewName, prerequisiteNewSubs, newCredits, replacementNewSubject, effectionNewSemester, failNewMark) {
        $.ajax({
            type: "POST",
            url: "/subject/create",
            data: {
                "sNewSubjectId": subjectNewId,
                "sNewSubjectName": subjectNewName,
                "sNewCredits": newCredits,
                "sNewReplacement": replacementNewSubject,
                "sNewPrerequisite": prerequisiteNewSubs,
                "sNewEffectionSemester": effectionSemester,
                "sNewFailMark": failNewMark,
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
    }

    function EditSubject(subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, effectionSemester, failMark) {

        $.ajax({
            type: "POST",
            url: "/subject/edit",
            data: {
                "sSubjectId": subjectId,
                "sSubjectName": subjectName,
                "sCredits": credits,
                "sReplacement": replacementSubject,
                "sPrerequisite": prerequisiteSubs,
                "sEffectionSemester": effectionSemester,
                "sFailMark": failMark,
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
                if (result.success) {
                    var subject = JSON.parse(result.subject);

                    $("#subjectId").val(subject.subjectID);
                    $("#subjectName").val(subject.subjectName);
                    $("#prerequisiteSubs").val(subject.prerequisiteSubject);
                    $("#credits").val(subject.credits);
                    $("#preEffectStart").val(subject.prerequisiteEffectStart);
                    $("#preEffectEnd").val(subject.prerequisiteEffectEnd);
                    $("#replacementSubject").val(subject.replacementSubject);

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