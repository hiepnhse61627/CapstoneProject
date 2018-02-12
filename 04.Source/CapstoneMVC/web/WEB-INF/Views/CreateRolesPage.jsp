<%--
  Created by IntelliJ IDEA.
  User: StormNs
  Date: 1/16/2018
  Time: 5:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div>
    <div>
        <button role="button"><i class="fa fa-plus"></i>Roles</button>
    </div>
    <div id="createRolesModal" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h4>
                        Tạo chức vụ mới
                    </h4>
                </div>
                <div class="modal-body">
                    <form action="/admin/createNewRole" method="post">
                        <input type="text" name="newRole">
                        <button type="submit">Tạo</button>
                    </form>
                </div>
                <%--<div class="modal-footer">--%>
                <%----%>
                <%--</div>--%>
            </div>
        </div>
    </div>
    <div class="form-group">
        <div class="row">
            <div class="col-md-12">
                <table id="table">
                    <thead>
                    <tr>
                        <th>No.</th>
                        <th>Tên chức vụ</th>
                        <th>Hành động</th>

                    </tr>
                    </thead>
                </table>
            </div>
        </div>
    </div>
</div>
<script type="javascript">
    $(document).ready(function () {

        table = $('#table').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/passfail/get", // url getData.php etc
            "oLanguage": {
                "sSearchPlaceholder": "Tìm theo MSSV, Môn học, Học Kỳ, Status",
                "sSearch": "Tìm kiếm:",
                "sZeroRecords": "Không có dữ liệu phù hợp",
                "sInfo": 'Hiển thị từ _START_ đến _END_ trên tổng số _TOTAL_ dòng',
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
                    "aTargets": [0, 2, 3, 4],
                    "sClass": "text-center",
                    "bSortable": false
                },
                {
                    "aTargets": [1],
                    "mRender": function (data, type, row) {
                        return "<a onclick='GetAllStudentMarks(" + row[5] + ")'>" + data + "</a>";
                    }
                }
            ],
            "bAutoWidth": false
        }).fnSetFilteringDelay(1000);
    });
</script>
