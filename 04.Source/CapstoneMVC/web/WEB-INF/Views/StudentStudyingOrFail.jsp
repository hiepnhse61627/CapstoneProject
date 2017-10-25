<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/25/2017
  Time: 8:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Danh sách sinh viên đang học và đang nợ</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Semester</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="semester" class="select form-control">
                                <%--<option value="0">-- Tất cả --</option>--%>
                                <c:forEach var="sem" items="${semester}">
                                    <option value="${sem.id}">${sem.semester}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Curriculum</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="curriculum" class="select form-control">
                                <%--<option value="0">-- Tất cả --</option>--%>
                                <c:forEach var="cur" items="${cur}">
                                    <option value="${cur.id}">${cur.programId.name}_${cur.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Kỳ</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="term" class="select form-control">
                                <%--<option value="0">-- Tất cả --</option>--%>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="RefreshTable()">Tìm kiếm</button>
            </div>


            <div class="col-md-12">
                <table id="table">
                    <thead>
                    <tr>
                        <th>MSSV</th>
                        <th>Tên SV</th>
                        <th>Môn học</th>
                        <th>Học kỳ</th>
                        <th>Điểm TB</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                </table>
            </div>

            <div class="col-md-12">
                <table id="table2">
                    <thead>
                    <tr>
                        <th>MSSV</th>
                        <th>Tên SV</th>
                        <th>Môn học</th>
                        <th>Học kỳ</th>
                        <th>Điểm TB</th>
                        <th>Status</th>
                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</section>

<script>
    var table = null;
    var table2 = null;

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

                if ((anControl.val().length == 0 || anControl.val().length >= 3) && (sPreviousSearch === null || sPreviousSearch != anControl.val())) {
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

        $('#curriculum').on('change', function () {
            GetCurriculum($("#curriculum").val());
        })

        GetCurriculum($("#curriculum").val());
    });

    function GetCurriculum(id) {
        var form = new FormData();
        form.append("term", id);

        $.ajax({
            type: "POST",
            url: "/getcurriculumtermlist",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    var data = result.data;
                    $('#term').find('option').remove();
                    data.forEach(function (item) {
                        $('#term').append("<option value='" + item + "'>" + item + "</option>");
                    });
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'warning');
                }
            }
        });
    }

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        } else {
            table = $('#table').dataTable({
                "bServerSide": true,
                "bFilter": true,
                "bRetrieve": true,
                "sScrollX": "100%",
                "bScrollCollapse": true,
                "bProcessing": true,
                "bSort": false,
                "sAjaxSource": "/getstudyingorfail", // url getData.php etc
                "fnServerParams": function (aoData) {
                    aoData.push({"name": "curId", "value": $('#curriculum').val()}),
                        aoData.push({"name": "term", "value": $('#term').val()}),
                        aoData.push({"name": "semester", "value": $('#semester').val()})
                },
                "oLanguage": {
                    "sSearchPlaceholder": "",
                    "sSearch": "Tìm kiếm:",
                    "sZeroRecords": "Không có dữ liệu phù hợp",
                    "sInfo": "Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng",
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
                        "aTargets": [0, 1, 2, 3, 4, 5],
                        "bSortable": false,
                    },
                ],
                "bAutoWidth": false,
            }).fnSetFilteringDelay(1000);
        }

        if (table2 != null) {
            table2._fnPageChange(0);
            table2._fnAjaxUpdate();
        } else {
            table2 = $('#table2').dataTable({
                "bServerSide": true,
                "bFilter": true,
                "bRetrieve": true,
                "sScrollX": "100%",
                "bScrollCollapse": true,
                "bProcessing": true,
                "bSort": false,
                "sAjaxSource": "/getstudyingorfail2", // url getData.php etc
                "fnServerParams": function (aoData) {
                    aoData.push({"name": "curId", "value": $('#curriculum').val()}),
                        aoData.push({"name": "term", "value": $('#term').val()}),
                        aoData.push({"name": "semester", "value": $('#semester').val()})
                },
                "oLanguage": {
                    "sSearchPlaceholder": "",
                    "sSearch": "Tìm kiếm:",
                    "sZeroRecords": "Không có dữ liệu phù hợp",
                    "sInfo": "Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng",
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
                        "aTargets": [0, 1, 2, 3, 4, 5],
                        "bSortable": false,
                    },
                ],
                "bAutoWidth": false,
            }).fnSetFilteringDelay(1000);
        }
    }
</script>
