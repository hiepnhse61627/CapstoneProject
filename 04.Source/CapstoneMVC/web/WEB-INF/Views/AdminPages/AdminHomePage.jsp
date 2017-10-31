<%@ taglib prefix="c" uri=""%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Rem
  Date: 10/31/2017
  Time: 1:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<section class="content">
    <div class="box">
        <div class="b-header">
            <div class="row">
                <div class="col-md-9 title">
                    <h1>Tất cả tài khoản</h1>
                </div>
            </div>
            <hr>
        </div>

        <div class="b-body">
            <div class="row">
                <div class="col-md-12">
                    <table id="tbl-student">
                        <thead>
                            <th>Username</th>
                            <th>Password</th>
                            <th>Hình avatar</th>
                            <th>Email</th>
                            <th>Tên</th>
                            <th>Roles</th>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${list}">
                                <tr>
                                    <td>${user.username}</td>
                                    <td>${user.password}</td>
                                    <td>${user.picture}</td>
                                    <td>${user.email}</td>
                                    <td>${user.fullname}</td>
                                    <td>${user.role}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</section>
