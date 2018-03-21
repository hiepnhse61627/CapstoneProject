<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 17/03/18
  Time: 10:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="/Resources/plugins/pretty-checkbox/pretty-checkbox.min.css"/>
<link rel="stylesheet" href="/Resources/plugins/materialdesign-icon/css/materialdesignicons.min.css"/>


<style>
    .checkbox-custom {
        margin: 20px 0px 0px 0px;
    }

    .overflowHidden {
        overflow: hidden;
    }
</style>
<section class="content">

    <div class="box">
        <div class="b-header">
            <h1>Phân quyền cho chức vụ</h1>
            <hr>
        </div>


        <div class="b-body">

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Chọn chức vụ: </h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="selectedRole" class="select form-control">
                                <c:forEach var="role" items="${roleList}">
                                    <option value="${role.id}">${role.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Trang được truy cập: </h4>
                    </div>
                    <div class="col-md-12" style="margin-bottom: 20px;">
                        <div class="row">
                            <div class="col-md-6">
                                <button class="btn btn-md" onclick="loadRoleAuthority4Menus()"><i class="glyphicon glyphicon-refresh">
                                </i> Reload
                                </button>
                            </div>
                            <div class="col-md-6">
                                <button class="btn btn-success text-right" onclick="SaveAuthority()"> Save</button>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <div class="form-group">
                            <div class="row">
                                <div class="form-group">
                                    <div class="col-md-12">
                                        <button type="button" class="btn btn-info btn-collapse-group"
                                                data-toggle="collapse"
                                                data-target="#noGroupName">
                                            <i class="glyphicon glyphicon-plus"></i></button>
                                        <span>Dashboard - Trang không phân group</span>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-12 m-t-10">
                                        <div id="noGroupName" class="collapse">
                                            <div class="cold-md-6">
                                                <div class="pretty p-icon p-jelly .checkbox-custom">
                                                    <input type="checkbox" class="check-btn"/>
                                                    <div class="state p-info-o">
                                                        <i class="icon mdi mdi-check-all"></i>
                                                        <label>Check all</label>
                                                    </div>
                                                </div>
                                            </div>
                                            <br/>
                                            <div class="form-group">
                                                <c:forEach var="noGroupItem" items="${noGroupName}">
                                                    <div class="">
                                                        <div class="pretty p-icon p-round p-jelly checkbox-custom col-md-6">
                                                            <input class="chkbox-Menu" type="checkbox"
                                                                   name="modal-transaction-filter"
                                                                   value="${noGroupItem.id}"
                                                            />
                                                            <div class="state p-primary overflowHidden">
                                                                <i class="icon mdi mdi-check"></i>
                                                                <label>${noGroupItem.functionName}
                                                                    - ${noGroupItem.link}</label>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                        <br/>
                    </div>

                    <c:forEach var="menuGrItem" items="${groupMenus}">
                        <div class="col-md-12">
                            <div class="form-group">
                                <div class="row">
                                    <div class="form-group">
                                        <div class="col-md-12">
                                            <button type="button" class="btn btn-info btn-collapse-group"
                                                    data-toggle="collapse"
                                                    data-target="#${menuGrItem.functionGroup}">
                                                <i class="glyphicon glyphicon-plus"></i></button>
                                            <span>${menuGrItem.groupName}</span>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-md-12 m-t-10">
                                            <div id="${menuGrItem.functionGroup}" class="collapse">
                                                <div class="cold-md-6">
                                                    <div class="pretty p-icon p-jelly .checkbox-custom">
                                                        <input type="checkbox" class="check-btn"/>
                                                        <div class="state p-info-o">
                                                            <i class="icon mdi mdi-check-all"></i>
                                                            <label>Check all</label>
                                                        </div>
                                                    </div>
                                                </div>
                                                <br/>
                                                <div class="form-group">
                                                    <div class="">
                                                        <c:forEach var="urlItem" items="${haveGroupName}">
                                                            <c:if test="${urlItem.groupName.contains(menuGrItem.groupName)}">
                                                                <div class="pretty p-icon p-round p-jelly checkbox-custom col-md-6">
                                                                    <input class="chkbox-Menu" type="checkbox"
                                                                           name="modal-transaction-filter"
                                                                           value="${urlItem.id}"
                                                                    />
                                                                    <div class="state p-primary overflowHidden">
                                                                        <i class="icon mdi mdi-check"></i>
                                                                        <label>${urlItem.functionName}
                                                                            - ${urlItem.link} </label>
                                                                    </div>
                                                                </div>
                                                            </c:if>
                                                        </c:forEach>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                            </div>
                            <br/>
                        </div>
                    </c:forEach>

                </div>
            </div>
        </div>
    </div>
</section>

<script>
    $(document).ready(function () {
        $('.select').select2();
        loadRoleAuthority4Menus();
    });

    $('.select').on('change', function () {
        loadRoleAuthority4Menus();
    });

    function collapseChange(btn, collapseArea) {
        var symbol = $(btn).find('i')[0];
        var symClass = $(symbol).attr('class');
        if (symClass.includes("glyphicon-plus")) {
            symClass = symClass.replace("glyphicon-plus", "glyphicon-minus");
            $(collapseArea).collapse("show");
        } else {
            symClass = symClass.replace("glyphicon-minus", "glyphicon-plus");
            $(collapseArea).collapse("hide");
        }
        $(symbol).attr('class', symClass);
    }


    $('.collapse').on('shown.bs.collapse', function () {
        $(this).parent().parent().parent().find(".glyphicon-plus").removeClass("glyphicon-plus").addClass("glyphicon-minus");
    }).on('hidden.bs.collapse', function () {
        $(this).parent().parent().parent().find(".glyphicon-minus").removeClass("glyphicon-minus").addClass("glyphicon-plus");
    });


    $('.check-btn').change(function () {
        var btn = $(this);
        btn.closest('.form-group').find('input:checkbox').each(function () {
            $(this).prop('checked', btn.prop("checked"));
        });
    });

    function loadRoleAuthority4Menus() {
        var form = new FormData();
        form.append("selectedRoleId", $('#selectedRole').val());

        $.ajax({
            type: "POST",
            url: "/admin/getRoleAuthorityData",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    fillCheckbox(result.authorityArray);
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });
    }

    function fillCheckbox(array) {
        $('input:checkbox').each(function () {
            var chkValue = Number($(this).val());
            //inArray(value, array), return index of Value, return -1 if value not exist in array
            var index = $.inArray(chkValue, array);
            if (index !== -1) {
                $(this).prop('checked', true);
            } else {
                $(this).prop('checked', false);
            }

        })
    }

    function SaveAuthority() {
        var allMenus = [];
        $('.chkbox-Menu:checked').each(function() {
            allMenus.push(Number($(this).val()));
        });
        var form = new FormData();
        form.append('allMenuIds', allMenus);
        form.append('selectedRoleId', $('#selectedRole').val());


        swal({
            title: 'Lưu quyền hạn của chức vụ?',
            text: "Cấp quyền cho những trang mà chức vụ này có thể truy cập",
            type: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            cancelButtonText: 'Không',
            confirmButtonText: 'Có, lưu'
        }).then(function (result)  {
            if (result) {
                swal({
                    title: 'Đang xử lý',
                    html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
                    type: 'info',
                    onOpen: function () {
                        swal.showLoading();
                        $.ajax({
                            type: "POST",
                            url: "/admin/updateRolesAuthority",
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
                                    loadRoleAuthority4Menus();
                                } else {
                                    swal('Đã xảy ra lỗi!', result.message, 'error');
                                    loadRoleAuthority4Menus();
                                }
                            }
                        });
                    },
                    allowOutsideClick: false
                });

            }
        });


    }

</script>