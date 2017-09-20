<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<section class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</section>
<section class="content">
    <h1>
        Page Header
        <small>Optional description</small>
    </h1>
    <div class="col-md-12">
        <button type="button" onclick="test()">Click</button>
    </div>
</section>

<script>
    var isRunning = true;

    function test() {
        swal({
            title: 'Đang xử lý',
            html: "<div id='progress'>Progress</div>",
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                isRunning = true;
                $.ajax({
                    type: "GET",
                    url: "/runstatus",
                    processData: false,
                    contentType: false,
                    success: function (result) {
                        isRunning = false;
                        swal(
                            'Thành công!',
                            'Đã import các môn học!',
                            'success'
                        );
                    }
                });
                waitForTaskFinish(isRunning);
            },
            allowOutsideClick: false
        });
    }

    function waitForTaskFinish(running) {
        $.ajax({
            type: "GET",
            url: "/status",
            processData: false,
            contentType: false,
            success: function (result) {
                $('#progress').html("<div>" + result +"</div>");
                console.log("task running");
                if (running) {
                    setTimeout("waitForTaskFinish(isRunning)", 50);
                }
            }
        });
    }
</script>