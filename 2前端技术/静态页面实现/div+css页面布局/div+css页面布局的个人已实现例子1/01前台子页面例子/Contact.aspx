<%@ Page Language="C#" AutoEventWireup="true" CodeFile="Contact.aspx.cs" Inherits="Front_Contact" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head runat="server">
    <title></title>
    <link href="css/2.css" rel="stylesheet" type="text/css" />
    <script src="js/AC_RunActiveContent.js" type="text/javascript"></script>
</head>
<body>
    <form id="form1" runat="server">
    <div id ="all">
        <div id="top">
            <div id="top_l"><img src="images/jy_01.jpg" width="690" height="87" alt="顶部标题"/></div>  
            <div id="top_r">
            <ul><li><a onclick="AddFavorite(window.location,document.title)">加入收藏</a></li>
                <li><a onclick="SetHome(this,window.location)">设为首页</a></li>        
                <li><a href="msg.html">留言板</a></li></ul>
            </div>
            <div id="top_dh"><ul>    <li><a href="index.html"><img src="images/jy_03.jpg" width="79" height="31" /></a></li>   
            <li><a href="intro.html"><img src="images/jy_04.jpg" width="101" height="31" /></a></li>                <li><a href="newclass-7.html"><img src="images/jy_05.jpg" width="103" height="31" /></a></li>                <li><a href="pro.html"><img src="images/jy_06.jpg" width="103" height="31" /></a></li>                <li><a href="case.html"><img src="images/jy_07.jpg" width="102" height="31" /></a></li>	            <li><a href="msg.html"><img src="images/jy_08.jpg" width="101" height="31" /></a></li>                <li><a href="order.html"><img src="images/jy_09.jpg" width="103" height="31" /></a></li>                <li><a href="contact.html"><img src="images/jy_10.jpg" width="100" height="31" /></a></li>	            <li id="date"><div id="webjx">              <script type="text/javascript">                                setInterval("webjx.innerHTML='时间：'+new Date().toLocaleString()+' 星期'+'日一二三四五六'.charAt(new Date().getDay());", 1000);            
            </script> </div></li></ul>  
            </div>
            <div id="top_fl">                <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"
                        codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" 
                        height="263" width="1002">
			        <param name="movie" value="images/akin_flash.swf">
			        <param name="quality" value="high">
			        <embed src="/images/akin_flash.swf" quality="high"
				        pluginspage="http://www.macromedia.com/go/getflashplayer"
				        type="application/x-shockwave-flash" width="1002" height="200">
			        </embed> </object> 
            </div> 
        </div> 
        <!--上边部分结束-->
        <!--上边部分结束-->
        <div id="mid"> 
             <div id="left">
                <div id="l_dh">
                    <dl><img src="images/tit_l_cont.jpg" width="196" height="40" /></dl>
                    	<ul><li><a href="contact.html">联系我们</a></li></ul>
                </div>
                <div id="l_lx">	
                    <ul><li>电 话:027-51854190<br />
                             Q Q:371723297<br />
                            E-mail:junyandz@163.com<br />
                           手机:13037131615<br />
                           地址武汉市广埠屯华中电脑数码城一楼1158号<br />
                    </li></ul>
                 </div>
             </div>    
            <div id="right">
                <div id="comp">
                    <dl style="background-image: url(images/tit_cont_04.jpg);">您的当前位置：<a href="index.html">首页</a> >> 联系我们</dl>
            	    <ul><li>
            	        <span style="font-size: small">武汉钧焱电子有限公司 <br />
            	        电&nbsp; 话: 027-51854190 <br />
            	        Q&nbsp;&nbsp;&nbsp; Q:&nbsp;1060871529 <br />
            	        E-mail: </span><a href="mailto:junyandz@163.com">
            	        <span style="font-size: small">junyandz@163.com</span></a><span style="font-size: small"> <br />
            	        手&nbsp; 机: 13037131615 <br />地&nbsp; 址: 武汉市广埠屯华中电脑数码城一楼1158号 </span>
            	    </li></ul>   
                </div>  
            </div>   
      </div> 
      <!--中间部分结束-->
      <!--中间部分结束-->
      <div id="bottom">   
               <div id="dh"><a href="pro.html" class="white">产品展示</a> | <a class="white" href="contact.html">联系我们</a></div>
        </div>
        <!--底部部分结束-->
        </div>         
    </form>
</body>
</html>
