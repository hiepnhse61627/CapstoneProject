<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<style>
    .dataTables_wrapper .dataTables_filter input {
        width: 230px;
    }
</style>

<link rel="stylesheet" href="https://code.jquery.com/jquery-1.12.4.js"/>
<link rel="stylesheet" href="https://cdn.datatables.net/1.10.16/js/jquery.dataTables.min.js"/>
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

        <div class="form-group">
            <button type="button" onclick="" class="btn btn-success" data-toggle="modal"
                    data-target="#createCredentialModal">
                <i class="fa fa-plus"></i> Tạo tài khoản
            </button>
        </div>
        <div class="form-group">
            <button type="button" onclick="toggleDelete()" class="btn btn-danger">
                <i class="fa fa-minus"></i> Xóa tài khoản
            </button>
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
                        <th>Xóa</th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<div id="userDetail" class="modal fade" role="dialog">
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

                            <%--<div class="form-group">--%>
                            <%--<label for="role" class="col-sm-3 control-label">Chức vụ</label>--%>
                            <%--<div class="col-sm-9">--%>
                            <%--<select class="form-control" id="role" name="role">--%>
                            <%--<option value="ROLE_STUDENT">Student</option>--%>
                            <%--<option value="ROLE_ADMIN">Admin</option>--%>
                            <%--<option value="ROLE_STAFF">Staff</option>--%>
                            <%--<option value="ROLE_MANAGER">Manager</option>--%>
                            <%--</select>--%>
                            <%--</div>--%>
                            <%--</div>--%>

                            <div class="form-group">
                                <label for="roles" class="col-sm-3 control-label">Chức vụ</label>
                                <div class="col-sm-9">
                                    <select class="form-control select" id="roles" name="roles[]" multiple="multiple">
                                        <c:forEach var="role" items="${roleList}">
                                            <option value="${role.name}">${role.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="picture" class="col-sm-3 control-label">Đường dẫn ảnh</label>
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

<div id="createCredentialModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Chỉnh sửa tài khoàn</h4>
            </div>
            <div class="modal-body">
                <div class="col-md-12">
                    <form id="createForm" class="form-horizontal">
                        <div class="form-group">

                            <div class="form-group">
                                <label for="username" class="col-sm-3 control-label">Tài khoản</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="c-username" name="username"
                                           placeholder="Username"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="inputPassword" class="col-sm-3 control-label">Mật khẩu</label>
                                <div class="col-sm-9">
                                    <input type="password" class="form-control" id="c-inputPassword" name="password"
                                           placeholder="Password">
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="fullname" class="col-sm-3 control-label">Tên người dùng</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="c-fullname" name="fullname"
                                           placeholder="Fullname"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="email" class="col-sm-3 control-label">Email</label>
                                <div class="col-sm-9">
                                    <input type="email" class="form-control" id="c-email" placeholder="Email"
                                           name="email"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="roles" class="col-sm-3 control-label">Chức vụ</label>
                                <div class="col-sm-9">
                                    <select class="form-control select" id="c-roles" name="roles[]" multiple="multiple">
                                        <c:forEach var="role" items="${roleList}">
                                            <option value="${role.name}">${role.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="picture" class="col-sm-3 control-label">Đường dẫn ảnh</label>
                                <div class="col-sm-9">
                                    <input type="text" class="form-control" id="c-picture" placeholder="picture"
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
                <button type="button" class="btn btn-primary" onclick="CreateCredential()">Tạo</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<script>
    var table = null;
    $(document).ready(function () {
        $('.select').select2();

        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%' // optional
        });
        CreateMainTable();

    });

    function RefreshTable() {
        if (table != null) {
            table.ajax.reload();
        } else {
            //destroy empty table
            $('#table').dataTable().fnDestroy();
            CreateMainTable();
        }
    }

    function CreateMainTable() {
        table = $('#table').DataTable({
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
                    "aTargets": [0, 1, 2, 3, 4, 5, 6],
                    "bSortable": false,
                    "sClass": "text-center",
                },
                {
                    "targets": [6],
                    "visible": false
                },
                {
                    "aTargets": [0],
                    "mRender": function (data, type, row) {
                        if (data == "N/A") {
                            data = "/Resources/plugins/dist/img/anonymous.jpg";
                        }
                        return "<img class='profile-user-img img-responsive img-circle' src='" + data + "' alt='User profile picture'>";
                    }
                },
                {
                    "aTargets": [5],
                    "mRender": function (data, type, row) {
                        console.log(data);
                        return "<a class='btn btn-success tbl-btn' onclick='Edit(\"" + data + "\")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                    }
                },
                {
                    "aTargets": [6],
                    //Id, function Group, func name, group name, link
                    "mRender": function (data, type, row) {
                        var result = "<a class='btn btn-danger tbl-btn' onclick='deleteCredential(" + row[5] + ")'>" +
                            "<i class='glyphicon glyphicon-minus-sign'></i></a>";
                        return result;
                    }
                }
            ],
            "bAutoWidth": false,
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

    function Edit(data) {
        var form = new FormData();
        form.append("userId", data);
//        form.append("jsonRoles", JSON.stringify($('#roles').val()));
//        form.append("roles", $('#roles').val());

        $.ajax({
            type: "POST",
            url: "/admin/edit",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    $("#userDetail").find(".modal-title").html("Thông tin tài khoản - " + result.data.username);
                    $('#id').val(result.data.id);
                    $('#username').val(result.data.username);
                    $('#email').val(result.data.email);
                    $('#roles').val(result.data.roles).trigger("change");
//                    $('#studentRollNumber').val(result.data.studentRollNumber);
//                    $('#inputPassword').val(result.data.password);
                    $('#fullname').val(result.data.fullname);
                    $('#picture').val(result.data.picture);

                    $("#userDetail").modal();
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
                    swal('', 'Thành công', 'success').then(function () {
                        RefreshTable();
                        $("#userDetail").modal("hide");
                    });
                } else {
                    swal('', data.message, 'error');
                }
            }
        });
    }

    function CreateCredential() {
        if ($.trim($('#c-username').val()).length == 0) {
            swal('Cảnh báo!', "Username không được để trống", 'info');
            return;
        } else if ($.trim($('#c-inputPassword').val()).length == 0) {
            swal('Cảnh báo!', "Password không được để trống", 'info');
            return;
        } else if ($.trim($('#c-email').val()).length == 0) {
            swal('Cảnh báo!', "Email không được để trống", 'info');
            return;
        }

        var form = JSON.stringify($('#createForm').serializeJSON());

        $.ajax({
            type: "POST",
            url: "/admin/createNewCredential",
            processData: false,
            contentType: "application/json",
            data: form,
            success: function (data) {
                if (data.success) {
                    swal('', data.message, 'success').then(function () {
                        RefreshTable();
                    });
                } else {
                    swal('', data.message, 'error');
                }
            }
        });
    }

    function deleteCredential(credentialId) {
        swal({
            title: 'Xóa tài khoản',
            text: "Một khi xóa, sẽ xóa toàn bộ tất cả phân quyền liên quan đến tài khoản này",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            cancelButtonText: "Không",
            confirmButtonText: 'Có, hãy xóa!'
        }).then(function (result) {
            if (result) {
                var form = new FormData();
                form.append("credentialId", credentialId);
                swal({
                    title: 'Đang xử lý',
                    html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                    type: 'info',
                    onOpen: function () {
                        swal.showLoading();
                        $.ajax({
                            type: "POST",
                            url: "/admin/deleteExistCredential",
                            processData: false,
                            contentType: false,
                            data: form,
                            success: function (result) {
                                if (result.success) {
                                    swal({
                                        title: 'Thành công',
                                        text: result.message,
                                        type: 'success',
                                        timer: 3000
                                    }).then(function () {
                                        RefreshTable();
                                        $("#createCredentialModal").modal("hide");
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
        })
    }

    function toggleDelete() {
        var column = table.column(6);

        // Toggle the visibility
        column.visible(!column.visible());
    }



</script>
