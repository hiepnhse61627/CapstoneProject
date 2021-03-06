<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<style>
    .table-row {
        cursor: pointer;
    }

    .table-row:hover {
        background-color: #f4f4f5;
    }

    .scroll-wrapper {
        height: 400px;
        overflow: auto;
    }

    .b-footer {
        height: 30px;
    }

    .subject-row-btn {
        visibility: hidden;
        float: left;
    }

    .table > tbody > tr > td {
        vertical-align: middle;
    }
</style>

<div id="subjectDetailModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Chi tiết môn học</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <div class="form-group" style="display: none">
                            <label for="curId"></label>
                            <input disabled id="curId" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="subjectId">Mã môn:</label>
                            <input disabled id="subjectId" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="subjectName">Tên môn:</label>
                            <input id="subjectName" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="prerequisiteSubs">Tiên quyết:</label>
                            <input id="prerequisiteSubs" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="credits">Tín chỉ:</label>
                            <input id="credits" type="text" maxlength="2" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="replacementSubject">Môn thay thế:</label>
                            <input id="replacementSubject" type="text" class="form-control"/>
                        </div>
                        <div class="form-group">
                            <label for="effectionSemester">Học kì bắt đầu áp dụng tiên quyết:</label>
                            <select id="effectionSemester" class="select form-control">
                                <option value="0">-------------------</option>
                                <c:forEach var="effectionSemester" items="${effectionSemester}">
                                    <option value="${effectionSemester.semester}">${effectionSemester.semester}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label for="failMark">Điểm tiên quyết môn</label>
                            <input id="failMark" type="text" class="form-control"/>
                        </div>

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button id="btnSubmit" type="button" class="btn btn-primary" onclick="return confirmChange($('#curId').val(),$('#subjectId').val(),$('#subjectName').val()
                ,$('#prerequisiteSubs').val(),$('#credits').val(),$('#replacementSubject').val(),
                $('#effectionSemester').val(),$('#failMark').val())">Thay đổi thông tin
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Chỉnh sửa khung chương trình</h1>
                </div>
                <div class="col-md-3 text-right">
                    <a href="/subcurriculum" class="btn btn-danger btn-with-icon">
                        <i class="fa fa-arrow-left"></i>
                        <div class="m-l-3">QUAY LẠI</div>
                    </a>
                    <a href="#" class="btn btn-success btn-with-icon" onclick="ExportExcel()">
                        <i class="glyphicon glyphicon-open"></i>
                        <div class="m-l-3">XUẤT DỮ LIỆU</div>
                    </a>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <%--<div class="form-group">--%>
                <%--<div class="row">--%>
                    <%--<div class="title">--%>
                        <%--<h4>Thông tin chi tiết</h4>--%>
                    <%--</div>--%>
                    <%--<div class="my-content">--%>
                        <%--<div class="my-input-group p-l-30">--%>
                        <%--<div class="left-content" style="width: 90px">--%>
                        <%--<label class="p-t-8">Khóa:</label>--%>
                        <%--</div>--%>
                        <%--<div class="right-content width-30">--%>
                        <%--<input id="curriculumName" type="text" class="form-control" value="${data.name}"/>--%>
                        <%--</div>--%>
                        <%--</div>--%>

                        <%--<div class="my-input-group p-l-30">--%>
                        <%--<div class="left-content" style="width: 90px">--%>
                        <%--<label class="p-t-8">Ngành học:</label>--%>
                        <%--</div>--%>
                        <%--<div class="right-content width-30">--%>
                        <%--<select id="program" class="form-control">--%>
                        <%--<c:forEach var="p" items="${programs}">--%>
                        <%--<c:choose>--%>
                        <%--<c:when test="${data.programId.id == p.id}">--%>
                        <%--<option selected value="${p.id}">${p.name}</option>--%>
                        <%--</c:when>--%>
                        <%--<c:otherwise>--%>
                        <%--<option value="${p.id}">${p.name}</option>--%>
                        <%--</c:otherwise>--%>
                        <%--</c:choose>--%>
                        <%--</c:forEach>--%>
                        <%--</select>--%>
                        <%--</div>--%>
                        <%--</div>--%>

                    <%--</div>--%>
                <%--</div>--%>
            <%--</div>--%>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Khung chương trình</h4>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-6">
                        <div class="scroll-wrapper custom-scrollbar">
                            <table id="subjects" class="table table-hover">
                                <c:forEach var="s" items="${subs}">
                                    <tr class="table-row">
                                        <td id="${s.id}">${s.id} - ${s.name}</td>
                                        <td>
                                            <button type="button" class="btn btn-link" style="visibility: hidden"
                                                    onclick="AddSubject('${s.id}', '${s.name}')"><i
                                                    class="fa fa-plus"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="scroll-wrapper custom-scrollbar">
                                    <table class="table" id="table">
                                        <tbody>
                                        <c:forEach var="row" items="${displayList}">
                                            <tr id="${row.key}" class="index nodrag">
                                                <td colspan="2"><b>${row.key}</b></td>
                                            </tr>
                                            <c:forEach var="list" items="${row.value}">
                                                <tr id="${list.subjectId.id}" class="draggable">
                                                    <td>${list.subjectId.id}</td>
                                                    <td>${list.subjectId.name}</td>
                                                    <td>${list.subjectCredits}</td>
                                                    <td>
                                                            <button class="btn btn-link tbl-btn subject-row-btn" type="button" onclick="ShowModal(${list.id})">
                                                                <i class="glyphicon glyphicon-pencil"></i>
                                                            </button>
                                                            <button class="up btn btn-link tbl-btn subject-row-btn" type="button">
                                                                <i class="fa fa-arrow-up"></i>
                                                            </button>
                                                            <button class="down btn btn-link tbl-btn subject-row-btn" type="button">
                                                                <i class="fa fa-arrow-down"></i></button>
                                                            <button class="remove btn btn-link tbl-btn subject-row-btn" type="button">
                                                                <i class="fa fa-times"></i>
                                                            </button>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12 m-t-10">
                                <a type="button" onclick="Add()" class="btn btn-default btn-with-icon">
                                    <i class="fa fa-plus" style="color: #666"></i>
                                    <div style="margin-bottom: 1px; margin-left: 2px;">Thêm học kỳ tiếp theo</div>
                                </a>
                                <button type="button" onclick="Send()" class="btn btn-success">Lưu</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

        <div class="b-footer">
        </div>

        <%-- End body--%>
    </div>

    <form id="export-excel" action="/exportExcel" hidden>
        <input name="objectType"/>
        <input name="curId"/>
    </form>

