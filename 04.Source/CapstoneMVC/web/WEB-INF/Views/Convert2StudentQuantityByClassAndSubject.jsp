<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 1/23/2018
  Time: 11:06 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<script src="/Resources/plugins/export-DataTable/dataTables.buttons.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.flash.min.js"></script>
<script src="/Resources/plugins/export-DataTable/jszip.min.js"></script>
<script src="/Resources/plugins/export-DataTable/pdfmake.min.js"></script>
<script src="/Resources/plugins/export-DataTable/vfs_fonts.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.html5.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.print.min.js"></script>

<script src="/Resources/plugins/DataTable-RowGroup/dataTables.rowGroup.min.js"></script>
<link rel="stylesheet" href="/Resources/plugins/DataTable-RowGroup/rowGroup.dataTables.min.css"></link>
<style>

</style>
<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Số lượng sinh viên theo lớp môn</h1>
            <hr>
        </div>
        <img src=""/>
        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <label>Chọn file:</label>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <label for="file" hidden></label>
                            <input type="file" accept=".xlsx, .xls" id="file" name="file"/>
                        </div>
                    </div>
                </div>
            </div>
            <div class="form-group">
                <button type="button" onclick="Add()" class="btn btn-success">Upload</button>
            </div>
            <br/>
            <div class="form-group">
                <h5><b>Nhấn để down file được convert</b></h5>
                <button type="button" disabled id="exportEXCEL" onclick="ExportExcelInfo()" class="btn btn-success">Download</button>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-student">
                        <thead>
                        <th>Môn học</th>
                        <th>Tên lớp</th>
                        <th>Số lượng sinh viên</th>
                        </thead>
                        <tbody></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<form id="export-excel-info" action="/exportExcel" hidden>
    <input name="objectType"/>

</form>

<script>
    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
        // form.append('semesterId', $('#semester').val());
        $.ajax({
            type: "POST",
            url: "/convertToStudentQuantity",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                // window.open(result.downloadPath);
                if (result.success) {
                    swal({
                        title: 'Thành công',
                        text: "Đã import các sinh viên!",
                        type: 'success',
                        timer: 1500
                    });
                    loadData4Table();
                    $('#exportEXCEL').removeAttr('disabled');
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });

        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();

            },
            // allowOutsideClick: false
        });
    }

    function loadData4Table() {
        //use new Datatable not legacy datatable
        $('#tbl-student').DataTable({
            "serverSide": false,
            "filter": true,
            "retrieve": true,
            "scrollX": "100%",
            "scrollCollapse": true,
            "processing": true,
            "sort": false,
            "sAjaxSource": "/convertToStudentQuantityData",
            "fnServerParams": function (aoData) {
                aoData.push({"name": "semesterId", "value": $('#semester').val()});
                aoData.push({"name": "programId", "value": $('#program').val()});
            },
            "language": {
                "searchPlaceholder": "Tên hoặc MSSV",
                "search": "Tìm kiếm:",
                "zeroRecords": "Không có dữ liệu phù hợp",
                "info": "Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng",
                "emptyTable": "Không có dữ liệu",
                "infoFiltered": " - lọc ra từ _MAX_ dòng",
                "lengthMenu": "Hiển thị _MENU_ dòng",
                "processing": "Đang xử lý...",
                "paginate": {
                    "next": "<i class='fa fa-chevron-right'></i>",
                    "previous": "<i class='fa fa-chevron-left'></i>"
                }
            },
            "columnDefs": [
                {
                    "targets": [0, 1, 2],
                    "bSortable": false,
                    "className": "text-center",
                },
                {
                    "targets": [0],
                    "visible": false
                }

            ],
            "autoWidth": false,
             // dom: 'Bfrtip',
             lengthMenu: [[10,25,50],[ '10 dòng', '25 dòng', '50 dòng', 'Tất cả' ]],
            // buttons: [
            //     {
            //         extend:    'excel',
            //         text:      'Xuất Excel',
            //     },
            //     {
            //         extend:    'pdf',
            //         text:      'Xuất PDF',
            //     },
            // ],
            "order": [[ 0, 'asc' ]],
            rowGroup: {
                startRender: function ( rows, group ) {
                    var total = rows
                        .data()
                        .pluck(2)
                        .reduce( function (a, b) {
                            return a + b*1;
                        }, 0);


                    return $('<tr/>')
                        .append( '<td colspan="1" class=""><b>'+group+'</b></td>' )
                        .append( '<td class="text-center"><b>'+total+'</b></td>' )
                },
                dataSrc: 0
            }

        });
    }


    function ExportExcelInfo() {
        $("input[name='objectType']").val(22);
        $('#export-excel-info').submit();
    }


</script>