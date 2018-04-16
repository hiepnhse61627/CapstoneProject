<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 12/04/18
  Time: 11:31 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">

<script src="/Resources/plugins/export-DataTable/dataTables.buttons.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.flash.min.js"></script>
<script src="/Resources/plugins/export-DataTable/jszip.min.js"></script>
<script src="/Resources/plugins/export-DataTable/pdfmake.min.js"></script>
<script src="/Resources/plugins/export-DataTable/vfs_fonts.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.html5.min.js"></script>
<script src="/Resources/plugins/export-DataTable/buttons.print.min.js"></script>

<style>
    ul.custom {
        list-style-type: none;
        -webkit-padding-start: 150px;
    }
</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-5 title">
                    <h1>Chứng nhận sinh viên tốt nghiệp</h1>
                </div>
                <div class="col-md-7 text-right">
                    <button type="button" class="btn btn-warning btn-with-icon" onclick="ExportGraduationCertification()">
                        <i class="glyphicon glyphicon-download"></i>
                        <div>Xuất chứng nhận tốt nghiệp</div>
                    </button>
                </div>
            </div>
            <hr>
        </div>
        <div class="b-body">
            <div class="form-group">
                <div class="right-content width-30 width-m-70">
                    <label class="p-t-8">Chọn sinh viên:</label>
                    <select id="cb-student" class="select"> </select>
                </div>
            </div>
            <div class="form-group">
                <button type="button" onclick="GetGraduateCertificate()"
                        title="Chức năng thực hiện đồng bộ hóa điểm bên FAP"
                        class="btn btn-success">Tìm kiếm
                </button>
            </div>

            <br/>
            <div class="form-group">
                <div class="row" id="certificate-content">
                    <div class="col-md-12 text-center">
                        <h2>GIẤY XÁC NHẬN</h2>
                    </div>
                    <div class="col-md-12">
                        <div class="text-left col-md-12">
                            <u><i><b>Trường Đại học FPT xác nhận:</b></i></u>
                        </div>
                        <div class="col-md-6">
                            Sinh viên:
                        </div>
                        <div class="col-md-6">
                            Sinh ngày:
                        </div>
                        <div class="col-md-6">
                            Mã số sinh viên:
                        </div>
                        <div class="col-md-6">
                            Ngành học:
                        </div>
                        <div class="col-md-6">
                            Hệ
                        </div>
                        <div class="col-md-12">
                            Tình trạng tốt nghiệp:
                            <ul class="custom">
                                <li>Tốt nghiệp năm</li>
                                <li>Số hiệu văn bằng</li>
                                <li>Số vào sổ cấp văn bằng</li>
                                <li>Quyết định: ngày</li>
                            </ul>
                        </div>
                        <div class="col-md-6">
                            Tại trường Đại học FPT
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<form id="export-excel-2" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="programId"/>
    <input name="semesterId"/>
</form>

<script>
    $(document).ready(function () {
        CreateSelect();
    });

    function CreateSelect() {
        $('#cb-student').select2({
            width: 'resolve',
            minimumInputLength: 2,
            ajax: {
                url: '/getStudentList',
                delay: 1000, //delay search
                data: function (params) {
                    var queryParameters = {
                        searchValue: params.term
                    }
                    return queryParameters;
                },
                processResults: function (result) {
                    if (result.success) {
                        return {
                            results: $.map(result.items, function (item) {
                                return {
                                    id: item.value,
                                    text: item.text,
                                }
                            })
                        };
                    } else {
                        swal('', 'Có lỗi xảy ra, vui lòng thử lại', 'warning');
                    }
                }
            }
        });
    }

    function GetGraduateCertificate() {

        var notSelectedStudents = [];
        swal({
            title: 'Đang xử lý',
            html: '<div class="form-group">Tiến trình có thể kéo dài vài phút</div>',
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/getGraduatedStudentCertificate",
                    data: {
                        "studentId": $("#cb-student").val(),
                    },
                    success: function (result) {
                        if (result.success) {
                            var certificate = result.certificate;
                            var student = result.student;
                            swal({
                                title: 'Thành công',
                                text: result.message,
                                type: 'success',
                                timer: 3000,
                            });
                            var content = '<div class="col-md-12 text-center">\n' +
                                '                        <h2>GIẤY XÁC NHẬN</h2>\n' +
                                '                    </div>\n' +
                                '                    <div class="col-md-12">\n' +
                                '                        <div class="text-left col-md-12">\n' +
                                '                            <u><i><b>Trường Đại học FPT xác nhận:</b></i></u>\n' +
                                '                        </div>\n' +
                                '                        <div class="col-md-6">\n' +
                                '                            Sinh viên: '+student.fullName +' \n' +
                                '                        </div>\n' +
                                '                        <div class="col-md-6">\n' +
                                '                            Sinh ngày: '+ student.dateOfBirth +'\n' +
                                '                        </div>\n' +
                                '                        <div class="col-md-6">\n' +
                                '                            Mã số sinh viên: '+student.rollNumber+'\n' +
                                '                        </div>\n' +
                                '                        <div class="col-md-6">\n' +
                                '                            Ngành học: '+student.program+'\n' +
                                '                        </div>\n' +
                                '                        <div class="col-md-6">\n' +
                                '                            Hệ: '+certificate.form+'\n' +
                                '                        </div>\n' +
                                '                        <div class="col-md-12">\n' +
                                '                            Tình trạng tốt nghiệp:\n' +
                                '                            <ul class="custom">\n' +
                                '                                <li>Tốt nghiệp năm '+certificate.date+'</li>\n' +
                                '                                <li>Số hiệu văn bằng: '+certificate.diplomaCode +'</li>\n' +
                                '                                <li>Số vào sổ cấp văn bằng: '+certificate.certificateCode+'</li>\n' +
                                '                                <li>Quyết định: ngày ' + certificate.date+'</li>\n' +
                                '                            </ul>\n' +
                                '                        </div>\n' +
                                '                        <div class="col-md-6">\n' +
                                '                            Tại trường Đại học FPT\n' +
                                '                        </div>\n' +
                                '                    </div>';
                            $("#certificate-content").html(content);
                        } else {
                            swal('', result.message, 'error');
                            $("#certificate-content").html("");
                        }
                    }
                });
            },
            allowOutsideClick: false
        });


    }

    function ExportGraduationCertification() {
        ExportExcelPDF2();
        var sId = $("#cb-student").val();
        if (sId.length == 0) {
            swal({
                type: 'error',
                title: 'Lỗi',
                text: 'Chưa chọn sinh viên',
            });
            return;
        }
        swal({
            title: 'Đang xử lý',
            html: "<div class='form-group'>Tiến trình có thể kéo dài vài phút!<div><div id='progress' class='form-group'></div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                waitForTaskFinish(isRunning);
            },
            allowOutsideClick: false
        });
    }

    function waitForTaskFinish(running) {
        $.ajax({
            type: "GET",
            url: "/getStatusExport",
            processData: false,
            contentType: false,
            success: function (result) {
                console.log("task running");
                if (result.running) {
                    running = result.running;
                    $('#progress').html("<div>(" + result.status + ")</div>");
                    setTimeout(function () {
                            waitForTaskFinish(running);
                        }
                        , 1000);
                } else {
                    swal({
                        title: 'Thành công',
                        text: "Tạo file thành công!",
                        type: 'success',
                        timer: 3000,
                    });
                }
            }
        });
    }

    function ExportExcelPDF2() {
        $("input[name='objectType']").val();
        $("input[name='studentId']").val($("#cb-student").val());

        $('#export-excel-2').submit();
    }


</script>
