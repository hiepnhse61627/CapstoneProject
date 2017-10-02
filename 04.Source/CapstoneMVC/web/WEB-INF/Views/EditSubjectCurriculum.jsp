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

<div>
    Name: ${data.name}
</div>
<div>
    Descriptuin: ${data.description}
</div>
<div class="col-md-6">
    <div class="col-md-12">
        <table class="table" id="table">
            <tbody>
            <c:forEach var="row" items="${list}" >
                <tr id="${row.key}" class="index">
                    <td><b>${row.key}</b></td>
                </tr>
                <c:forEach var="list" items="${row.value}">
                    <tr id="${list.subjectEntity.id}">
                        <td>${list.subjectEntity.id}</td>
                        <td>${list.subjectEntity.name}</td>
                    </tr>
                </c:forEach>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <div class="col-md-12">
        <button type="button" onclick="Add()">Add New Term</button>
        <button type="button" onclick="Send()">Send</button>
    </div>
</div>
<div class="col-md-6">
    <table id="subjects" class="table table-hover">
        <c:forEach var="s" items="${subs}">
            <tr class="table-row">
                <td id="${s.id}">${s.id} - ${s.name}</td>
                <td><button type="button" style="display: none" onclick="AddSubject('${s.id}', '${s.name}')">Add</button></td>
            </tr>
        </c:forEach>
    </table>
</div>



<script>
    var data = []
    var dnd;

    $(document).ready(function () {
        dnd = $('#table').tableDnD({
            onDrop: function (table, row) {
                data = []
                var rows = table.tBodies[0].rows;
                for (var i = 0; i < rows.length; i++) {
                    data.push(rows[i].id);
                }

            }
        });

        $('#subjects tr').hover(function(){ /* hover first argument is mouseenter*/
            $(this).find('button').css({"display": "block"});
        },function(){  /* hover second argument is mouseleave*/
            $(this).find('button').css({"display": "none"});
        });
    });

    function Add() {
        var term = $('.index').length + 1;
        $('#table').find('tbody').append("<tr id='Học kỳ " + term + "' class='index'><td><b>Học kỳ " + term + "</b></td></tr>");
    }

    function AddSubject(id, name) {
        $('#table').find('tbody').append("<tr id='" + id + "'><td>" + id + "</td><td>" + name + "</td></tr>");
        if (dnd != null) {
            dnd = $('#table').tableDnD({
                onDrop: function (table, row) {
                    data = []
                    var rows = table.tBodies[0].rows;
                    for (var i = 0; i < rows.length; i++) {
                        data.push(rows[i].id);
                    }

                }
            });
        }
    }

    function Send() {
        var form = new FormData();
        form.append("data", data);
        form.append("id", '${data.id}');

        $.ajax({
            type: "POST",
            url: "/editcurriculum",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                console.log(result);
            }
        });
    }
</script>