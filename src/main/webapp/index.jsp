<%--
  Created by IntelliJ IDEA.
  User: yangqun
  Date: 2017/12/26
  Time: 19:56
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>mmall</title>
</head>
<body>
提交测试
<form name="01" action="/mmall/manger/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" name="提交"/>
</form>
富文本测试
<form name="01" action="/mmall/manger/product/rich_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" name="富文本提交"/>
</form>
</body>
</html>
