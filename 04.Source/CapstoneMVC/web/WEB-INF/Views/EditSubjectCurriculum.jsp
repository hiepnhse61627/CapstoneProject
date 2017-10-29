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
</style>

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
                        <i class="fa fa-arrow-left"></i>
                        <div class="m-l-3">Xuất file excel</div>
                    </a>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Thông tin chi tiết</h4>
                    </div>
                    <div class="my-content">
                        <div class="my-input-group p-l-30">
                            <div class="left-content" style="width: 90px">
                                <label class="p-t-8">Khóa:</label>
                            </div>
                            <div class="right-content width-30">
                                <input id="curriculumName" type="text" class="form-control" value="${data.name}"/>
                            </div>
                        </div>

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

                    </div>
                </div>
            </div>

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
                                                <td>
                                                    <button class="up btn btn-link" type="button"
                                                            style="visibility: hidden"><i
                                                            class="fa fa-arrow-up"></i></button>
                                                    <button class="down btn btn-link" type="button"
                                                            style="visibility: hidden">
                                                        <i
                                                                class="fa fa-arrow-down"></i></button>
                                                    <button class="remove btn btn-link" type="button"
                                                            style="visibility: hidden"><i
                                                            class="fa fa-times"></i></button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                        <div class="col-md-12 m-t-10">
                            <button type="button" onclick="Add()" class="btn btn-default">Thêm học kỳ tiếp theo</button>
                            <button type="button" onclick="Send()" class="btn btn-success">Lưu</button>
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
        dnd = $('#table').tableDnD({
            onDrop: function (table, row) {
                GetRowsData();
            },
        });

        IntializeRows();
        GetRowsData();
    });

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
            "<button class='up btn btn-link' type='button' style='visibility: hidden'><i class='fa fa-arrow-up'></i></button>" +
            "<button class='down btn btn-link' type='button' style='visibility: hidden'><i class='fa fa-arrow-down'></i></button>" +
            "<button class='remove btn btn-link' type='button' style='visibility: hidden'><i class='fa fa-times'></i></button>" +
            "</td>" +
            "</tr>");
        IntializeRows();
        GetRowsData();
        dnd = $('#table').tableDnD({
            onDrop: function (table, row) {
                GetRowsData();
            },
            activeCols: ["draggable"]
        });
    }

    function Send() {
        if ($('#curriculumName').val().trim() == '') {
            swal('', 'Tên khóa không được để trống', 'error');
            return;
        }

        var form = new FormData();
        form.append("data", data);
        form.append("curriculumId", '${data.id}');
        form.append("curriculumName", $('#curriculumName').val());
        form.append("programId", $('#program').val());

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
</script>