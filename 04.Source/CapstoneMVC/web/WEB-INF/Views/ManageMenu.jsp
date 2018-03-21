<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 17/03/18
  Time: 4:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet"  href="/Resources/plugins/pretty-checkbox/pretty-checkbox.min.css"/>
<link rel="stylesheet"  href="/Resources/plugins/materialdesign-icon/css/materialdesignicons.min.css"/>

<section class="content">

    <div class="box">
        <div class="b-header">
            <h1>Quản lý chức vụ</h1>
            <hr>
        </div>


        <div class="b-body">

            <div class="form-group">
                <button type="button" onclick="" class="btn btn-success" data-toggle="modal" data-target="#createMenu">
                    <i class="fa fa-plus"></i> Tạo menu
                </button>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>Nhóm chức năng</th>
                                <th>Tên menu</th>
                                <th>Tên nhóm chức năng</th>
                                <th>Link</th>
                                <th>Edit</th>
                                <th>Xóa</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<div id="createMenu" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Tạo menu mới</h4>
            </div>
            <div class="modal-body">
                <div class="title">
                    <label>Nhóm chức năng:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="newFunctionGroup" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
                <div class="title">
                    <label>Tên menu:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="newMenuName" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
                <div class="title">
                    <label>Tên nhóm chức năng:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="newGroupName" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
                <div class="title">
                    <label>Link:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="newLink" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="CreateMenu()" class="btn btn-info btn-md">Tạo</button>
            </div>
        </div>

    </div>
</div>

<div id="menuEditor" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Sửa menu</h4>
            </div>
            <div class="modal-body">
                <input type="hidden" id="e-menuId" class="form-control"/>
                <div class="title">
                    <label>Nhóm chức năng:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="e-functionGroup" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
                <div class="title">
                    <label>Tên menu:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="e-menuName" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
                <div class="title">
                    <label>Tên nhóm chức năng:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="e-groupName" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
                <div class="title">
                    <label>Link:</label>
                </div>
                <div class="my-content">
                    <div class="form-group">
                        <div class="col-md-6">
                            <input type="text" id="e-Link" class="form-control"/>
                        </div>
                        <br/>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" onclick="UpdateMenu()" class="btn btn-info btn-md">Cập nhật</button>
            </div>
        </div>

    </div>
</div>

<script>
    var table = null;

    $(document).ready(function () {
        CreateMenuTable();
    });

    function CreateMenuTable() {
        table = $('#table').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/admin/currentMenuData", // url getData.php etc
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
                    "aTargets": [0, 1, 2, 4, 5],
                    "sClass": "text-center",
                    "bSortable": false
                },
                {
                    "aTargets": [4],
                    //Id, function Group, func name, group name, link
                    "mRender": function (data, type, row) {
                        var result = "<a class='btn btn-success tbl-btn' onclick='openMenuEditor(" + row[4] + ", " + JSON.stringify(row[0]) +
                            ", " + JSON.stringify(row[1]) + ", " + JSON.stringify(row[2]) + "," + JSON.stringify(row[3]) + ")'>" +
                            "<i class='glyphicon glyphicon-pencil'></i></a>";
                        return result;
                    }
                },
                {
                    "aTargets": [5],
                    //Id, function Group, func name, group name, link
                    "mRender": function (data, type, row) {
                        var result = "<a class='btn btn-danger tbl-btn' onclick='deleteMenu(" + row[4] + ")'>" +
                            "<i class='glyphicon glyphicon-minus-sign'></i></a>";
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
            CreateMenuTable();
        }
    }


    function CreateMenu() {
        var form = new FormData();
         if ($.trim($('#newMenuName').val()).length == 0) {
            swal('Cảnh báo!', "Tên menu không được để trống", 'info');
            return;
        } else if ($.trim($('#newLink').val()).length == 0) {
            swal('Cảnh báo!', "Link không được để trống", 'info');
            return;
        }
        form.append('newFunctionGroup', $.trim($('#newFunctionGroup').val()));
        form.append('newMenuName', $.trim($('#newMenuName').val()));
        form.append('newGroupName', $.trim($('#newGroupName').val()));
        form.append('newLink', $.trim($('#newLink').val()));

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/admin/createNewMenu",
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

    $('#createMenu').on('hidden.bs.modal', function () {
        // do something…
        $('#newFunctionGroup').val("");
        $('#newMenuName').val("");
        $('#newGroupName').val("");
        $('#newLink').val("");
    })

    function openMenuEditor(menuId, funcGroup, funcName, GName, Link) {
        $('#menuEditor').modal('show');
        $('#e-menuId').val(menuId);
        $('#e-functionGroup').val(funcGroup);
        $('#e-menuName').val(funcName);
        $('#e-groupName').val(GName);
        $('#e-Link').val(Link);
    }

    function UpdateMenu() {
        var form = new FormData();
         if ($.trim($('#e-menuName').val()).length == 0) {
            swal('Cảnh báo!', "Tên menu không được để trống", 'info');
            return;
        }  else if ($.trim($('#e-Link').val()).length == 0) {
            swal('Cảnh báo!', "Link không được để trống", 'info');
            return;
        }
        form.append('e-functionGroup', $.trim($('#e-functionGroup').val()));
        form.append('e-menuName', $.trim($('#e-menuName').val()));
        form.append('e-groupName', $.trim($('#e-groupName').val()));
        form.append('e-Link', $.trim($('#e-Link').val()));
        form.append('e-menuId', $.trim($('#e-menuId').val()));

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/admin/updateExistMenu",
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



    function deleteMenu(menuId){
        swal({
            title: 'Xóa menu',
            text: "Một khi xóa, sẽ xóa toàn bộ tất cả phân quyền liên quan đến menu này",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            cancelButtonText: "Không",
            confirmButtonText: 'Có, hãy xóa!'
        }).then(function (result) {
            if (result) {
                var form = new FormData();
                form.append("menuId", menuId);
                swal({
                    title: 'Đang xử lý',
                    html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                    type: 'info',
                    onOpen: function () {
                        swal.showLoading();
                        $.ajax({
                            type: "POST",
                            url: "/admin/deleteExistMenu",
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
        })
    }
</script>
