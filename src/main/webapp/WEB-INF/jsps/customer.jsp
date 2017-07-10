<%@page pageEncoding="UTF-8" language="java" contentType="text/html; UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="BASE" value="${pageContext.request.contextPath}" />

<html>
<head>
    <title>客户管理</title>
</head>
<body>

<h1>客户列表</h1>

<table>
    <tr>
        <th>客户名称</th>
        <th>联系人</th>
        <th>操作</th>
    </tr>

    <c:forEach var="customer" items="${customerList}">
        <tr>
            <td>${customer.name}</td>
            <td>${customer.contact}</td>
            <td>
                <a href="${BASE}/customer_edit?id={customer.id}">编辑</a>
                <a href="${BASE}/customer_delete?id={customer.id}">删除</a>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>
<!--
    在IDEA中，查找文件有三种方法：
                            1. ctrl + N :根据Java类文件名进行查找
                            2. ctrl + shift + N ：根据任意文件名进行查找
                            3. 在Project目录树上，使用 ctrl + shift + F 在指定路径下查找文件
-->