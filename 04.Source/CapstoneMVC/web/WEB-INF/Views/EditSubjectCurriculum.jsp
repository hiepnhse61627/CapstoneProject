<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<style>
    .table-row {
        cursor: pointer;
    }

    .table-row:hover {
        background-color: #f4f4f5;
    }
</style>


<section class="content-header">
    <h1>
        Chỉnh sửa khung chương trình
        <small class="pull-right"><a href="/subcurriculum" class="btn btn-primary">Trở về</a></small>
    </h1>
</section>
<section class="content">
    <div class="form-group">
        <div class="col-md-12">
            <div>
                <label for="name">Tên</label>
                <input class="form-control" id="name" value="${data.name}"/>
            </div>
            <div>
                <label for="des">Miêu tả</label>
                <input class="form-control" id="des" value="${data.description}"/>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="col-md-6">
            <div class="col-md-12">
                <table class="table" id="table">
                    <tbody>
                    <c:forEach var="row" items="${list}">
                        <tr id="${row.key}" class="index nodrag">
                            <td colspan="2"><b>${row.key}</b></td>
                        </tr>
                        <c:forEach var="list" items="${row.value}">
                            <tr id="${list.subjectEntity.id}" class="draggable">
                                <td>${list.subjectEntity.id}</td>
                                <td>${list.subjectEntity.name}</td>
                                <td>
                                    <button class="up btn btn-link" type="button" style="visibility: hidden"><i
                                            class="fa fa-arrow-up"></i></button>
                                    <button class="down btn btn-link" type="button" style="visibility: hidden"><i
                                            class="fa fa-arrow-down"></i></button>
                                    <button class="remove btn btn-link" type="button" style="visibility: hidden"><i
                                            class="fa fa-times"></i></button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <div class="col-md-12">
                <button type="button" onclick="Add()" class="btn btn-default">Thêm học ký tiếp theo</button>
                <button type="button" onclick="Send()" class="btn btn-success">Lưu</button>
            </div>
        </div>
        <div class="col-md-6">
            <table id="subjects" class="table table-hover">
                <c:forEach var="s" items="${subs}">
                    <tr class="table-row">
                        <td id="${s.id}">${s.id} - ${s.name}</td>
                        <td>
                            <button type="button" class="btn btn-link" style="visibility: hidden"
                                    onclick="AddSubject('${s.id}', '${s.name}')"><i class="fa fa-plus"></i></button>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
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
        var form = new FormData();
        form.append("data", data);
        form.append("id", '${data.id}');
        form.append("name", $('#name').val());
        form.append("des", $('#des').val());

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