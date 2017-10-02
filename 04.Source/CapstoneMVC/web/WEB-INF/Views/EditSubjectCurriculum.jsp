<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div>
    Nsme: ${data.name}
</div>
<div>
    Descriptuin: ${data.description}
</div>
<div class="col-md-12">
    <table class="table" id="table">
        <c:forEach var="row" items="${list}">
            <tr id="${row.key}"><td><b>${row.key}</b></td></tr>
            <c:forEach var="list" items="${row.value}">
                <tr id="${list.subjectEntity.id}">
                    <td>${list.subjectEntity.id}</td>
                    <td>${list.subjectEntity.name}</td>
                </tr>
            </c:forEach>
        </c:forEach>
    </table>
</div>
<div class="col-md-12">
    <button type="button" onclick="Send()">Send</button>
</div>


<script>
    var data = []

    $(document).ready(function () {
        $('#table').tableDnD({
            onDrop: function(table, row) {
                data = []
                var rows = table.tBodies[0].rows;
                for (var i = 0; i < rows.length; i++) {
                    data.push(rows[i].id);
                }

            }
        });
    });

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