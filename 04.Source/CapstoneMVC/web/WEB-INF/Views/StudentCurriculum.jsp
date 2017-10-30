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
        width: 16%;
        padding: 0px 15px;
        float: left;
    }

    .medium-col {
        width: 36%;
        padding: 0px 15px;
        float: left;
    }
</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Bảng điểm</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Khung chương trình hiện tại</h4>
                    </div>

                    <div class="my-content">
                        <div class="mark-detail-wrapper col-md-12">
                            <div class="mark-detail">
                                <div class="header">
                                    <div class="first-col"></div>
                                    <div class="other-col">
                                        <div class="small-col"><span>Mã môn</span></div>
                                        <div class="small-col"><span>Tên môn</span></div>
                                        <div class="small-col"><span>Điểm</span></div>
                                        <div class="small-col"><span>Trạng thái</span></div>
                                    </div>
                                </div>

                                <div class="mark-detail-content">
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
        CreateTable();
    });

    function CreateTable() {
        $.ajax({
            type: "POST",
            url: "/studentcurriculum/getmarks",
            processData: false,
            contentType: false,
            success: function (result) {
                var list = result.data;

                var html = "";
                for (var key in list) {
                    var term = key;
                    var markList = list[key];

                    html += "<div class='term-wrapper'>";
                    html += "<div class='term'><span>" + term + "</span></div>";
                    html += "<div class='marks'>";
                    for (var j = 0; j < markList.length; j++) {
                        html += "<div class='mark'>"
                        html += "<div class='medium-col'><span>" + markList[j][0] + "</span></div>";
                        html += "<div class='medium-col'><span>" + markList[j][1] + "</span></div>";
                        html += "<div class='medium-col'><span>" + markList[j][2] + "</span></div>";
                        html += "<div class='medium-col'><span>" + markList[j][3] + "</span></div>";
                        html += "</div>";
                    }
                    html += "</div></div>"
                }

                $('.mark-detail-content').html(html);
            }
        });
    }
</script>