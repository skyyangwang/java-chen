<%@ Page Language="C#" AutoEventWireup="true" CodeBehind="selectOprate.aspx.cs" Inherits="WebForm.test.selectOprate" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" >
<head runat="server">
    <title>处理页面</title>
</head>
<body>
<form id="f_Upload" runat="server" method="post" action="" enctype="multipart/form-data">
    <div>
        内容：<input  type="text" id="cont1" name="cont1" runat="server"/>
        <input type="button" id="tj" value=" 上 传 " onclick="javascript:SubmitClick();" />
    </div>
    </form>
</body>
<script type="text/javascript">
    function SubmitClick()
    {
        document.f_Upload.action = "selectOprate.aspx?Type=Upload";
            document.f_Upload.submit();
    }
    function killErrors() 
    { 
        return true; 
    } 
    window.onerror = killErrors; 
 
</script>
</html>
