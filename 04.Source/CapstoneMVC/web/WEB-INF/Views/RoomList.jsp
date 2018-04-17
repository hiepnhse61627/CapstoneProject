<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách phòng</h1>
                </div>
                <div class="col-md-3 text-right">
                    <button type="button" class="btn btn-success btn-with-icon" onclick="CreateModal()">
                        <i class="glyphicon glyphicon-plus"></i>
                        <div>Thêm phòng</div>
                    </button>
                </div>
            </div>
            <hr>
        </div>
        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-room">
                        <thead>
                        <th>ID</th>
                        <th>Tên phòng</th>
                        <th>Sức chứa</th>
                        <th>Ghi chú</th>
                        <th>isAvailable</th>
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
                <h4 class="modal-title">Phòng học</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="name">Tên phòng:</label>
                                    <input id="name" type="text" class="form-control">
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label for="capacity">Sức chứa:</label>
                                    <input id="capacity" type="number" class="form-control" min="0">
                                </div>
                            </div>
                        </div>

                        <div class="form-check" id="all-container">
                            <input class="form-check-input" type="checkbox" value="" id="isAvailable">
                            <label class="form-check-label" for="isAvailable">
                                Phòng sử dụng được
                            </label>
                        </div>

                        <div class="form-group" id="subject-edit">
                            <label for="note">Ghi chú:</label>
                            <textarea id="note" rows="4" cols="50" class="form-control"></textarea>
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
    var tblRoom;
    jQuery.fn.dataTableExt.oApi.fnSetFilteringDelay = function (oSettings, iDelay) {
        var _that = this;

        if (iDelay === undefined) {
            iDelay = 250;
        }

        this.each(function (i) {
            $.fn.dataTableExt.iApiIndex = i;
            var
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
        LoadRoomList();

        $("#btnSubmit").on("click", function () {
            if (Validate($(this).data("type"))) {
                var successMsg = "";
                var url = "";
                if ($(this).data("type") == "create") {
                    successMsg = "Tạo thành công";
                    url = "/roomList/create";
                } else {
                    successMsg = "Cập nhật thành công";
                    url = "/roomList/edit";
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
                                roomId: $("#btnSubmit").attr("data-schedule-id"),
                                name: $('#name').val(),
                                capacity: $('#capacity').val(),
                                note: $('#note').val(),
                                isAvailable: $("#isAvailable").is(":checked"),
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

        if ($("#name").val() === "") {
            alert("Tên phòng không được bỏ trống");
            isError = true;
        } else if ($("#capacity").val() < 10) {
            alert("Sức chứa không được nhỏ hơn 10");
            isError = true;
        }
        return !isError;
    }

    function LoadRoomList() {
        tblRoom = $('#tbl-room').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/loadRoomList",
            "oLanguage": {
                "sSearchPlaceholder": "Tên phòng",
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
                {
                    "aTargets": [0],
                    "bVisible": false,
                },
                {
                    "aTargets": [4],
                    "bVisible": false,
                },
                {
                    "aTargets": [5],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-success tbl-btn' onclick='EditModal(\"" + row[0] + "\",\"" + row[1] + "\",\"" + row[2] + "\",\"" + row[3] + "\",\"" + row[4] + "\")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }

    function ClearModal() {
        $('#name').val('');
        $('#capacity').val(10);
        $('#note').val('');
        $('#isAvailable').prop('checked', false);
    }

    function EditModal(id, roomName, capacity, note, isAvailable) {
        ClearModal();
        $('#name').val(roomName);
        $('#capacity').val(capacity);
        $('#note').val(note);
        console.log(isAvailable);
        if (isAvailable == 'true') {
            $('#isAvailable').prop('checked', true);
        } else {
            $('#isAvailable').prop('checked', false);
        }
        $("#btnSubmit").html("Cập nhật");
        $('#btnSubmit').data("type", "edit");
        $("#btnSubmit").attr("data-schedule-id", id);

        $("#scheduleModal").modal('toggle');
    }

    function CreateModal() {
        ClearModal();

        $("#btnSubmit").html("Tạo mới");
        $('#btnSubmit').data("type", "create")
        $("#btnSubmit").attr("data-schedule-id", 0);
        $("#scheduleModal").modal('toggle');
    }

    function RefreshTable() {
        if (tblRoom != null) {
            tblRoom._fnAjaxUpdate();
        }
    }

</script>