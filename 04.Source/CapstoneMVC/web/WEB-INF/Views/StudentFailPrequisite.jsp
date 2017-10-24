<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .content {
        min-height: 700px;
    }

    .cbox-row {
        width: 100%;
        display: flex;
    }

    .cbox-subject-wrapper {
        display: flex;
        margin-bottom: 6px;
    }

    .cbox-subject-wrapper .cbox {
        float: left;
        display: inline;
        margin-right: 5px;
    }

    #btn-display-subject,
    #btn-search {
        width: 85px;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Danh sách sinh viên rớt môn tiên quyết</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Chọn môn</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="select" class="select form-control">
                                <option value="0">All</option>
                                <c:forEach var="sub" items="${subs}">
                                    <option value="${sub.id}">${sub.id} - ${sub.name} - ${sub.abbreviation}</option>
                                </c:forEach>
                            </select>
                            <div id="comment"></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Chọn môn tiên quyết</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <div id="prequisite" class="m-b-7"></div>
                        </div>
                        <div class="col-md-12">
                            <button id="btn-search" type="button" class="btn btn-default m-r-2" onclick="GetStudents()">Tìm kiếm</button>
                            <button id="btn-display-subject" data-display="show" type="button" class="btn btn-primary">Ẩn môn</button>
                            <button type="button" class="btn btn-success" onclick="ExportExcel()">Xuất dữ liệu</button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Danh sách kết quả</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <table id="table">
                                <thead>
                                <tr>
                                    <th>MSSV</th>
                                    <th>Tên</th>
                                    <th>Môn hiện tại</th>
                                    <th>Môn tiên quyết</th>
                                    <th>Lớp</th>
                                    <th>Semester</th>
                                    <th>Điểm</th>
                                    <th>Tình trạng</th>
                                </tr>
                                </thead>
                            </table>
                            <input type="hidden" id="hidden" value=""/>
                        </div>
                    </div>
                </div>
            </div>


        </div>
    </div>
</section>

<!-- MODAL -->
<div id="markDetail" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Chi tiết điểm</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table-mark-detail">
                            <thead>
                            <tr>
                                <th>Môn học</th>
                                <th>Học kỳ</th>
                                <th>Số lần học</th>
                                <th>Điểm trung bình</th>
                                <th>Trạng thái</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<!-- checkbox template -->
<div id="cbox-template" hidden>
    <div class="col-md-4 col-xs-12 cbox-subject-wrapper">
        <div class="cbox"><input type='checkbox'/></div>
        <div class="text"></div>
    </div>
</div>

<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="subId"/>
    <input name="prequisiteId"/>
    <input name="sSearch"/>
</form>

