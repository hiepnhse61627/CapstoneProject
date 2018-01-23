<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 1/23/2018
  Time: 11:06 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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

    function ExportExcelInfo() {
        $("input[name='objectType']").val(22);
        $('#export-excel-info').submit();
    }


</script>