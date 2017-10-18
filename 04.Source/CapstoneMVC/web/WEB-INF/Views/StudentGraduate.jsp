<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .form-group .my-content .my-input-group .left-content {
        min-width: 70px;
    }

    .arrow-up {
        width: 0;
        height: 0;
        border-left: 5px solid transparent;
        border-right: 5px solid transparent;

        border-bottom: 5px solid #00a65a;
        position: absolute;
        left: 70%;
    }

    .btn-excel:hover ~ .excel-modal {
        /*opacity: 1;*/
        /*transition-delay: 0.5s;*/
    }

    .excel-modal {
        width: 100%;
        position: absolute;
        z-index: 100;
        right: 0;
        /*display: none;*/

        /*transition: 0s;*/
        /*opacity: 0;*/
    }

    .excel-modal .my-content-wrap {
        padding-top: 4px;
        position: relative;

    }

    .excel-modal {
        visibility: hidden;
        opacity: 0;
        transform: translateY(-2px);
        transition: all 0.3s ease-in-out 0s, visibility 0s linear 0.3s, z-index 0s linear 0.01s;
    }

    .export-content:hover .excel-modal {
        visibility: visible;
        opacity: 1;
        transform: translateY(0%);
        transition-delay: 0.3s;
    }

    .excel-modal .my-content-wrap .content {
        min-height: 0px !important;
        width: 150px;
        padding: 10px;
        float: right;
        display: block;
        border-radius: 5px;
        border: 1px #00a65a solid;
        background: white;

    }

    .excel-modal .my-content-wrap .content .item,
    .excel-modal .my-content-wrap .sub-item-wrapper .item {
        width: 100%;
        color: black;
        text-align: left;
        min-height: 30px;
        vertical-align: middle;
        line-height: 26px;
        display: flex;
        font-weight: 600;
        cursor: pointer;

    }

    .excel-modal .my-content-wrap .content .item:hover,
    .excel-modal .my-content-wrap .sub-item-wrapper .item:hover {
        /*border-bottom: 1px #4CAF50 solid;*/
        color: #dc2929;
    }

    .excel-modal .my-content-wrap .content .item .fa {
        display: flex;
        font-size: 15px;
        margin-right: 10px;
        margin-top: 5px;
    }

    .excel-modal .my-content-wrap .sub-item-wrapper {
        min-width: 75px;
        position: absolute;
        background: white;
        border: 1px #00a65a solid;
        padding: 5px 15px 5px 15px;
        border-radius: 25px 0px;
        /*display: none;*/
    }

    .excel-modal .my-content-wrap .sub-item-wrapper .item {
        text-align: center;
        display: block;
    }

    .sub-item-1 {
        right: 151px;
        top: 10px;
    }

    .sub-item-2 {
        right: 151px;
        top: 37px;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách sinh viên được xét tốt nghiệp</h1>
                </div>
                <div class="col-md-3 text-right">
                    <div class="export-content">
                        <div class="btn btn-success btn-with-icon btn-excel">
                            <i class="glyphicon glyphicon-open"></i>
                            <div>XUẤT DỮ LIỆU</div>
                        </div>
                        <div class="excel-modal">
                            <div class="arrow-up"></div>
                            <div class="my-content-wrap">
                                <div class="content">
                                    <div class="item">
                                        <i class="fa fa-angle-left"></i>
                                        <span>In tất cả sinh viên</span>
                                        <div class="sub-item-wrapper">
                                            <div class="item"><span>Word</span></div>
                                            <div class="item"><span>PDF</span></div>
                                            <div class="item"><span>Excel</span></div>
                                        </div>
                                    </div>
                                    <div class="item">
                                        <i class="fa fa-angle-left"></i>
                                        <span>In một sinh viên</span>
                                    </div>
                                </div>
                                <%--<div class="sub-item-wrapper sub-item-1">--%>
                                <%--<div class="item"><span>Word</span></div>--%>
                                <%--<div class="item"><span>PDF</span></div>--%>
                                <%--<div class="item"><span>Excel</span></div>--%>
                                <%--</div>--%>
                                <%--<div class="sub-item-wrapper sub-item-2">--%>
                                <%--<div class="item">Word</div>--%>
                                <%--<div class="item">PDF</div>--%>
                                <%--<div class="item">Excel</div>--%>
                                <%--</div>--%>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Thông tin bộ lọc</h4>
                    </div>
                    <div class="my-content p-l-10">
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Tín chỉ:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <input class="form-control bfh-number" id="credit" type="number" value="145" min="1"/>
                            </div>
                        </div>
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Tín chỉ CN:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <input class="form-control bfh-number" id="sCredit" type="number" value="145" min="1"/>
                            </div>
                        </div>
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Ngành:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="program" class="select form-control">
                                    <option value="0" selected>Tất cả</option>
                                    <c:forEach var="program" items="${programList}">
                                        <option value="${program.id}">${program.name}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="my-input-group">
                            <div class="left-content m-r-5">
                                <label class="p-t-8">Học kỳ:</label>
                            </div>
                            <div class="right-content width-30 width-m-70">
                                <select id="semester" class="select form-control">
                                    <option value="0" selected>Tất cả</option>
                                    <c:forEach var="semester" items="${semesterList}">
                                        <option value="${semester.id}">${semester.semester}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-12">
                        <button class="btn btn-success" onclick="Refresh()">Tìm kiếm</button>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên</th>
                                <th>Tín chỉ</th>
                                <th>Tín chỉ CN</th>
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
    var timeOut = 0;

    $(document).ready(function () {
        $('.select').select2();

//        $('#credit').on("keypress change", function (e) {
//            if (e.keyCode === 13) {
//                e.preventDefault();
//            } else {
//                clearTimeout(timeOut);
//                timeOut = setTimeout(Refresh, 250);
//            }
//        });

        $('#credit').keyup(function (e) {
            if ($(this).val().length == 0) {
                $(this).val("1");
            }
        });

        $('#sCredit').keyup(function (e) {
            if ($(this).val().length == 0) {
                $(this).val("1");
            }
        });

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/processgraduate", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "programId", "value": $("#program").val()}),
                aoData.push({"name": "semesterId", "value": $("#semester").val()}),
                aoData.push({"name": "credit", "value": $("#credit").val()}),
                aoData.push({"name": "sCredit", "value": $("#sCredit").val()})
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
                    "aTargets": [0, 1, 2, 3],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        });
    });

    function Refresh() {
        table._fnPageChange(0);
        table._fnAjaxUpdate();
    }
</script>