<script>
    var table = null;
    var tableMarkDetail = null;

    $(document).ready(function () {
        $('.select').select2();

        $('#select').on('change', function () {
            Get();
        })

        Get();
        GetStudents();
    });

    $('#btn-display-subject').click(function () {
       var display = $(this).data("display");
       if (display == "show") {
           $(this).data("display", "hide");
           $(this).html("Hiện môn");
           $('#prequisite').removeClass('m-b-7');
           $('#prequisite').slideUp('slow');
       } else {
           $(this).data("display", "show");
           $(this).html("Ẩn môn");
           $('#prequisite').addClass('m-b-7');
           $('#prequisite').slideDown('slow');
       }
    });

    function Get() {
        var form = new FormData();
        form.append("subId", $('#select').val());

        $.ajax({
            type: "POST",
            url: "/getAllPrequisites",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    if (result.data.length == 0) {
                        $('#comment').html("<font color='red'>Môn này không có môn tiên quyết</font>");
                        $("#prequisite").html("");
                    } else {
                        var html = "<div class='cbox-row'>";
                        html += createCheckBox("Tất cả", -1, 'all', '');

                        var count = 1;
                        var numOfColumn = 3;
                        $.each(result.data, function (i, item) {
                            ++count;
                            html += count % numOfColumn == 1 ? "<div class='cbox-row'>" : "";
                            html += createCheckBox(item.name, item.value, '', 'pre');
                            html += count % numOfColumn == 0 ? "</div>" : "";
                        });
                        html += count % numOfColumn != 0 ? "</div>" : "";

                        $("#prequisite").html(html);
                        $('#comment').html("");

                        $('#prequisite input').iCheck({
                            checkboxClass: 'icheckbox_square-blue',
                            radioClass: 'iradio_square-blue',
                            increaseArea: '20%' // optional
                        });

                        $('#all').on('ifClicked', function (event) {
                            console.log($('#all').not(':checked').length);
                            if ($('#all').not(':checked').length > 0) {
                                $('#prequisite input[name=pre]').iCheck('check');
                            } else {
                                $("#prequisite input[name=pre]").iCheck('uncheck');
                            }
                        });

                        $('#prequisite input[name=pre]').on('ifToggled', function (event) {
                            if ($("#prequisite input[name=pre]:checked").length == $('#prequisite input[name=pre]').length) {
                                $('#all').iCheck('check');
                            } else {
                                $('#all').iCheck('uncheck');
                            }
                        });
                    }
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'error');
                }
            }
        });
    }

    function GetStudents() {
        var jsonString = [];
        $("#prequisite input:checked").each(function () {
            if ($(this).val() != -1) {
                jsonString.push($(this).val());
            }
        });

        $('#hidden').val(jsonString.join(';'));

        if (table == null || table == 'undefined') {
            table = $('#table').dataTable({
                "bServerSide": true,
                "bFilter": true,
                "bRetrieve": true,
                "sScrollX": "100%",
                "bScrollCollapse": true,
                "bProcessing": true,
                "bSort": false,
                "sAjaxSource": "/getFailStudents", // url getData.php etc
                "fnServerParams": function (aoData) {
                    aoData.push({"name": "subId", "value": $('#select').val()}),
                        aoData.push({"name": "prequisiteId", "value": $('#hidden').val()})
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
                        "aTargets": [0, 2, 3, 4, 5, 6, 7],
                        "bSortable": false,
                        "sClass": "text-center",
                    },
                    {
                        "aTargets": [1],
                        "mRender": function (data, type, row) {
                            return "<a onclick='GetAllStudentMarks(" + row[8] + ")'>" + data + "</a>";
                        }
                    },
                ],
                "bAutoWidth": false,
            });
        } else {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }

    function GetAllStudentMarks(studentId) {
        var form = new FormData();
        form.append("studentId", studentId);

        $.ajax({
            type: "POST",
            url: "/student/getAllLatestMarks",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {

                if (result.success) {
                    result.studentMarkDetail = JSON.parse(result.studentMarkDetail);

                    $("#markDetail").find(".modal-title").html("Chi tiết điểm - " + result.studentMarkDetail.studentName);
                    CreateMarkDetailTable(result.studentMarkDetail.markList);
                    $("#markDetail").modal();
                } else {
                    swal('', 'Có lỗi xảy ra, vui lòng thử lại sau', 'warning');
                }
            }
        });
    }

    function CreateMarkDetailTable(dataSet) {
        if (tableMarkDetail != null) {
            tableMarkDetail.fnDestroy();
        }

        tableMarkDetail = $('#table-mark-detail').dataTable({
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "data": dataSet,
            "aoColumns": [
                {"mData": "subject"},
                {"mData": "semester"},
                {"mData": "repeatingNumber"},
                {"mData": "averageMark"},
                {"mData": "status"},
            ],
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
                    "aTargets": [0, 1, 2, 3, 4],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        });
        $("#markDetail").modal();
    }

    function createCheckBox(text, value, id, name) {
        var template = $('#cbox-template');
        var cBox = template.find("input[type='checkbox']");
        var cBoxText = template.find(".text");

        cBoxText.html(text);
        cBox.val(value);

        if (id != "") {
            cBox.attr("id", id);
        }

        if (name != "") {
            cBox.attr("name", name);
        }

        return template.html();
    }

    function ExportExcel() {
        var jsonString = [];
        $("#prequisite input:checked").each(function () {
            if ($(this).val() != -1) {
                jsonString.push($(this).val());
            }
        });

        $('#hidden').val(jsonString.join(';'));

        $("input[name='objectType']").val(3);
        $("input[name='subId']").val($('#select').val());
        $("input[name='prequisiteId']").val($('#hidden').val());
        $("input[name='sSearch']").val(table.api().context[0].oPreviousSearch.sSearch);

        $("#export-excel").submit();
    }
</script>

