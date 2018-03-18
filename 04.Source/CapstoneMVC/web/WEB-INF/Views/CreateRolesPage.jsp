<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 1/16/2018
  Time: 5:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Quản lý chức vụ</h1>
            <hr>
        </div>

        <div class="b-body">

            <div class="form-group">
                <button type="button" onclick="" class="btn btn-success" data-toggle="modal" data-target="#createRoles">
                    <i class="fa fa-plus"></i> Tạo chức vụ
                </button>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>No.</th>
                                <th>Chức vụ</th>
                                <th>Edit</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<div id="createRoles" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Tạo chức vụ mới</h4>
            </div>
            <div class="modal-body">
                <div class="title">
                    <label>Nhập chức vụ mới:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="newRole" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="CreateRole()" class="btn btn-info">Tạo</button>
            </div>
        </div>

    </div>
</div>

<div id="roleEditor" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Sửa chức vụ</h4>
            </div>
            <div class="modal-body">
                <input type="hidden" id="e-roleId" class="form-control"/>
                <div class="title">
                    <label>Tên chức vụ:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="e-roleName" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="UpdateRole()" class="btn btn-info btn-md">Cập nhật</button>
            </div>
        </div>

    </div>
</div>

<script>
    var table = null;

    $(document).ready(function () {
        CreateRolesTable();
    });

    function CreateRolesTable() {
        table = $('#table').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/admin/currentRolesData", // url getData.php etc
            "oLanguage": {
                "sSearchPlaceholder": "Tìm theo MSSV, Môn học, Học Kỳ, Status",
                "sSearch": "Tìm kiếm:",
                "sZeroRecords": "Không có dữ liệu phù hợp",
                "sInfo": 'Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng',
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
                    "sClass": "text-center",
                    "bSortable": false
                },
                {
                    "aTargets": [2],
                    //Id, function Group, func name, group name, link
                    "mRender": function (data, type, row) {
                        var result = "<a class='btn btn-success tbl-btn' onclick='editRole(" + row[2] + "," + JSON.stringify(row[1]) + ")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                        return result;
                    }
                }

            ],
            "bAutoWidth": false
        });
    }

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        } else {
            //destroy empty table
            $('#table').dataTable().fnDestroy();
            CreateRolesTable();
        }
    }


    function CreateRole() {
        var form = new FormData();
        if ($.trim($('#newRole').val()).length == 0) {
            swal('Cảnh báo!', "Chức vụ không được để trống", 'info');
            return;
        }
        form.append('newRole', $.trim($('#newRole').val()));

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/admin/createNewRole",
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
                            });
                            RefreshTable();
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                    }
                });
            },
            allowOutsideClick: false
        });
    }

    $('#createRoles').on('hidden.bs.modal', function () {
        // do something…
        $('#newRole').val("");
    })

    function editRole(roleId, currentRoleName) {
        $('#e-roleId').val(roleId);
        $('#e-roleName').val(currentRoleName);
        $('#roleEditor').modal('show');
    }

    function UpdateRole() {
        var form = new FormData();
        if ($.trim($('#e-roleName').val()).length == 0) {
            swal('Cảnh báo!', "Chức vụ không được để trống", 'info');
            return;
        }
        form.append('roleName', $.trim($('#e-roleName').val()));
        form.append('roleId', $.trim($('#e-roleId').val()));

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/admin/updateExistRole",
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
                            });
                            RefreshTable();
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
