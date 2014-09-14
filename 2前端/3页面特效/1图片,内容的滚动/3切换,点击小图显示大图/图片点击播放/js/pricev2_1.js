// JavaScript Document
//全站基本js
function MM_openBrWindow(theURL,winName,features) { //v2.0
window.open(theURL,winName,features);
}
function mOvr(src,clrOver) { if (!src.contains(event.fromElement)) { src.style.cursor = 'hand'; src.bgColor = clrOver; }}function mOut(src,clrIn){ if (!src.contains(event.toElement)) { src.style.cursor = 'default'; src.bgColor = clrIn; }} function mClk(src) { if(event.srcElement.tagName=='TD'){src.children.tags('A')[0].click();} }
function mClk2(src) { if(event.srcElement.tagName=='TR'){src.children.tags('A')[0].click();} }
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
function mover(src,color){src.style.backgroundColor=color}
function mout(src,color){src.style.backgroundColor=color}
function MM_jumpMenu(targ,selObj,restore){ //v3.0
eval(targ+".location='"+selObj.options[selObj.selectedIndex].value+"'");
if (restore) selObj.selectedIndex=0;
}
// -->
function MM_findObj(n, d) { //v4.01
var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
if(!x && d.getElementById) x=d.getElementById(n); return x;
}
function MM_showHideLayers() { //v6.0
var i,p,v,obj,args=MM_showHideLayers.arguments;
for (i=0; i<(args.length-2); i+=3) if ((obj=MM_findObj(args[i]))!=null) { v=args[i+2];
if (obj.style) { obj=obj.style; v=(v=='show')?'visible':(v=='hide')?'hidden':v; }
obj.visibility=v; }
}


//选中checkbox后成个li变色
function ischecked(frm){
for(i=0;i<frm.elements.length;++i){
var e = frm.elements[i];
if (e.type=='checkbox'){
if (e.parentNode.tagName=='LI')
e.parentNode.className=(e.checked?'focus':'');
}
}
}

//tab切换
function chDiv(obj){
	var un=obj.getAttribute("un");
	if(document.getElementById(un).style.display=="none"){
		document.getElementById(un).style.display="";
	}
	else{document.getElementById(un).style.display="none";}
}

var layerT;
function chTab(obj){
	if (obj=='0'){
	clearTimeout(layerT);
	}else{
		layerT = setTimeout(function(){chTab2(obj);},300);
	}
}
function chTab2(obj){
	var objPar=obj.parentNode;
	var un=obj.getAttribute("un");
	var un2=objPar.getAttribute("staus");
	
	document.getElementById(un2+"n").className="n";
	document.getElementById(un2).style.display="none"
	document.getElementById(un+"n").className="now";
	document.getElementById(un).style.display="";
	
	objPar.setAttribute("staus",un);
}


//显示隐藏层
function show_div(div_id){
							var obj=document.getElementById(div_id);
							obj.style.display = obj.style.display==""?"none":"";
	}

//经过li时变类名
function cns_li_hover(obj,ClassName){
      obj.className=obj.className+ClassName;
}
function cns_li_out(obj,ClassName){
      obj.className=obj.className.substring(0,obj.className.indexOf(ClassName));
}

//产品菜单
if (document.getElementById("leftmenu")){
var sfEls = document.getElementById("leftmenu").getElementsByTagName("li");
for (var i=0; i<sfEls.length; i++) {
sfEls[i].onmouseover=function() {
this.className+=(this.className.length>0? " ": "") + "sfhover";
}
sfEls[i].onmouseout=function() {
this.className=this.className.replace(new RegExp("( ?|^)sfhover\\b"), 
"");
}
}
}

//一组li,鼠标经过的那个是为focus,LI与A关联
function thisfocus(obj){
	var p = obj.parentNode.parentNode.getElementsByTagName("li");
		for(i=0;i<p.length;i++){
			var p1 = p[i].getElementsByTagName("a");
			if(obj==p1[0]){						 
				p[i].className=p[i].className+" focus";	
			}else{
				p[i].className="li" + (i+1);
		}
	}
}
//复制当前Url
function copyToClipBoard(){
     var clipBoardContent="";
       clipBoardContent+=this.location.href;
     window.clipboardData.setData("Text",clipBoardContent);
     alert("复制成功，请粘贴到你的QQ/MSN上推荐给你的好友");
   }

