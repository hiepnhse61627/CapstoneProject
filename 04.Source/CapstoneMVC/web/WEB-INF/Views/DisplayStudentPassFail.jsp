<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</section>
<section class="content">
    <h1>
        Danh sách sinh viên nợ môn
    </h1>
    <div class="col-md-12">
        <form id="form">
            <div class="form-group">
                <label for="semester">Học kỳ</label>
                <select id="semester" class="select form-control">
                    <option value="0">All</option>
                    <c:forEach var="semester" items="${semesters}">
                        <option value="${semester.id}">${semester.semester}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label for="subject">Môn học</label>
                <select id="subject" class="select form-control">
                    <option value="0">All</option>
                    <c:forEach var="sub" items="${subjects}">
                        <option value="${sub.id}">${sub.id} - ${sub.name} - ${sub.abbreviation}</option>
                    </c:forEach>
                </select>
            </div>
            <button type="button" onclick="RefreshTable()" class="btn-success btn-success">Tìm kiếm</button>
        </form>
    </div>
    <div class="col-md-12">
        <table id="table">
            <thead>
                <tr>
                    <th>MSSV</th>
                    <th>Tên SV</th>
                    <th>Môn học</th>
                    <th>Lớp</th>
                    <th>Học kỳ</th>
                    <th>Điểm TB</th>
                    <th>Status</th>
                </tr>
            </thead>
        </table>
    </div>
</section>

<script>
    var table = null;

    jQuery.fn.dataTableExt.oApi.fnSetFilteringDelay = function (oSettings, iDelay) {
        var _that = this;

        if (iDelay === undefined) {
            iDelay = 250;
        }

        this.each(function (i) {
            $.fn.dataTableExt.iApiIndex = i;
            var
                $this = this,
                oTimerId = null,
                sPreviousSearch = null,
                anControl = $('input', _that.fnSettings().aanFeatures.f);

            anControl.off('keyup search input').on('keyup search input', function () {
                var $$this = $this;

                if ( (anControl.val().length == 0 || anControl.val().length >= 3) && (sPreviousSearch === null || sPreviousSearch != anControl.val()) ){
                    window.clearTimeout(oTimerId);
                    sPreviousSearch = anControl.val();
                    oTimerId = window.setTimeout(function () {
                        $.fn.dataTableExt.iApiIndex = i;
                        _that.fnFilter(anControl.val());
                    }, iDelay);
                }
            });

            return this;
        });
        return this;
    };

    $(document).ready(function () {
        $('.select').select2();

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "sAjaxSource": "/getstudents", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "semesterId", "value": $('#semester').val()}),
                aoData.push({"name": "subjectId", "value": $('#subject').val()})
            },
            "aoColumnDefs": [
                {
                    "aTargets": [0, 1, 2, 3, 4, 5],
                    "bSortable": false,
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    });

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }
</script>