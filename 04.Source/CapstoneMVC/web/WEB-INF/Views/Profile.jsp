<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/30/2017
  Time: 10:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<div class="col-md-12">
    <div class="box box-primary">
        <div class="box-body box-profile">
            <c:choose>
                <c:when test="${not empty user.picture}">
                    <img class="profile-user-img img-responsive img-circle" src="${user.picture}"
                         alt="User profile picture">
                </c:when>
                <c:otherwise>
                    <img class="profile-user-img img-responsive img-circle"
                         src="/Resources/plugins/dist/img/anonymous.jpg" alt="User profile picture">
                </c:otherwise>
            </c:choose>

            <h3 class="profile-username text-center">${user.fullname}</h3>

            <p class="text-muted text-center">Software Engineer</p>

            <div class="row">
                <div class="col-md-12">
                    <form id="form" class="form-horizontal">
                        <div class="form-group">
                            <h4>Cập nhật profile</h4>
                            <input type="hidden" name="id" value="${user.id}">

                            <div class="form-group">
                                <label for="fullname" class="col-sm-2 control-label">Name</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" id="fullname" name="fullname"
                                           placeholder="Fullname" value="${user.fullname}">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="email" class="col-sm-2 control-label">Email</label>
                                <div class="col-sm-10">
                                    <input type="email" class="form-control" id="email" placeholder="Email"
                                           name="email" value="${user.email}" disabled>
                                </div>
                            </div>
                            <%--<div class="form-group">--%>
                                <%--<label for="role" class="col-sm-2 control-label">Role</label>--%>
                                <%--<div class="col-sm-10">--%>
                                    <input type="hidden" class="form-control" id="role" placeholder="Role" name="role" value="${user.role}"/>
                                <%--</div>--%>
                            <%--</div>--%>
                            <%--<div class="form-group">--%>
                                <%--<label for="studentRollNumber" class="col-sm-2 control-label">MSSV</label>--%>
                                <%--<div class="col-sm-10">--%>
                                    <input type="hidden" class="form-control" id="studentRollNumber"
                                           placeholder="Rollnumber"
                                           name="studentRollNumber" value="${user.studentRollNumber}">
                                <%--</div>--%>
                            <%--</div>--%>
                        </div>
                        <div class="form-group">
                            <h4>Đổi password</h4>

                            <div class="form-group">
                                <label for="inputPassword" class="col-sm-2 control-label">Old password</label>
                                <div class="col-sm-10">
                                    <input type="password" class="form-control" id="inputPassword" name="password"
                                           placeholder="Password">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="newPassword" class="col-sm-2 control-label">New Password</label>
                                <div class="col-sm-10">
                                    <input type="password" class="form-control" id="newPassword" name="newPassword"
                                           placeholder="New Password">
                                </div>
                            </div>
                            <div class="form-group">
                                <label for="confirm" class="col-sm-2 control-label">Confirm Password</label>
                                <div class="col-sm-10">
                                    <input type="password" class="form-control" id="confirm" placeholder="Confirm new Password">
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-2 col-sm-10">
                                <input type="checkbox"> I agree to the <a href="#">terms and conditions</a>
                            </div>
                        </div>
                        <div class="form-group">
                            <div class="col-sm-offset-2 col-sm-10">
                                <button type="button" class="btn btn-danger" onclick="Edit()">Submit</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <script>
        $(document).ready(function () {
            $('input').iCheck({
                checkboxClass: 'icheckbox_square-blue',
                radioClass: 'iradio_square-blue',
                increaseArea: '20%' // optional
            });
        });

        function Edit() {
            if ($('#confirm').val() != '' && $('#newPassword').val() != '' && $('#confirm').val() != $('#newPassword').val()) {
                swal('', 'Password confirm mới không đúng với password đã nhập', 'error');
            }  else {
                var form = JSON.stringify($('#form').serializeJSON());
                console.log(form);
                $.ajax({
                    type: "POST",
                    url: "/profile/edit",
                    contentType: 'application/json',
                    data: form, // serializes the form's elements.
                    success: function (data) {
                        if (data.success) {
                            swal('', 'Thành công', 'success').then(function() {
                                location.reload();
                            });

                        } else {
                            swal('', data.msg, 'error');
                        }
                    }
                });
            }
        }
    </script>