//一组li,鼠标经过的那个是为focus,内可用DIV
function doShowThis(t){
	var oul=document.getElementById('_xjzxsyul');
	for (var i=0;i<oul.childNodes.length;++i){
		var ochild=oul.childNodes[i];
		if (ochild.tagName=='LI'){
			ochild.className=ochild.className.split(' ')[0];
			//alert(ochild.className+' - '+ochild.tagName);
		}
	}
	t.className=t.className+' focus';
}

//点击more弹出隐藏部分，原来的more字样隐藏；当点击另外一个more时，原弹出部分隐藏
var layerT;
function chDiv(obj){
	if (obj=='0'){
	clearTimeout(layerT);
	}else{
		layerT = setTimeout(function(){chDiv2(obj);},300);
	}
}
function chDiv2(obj){
	var objPar=obj.parentNode;
	var un=obj.getAttribute("un");
	var un2=objPar.getAttribute("staus");
	
	document.getElementById(un2+"n").className="";
	document.getElementById(un2).style.display="none"
	document.getElementById(un+"n").className="now";
	document.getElementById(un).style.display="";
	
	objPar.setAttribute("staus",un);
}

var tMenu=new Array();
var tempHeight=new Array();
function showMenu(obj){
	var un=obj.getAttribute("un");
	var objPar=obj.parentNode.parentNode;
	var height1=objPar.offsetHeight;
	
	if(document.getElementById(un).style.display=="block"){
		document.getElementById(un).style.display="none";
	}
	else{
		document.getElementById(un).style.display="block";
	}
	
	var height2=objPar.offsetHeight;
	var height3=height2-height1;
	
	document.getElementById(un+"_m").style.display='none';
	document.getElementById(un).style.display="block";
	
	if (height3>0){
		objPar.style.height=height1+"px";
		objPar.style.overflow='hidden';
	}
	else{
		objPar.style.height=height1;
	}
	tempHeight[un]=0;
	scroolIt(un,height3);
}

var objPar=new Array();
var waittime=2;
function scroolIt(objName,objheight){
	objPar[objName]=document.getElementById(objName).parentNode.parentNode;
	if(document.getElementById(objName).parentNode.tagName.toLowerCase()=="dl"){
		objPar[objName]=document.getElementById(objName).parentNode;
		waittime=1;
	}
	objPar[objName].setAttribute("objheight",-objheight);
	clearInterval(tMenu[objName]);
	
	if (objheight>0){
		if (tempHeight[objName]<objheight){
			tempHeight[objName]=tempHeight[objName]+Math.abs(objheight)/5;
			objPar[objName].style.height=objPar[objName].offsetHeight+Math.abs(objheight)/5+"px";
			tMenu[objName] = setInterval(function(){scroolIt(objName,objheight);},waittime);
		}
		else{
			objPar[objName].style.height="auto";
			objPar[objName].style.overflow='visible';
			tempHeight[objName]=0;
		}
	}
	else{
		if (tempHeight[objName]>objheight){
			tempHeight[objName]=tempHeight[objName]-Math.abs(objheight)/5;
			objPar[objName].style.overflow='hidden';
			objPar[objName].style.height=objPar[objName].offsetHeight-Math.abs(objheight)/5+"px";
			tMenu[objName] = setInterval(function(){scroolIt(objName,objheight);},waittime);
		}
		else{
			document.getElementById(objName).style.display="none";
			objPar[objName].style.overflow='visible';
			objPar[objName].style.height="auto";
			tempHeight[objName]=0;
			document.getElementById(objName+"_m").style.display='';
		}
	}
}

