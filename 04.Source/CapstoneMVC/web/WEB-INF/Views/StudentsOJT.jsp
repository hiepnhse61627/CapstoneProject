<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: hiepnhse61627
  Date: 15/10/2017
  Time: 08:56 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-header">
    <h1>Danh sách sinh viên đủ điều kiện đi OJT</h1>
</section>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <div class="form-group">
                    <div class="box-header">
                        <h4 class="box-title">Chương trình</h4>
                    </div>
                    <select id="program" class="select form-control">
                        <c:forEach var="p" items="${programs}">
                            <option value="${p.id}">${p.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <button type="button" class="btn btn-success" onclick="RefreshTable()">Tìm kiếm</button>
                </div>
            </div>
        </div>
        <div class="col-md-12">
            <table id="table">
                <thead>
                    <tr>
                        <th>MSSV</th>
                        <th>Họ và Tên</th>
                    </tr>
                </thead>
            </table>
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
        $('.select').select2();

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getStudentsOJT", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "programId", "value": $('#program').val()})
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
                    "aTargets": [0, 1],
                    "bSortable": false,
                    "sClass": "text-center",
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    });

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }
</script>
