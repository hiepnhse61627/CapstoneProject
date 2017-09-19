<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>

<section class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</section>
<section class="content">
    <h1>
        Import Subjects
    </h1>
    <form id="form" enctype=”multipart/form-data”>
        <div class="col-md-12">
            <div class="form-group">
                <label for="file">File</label>
                <input type="file" accept=".xls" id="file" name="file" placeholder="Roll Number"/>
                <button type="button" onclick="Add()" class="btn btn-success">Upload</button>
            </div>
        </div>
    </form>
</section>

<script>
    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
//        console.log(form);


        swal({
            title: 'Đang xử lý',
            text: 'Tiến trình có thể kéo dài vài phút!',
            type: 'info',
            onOpen: function () {
                swal.showLoading();
                $.ajax({
                    type: "POST",
                    url: "/subject",
                    processData: false,
                    contentType: false,
                    data: form,
                    success: function(result) {
                        console.log(result);
                        if (result.success) {
                            swal(
                                'Thành công!',
                                'Đã import các môn học!',
                                'success'
                            );
                        } else {
                            swal('Đã xảy ra lỗi!', result.message, 'error');
                        }
                    }
                });
            },
            allowOutsideClick: false
        });
    }
</script>