var slTime=new Array();
function scroolTime(objName,type){
	var waittime2=3000;
	if (type=='on'){
		clearTimeout(slTime[objName]);
	}
	else if (type=='out'){
		var un=objName;
		
		var objPar=document.getElementById(objName).parentNode.parentNode;
		if(document.getElementById(objName).parentNode.tagName.toLowerCase()=="dl"){
			objPar=document.getElementById(objName).parentNode;
			waittime2=5000;
		}
		//var un=document.getElementById(objName).getAttribute("id");
		var objheight=objPar.getAttribute("objheight");
		if (objheight<0){
			slTime[objName] = setTimeout(function(){scroolIt(un,objheight);},waittime2);
		}
	}
}
//增加购物车图标
	function showEmallStore() {
		for(var i = 0; i < _emallStoreResult.length; i++) {
			var recommendedCompanyId = 'recommend_company_' + _emallStoreResult[i].cid;
			var company = document.getElementById(recommendedCompanyId); //li element
			if(company != null) {
				company.innerHTML = company.innerHTML + '<a href="http://emall.pconline.com.cn/b2c/storeIndex.jsp?companyId='+_emallStoreResult[i].cid+'" class="buy"><img src="http://www1.pconline.com.cn/price/08art/images/buycar.gif" border=0 /> </a>';
			}
		}
	}

//LI伪类hover,须调用jq框架
  //.px for 排行榜
  //shoplist for 购买地点
function ie6hover(){
   $(".px li").each(function(){
	   var me=$(this);
	   var cls=me.attr("class");
	   me.hover(function(){
		  me.attr("class",cls+"hover");
	   },function(){
		  me.attr("class",cls);
	   });
	});
  $(".shoplist li").hover(
    function(){
     $(this).addClass("hover");
    },function(){
     $(this).removeClass("hover");
    }
   );
}

/**
* 获取IT商城商品的函数
* @param data -- 是商品的数组；
* @param max -- 是显示的商品的个数；
* @param picWH -- 是显示图片的大小，值必须为：80、120、250，这三种。
* @param eRec -- 显示推荐类，默认值是0，0:是显示全局推荐；1:是显示小类推荐
*/
function doShowCProducts(data,max,picW,picH,eRec){
	var shtml='';
	var icnt=max;
	var sused='';
	var ircnt=0;
	var isize=data.length;
	var ierec=-1;
	if(typeof eRec =='undefined' || eRec==0){
		ierec=0;
	}else if (eRec==1){
		ierec=1;
	}
	if (isize>0){
		shtml+='<div class="itMallPrd"><div class="thead"><span class="mark"></span></div><div class="tbody"><ul>';
		for (var i=0;i<isize;++i){
			var bRecommend=ierec==0 && data[i].editorRecommend==1?true:ierec==1 && data[i].editorSmallRecommend==1?true:false;
			if (bRecommend){//已推荐的
				shtml+='<li>'
						+'<i class="iPic"><a target="_blank" href="'+data[i].detailUrl+'"><img src="'+data[i].thumbPic+'" width="'+picW+'" height="'+picH+'" /></a></i>'
						+'<i class="iText"><a target="_blank" href="'+data[i].detailUrl+'">'+data[i].shortName+'</a></i>'
						+'<i class="iPrice">￥<span>'+data[i].retailPrice+'</span></i>'
						+'<i class="iBuy"/><a class="cart" target="_blank" href="'+data[i].detailUrl+'">立即购买</a></li>';			
			}else{//未推荐的随机
				var iindex=Math.floor(Math.random()*isize);
				while (sused.indexOf(iindex+',')!=-1){
					//alert('t1='+iindex);
					iindex=Math.floor(Math.random()*isize);
					//alert('t2='+iindex);
				}
				shtml+='<li>'
						+'<i class="iPic"><a target="_blank" href="'+data[iindex].searchUrl+'"><img src="'+data[iindex].thumbPic+'" width="'+picW+'" height="'+picH+'" /></a></i>'
						+'<i class="iText"><a target="_blank" href="'+data[iindex].searchUrl+'">'+data[iindex].shortName+'</a></i>'
						+'<i class="iPrice">￥<span>'+data[iindex].retailPrice+'</span></i>'
						+'<i class="iBuy"/><a class="cart" target="_blank" href="'+data[iindex].detailUrl+'">立即购买</a></li>';						
				sused+=iindex+',';
			}
			//alert(sused);
			icnt--;
			if (icnt==0) break;
		}
		shtml+='</ul><div class="clear"></div></div></div>';
	}
	return shtml;
	
}


