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

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Tạo khung chương trình</h1>
                </div>
                <div class="col-md-3 text-right">
                    <a href="/subcurriculum" class="btn btn-danger btn-with-icon">
                        <i class="fa fa-arrow-left"></i>
                        <div class="m-l-3">QUAY LẠI</div>
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
                            <div class="left-content">
                                <label class="p-t-8">Khóa:</label>
                            </div>
                            <div class="right-content width-40">
                                <input id="name" type="text" class="form-control"/>
                            </div>
                        </div>

                        <div class="my-input-group p-l-30">
                            <div class="left-content">
                                <label class="p-t-8">Ngành:</label>
                            </div>
                            <div class="right-content width-40">
                                <select id="program" class="form-control">
                                    <c:forEach var="p" items="${programs}">
                                        <c:choose>
                                            <c:when test="${data.programId.id == p.id}">
                                                <option selected value="${p.id}">${p.name}</option>
                                            </c:when>
                                            <c:otherwise>
                                                <option value="${p.id}">${p.name}</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
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
                        <div class="row">
                            <div class="col-md-12">
                                <div class="scroll-wrapper custom-scrollbar">
                                    <table class="table" id="table">
                                        <tbody>
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

        Add();
        IntializeRows();
        GetRowsData();
    });

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
        var term = $('.index').length + 1;
        $('#table').find('tbody').append("<tr id='Học kỳ " + term + "' class='index nodrag'>" +
            "<td colspan='2'><b>Học kỳ " + term + "</b></td>" +
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
        var form = new FormData();
        form.append("data", data);
        form.append("name", $('#name').val());
        form.append("programId", $('#program').val());

        $.ajax({
            type: "POST",
            url: "/createcurriculum",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                console.log(result);
                if (result.success) {
                    swal({
                        title: 'Thành công',
                        text: "Tạo khung chương trình thành công!",
                        type: 'success'
                    }).then(function () {
                        window.location = "/subcurriculum";
                    });
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });
    }
</script>
