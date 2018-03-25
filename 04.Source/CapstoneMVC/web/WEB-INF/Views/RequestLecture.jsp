<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<style>
    .dataTables_filter input {
        width: 250px;
    }

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

    .select2-selection--single .select2-selection__rendered {
        padding-left: 0px !important;
    }

    .select2-selection--single .select2-selection__arrow {
        height: 30px !important;
    }

    .select2-search__field {
        width: 100% !important;
    }

    .select2-selection__choice {
        color: black !important;;
    }

    .table-condensed > tbody > tr > td {
        padding: 3px;
    }

</style>

<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-6 title">
                    <h1>GV dạy thế theo môn</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="form-group form-date-range">
                <label for="scheduleDate">Ngày dạy:</label>
                <input id="scheduleDate" type="text" class="form-control"/>
                <i class="fa fa-calendar"></i>
            </div>

            <div class="form-group">
                <label for="subject2">Môn học:</label>
                <select id="subject2" class="select department2-select">
                    <c:forEach var="aSubject" items="${subjects}">
                        <option value="${aSubject.id}">${aSubject.id} - ${aSubject.name}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label for="aTime">Slot:</label>
                <select id="aTime" class="select aTime-select">
                    <c:forEach var="aSlot" items="${slots}">
                        <option value="${aSlot.slotName}">${aSlot.slotName}</option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="RefreshTable()" id="searchBtn">Tìm kiếm</button>
                <button type="button" class="btn btn-primary" onclick="resetFilter()" id="removeFilterBtn">Xóa bộ lọc
                </button>
            </div>

            <div class="form-group">
                <div class="row">
                    <div class="col-md-12">
                        <table id="table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên</th>
                                <th>SĐT</th>
                                <th>Email</th>
                                <th>Gửi mail</th>
                            </tr>
                            </thead>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>


<div id="scheduleModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Tiêu đề</h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-12">

                        <div class="form-group">
                            <label for="lectureFrom">Giảng viên yêu cầu đổi:</label>
                            <select id="lectureFrom" class="select lecture-select">
                                <c:forEach var="emp" items="${employees}">
                                    <option value="${emp.fullName}">${fn:substring(emp.emailEDU, 0, fn:indexOf(emp.emailEDU, "@"))}
                                        - ${emp.fullName}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group" id="room-container">
                            <label for="room">Phòng học:</label>
                            <select id="room" class="select room-select">
                                <c:forEach var="room" items="${rooms}">
                                    <option value="${room.name}">${room.name}</option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="lectureTo">Giảng viên dạy thế:</label>
                            <input id="lectureTo" type="text" class="form-control"/>
                        </div>

                        <div class="form-group">
                            <label for="email">Email:</label>
                            <input id="email" type="text" class="form-control"/>
                        </div>

                        <div class="form-group">
                            <label for="editor">Nội dung mail:</label>
                            <textarea name="content" id="editor" placeholder="Nội dung mail">
                            </textarea>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Đóng</button>
                <button id="btnSubmit" type="button" class="btn btn-primary">Gửi mail</button>
            </div>
        </div>

    </div>
