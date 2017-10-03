<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<section class="content-header">
    <h1>Các khung chương trình</h1>
</section>

<section class="content">
    <div class="row">
        <div class="col-md-6">
            <div class="box">
                <c:if test="${not empty files}">
                    <div class="box-header">
                        <h4 class="box-title">Các file gần đây</h4>
                    </div>
                    <div class="form-group">
                        <table id="choose" class="table">
                            <c:forEach var="file" items="${files}">
                                <tr class="table-row">
                                    <td>${file.name}</td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                    <div class="form-group">
                        <button type="button" class="btn btn-info" onclick="UseFile()">Sử dụng</button>
                    </div>
                </c:if>
                <div class="form-group">
                    <div class="box-header">
                        <h4 class="box-title">Chọn file</h4>
                    </div>
                    <label for="file" hidden></label>
                    <input type="file" accept=".xlsx, .xls" id="file" name="file" placeholder="Chọn khung"/>
                </div>
                <div class="form-group">
                    Bấm vào <a class="link" href="/Resources/FileTemplates/DSSV_Template.xlsx">Template</a> để tải
                    về bản mẫu
                </div>
                <div class="form-group">
                    <button type="button" onclick="Upload()" class="btn btn-success">Upload</button>
                </div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="box p-t-15">
                <table id="table">
                    <thead>
                        <th>Tên</th>
                        <th>Description</th>
                        <th>Chỉnh sửa</th>
                        <th>Xóa</th>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</section>

<script>
    $(document).ready(function () {
        LoadCurriculum();
        $("#choose tbody tr").click(function () {
            $('.selectedRow').removeClass('selectedRow');
            $('#selected').removeAttr('id', 'selected');
            $(this).addClass("selectedRow");
            $('.selectedRow').attr('id', 'selected');
            //            var file = $('td', this).html();
        });
    });

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
                                location.reload();
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
                    "aTargets": [0, 1],
                    "bSortable": false,
                },
                {
                    "aTargets": [2],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-primary' href='/editcurriculum/" + data + "'>" +
                            "<i class='fa fa-eye'></i></a>";
                    }
                },
                {
                    "aTargets": [3],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-primary' onclick='Remove(" + row[2] + ")'>" +
                            "<i class='fa fa-trash'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }
</script>