<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: hiepnhse61627
  Date: 23/09/2017
  Time: 09:59 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content">
    <div class="row">
        <div class="col-md-12">
            <div class="box">
                <div class="box-header with-border">
                    <h2 class="box-title">Truy vấn dữ liệu</h2>
                </div>

                <form class="form-horizontal">
                    <div class="box-body">
                        <div class="form-group">
                            <div class="col-md-9">
                                <input id="inputQuery" class="form-control" placeholder="Nhập câu truy vấn">
                            </div>
                            <div class="col-md-3">
                                <button type="button" class="btn btn-warning" onclick="LoadTable()">Search</button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <table id="table">
        <thead>
        <tr>
            <th>Header 1</th>
            <th>Header 2</th>
            <th>Header 3</th>
            <th>Header 4</th>
            <th>Header 5</th>
            <th>Header 6</th>
            <th>Header 7</th>
            <th>Header 8</th>
            <th>Header 9</th>
            <th>Header 10</th>
        </tr>
        </thead>
    </table>
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

                if ( (anControl.val().length == 0 || anControl.val().length >= 3) && (sPreviousSearch === null || sPreviousSearch != anControl.val()) ){
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

    function LoadTable() {
        if (table != null) {
            table.fnDestroy();
            table = null;
        }

        table = $('#table').dataTable({
            "bServerSide": true,
            "bFilter": true,
            "bRetrieve": true,
            "bScrollCollapse": true,
            "bProcessing": true,
            "sAjaxSource": "/query-in-database", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "queryStr", "value": $('#inputQuery').val()})
            },
//            "aoColumnDefs": [
//                {
//                    "aTargets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
//                    "bSortable": false,
//                },
//            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }

//    function search() {
//        var form = new FormData();
//        form.append("queryStr", $('#inputQuery').val());
//
//        $.ajax({
//           type: 'POST',
//           url: '/query-in-database',
//           processData: false,
//           contentType: false,
//           data: form,
//           success: function (result) {
//               console.log(result.databaseData);
//           }
//        });
//    }
</script>
