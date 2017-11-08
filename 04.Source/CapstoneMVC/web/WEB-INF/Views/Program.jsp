<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<%--<section class="content-header">--%>
<%--<h1>Danh sách môn học</h1>--%>
<%--</section>--%>

<section class="content">
    <br class="box">
    <div class="b-header">
        <div class="col-md-9 title">
            <h1>Danh sách chương trình học</h1>
        </div>
    </div>
    </br>
    <div class="b-body">
        <div class="row">
            <div class="col-md-12">
                <table id="tbl-subject">
                    <thead>
                    <th>Mã chương trình</th>
                    <th>Full Name</th>
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
                <h4 class="modal-title">Chi Tiết khung chương trình</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group visible-md-block" >
                            <label for="id">ID:</label>
                            <input disabled id="id" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="name">Mã chương trình:</label>
                            <input disabled id="name" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="fullName">Full name</label>
                            <input id="fullName" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="ojt">Tín chỉ OJT:</label>
                            <input id="ojt" type="text" maxlength="3" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="capstone">Tín chỉ đồ án tốt nghiệp:</label>
                            <input id="capstone" type="text" maxlength="3" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="graduate">Tín chỉ tốt nghiệp:</label>
                            <input id="graduate" type="text" maxlength="3" class="form-control"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                <button id="btnSubmit" type="button" class="btn btn-primary" onclick="return confirmChange($('#id').val(),$('#name').val(),$('#fullName').val()
                ,$('#ojt').val(),$('#capstone').val(),$('#graduate').val())">Thay đổi thông tin
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
        $('[id^=ojt]').keypress(validateNumber);
        $('[id^=capstone]').keypress(validateNumber);
        $('[id^=graduate]').keypress(validateNumber);
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


    function confirmChange(id, name, fullName, ojt, capstone, graduate) {

        if (confirm("Xác nhận thay đổi thông tin cho chương trình " + name + " ?")) {
            EditSubject(id, name, fullName, ojt, capstone, graduate);
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
            "sAjaxSource": "/loadProgramList",
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


    function EditSubject(id, name, fullName, ojt, capstone, graduate) {

        $.ajax({
            type: "POST",
            url: "/program/edit",
            data: {
                "sId": id,
                "sName": name,
                "sFullName": fullName,
                "sOJT": ojt,
                "sCapstone": capstone,
                "sGraduate": graduate,
            },
            success: function (result) {
                if (result.success) {
                    swal({
                        title: 'Thành công',
                        text: "Đã cập nhật chương trình học!",
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

    function ShowModal(name) {
        LoadSubjectList();
        var form = new FormData();
        form.append("name", name)

        $.ajax({
            type: "POST",
            url: "/getProgram",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    var program = JSON.parse(result.program);

                    $("#id").val(program.id);
                    $("#name").val(program.name);
                    $("#fullName").val(program.fullName);
                    $("#ojt").val(program.ojt);
                    $("#capstone").val(program.capstone);
                    $("#graduate").val(program.graduate);

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