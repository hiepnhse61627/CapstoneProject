<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Các khung chương trình</h1>
                </div>
                <div class="col-md-3 text-right">
                    <input class="btn btn-primary" type="button" value="Nhập dữ liệu" onclick="ShowImportModal()"/>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="table">
                        <thead>
                        <th>Tên</th>
                        <th>Mô tả</th>
                        <th>Chỉnh sửa</th>
                        <th>Xóa</th>
                        </thead>
                    </table>
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
                                <input type="file" accept=".xlsx, .xls" id="file" name="file" />
                            </div>
                        </div>
                    </div>
                </div>

                <div class="form-group">
                    Bấm vào <a class="link" href="/Resources/FileTemplates/SubjectList_Upload_Template.xls">Template</a> để tải
                    về bản mẫu
                </div>
                <div class="form-group">
                    <button type="button" onclick="Upload()" class="btn btn-success">Upload</button>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>

<script>
    var tbl;

    $(document).ready(function () {
        LoadCurriculum();
        $("#choose tbody tr").click(function () {
            $('.selectedRow').removeClass('selectedRow');
            $('#selected').removeAttr('id', 'selected');
            $(this).addClass("selectedRow");
            $('.selectedRow').attr('id', 'selected');
        });
    });

    function ShowImportModal() {
        $('#importModal').modal('toggle');
    }

    function UseFile() {
        if ($('#selected td').length == 0) {
            swal('', 'Hãy chọn file trước', 'error');
        } else {
            swal({
                title: 'Bạn có chắc là sử dụng file này?',
                type: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Tiếp tục',
                cancelButtonText: 'Đóng'
            }).then(function () {
                var form = new FormData();
                form.append('file', $('#selected td').html());

                swal({
                    title: 'Đang xử lý',
                    html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                    type: 'info',
                    onOpen: function () {
                        swal.showLoading();
                        isRunning = true;
                        $.ajax({
                            type: "POST",
                            url: "/subcurriculum/choose",
                            processData: false,
                            contentType: false,
                            data: form,
                            success: function (result) {
                                isRunning = false;
                                if (result.success) {
                                    swal({
                                        title: 'Thành công',
                                        text: "Đã import các khóa học!",
                                        type: 'success'
                                    }).then(function () {
                                        location.reload();
                                    });
                                } else {
                                    swal('Đã xảy ra lỗi!', result.message, 'error');
                                }
                            }
                        });
//                        waitForTaskFinish(isRunning);
                    },
                    allowOutsideClick: false
                });
            });

        }
    }

    function Remove(id) {
        swal({
            title: 'Bạn có chắc là xóa?',
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Tiếp tục',
            cancelButtonText: 'Đóng'
        }).then(function () {
            var form = new FormData();
            form.append('curId', id);

            swal({
                title: 'Đang xử lý',
                html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                type: 'info',
                onOpen: function () {
                    swal.showLoading();
                    isRunning = true;
                    $.ajax({
                        type: "POST",
                        url: "/deletesubcurriculum",
                        processData: false,
                        contentType: false,
                        data: form,
                        success: function (result) {
                            if (result.success) {
                                swal({
                                    title: 'Thành công',
                                    text: "Đã xóa!",
                                    type: 'success'
                                }).then(function () {
                                    location.reload();
                                });
                            } else {
                                swal('Đã xảy ra lỗi!', result.message, 'error');
                            }
                        }
                    });
                },
                allowOutsideClick: false
            });
        });
    }

    function Upload() {
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
                    url: "/subcurriculum/upload",
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
//                waitForTaskFinish(isRunning);
            },
            allowOutsideClick: false
        });
    }

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


    function LoadCurriculum() {
        tbl = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getsubcurriculum",
            "oLanguage": {
                "sSearchPlaceholder": "Tên",
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
                    "aTargets": [0, 1, 2, 3],
                    "bSortable": false,
                },
                {
                    "aTargets": [2, 3],
                    "sClass": "text-center",
                },
                {
                    "aTargets": [2],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-success tbl-btn' href='/editcurriculum/" + data + "'>" +
                            "<i class='fa fa-eye'></i></a>";
                    }
                },
                {
                    "aTargets": [3],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-danger tbl-btn' onclick='Remove(" + row[2] + ")'>" +
                            "<i class='fa fa-trash'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }

    function RefreshTable() {
        if (tbl != null) {
            tbl._fnPageChange(0);
            tbl._fnAjaxUpdate();
        }
    }

</script>