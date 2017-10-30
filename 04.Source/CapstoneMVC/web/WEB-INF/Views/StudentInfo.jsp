<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .form-group .my-content .my-input-group .right-content {
        width: 76%;
    }

    .first-col {
        width: 12%;
        padding-right: 10px;
        text-align: right;
    }

    .other-col {
        width: 88%;
    }

    .mark-detail-wrapper {
        width: 100%;
        text-align: center;
        font-size: 16px;
    }

    .mark-detail-wrapper .mark-detail {
        width: 80%;
        background-color: #f9f9f9;
    }

    .mark-detail-wrapper .mark-detail .header {
        display: flex;

        background-color: #3c8dbc;
        color: white;
        border-bottom: 1px solid #333333;
    }

    .mark-detail-wrapper .mark-detail .header,
    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper {
        padding: 5px 0px;
    }

    .mark-detail-wrapper .mark-detail .header span {
        font-weight: 600;
        margin: 0px;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper {
        width: 100%;
        display: flex;
        flex-flow: row wrap;

        border-bottom: 1px solid #333333;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .term {
        padding-top: 5px;
        font-weight: 600;
        float: left;
        width: 12%;
        padding-right: 10px;
        text-align: right;
        /*color: #de2121;*/
        color: chocolate;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .marks {
        width: 88%;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .marks .mark {
        display: flex;
        background: none;
        padding: 5px 0px;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .marks .mark:nth-child(odd) {
        background-color: white;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .average {
        width: 100%;
        font-weight: 600;
        padding: 5px 0px;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .average .title {
        width: 12%;
        float: left;
        margin: 0px;
        padding: 0px 10px;
        text-align: right;
    }

    .mark-detail-wrapper .mark-detail .mark-detail-content .term-wrapper .average .number {
        width: 88%;
        float: left;
        text-align: left;
        padding-left: 30px;
    }

    .small-col {
        width: 12%;
        padding: 0px 15px;
        float: left;
    }

    .medium-col {
        width: 15%;
        padding: 0px 15px;
        float: left;
    }

    .large-col {
        width: 34%;
        padding: 0px 15px;
        float: left;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Thông tin sinh viên</h1>
                </div>
                <div class="col-md-3 text-right">
                    <a href="/studentList" class="btn btn-danger btn-with-icon">
                        <i class="fa fa-arrow-left"></i>
                        <div class="m-l-3">QUAY LẠI</div>
                    </a>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Thông tin chi tiết</h4>
                    </div>
                    <div class="my-content">
                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 80px">
                                    <label class="p-t-8">Tên:</label>
                                </div>
                                <div class="right-content">
                                    <input id="studentName" disabled type="text" class="form-control"
                                           value="${student.fullName}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 80px">
                                    <label class="p-t-8">Giới tính:</label>
                                </div>
                                <div class="right-content">
                                    <input id="gender" disabled type="text" class="form-control"
                                           value="${gender}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 80px">
                                    <label class="p-t-8">MSSV:</label>
                                </div>
                                <div class="right-content">
                                    <input id="rollNumber" disabled type="text" class="form-control"
                                           value="${student.rollNumber}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 80px">
                                    <label class="p-t-8">Ngày sinh:</label>
                                </div>
                                <div class="right-content">
                                    <input id="dateOfBirth" disabled type="text" class="form-control"
                                           value="${dateOfBirth}"/>
                                </div>
                            </div>
                        </div>

                        <div class="row m-0">
                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 80px">
                                    <label class="p-t-8">Ngành học:</label>
                                </div>
                                <div class="right-content">
                                    <input id="program" disabled type="text" class="form-control"
                                           value="${program}"/>
                                </div>
                            </div>

                            <div class="my-input-group width-40 p-l-30 text-left">
                                <div class="left-content" style="width: 80px">
                                    <label class="p-t-8">Khóa ngành:</label>
                                </div>
                                <div class="right-content">
                                    <input id="curriculum" disabled type="text" class="form-control"
                                           value="${curriculum}"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Thông tin điểm</h4>
                    </div>

                    <div class="my-content">
                        <div hidden class="no-data col-md-12">
                            Không có dữ liệu
                        </div>
                        <div hidden class="mark-detail-wrapper col-md-12">
                            <div class="mark-detail">
                                <div class="header">
                                    <div class="first-col"></div>
                                    <div class="other-col">
                                        <div class="small-col"><span>Mã môn</span></div>
                                        <div class="large-col text-left"><span>Tên môn</span></div>
                                        <div class="medium-col"><span>Học kỳ</span></div>
                                        <div class="small-col"><span>Tín chỉ</span></div>
                                        <div class="small-col"><span>Điểm</span></div>
                                        <div class="medium-col"><span>Trạng thái</span></div>
                                    </div>
                                </div>

                                <div class="mark-detail-content">
                                    <%--<div class="term-wrapper">--%>
                                        <%--<div class="term"><span>Học kỳ 1</span></div>--%>
                                        <%--<div class="marks">--%>
                                            <%--<div class="mark">--%>
                                                <%--<div class="small-col"><span>CSD211</span></div>--%>
                                                <%--<div class="medium-col text-left"><span>Lập trình blap</span></div>--%>
                                                <%--<div class="small-col"><span>3</span></div>--%>
                                                <%--<div class="small-col"><span>8.3</span></div>--%>
                                                <%--<div class="small-col"><span>Passed</span></div>--%>
                                            <%--</div>--%>
                                        <%--</div>--%>
                                        <%--&lt;%&ndash;<div class="average">&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;<div class="title">Điểm TB</div>&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;<div class="number">7.3</div>&ndash;%&gt;--%>
                                        <%--&lt;%&ndash;</div>&ndash;%&gt;--%>
                                    <%--</div>--%>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>



<script>
    $(document).ready(function () {
        LoadMarkList();
    });

    function LoadMarkList() {
        var form = new FormData();
        form.append("studentId", '${student.id}');
        $.ajax({
            type: "POST",
            url: "/studentList/marks",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result.success) {
                    RenderMarkList(result.detailList)
                } else {
                    swal('Đã xảy ra lỗi!', result.message, 'error');
                }
            }
        });
    }

    function RenderMarkList(list) {
        debugger

        if (list.length > 0) {
            $('.mark-detail-wrapper').show();
            $('.no-data').hide();
        } else {
            $('.mark-detail-wrapper').hide();
            $('.no-data').show();
        }

        var html = "";
        for (var i = 0;  i < list.length; ++i) {
            var term = list[i].term;
            var markList = list[i].markList;

            var termStr = term != -1 ? "Học kỳ " + term : "Môn khác";

            html += "<div class='term-wrapper'>";
            html += "<div class='term'><span>" + termStr + "</span></div>";
            html += "<div class='marks'>";
            for (var j = 0; j < markList.length; ++j) {
                var mark = markList[j];

                html += "<div class='mark'>"
                html += "<div class='small-col'><span>" + mark.subject + "</span></div>";
                html += "<div class='large-col text-left'><span>" + mark.subjectName + "</span></div>";
                html += "<div class='medium-col'><span>" + mark.semester + "</span></div>";
                html += "<div class='small-col'><span>" + mark.credits + "</span></div>";
                html += "<div class='small-col'><span>" + mark.averageMark + "</span></div>";
                html += "<div class='medium-col'><span>" + mark.status + "</span></div>";
                html += "</div>";
            }
            html += "</div></div>"
        }

        $('.mark-detail-content').html(html);
    }



</script>

