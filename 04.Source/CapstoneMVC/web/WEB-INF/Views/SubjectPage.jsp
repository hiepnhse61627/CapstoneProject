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
            <input class="btn btn-primary" type="button" value="Tạo khóa học" onclick="CreateSubject()"/>
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
                            <input id="credits" type="number" max='100' maxlength="2" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="replacementSubject">Môn thay thế:</label>
                            <input id="replacementSubject" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="preEffectStart">Học kì bắt đầu tiên quyết:</label>
                            <input id="preEffectStart" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="preEffectEnd">Học kì kết thúc tiên quyết:</label>
                            <input id="preEffectEnd" type="text" class="form-control"/>
                        </div>

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="btnSubmit" type="button" class="btn btn-primary" onclick="return confirmChange($('#subjectId').val(),$('#subjectName').val()
                ,$('#prerequisiteSubs').val(),$('#credits').val(),$('#replacementSubject').val(),
                $('#preEffectStart').val(),$('#preEffectEnd').val())">Thay đổi thông tin
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


    $(document).ready(function () {
        LoadSubjectList();
    });

    function confirmChange(subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, preEffectStart, preEffectEnd) {
        debugger
        if (confirm("Xác nhận thay đổi thông tin cho môn " + subjectId + "?")) {
            EditSubject(subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, preEffectStart, preEffectEnd);
        }
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
        ClearModal();
        $("#btnSubmit").html("Tạo");
        $('#btnSubmit').data("type", "create");
        $("#btnSubmit").attr("data-subject-id", 0);

        $("#subjectDetailModal").modal('toggle');
    }

    function EditSubject(subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, preEffectStart, preEffectEnd) {

        $.ajax({
            type: "POST",
            url: "/subject/edit",
            data: {
                "sSubjectId": subjectId,
                "sSubjectName": subjectName,
                "sCredits": credits,
                "sReplacement": replacementSubject,
                "sPrerequisite": prerequisiteSubs,
                "sPreEffectStart": preEffectStart,
                "sPreEffectEnd": preEffectEnd,
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
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });


    }

    function ClearModal() {
        $("#class").val("");
        $('#subject').val(-1).trigger('change');
    }

    function ShowModal(subjectId) {
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

                    console.log("yas");
                    console.log(subject);

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

    //cố lên qq :)))

</script>