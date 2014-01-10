<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="form.aspx.cs" Inherits="WebForm.test.xj.form" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" >
<head runat="server">
    <title>表单页面</title>
    <script type="text/javascript">
        function select() {
            var WWidth = (window.screen.width - 500) / 2;
            var Wheight = (window.screen.height - 150) / 2;
            window.open("selectOprate.aspx", '编辑器图片上传', 'height=200, width=500, top=' + Wheight + ', left=' + WWidth + ', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no,directories=yes');
        }
        function insertHTMLEdit(str3) {
            text2.value = "上传成功:" + str3 + "";
        }

    </script>
</head>
<body>
    <div>
        用户名：<input type="text" id="text1" name="text1"/>
        内容：<input type="text" id="text2" name="text2" />
        <a href="#" onclick="select()">查询</a>
    </div>
</body>
</html>
