<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .form-date-range {
        position: relative;
    }

    .form-date-range i {
        position: absolute;
        bottom: 10px;
        right: 10px;
        top: auto;
        cursor: pointer;
    }
</style>
<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách học kỳ</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <h4>Tạo học kì mới</h4>

            <div class="form-group">
                <label for="s">Tên học kì: </label>
                <input id="s" placeholder="Tên" class="form-control"/>
            </div>

            <div class="form-group form-date-range">
                <label for="scheduleDate">Ngày bắt đầu - kết thúc:</label>
                <input id="scheduleDate" type="text" class="form-control"/>
                <i class="fa fa-calendar"></i>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="Create()">Tạo học kì</button>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-semester">
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Học kỳ</th>
                            <th>Đóng mở</th>
                            <th>Xem thư mục</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="semester" items="${semesters}" varStatus="count">
                            <tr>
                                <td class="text-center">${count.count}</td>
                                <td class="text-center">${semester.entity.semester}</td>
                                <td class="text-center">
                                    <input type="checkbox" data-on-text="Mở" data-off-text="Đóng"
                                           value="${semester.entity.id}"
                                            <c:if test="${semester.entity.active eq true}">
                                                checked
                                            </c:if>
                                    />
                                </td>
                                <td class="text-center">
                                    <c:if test="${semester.finished}">
                                        <a href="${semester.link}">Download Link</a>
                                    </c:if>
                                    <c:if test="${not semester.finished}">
                                        ${semester.link}
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>

<script>
    var startDate;
    var endDate;

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

                if ((anControl.val().length == 0 || anControl.val().length >= 2) && (sPreviousSearch === null || sPreviousSearch != anControl.val())) {
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
        startDate = endDate = moment().format('DD/MM/YYYY');
        $('#scheduleDate').daterangepicker({
            startDate: moment(),
            endDate: moment(),
            minDate: moment(),
//            drops: "up",
            locale: {
                format: 'DD/MM/YYYY'
            }
        }, function (start, end) {
            startDate = start.format('DD/MM/YYYY');
            endDate = end.format('DD/MM/YYYY');
            $('#startDate span').html(startDate + ' - ' + endDate);
        });

        $("input[type='checkbox']").bootstrapSwitch();
        $("input[type='checkbox']").on('switchChange.bootstrapSwitch', function (event, state) {
            $.ajax({
                type: "GET",
                url: "/managerrole/semester/edit",
                data: {"semesterId": $(this).val(), "onoff": $(this).is(':checked')},
                success: function (result) {
                    console.log(result);
                }
            });
            if ($(this).is(':checked') == false) {
                $.ajax({
                    type: "GET",
                    url: "/managerrole/export",
                    data: {"semesterId": $(this).val()},
                    success: function (result) {
                        console.log(result);
                    }
                });
            }
        });
        $("#tbl-semester").DataTable();
    });

    function Create() {
        $.ajax({
            type: "GET",
            url: "/managerrole/semester/create",
            data: {
                name: $("#s").val(),
                startDate: $('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY'),
                endDate: $('#scheduleDate').data('daterangepicker').endDate.format('DD/MM/YYYY'),
            },

            success: function (result) {
                console.log(result);
                if (result.success) {
                    swal('', 'Thành công!', 'success').then(function () {
                        location.reload();
                    });
                } else {
                    swal('', result.msg, 'error');
                }
            }
        });
    }
</script>