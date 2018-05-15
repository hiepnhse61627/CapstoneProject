<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 15/05/18
  Time: 9:57 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Xuất bảng điểm theo học kỳ</h1>
            <hr>
        </div>
        <div class="b-body">
            <div class="form-group">
                <div class="left-content m-r-5">
                    <label>Chọn học kỳ để xuất điểm:</label>
                </div>
                <div class="right-content width-30 width-m-70">
                    <select id="semester" class="select form-control">
                        <c:forEach var="semester" items="${semesters}">
                            <option value="${semester.id}">${semester.semester}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="left-content m-r-5">
                    <label class="p-t-8">Ngành:</label>
                </div>
                <div class="right-content width-30 width-m-70">
                    <select id="program" class="select form-control">
                        <option value="-1">Tất cả</option>
                        <c:forEach var="program" items="${programList}">
                            <option value="${program.id}">${program.name}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
            <div class="form-group">
                <button type="button" onclick="ExportExcelAcademicTranscript()"
                        title="Chức năng thực hiện xuất điểm của tất cả sinh viên theo học kỳ"
                        class="btn btn-success">Xuất bảng điểm
                </button>
                <button type="button" onclick="ExportExcelAcademicTranscriptInfo()"
                        title="Chức năng thực hiện xuất danh sách sinh viên theo thứ tự bảng điểm được in ra"
                        class="btn btn-success">Xuất thông tin danh sách sinh viên
                </button>
            </div>

            <br/>
        </div>
    </div>
</section>
<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="semesterId"/>
    <input name="programId"/>
</form>

<script>
    function ExportExcel() {
        $("input[name='objectType']").val(20);
        $("input[name='semesterId']").val($("#semester").val());
        $("input[name='programId']").val($("#program").val());
        $("#export-excel").submit();
    }

    function ExportExcelInfo() {
        $("input[name='objectType']").val(21);
        $("input[name='semesterId']").val($("#semester").val());
        $("input[name='programId']").val($("#program").val());

        $("#export-excel").submit();
    }

    function ExportExcelAcademicTranscript() {
        ExportExcel();
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


    function ExportExcelAcademicTranscriptInfo() {
        ExportExcelInfo();
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
</script>
