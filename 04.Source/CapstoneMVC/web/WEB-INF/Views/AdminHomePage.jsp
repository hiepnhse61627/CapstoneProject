<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/31/2017
  Time: 1:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Tất cả tài khoản</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="table">
                        <thead>
                        <th>Username</th>
                        <th>Hình avatar</th>
                        <th>Email</th>
                        <th>Tên</th>
                        <th>Roles</th>
                        </thead>
                        <tbody>
                        </tbody>
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
                                <label for="fullname" class="col-sm-2 control-label">Name</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" id="fullname" name="fullname"
                                           placeholder="Fullname"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="email" class="col-sm-2 control-label">Email</label>
                                <div class="col-sm-10">
                                    <input type="email" class="form-control" id="email" placeholder="Email"
                                           name="email"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="role" class="col-sm-2 control-label">Role</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" id="role" placeholder="Role"
                                           name="role"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="role" class="col-sm-2 control-label">Picture link</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" id="picture" placeholder="picture"
                                           name="picture"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="studentRollNumber" class="col-sm-2 control-label">MSSV</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" id="studentRollNumber"
                                           placeholder="Rollnumber"
                                           name="studentRollNumber"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="username" class="col-sm-2 control-label">Username</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" id="username" name="username"
                                           placeholder="Username"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="inputPassword" class="col-sm-2 control-label">Password</label>
                                <div class="col-sm-10">
                                    <input type="password" class="form-control" id="inputPassword" name="password"
                                           placeholder="Password">
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-2 col-sm-10">
                                <input type="checkbox"> I agree to the <a href="#">terms and conditions</a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-danger" onclick="Confirm()">Submit</button>
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
            "sAjaxSource": "/admin/getUsers", // url getData.php etc
            "oLanguage": {
                "sSearchPlaceholder": "",
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
                    "aTargets": [0, 2, 3, 4],
                    "bSortable": false,
                    "sClass": "text-center",
                },
                {
                    "aTargets": [1],
                    "mRender": function (data, type, row) {
                        return "<img class='profile-user-img img-responsive img-circle' src='" + data + "' alt='User profile picture'>";
                    }
                },
                {
                    "aTargets": [5],
                    "mRender": function (data, type, row) {
                        return "<button type='button' class='btn btn-warning' onclick='Edit(" + data + ")'>Edit</button>";
                    }
                }
            ],
            "bAutoWidth": false,
        });
    });

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
                    console.log(result.data);

                    $("#markDetail").find(".modal-title").html("Chi tiết điểm - " + result.data.fullname);
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
