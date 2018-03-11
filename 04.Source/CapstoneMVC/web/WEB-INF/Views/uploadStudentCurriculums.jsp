<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<script src="/Resources/plugins/export-DataTable/dataTables.buttons.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.flash.min.js"></script>
<script src="/Resources/plugins/export-DataTable/jszip.min.js"></script>
<script src="/Resources/plugins/export-DataTable/pdfmake.min.js"></script>
<script src="/Resources/plugins/export-DataTable/vfs_fonts.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.html5.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.print.min.js"></script>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Cập Nhật khung chương trình và trạng thái cho sinh viên</h1>
            <hr>
        </div>
        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <label>Chọn file:</label>
                    </div><br/>
                    <div class="">
                        Bấm vào <a class="link" href="/Resources/FileTemplates/Danh_sach_sinhvien_ki_moi.xlsx">Template</a> để tải
                        về bản mẫu
                    </div><br/>
                    <div class="my-content">
                        <div class="col-md-12">
                            <label for="file" hidden></label>
                            <input type="file" accept=".xlsx, .xls" id="file" name="file" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <div class="left-content m-r-5">
                    <label>Chọn học kỳ để cập nhật điểm khung chương trình và trạng thái cho sinh viên đang học:</label>
                </div>
                <div class="right-content width-30 width-m-70">
                    <select id="semester" class="select form-control">
                        <c:forEach var="semester" items="${semesters}">
                            <option value="${semester.id}">${semester.semester}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <button type="button" onclick="Add()" class="btn btn-success">Import</button>
            </div>
            <br/>

            <div class="form-group">
                <div class="row">
                    <h4 style="color: #e90d7d">Danh sách sinh viên lỗi khi import</h4>
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên</th>
                                <th>Kỳ</th>
                                <th>Lỗi</th>
                            </tr>
                            </thead>
                            <tbody></tbody>
                        </table>
                    </div>
                </div>
            </div>

        </div>
    </div>
</section>



<script>

    var table = null;
    $(document).ready(function(){
        $('.select').select2();

        CreateEmptyDataTable('#table');
    });
    //trước đây chạy = updateStudentCurriculums ver đầu
    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
        form.append('semesterId',$('#semester').val())

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                $.ajax({
                    type: "POST",
                    url: "/updateStudentCurriculumsVer2",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function (result) {
                        isRunning = false;
                        if (result.success) {
                            swal({
                                title: 'Thành công',
                                text: result.message,
                                type: 'success',
                                timer: 3000,
                            });
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                        RefreshTable();
                    }
                });
//                waitForTaskFinish(isRunning);
            },
            allowOutsideClick: false
        });


    }

    //    function waitForTaskFinish(running) {
    //        $.ajax({
    //            type: "GET",
    //            url: "/getlinestatus",
    //            processData: false,
    //            contentType: false,
    //            success: function (result) {
    //                $('#progress').html("<div>(" + result.current + "/" + result.total + ")</div>");
    //                console.log("task running");
    //                if (running) {
    //                    setTimeout("waitForTaskFinish(isRunning)", 50);
    //                }
    //            }
    //        });
    //    }

    function CreateMainTable() {
        table = $('#table').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/processFailImportCurriculums",
            "fnServerParams": function (aoData) {

            },
            "oLanguage": {
                "sSearchPlaceholder": "Tìm kiếm theo MSSV, Tên",
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
                    "aTargets": [0, 1, 2, 3],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
            dom: 'Bfrtip',
            buttons: [
                'copy', 'excel', 'pdf', 'print'
            ],
        }).fnSetFilteringDelay(1000);
    }

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        } else {
            //destroy empty table
            $('#table').dataTable().fnDestroy();
            CreateMainTable();
        }
    }
</script>
