<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<link rel="stylesheet" href="/Resources/plugins/dist/css/upload-page.css">
<style>
    .min-width-150{
        width: 150px;
    }
</style>
<script src="https://cdn.datatables.net/buttons/1.5.1/js/dataTables.buttons.min.js"></script>
<script src="//cdn.datatables.net/buttons/1.5.1/js/buttons.flash.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/pdfmake.min.js"></script>
<script src="//cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.32/vfs_fonts.js"></script>
<script src="//cdn.datatables.net/buttons/1.5.1/js/buttons.html5.min.js"></script>
<script src="//cdn.datatables.net/buttons/1.5.1/js/buttons.print.min.js"></script>
<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Danh sách sinh viên đã học và đang nợ tín chỉ</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
            <label style="float: left; font-size: 20px;">Danh sách sinh viên đang nợ hơn </label>
            <input id="inputCredits" class="form-control" style="width: 50px; float: left">
            <label style="font-size: 20px">tín chỉ</label>
        </div>

            <div class="form-group">
                <button type="button" onclick="GetResult()" class="btn btn-success">Tìm kiếm</button>
                <%--<button type="button" class="btn btn-success" onclick="ExportExcel()">Xuất dữ liệu</button>--%>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table" class="cell-border">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên sinh viên</th>
                                <th>Các môn đang rớt</th>
                                <th>Các môn đã học lại</th>
                                <th>Số tín chỉ nợ</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>
<form id="export-excel" action="/exportExcel" hidden>
    <input name="objectType"/>
    <input name="credits"/>
</form>
<script>
    $(document).ready(function () {

    });
    var table = null;
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

    function GetResult() {
         if (table != null) {
        //     table._fnPageChange(0);
        //     table._fnAjaxUpdate();
            table.ajax.reload();
         }

        table = $('#table').DataTable({
            // "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/studentFailCredits", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "numOfCredits", "value": $('#inputCredits').val()})
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
                {   "aTargets":[1],
                    "sClass":"min-width-150",
                    "bSortable":false,
                    "sWidth": "150px"
                },
                {
                    "aTargets": [0, 1, 2, 3, 4],
                    "bSortable": false,
                },
                {
                    "aTargets": [0, 1, 2, 3, 4],
                    "sClass": "text-center",
                }
            ],
            "bAutoWidth": false,
            dom: 'Bfrtip',
            lengthMenu: [[10,25,50],[ '10 dòng', '25 dòng', '50 dòng', 'Tất cả' ]],
            buttons: [
                {
                    extend:    'pageLength',
                    text:      'Hiển thị 10 dòng',
                },
                {
                    extend:    'excel',
                    text:      'Xuất Excel',
                },
                {
                    extend:    'pdf',
                    text:      'Xuất PDF',
                },
            ]
        });
    }
</script>