<section class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</section>
<section class="content">
    <h1>
        Import Marks
    </h1>
    <form id="form" enctype=”multipart/form-data”>
        <div class="col-md-12">
            <div class="form-group">
                <form:form method="POST" action="/uploadMark" enctype="multipart/form-data">
                    <label for="file">File</label>
                    <input type="file" accept=".xls" id="file" name="file" placeholder="Roll Number"/>
                    <button type="button" onclick="Add()" class="btn btn-success">Upload</button>
                </form:form>
            </div>
        </div>
    </form>
</section>

<script>
    function Add() {
        var form = new FormData();
        form.append('file', $('#file')[0].files[0]);
//        console.log(form);
        $.ajax({
            type: "POST",
            url: "/uploadMark",
            processData: false,
            contentType: false,
            data: form,
            success: function (result) {
                console.log(result);
            }
        });
    }
</script>