</div>

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

    $(document).ready(function () {
        $('#subject2').select2({
            placeholder: '- Chọn bộ môn -'
        });

        $('#room').select2({
            placeholder: '- Chọn phòng -'
        });

        $('#aTime').select2({
            placeholder: '- Chọn slot -'
        });

        $('select').on('change', function (evt) {
            $('#select2-subject2-container').removeAttr('title');
            $('#select2-aTime-container').removeAttr('title');
        });

        $("#subject2").val('').trigger('change');

        $("#aTime").val('').trigger('change');

        // Show daterangepicker when click on icon
        $('.form-date-range i').click(function () {
            $(this).parent().find('input').click();
        });

        startDate = endDate = "";

        $('#scheduleDate').daterangepicker({
            autoUpdateInput: false,
            singleDatePicker: true,
            locale: {
                applyLabel: "Chọn",
                cancelLabel: 'Xóa',
                format: 'DD/MM/YYYY'
            }
        });

        $('#scheduleDate').on('apply.daterangepicker', function (ev, picker) {
            startDate2 = picker.startDate.format('DD/MM/YYYY');
            endDate2 = picker.endDate.format('DD/MM/YYYY');
            $(this).val(picker.startDate.format('DD/MM/YYYY'));
        });

        $('#scheduleDate').on('cancel.daterangepicker', function (ev, picker) {
            $(this).val('');
        });

        table = $('#table').dataTable({
            "bServerSide": false,
            "bFilter": true,
            "bRetrieve": true,
            "sScrollX": "100%",
            "bScrollCollapse": true,
            "bProcessing": true,
            "bSort": false,
            "sAjaxSource": "/requestLecture/get", // url getData.php etc
            "fnServerParams": function (aoData) {
                aoData.push({
                    "name": "startDate",
                    "value": $('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY')
                }),
                    aoData.push({
                        "name": "endDate",
                        "value": $('#scheduleDate').data('daterangepicker').endDate.format('DD/MM/YYYY')
                    }),
                    aoData.push({"name": "subject", "value": $('#subject2').val()}),
                    aoData.push({"name": "slot", "value": $('#aTime').val()})
            },
            "oLanguage": {
                "sSearchPlaceholder": "Nhập từ khóa",
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
                    "aTargets": [0, 1, 2, 3, 4],
                    "sClass": "text-center",
                    "bSortable": false
                },
                {
                    "aTargets": [0],
                    "bVisible": false,
                },
                {
                    "aTargets": [4],
                    "mRender": function (data, type, row) {
                            return "<a class='btn btn-success tbl-btn' onclick='SendEmail(" + row[0] + ",\""
                                + row[1] + "\",\"" + row[2] + "\",\"" + row[3] + "\")'>" +
                                "<i class='glyphicon glyphicon-envelope'></i></a>";
                    }
                },
            ],
            "bAutoWidth": false
        }).fnSetFilteringDelay(1000);


    });


    function RefreshTable() {
        if (table != null) {
            table._fnPageChange(0);
            table._fnAjaxUpdate();
        }
        $('#removeFilterBtn').removeAttr('disabled');

    }

    function resetFilter() {
        $("#lecture").val('').trigger('change');
        $('#scheduleDate').data('daterangepicker').setStartDate(moment());
        $('#scheduleDate').data('daterangepicker').setEndDate(moment());
        $('#scheduleDate').val('');
        $("#aTime").val('').trigger('change');
        $("#subject2").val('').trigger('change');

        $('#removeFilterBtn').attr('disabled', 'disabled');
    }


    function SendEmail(empId, name, phone, email) {
        ClearModal();

        $("#lectureTo").val(name);
        $("#email").val(email);

        $("#scheduleModal").modal('toggle');
    }

    function ClearModal() {
        //config for modal

        $('#editor').ckeditor();
        CKEDITOR.config.extraPlugins = 'justify';
        CKEDITOR.config.title = false;
        $('#cke_wysiwyg_frame cke_reset').removeAttr('title');

        $('#lectureFrom').select2({
            placeholder: '- Chọn giảng viên -'
        });

        // ui-helper-hidden-accessible
        $('select').on('change', function (evt) {
            $('.select2-selection__choice');

        });

        $("#lectureFrom").val("").trigger("change");
        $("#room").val("").trigger("change");


        $("#lectureTo").val("");

        $('#editor').val("");
    }


    var OAUTHURL = 'https://accounts.google.com/o/oauth2/auth?';
    var VALIDURL = 'https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=';
    var SCOPE = 'https://mail.google.com/ https://www.googleapis.com/auth/userinfo.email';
    var CLIENTID = '1024234376610-fa3r5s7db2g82ccqecolm6rbfskbv3ci.apps.googleusercontent.com';
    var url = window.location.hostname;
    if (url.indexOf("localhost") == -1 && url.indexOf("xip.io") == -1) {
        url += ".xip.io";
    }
    url += ":" + (location.port == '' ? "80" : location.port);
    var REDIRECT = "http://" + url + "/email/google";
    var TYPE = 'token';
    var url = OAUTHURL + 'scope=' + SCOPE + '&client_id=' + CLIENTID + '&redirect_uri=' + REDIRECT + '&response_type=' + TYPE;

    function Authenticate() {
        var win = window.open(url, "Choose a Email", 'width=800, height=600');

        var pollTimer = window.setInterval(function () {
            try {
                if (win.document.URL.indexOf(REDIRECT) != -1) {
                    window.clearInterval(pollTimer);
                    var url = win.document.URL;
                    var acToken = url.match(/#(?:access_token)=([\S\s]*?)&/)[1];
                    var tokenType = gup(url, 'token_type');
                    var expiresIn = gup(url, 'expires_in');
                    win.close();
//                    Send(acToken);
                    validateToken(acToken);
                }
            } catch (e) {
//                swal('', e.message, 'error');
            }
        }, 100);
    }

    function validateToken(token) {
        $.ajax({
            url: VALIDURL + token,
            data: null,
            success: function (responseText) {
                getUserInfo(token);
            },
            dataType: "jsonp"
        });
    }

    function getUserInfo(token) {
        $.ajax({
            url: 'https://www.googleapis.com/oauth2/v1/userinfo?access_token=' + token,
            data: null,
            success: function (resp) {
                var user = resp;
                Send(token, user.email, user.name);
            },
            dataType: "jsonp"
        });
    }

    function gup(url, name) {
        name = name.replace(/[[]/, "\[").replace(/[]]/, "\]");
        var regexS = "[\?&]" + name + "=([^&#]*)";
        var regex = new RegExp(regexS);
        var results = regex.exec(url);
        if (results == null)
            return "";
        else
            return results[1];
    }

    function Send(token, username, name) {
        swal({
            title: 'Đang xử lý',
            html: '<div class="form-group">Tiến trình có thể kéo dài vài phút</div>',
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/sendRequestLecture",
                    data: {
                        // "email": 'longphse62094@fpt.edu.vn',
                        "email": $("#email").val(),
                        "lectureFrom":  $("#lectureFrom").val(),
                        "lectureTo": $("#lectureTo").val(),
                        "date": $('#scheduleDate').data('daterangepicker').startDate.format('DD/MM/YYYY'),
                        "subjectCode": $("#subject2").val(),
                        "slot": $("#aTime").val(),
                        "room": $("#room").val(),
                        "token": token,
                        "username": username,
                        "name": name,
                        "editor": $('#editor').val()},
                    success: function (result) {
                        if (result.success) {
                            swal('', 'Đã gửi thành công', 'success');
                            $("#scheduleModal").modal('toggle');
                        } else {
                            swal('', result.msg, 'error');
                        }
                    }
                });
            },
            allowOutsideClick: false
        });
    }


    $("#btnSubmit").on("click", function () {

        Authenticate();
    });


</script>