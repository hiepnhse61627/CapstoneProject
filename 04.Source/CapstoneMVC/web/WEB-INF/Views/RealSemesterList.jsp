<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
            <div class="row">
                <div class="col-md-12">
                    <h4>Tạo học kì mới</h4>
                    <div class="col-md-12">
                        <label for="s">Tên học kì: </label>
                        <input id="s" placeholder="Tên" class="form-control"/>
                    </div>
                    <div class="col-md-12">
                        <button type="button" class="btn btn-success" onclick="Create()">Tạo</button>
                    </div>
                </div>
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
                                <td class="text-center">${semester.semester}</td>
                                <td class="text-center">
                                    <input type="checkbox" data-on-text="Mở" data-off-text="Đóng" value="${semester.id}"
                                            <c:if test="${semester.active eq true}">
                                                checked
                                            </c:if>
                                    />
                                </td>
                                <td class="text-center"></td>
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
        $("input[type='checkbox']").bootstrapSwitch();
        $("input[type='checkbox']").on('switchChange.bootstrapSwitch', function (event, state) {
            $.ajax({
                type: "GET",
                url: "/managerrole/semester/edit",
                data: { "semesterId": $(this).val(), "onoff": $(this).is(':checked') },
                success: function (result) {
                    console.log(result);
                }
            });
            if ($(this).is(':checked') == false) {
                $.ajax({
                    type: "GET",
                    url: "/managerrole/export",
                    data: { "semesterId": $(this).val()},
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
            data: { "name": $("#s").val() },
            success: function (result) {
                console.log(result);
                location.reload();
            }
        });
    }
</script>