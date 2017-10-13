<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/12/2017
  Time: 11:13 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content-header">
   <h1>Tạo tài khoản</h1>
</section>
<section class="content">
    <h1>
        Create New Student
    </h1>
    <form id="form">
        <div class="col-md-12">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" placeholder="Username"/>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" placeholder="Password"/>
            </div>
            <div class="form-group">
                <label for="role">Role</label>
                <select id="role" name="role">
                    <option value="ROLE_STUDENT">Student</option>
                    <option value="ROLE_ADMIN">Admin</option>
                </select>
            </div>
            <div class="form-group">
                <button type="button" class="btn btn-success" onclick="Add()">Add</button>
            </div>
        </div>
    </form>
</section>

<script>
    function Add() {
        var form = JSON.stringify($('#form').serializeJSON());
        $.ajax({
            type: "POST",
            url: "/register",
            contentType: 'application/json',
            data: form,
            success: function(result) {
                console.log(result);
            }
        });
    }
</script>