/**
* 获取IT商城商品的函数
* @param data -- 是商品的数组；
* @param max -- 是显示的商品的个数；
* @param picWH -- 是显示图片的大小，值必须为：80、120、250，这三种。
* @param eRec -- 显示推荐类，默认值是0，0:是显示全局推荐；1:是显示小类推荐
*/
function doShowCProducts1(data,max,picWH,eRec,smalltypeId){
	var shtml='';
	var icnt=max;
	var sused='';
	var ircnt=0;
	var isize=data.length;
	var ierec=-1;
	if(typeof eRec =='undefined' || eRec==0){
		ierec=0;
	}else if (eRec==1){
		ierec=1;
	}
	if (isize>0){
		shtml+='<div class="box220  itPrdHotSale">'
				+'	<div class="ColumnTitle"><strong><a	href="http://m.pconline.com.cn/searchProductResult.jsp?smalltypeId='+smalltypeId+'"></a></strong></div>'
				+'	<ul class="newest overview">';
		for (var i=0;i<isize;++i){
			var bRecommend=ierec==0 && data[i].editorRecommend==1?true:ierec==1 && data[i].editorSmallRecommend==1?true:false;
			if (bRecommend){//已推荐的
				shtml+='<li>'
						+'<a href="'+data[i].detailUrl+'" class="img"><img src="'+(picWH=='250'?data[i].middlePic:data[i].thumbPic)+'" title="'+data[i].title+'" /></a>'
						+'<a href="'+data[i].detailUrl+'" title="'+data[i].title+'" class="aTitle">'+data[i].title+'</a>'
						+'<a href="'+data[i].detailUrl+'" class="aPrice" >￥'+data[i].retailPrice+'</a>'
						+'</li>';
				sused+=i+',';
			}else{//未推荐的随机
				var iindex=Math.floor(Math.random()*isize);
				while (sused.indexOf(iindex+',')!=-1){
					//alert('t1='+iindex);
					iindex=Math.floor(Math.random()*isize);
					//alert('t2='+iindex);
				}
				shtml+='<li>'
						+'<a href="'+data[iindex].searchUrl+'" class="img"><img src="'+(picWH=='250'?data[iindex].middlePic:data[iindex].thumbPic)+'" title="'+data[iindex].title+'" /></a>'
						+'<a href="'+data[iindex].searchUrl+'" title="'+data[iindex].title+'" class="aTitle">'+data[iindex].title+'</a>'
						+'<a href="'+data[iindex].searchUrl+'" class="aPrice" >￥'+data[iindex].retailPrice+'</a>'
						+'</li>';
				sused+=iindex+',';
			}
			//alert(sused);
			icnt--;
			if (icnt==0) break;
		}
		shtml+='	</ul>'
				+'</div>';
	}
	return shtml;
}


/*09-10-19 Nvidia 广告*/
$(document).ready(function(){
	$('.ivyNvidiaIcon').hover(
		function(){
			var y = $(this).offset().top;
			var x = $(this).offset().left;
			$('body').append("<div class='ivyNvidiaBig'></div>");
			$('.ivyNvidiaBig').css({"top":y+"px","left":(x+50)+"px"})},
		function(){$('.ivyNvidiaBig').remove();}
	)
});


$(document).ready(function(){
	$('.ivyNvidia').hover(
		function(){
			var y = $(this).offset().top;
			var x = $(this).offset().left;
			$('body').append("<div class='ivyNvidiaBig'></div>");
			$('.ivyNvidiaBig').css({"top":y+"px","left":(x+110)+"px"})},
		function(){$('.ivyNvidiaBig').remove();}
	)
});
