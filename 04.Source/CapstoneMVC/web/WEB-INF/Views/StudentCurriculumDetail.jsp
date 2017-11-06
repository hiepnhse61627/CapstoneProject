<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 11/1/2017
  Time: 3:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section class="content">
    <div class="box">
        <div class="b-header">
            <h1>Danh Sách môn học thuộc ngành cũ của sinh viên đã chuyển ngành</h1>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group">
                <div class="row">
                    <div class="title">
                        <h4>Chọn sinh viên</h4>
                    </div>
                    <div class="my-content">
                        <div class="col-md-12">
                            <select id="select" class="select form-control">
                                <option value="-1">-----All-----</option>
                                <c:forEach var="student" items="${studentsFilter}">
                                    <option value="${student.id}">${student.rollNumber} - ${student.fullName}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                </div>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="my-content">
                        <div class="col-md-12">
                            <div id="prequisite" class="m-b-7">
                                <b>Thông tin</b>
                            </div>
                        </div>
                        <div class="col-md-12">
                            Ngành hiện tại: <span id="info"></span>
                        </div>
                    </div>
                </div>
            </div>

            <br class="box">
            <div class="b-header">
                <div class="col-md-9 title">
                    <h1>Danh sách môn học</h1>
                </div>
            </div>
            </br>
            <div class="b-body">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table-detail">
                            <thead>
                            <tr>
                                <th>RollNumber</th>
                                <th>FullName</th>
                                <th>Mã Môn</th>
                                <th>Tên Môn</th>
                                <th>IsActivated</th>
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

<input type="hidden" id="curid" value=""/>

<script>


    var table;
    var current = null;
    var newcurrent = null;

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
        $('.select').select2();

        $('#select').on('change', function () {
            console.log($('#select').val());
            RefreshTable();
            LoadList();
            Get();
        });

        LoadList();
        Get();
    });


    function Get() {
        $.ajax({
            type: "GET",
            url: "/managerrole/getinfo",
            data: {"stuId": $('#select').val()},
            success: function (result) {
                console.log(result);
                $('#info').html(result.info);
                $('#curid').val(result.curriculum);
            }
        });
    }


    function LoadList() {
//        console.log($('#select').val()); loi where????????????
        //vl phai~ goi ca~ controller goc nua~ a` ? yup
        table = $('#table-detail').dataTable({
            "bServerSide": true,
            "bFilter": false,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/managerrole/getDetailInfo", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({"name": "stuId", "value": $('#select').val()})
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
                    "aTargets": [0, 1, 2, 3, 4],
                    "bSortable": false,
                },
            ],
            "bAutoWidth": false,
        }).fnSetFilteringDelay(1000);
    }

    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
    }
</script>
