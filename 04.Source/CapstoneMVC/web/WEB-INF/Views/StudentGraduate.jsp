<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/16/2017
  Time: 1:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Danh sách sinh viên được xét tốt nghiệp</h1>
                </div>
                <div class="col-md-3 text-right">
                    <button type="button" class="btn btn-success btn-with-icon">
                        <i class="glyphicon glyphicon-open"></i>
                        <%--<i class="fa fa-upload"></i>--%>
                        <div>XUẤT DỮ LIỆU</div>
                    </button>
                </div>
            </div>
            <hr>
        </div>
        <div class="form-group">
            <div class="col-md-12">
                Số tín chỉ <input id="number" type="number" value="145" min="1"/>
                <button type="button" class="btn btn-default" onclick="Refresh()">Refresh</button>
            </div>
        </div>
        <div class="form-group">
            <div class="row">
                <div class="col-md-12">
                    <div class="bg-gray-light">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>MSSV</th>
                                <th>Tên</th>
                                <th>Tín chỉ</th>
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

    $(document).ready(function () {
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
                aoData.push({"name": "creditPass", "value": $("#number").val()})
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
                    "aTargets": [0, 1, 2],
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