</section>

<script>
    var data = [];
    var dnd;

    $(document).ready(function () {
//        dnd = $('#table').tableDnD({
//            onDrop: function (table, row) {
//                GetRowsData();
//            },
//        });

        IntializeRows();
        GetRowsData();
    });

    function confirmChange(curId, subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, effectionSemester, failMark) {

        if (confirm("Xác nhận thay đổi thông tin cho môn " + subjectId + "?")) {
            EditSubject(curId, subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, effectionSemester, failMark);
        }
    }

    function ExportExcel() {
        $("input[name='objectType']").val(6);
        $("input[name='curId']").val(${data.id});
        $("#export-excel").submit();
    }

    function GetRowsData() {
        var table = document.getElementById('table');
        var rowLength = table.rows.length;
        data = [];
        for (var i = 0; i < rowLength; i += 1) {
            var row = table.rows[i];
            data.push(row.id);
        }
        console.log(data);
    }

    function IntializeRows() {
        $('#table tr').hover(function () { /* hover first argument is mouseenter*/
            $(this).find('button').css({"visibility": "visible"});
        }, function () {  /* hover second argument is mouseleave*/
            $(this).find('button').css({"visibility": "hidden"});
        });

        $('#subjects tr').hover(function () { /* hover first argument is mouseenter*/
            $(this).find('button').css({"visibility": "visible"});
        }, function () {  /* hover second argument is mouseleave*/
            $(this).find('button').css({"visibility": "hidden"});
        });

        $('.remove').click(function () {
            $(this).parents('tr').first().remove();

            GetRowsData();
        });

        $('.down').click(function () {
            var thisRow = $(this).closest('tr');
            var nextRow = thisRow.next();
            if (nextRow.length > 0) {
                nextRow.after(thisRow);
            }

            GetRowsData();
        });

        $('.up').click(function () {
            var thisRow = $(this).closest('tr');
            var nextRow = thisRow.prev();
            var topRow = nextRow.prev();
            if (nextRow.length > 0 && topRow.length != 0) {
                nextRow.before(thisRow);
            }

            GetRowsData();
        });
    }

    function ShowModal(subjectCurId) {
        var form = new FormData();
        form.append("subjectCurId", subjectCurId)

        $.ajax({
            type: "POST",
            url: "/subcurriculum/getSubject",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    var subject = JSON.parse(result.subject);

                    $("#curId").val(subjectCurId);
                    $("#subjectId").val(subject.subjectID);
                    $("#subjectName").val(subject.subjectName);
                    $("#prerequisiteSubs").val(subject.prerequisiteSubject);
                    $("#credits").val(subject.credits);
                    $("#replacementSubject").val(subject.replacementSubject);

                    $("#subjectDetailModal").modal('toggle');
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });
    }

    function EditSubject(curId, subjectId, subjectName, prerequisiteSubs, credits, replacementSubject, effectionSemester, failMark) {

        $.ajax({
            type: "POST",
            url: "/subjectcur/edit",
            data: {
                "sCurId": curId,
                "sSubjectId": subjectId,
                "sSubjectName": subjectName,
                "sCredits": credits,
                "sReplacement": replacementSubject,
                "sPrerequisite": prerequisiteSubs,
                "sEffectionSemester": effectionSemester,
                "sFailMark": failMark,
            },
            success: function (result) {
                if (result.success) {
                    swal({
                        title: 'Thành công',
                        text: "Đã cập nhật môn học!",
                        type: 'success'
                    }).then(function () {
                        RefreshTable();
                    });
                    $("#subjectDetailModal").modal('toggle');
                } else {
                    swal('', result.message, 'error');
                }
            }
        });


    }

    function Add() {
        var term = $('.index:last td').html().replace("<b>", "").replace("</b>", "").replace("Học kỳ", "");
        $('#table').find('tbody').append("<tr id='Học kỳ " + (parseInt(term) + 1) + "' class='index nodrag'>" +
            "<td colspan='2'><b>Học kỳ " + (parseInt(term) + 1) + "</b></td>" +
            "</tr>");
        IntializeRows();
    }

    function AddSubject(id, name) {
        $('#table').find('tbody').append("<tr id='" + id + "'>" +
            "<td>" + id + "</td>" +
            "<td>" + name + "</td>" +
            "<td>" +
            "<button class='up btn btn-link tbl-btn subject-row-btn' type='button'><i class='fa fa-arrow-up'></i></button>" +
            "<button class='down btn btn-link tbl-btn subject-row-btn' type='button'><i class='fa fa-arrow-down'></i></button>" +
            "<button class='remove btn btn-link tbl-btn subject-row-btn' type='button'><i class='fa fa-times'></i></button>" +
            "</td>" +
            "</tr>");
        IntializeRows();
        GetRowsData();
//        dnd = $('#table').tableDnD({
//            onDrop: function (table, row) {
//                GetRowsData();
//            },
//            activeCols: ["draggable"]
//        });
    }

    function Send() {
//        if ($('#curriculumName').val() == '') {
//            swal('', 'Tên khóa không được để trống', 'error');
//            return;
//        }

        var form = new FormData();
        form.append("data", data);
        form.append("curriculumId", '${data.id}');
//        form.append("curriculumName", $('#curriculumName').val());
//        form.append("programId", $('#program').val());

        $.ajax({
            type: "POST",
            url: "/editcurriculum",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                console.log(result);
                if (result.success) {
                    swal({
                        title: 'Thành công',
                        text: "Đã lưu khung chương trình!",
                        type: 'success'
                    }).then(function () {
                        location.reload();
                    });
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });
    }

    function edit() {

    }
</script>