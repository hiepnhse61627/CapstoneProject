<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Truy vấn dữ liệu</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Nhập câu truy vấn:</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-9 p-r-0">
                            <input id="inputQuery" class="form-control">
                        </div>
                        <div class="col-md-3 p-l-10">
                            <button type="button" class="btn btn-warning" onclick="LoadTable()" style="width: 80px;">Search</button>
                        </div>
                    </div>
                </div>
            </div>

            <div id="display-result" class="form-group" hidden>
                <div class="row">
                    <div class="title">
                        <h4>Kết quả:</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <table id="myTable">
                                <thead></thead>
                                <tbody></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>

</section>

<script>
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

    $(document).ready(function (e) {
        $('.select').select2();
    });

    $('#inputQuery').keypress(function (e) {
        if (e.keyCode === 13) {
            e.preventDefault();
            LoadTable();
        }
    });

    function LoadTable() {
        if (table != null) {
            table.fnDestroy();
            $('#myTable').empty();
        }

        $('#display-result').show();

        var form = new FormData();
        form.append('queryStr', $('#inputQuery').val());

        $.ajax({
            type: 'POST',
            url: '/get-table-properties',
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                if (result) {
                    table = $('#myTable').dataTable({
                        "bServerSide": true,
                        "bFilter": true,
                        "bRetrieve": true,
                        "bScrollCollapse": true,
                        "bProcessing": true,
                        "bSort": false,
                        "sAjaxSource": "/query-in-database", // url getData.php etc
                        "fnServerParams": function (aoData) {
                            aoData.push({"name": "queryStr", "value": $('#inputQuery').val()})
                        },
                        "aoColumns": result.aoColumns,
                        "aoColumnDefs": result.aoColumnDefs,
                        "bAutoWidth": false,
                    }).fnSetFilteringDelay(1000);
                }
            }
        });
    }
</script>
