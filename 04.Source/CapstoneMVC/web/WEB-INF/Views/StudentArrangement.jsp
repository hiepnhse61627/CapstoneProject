<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/excel-sub-menu.css">

<style>
    .form-group .my-content .my-input-group .left-content {
        min-width: 70px;
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
                    <button type="button" class="btn btn-warning" onclick="ShowImportModal()">Import dữ liệu</button>
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
                <h4 class="modal-title">Nhập khung chương trình</h4>
            </div>
            <div class="modal-body">
                <c:if test="${not empty files}">
                    <div class="form-group">
                        <div class="row">
                            <div class="title">
                                <h4>Các file gần đây:</h4>
                            </div>
                            <div class="my-content">
                                <div class="col-md-12">
                                    <table id="choose" class="table">
                                        <c:forEach var="file" items="${files}">
                                            <tr class="table-row">
                                                <td>${file.name}</td>
                                            </tr>
                                        </c:forEach>
                                    </table>
                                </div>
                                <div class="col-md-12">
                                    <button type="button" class="btn btn-primary" onclick="UseFile()">Sử dụng</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>

                <div class="form-group">
                    <div class="row">
                        <div class="title">
                            <h4>Chọn file:</h4>
                        </div>
                        <div class="my-content">
                            <div class="col-md-12">
                                <label for="file" hidden></label>
                                <input type="file" accept=".xlsx, .xls" id="file" name="file"/>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    Bấm vào <a class="link" href="/Resources/FileTemplates/SubjectList_Upload_Template.xls">Template</a>
                    để tải
                    về bản mẫu
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
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
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
            },
            allowOutsideClick: false
        });
    }
</script>
