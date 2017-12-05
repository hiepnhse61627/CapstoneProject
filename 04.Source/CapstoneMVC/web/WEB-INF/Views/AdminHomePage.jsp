<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .dataTables_wrapper .dataTables_filter input {
        width: 230px;
    }
</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Tài khoản người dùng</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="table">
                        <thead>
                        <th>Ảnh đại diện</th>
                        <th>Tài khoản</th>
                        <th>Tên người dùng</th>
                        <th>Email</th>
                        <th>Chức vụ</th>
                        <th>Chỉnh sửa</th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<div id="markDetail" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Chỉnh sửa tài khoàn</h4>
            </div>
            <div class="modal-body">
                <div class="col-md-12">
                    <form id="form" class="form-horizontal">
                        <div class="form-group">
                            <input type="hidden" id="id" name="id"/>

                            <div class="form-group">
                                <label for="username" class="col-sm-3 control-label">Tài khoản</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="username" name="username"
                                           placeholder="Username"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="inputPassword" class="col-sm-3 control-label">Mật khẩu</label>
                                <div class="col-sm-9">
                                    <input type="password" class="form-control" id="inputPassword" name="password"
                                           placeholder="Password">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="fullname" class="col-sm-3 control-label">Tên người dùng</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="fullname" name="fullname"
                                           placeholder="Fullname"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="email" class="col-sm-3 control-label">Email</label>
                                <div class="col-sm-9">
                                    <input type="email" class="form-control" id="email" placeholder="Email"
                                           name="email"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="role" class="col-sm-3 control-label">Chức vụ</label>
                                <div class="col-sm-9">
                                    <select class="form-control" id="role" name="role">
                                        <option value="ROLE_STUDENT">Student</option>
                                        <option value="ROLE_ADMIN">Admin</option>
                                        <option value="ROLE_STAFF">Staff</option>
                                        <option value="ROLE_MANAGER">Manager</option>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="role" class="col-sm-3 control-label">Đường dẫn ảnh</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="picture" placeholder="picture"
                                           name="picture"/>
                                </div>
                            </div>

                            <%--<div class="form-group">--%>
                                <%--<label for="studentRollNumber" class="col-sm-3 control-label">MSSV</label>--%>
                                <%--<div class="col-sm-9">--%>
                                    <%--<input type="text" class="form-control" id="studentRollNumber"--%>
                                           <%--placeholder="Rollnumber"--%>
                                           <%--name="studentRollNumber"/>--%>
                                <%--</div>--%>
                            <%--</div>--%>
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="Confirm()">Cập nhật</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
    $(document).ready(function () {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' // optional
        });

        $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/admin/getUsers",
            "oLanguage": {
                "sSearchPlaceholder": "Tài khoản, Tên người dùng, Email",
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
                    "mRender": function (data, type, row) {
                        console.log(data);
                        if (data == "N/A") {
                            data = "/Resources/plugins/dist/img/anonymous.jpg";
                        }
                        return "<img class='profile-user-img img-responsive img-circle' src='" + data + "' alt='User profile picture'>";
                    }
                },
                {
                    "aTargets": [5],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-success tbl-btn' onclick='Edit(\""+ data +"\")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                    }
                }
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    });

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

    function Edit(data) {
        var form = new FormData();
        form.append("userId", data);

        $.ajax({
            type: "POST",
            url: "/admin/edit",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    $("#markDetail").find(".modal-title").html("Thông tin tài khoản - " + result.data.username);
                    $('#id').val(result.data.id);
                    $('#username').val(result.data.username);
                    $('#email').val(result.data.email);
                    $('#role').val(result.data.role);
                    $('#studentRollNumber').val(result.data.studentRollNumber);
                    $('#password').val(result.data.password);
                    $('#fullname').val(result.data.fullname);
                    $('#picture').val(result.data.picture);

                    $("#markDetail").modal();
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'warning');
                }
            }
        });
    }

    function Confirm() {
        var form = JSON.stringify($('#form').serializeJSON());

        $.ajax({
            type: "POST",
            url: "/admin/save",
            processData: false,
            contentType: "application/json",
            data: form,
            success: function (data) {
                if (data.success) {
                    swal('', 'Thành công', 'success').then(function() {
                        location.reload();
                    });
                } else {
                    swal('', data.msg, 'error');
                }
            }
        });
    }
</script>
