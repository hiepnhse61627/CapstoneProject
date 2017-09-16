<section class="content-header">
    <ol class="breadcrumb">
        <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
        <li class="active">Here</li>
    </ol>
</section>
<section class="content">
    <h1>
        Create New Student
    </h1>
    <form id="form">
        <div class="col-md-12">
            <div class="form-group">
                <label for="rollNumber">Student Roll Number</label>
                <input type="text" id="rollNumber" name="rollNumber" placeholder="Roll Number"/>
            </div>
            <div class="form-group">
                <label for="fullName">Full Name</label>
                <input type="text" id="fullName" name="fullName" placeholder="Student full name"/>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="Add();">Add</button>
            </div>
        </div>
    </form>
</section>

<script>
    function Add() {
        var form = JSON.stringify($('#form').serializeJSON());
        $.ajax({
            type: "POST",
            url: "/createnew",
            contentType: 'application/json',
            data: form,
            success: function(result) {
                console.log(result);
            }
        });
    }
</script>