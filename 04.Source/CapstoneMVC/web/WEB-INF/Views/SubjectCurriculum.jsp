<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<section class="content-header">
    <h1>Các khung chương trình</h1>
</section>

<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box p-t-15">
                <table id="table">
                    <thead>
                        <th>Tên</th>
                        <th>Description</th>
                        <th>Chỉnh sửa</th>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</section>

<script>
    var tbl;

    $(document).ready(function () {
        LoadCurriculum();
    });

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


    function LoadCurriculum() {
        tbl = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/getsubcurriculum",
            "oLanguage": {
                "sSearchPlaceholder": "Tên",
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
                },
                {
                    "aTargets": [2],
                    "mRender": function (data, type, row) {
                        return "<a class='btn btn-primary' onclick='alert(" + row[2] + ");'>" +
                            "<i class='fa fa-eye'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(700);
    }
</script>