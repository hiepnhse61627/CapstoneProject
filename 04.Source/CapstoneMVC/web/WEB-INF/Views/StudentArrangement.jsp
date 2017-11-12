<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/excel-sub-menu.css">

<style>
    .form-group .my-content .my-input-group .left-content {
        min-width: 70px;
    }

    .f-red {
        color: red;
    }

    .f-green {
        color: green;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-7 title">
                    <h1>Danh sách sinh viên theo lớp môn</h1>
                </div>
                <div class="col-md-5 text-right">
                    <button type="button" class="btn btn-success" onclick="ExportExcel()">Xuất dữ liệu</button>
                    <button type="button" class="btn btn-warning" onclick="ShowImportModal()">Nhập dữ liệu</button>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <%--<div class="form-group">--%>
            <%--<div class="row">--%>
            <%--<div class="title">--%>
            <%--<h4>Thông tin bộ lọc</h4>--%>
            <%--</div>--%>
            <%--<div class="my-content p-l-10">--%>
            <%--<div class="my-input-group">--%>
            <%--<div class="left-content m-r-5">--%>
            <%--<label class="p-t-8">Danh sách</label>--%>
            <%--</div>--%>
            <%--<div class="right-content width-30 width-m-70">--%>
            <%--<select id="type" class="select form-control">--%>
            <%--<option value="AM">Buổi sáng</option>--%>
            <%--<option value="PM">Buổi chiều</option>--%>
            <%--</select>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>
            <%--</div>--%>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>Mã môn</th>
                                <th>Tên môn</th>
                                <th>MSSV</th>
                                <th>Tên sinh viên</th>
                                <th>Lớp</th>
                                <th>Buổi</th>
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

<div id="importModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Nhập dữ liệu</h4>
            </div>
            <div class="modal-body">
                <%--<c:if test="${not empty files}">--%>
                <%--<div class="form-group">--%>
                <%--<div class="row">--%>
                <%--<div class="title">--%>
                <%--<h4>Các file gần đây:</h4>--%>
                <%--</div>--%>
                <%--<div class="my-content">--%>
                <%--<div class="col-md-12">--%>
                <%--<table id="choose" class="table">--%>
                <%--<c:forEach var="file" items="${files}">--%>
                <%--<tr class="table-row">--%>
                <%--<td>${file.name}</td>--%>
                <%--</tr>--%>
                <%--</c:forEach>--%>
                <%--</table>--%>
                <%--</div>--%>
                <%--<div class="col-md-12">--%>
                <%--<button type="button" class="btn btn-primary" onclick="UseFile()">Sử dụng</button>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--</div>--%>
                <%--</c:if>--%>

                <div class="form-group">
                    <div class="row">
                        <div class="title">
                            <h4>Chọn học kỳ:</h4>
                        </div>
                        <div class="my-content">
                            <div class="col-md-12">
                                <select id="cb-semester" class="select">
                                    <option value="-1">- Chọn học kỳ -</option>
                                    <c:forEach var="s" items="${semesters}">
                                        <option value="${s.id}">${s.semester}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <div class="row">
                        <div class="title">
                            <h4>Nhập danh sách kế hoạch học đi và học lại:</h4>
                        </div>
                        <div class="my-content">
                            <div class="col-md-12">
                                <label for="file-suggestion" hidden></label>
                                <input type="file" accept=".xlsx, .xls" id="file-suggestion"/>
                            </div>
                            <div class="col-md-12 m-t-5">
                                Bấm vào <a class="link" href="/Resources/FileTemplates/SubjectList_Upload_Template.xls">Template</a>
                                để tải
                                về bản mẫu
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <div class="row">
                        <div class="title">
                            <h4>Nhập danh sách sinh viên học đi:</h4>
                        </div>
                        <div class="my-content">
                            <div class="col-md-12">
                                <label for="file-going" hidden></label>
                                <input type="file" accept=".xlsx, .xls" id="file-going"/>
                            </div>
                            <div class="col-md-12 m-t-5">
                                Bấm vào <a class="link" href="/Resources/FileTemplates/SubjectList_Upload_Template.xls">Template</a>
                                để tải
                                về bản mẫu
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    <div class="row">
                        <div class="title">
                            <h4>Nhập danh sách sinh viên học lại:</h4>
                        </div>
                        <div class="my-content">
                            <div class="col-md-12">
                                <label for="file-relearn" hidden></label>
                                <input type="file" accept=".xlsx, .xls" id="file-relearn"/>
                            </div>
                            <div class="col-md-12 m-t-5">
                                Bấm vào <a class="link" href="/Resources/FileTemplates/SubjectList_Upload_Template.xls">Template</a>
                                để tải
                                về bản mẫu
                            </div>
                        </div>
                    </div>
                </div>


                <div class="form-group">
                    <button type="button" onclick="ImportFile()" class="btn btn-success">Import</button>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="credit"/>
    <input name="sCredit"/>
    <input name="programId"/>
    <input name="semesterId"/>
    <input name="sSearch"/>
</form>

<script>
    var tblStudenArrangement = null;
    var isRunning = false;

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
        $('.select').select2();
        CreateStudentArrangementTable();
    });

    function ShowImportModal() {
        $('#importModal').modal('toggle');
    }

    function CreateStudentArrangementTable() {
        tblStudenArrangement = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "iDisplayLength": '25',
            "sAjaxSource": "/studentArrangement/loadTable",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "type", "value": $("#type").val()})
            },
            "oLanguage": {
                "sSearchPlaceholder": "Tìm kiếm theo MSSV, Tên",
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
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function ExportExcel() {
        $("input[name='objectType']").val(4);
        $("input[name='credit']").val($('#credit').val());
        $("input[name='sCredit']").val($('#sCredit').val());
        $("input[name='programId']").val($('#program').val());
        $("input[name='semesterId']").val($('#semester').val());
        $("input[name='sSearch']").val(table.api().context[0].oPreviousSearch.sSearch);

        $("#export-excel").submit();
    }

    function RefreshTable() {
        if (tblStudenArrangement != null) {
            tblStudenArrangement._fnPageChange(0);
            tblStudenArrangement._fnAjaxUpdate();
        }
    }

    function ImportFile() {
        if ($('#cb-semester').val() == '-1') {
            swal('', 'Xin chọn học kỳ', 'warning');
            return;
        } else if (typeof($('#file-suggestion')[0].files[0]) == 'undefined' || $('#file-suggestion')[0].files[0] == null) {
            swal('', 'Xin nhập danh sách kế hoạch học đi và học lại', 'warning');
            return;
        } else if (typeof($('#file-going')[0].files[0]) == 'undefined' || $('#file-going')[0].files[0] == null) {
            swal('', 'Xin nhập danh sách sinh viên học đi', 'warning');
            return;
        } else if (typeof($('#file-relearn')[0].files[0]) == 'undefined' || $('#file-relearn')[0].files[0] == null) {
            swal('', 'Xin nhập danh sách sinh viên học lại', 'warning');
            return;
        }

        var form = new FormData();
        form.append('semesterId', $('#cb-semester').val());
        form.append('file-suggestion', $('#file-suggestion')[0].files[0]);
        form.append('file-going', $('#file-going')[0].files[0]);
        form.append('file-relearn', $('#file-relearn')[0].files[0]);

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!</div><div id='progress-file-1' class='form-group'></div><div id='progress-file-2' class='form-group'></div><div id='progress-file-3' class='form-group'></div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                $.ajax({
                    type: "POST",
                    url: "/studentArrangement/import",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        isRunning = false;
                        if (result.success) {
                            swal({
                                title: 'Thành công',
                                text: "Đã import curriculum!",
                                type: 'success'
                            }).then(function () {
                                RefreshTable();
                            });
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                    }
                });
                updateProgress(isRunning);
            },
            allowOutsideClick: false
        });
    }

    function updateProgress(running) {
        $.ajax({
            type: "GET",
            url: "/studentArrangement/updateProgress",
            processData: false,
            contentType: false,
            success: function (result) {
                if (result.file1Done) {
                    $('#progress-file-1').html("<div class='f-green'>Đã xử lý danh sách kế hoạch học đi và học lại</div>");
                } else {
                    $('#progress-file-1').html("<div class='f-red'>Đang xử lý danh sách kế hoạch học đi và học lại</div>");
                }

                if (result.file2Done) {
                    $('#progress-file-2').html("<div class='f-green'>Đã xử lý danh sách sinh viên học đi</div>");
                } else {
                    $('#progress-file-2').html("<div class='f-red'>Đang xử lý danh sách sinh viên học đi</div>");
                }

                if (result.file3Done) {
                    $('#progress-file-3').html("<div class='f-green'>Đã xử lý danh sách sinh viên học lại</div>");
                } else {
                    $('#progress-file-3').html("<div class='f-red'>Đang xử lý danh sách sinh viên học lại</div>");
                }

                $('#progress').html("<div>(" + result.count + "/" + result.total + ")</div>");
                if (running) {
                    setTimeout("updateProgress(isRunning)", 50);
                }
            }
        });
    }
</script>
