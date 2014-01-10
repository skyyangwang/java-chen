
var Class={create:function(){return function(){this.initialize.apply(this,arguments);}}}
var AutoPlayer=Class.create();AutoPlayer.prototype={data:null,index:null,navSize:null,interval:null,screen:null,nav:null,alt:null,defaultAlt:null,butPlay:null,butStop:null,butPause:null,butPrev:null,butNext:null,butPrevLink:null,butNextLink:null,butPrevGroup:null,butNextGroup:null,butExamine:null,butSpeedGroup:null,showTotal:null,showPage:null,showSource:null,showSourceComposite:null,playStatus:null,pvCounter:null,hidePlayBut:null,timmer:null,playFlag:false,pauseFlag:false,tag:null,baseUrl:null,curId:null,_defaultInterval:5,_cookiePrefix:"pconline_autoplayer_",initialize:function(args){var self=this;self._debug("begin to init");var coolieInterval=jQuery.cookie(self._cookiePrefix+"interval");coolieInterval=""!=coolieInterval?Number(coolieInterval):null;if(null!=coolieInterval){self._debug("get interval from cookie");}
self.data=args.data||{};self.interval=args.interval||coolieInterval||self._defaultInterval;self.index=0;self.navSize=args.navSize||5;self.tag=args.tag||"";self.baseUrl=args.baseUrl||"/";self.screen=jQuery("#"+args.screenId);if(self._isDefined(args.picFilter)){self._filter(args.picFilter);}
if(self._isDefined(args.navId)){self.nav=jQuery("#"+args.navId);}
if(self._isDefined(args.butPlayId)){self.butPlay=jQuery("#"+args.butPlayId);}
if(self._isDefined(args.butPauseId)){self.butPause=jQuery("#"+args.butPauseId);}
if(self._isDefined(args.butStopId)){self.butStop=jQuery("#"+args.butStopId);}
if(self._isDefined(args.butSpeedGroupId)){self.butSpeedGroup=jQuery("#"+args.butSpeedGroupId);}
if(self._isDefined(args.butPrevId)){self.butPrev=self._getElementsById(args.butPrevId);}
if(self._isDefined(args.butNextId)){self.butNext=self._getElementsById(args.butNextId);}
if(self._isDefined(args.butPrevLinkId)){self.butPrevLink=self._getElementsById(args.butPrevLinkId);}
if(self._isDefined(args.butNextLinkId)){self.butNextLink=self._getElementsById(args.butNextLinkId);}
if(self._isDefined(args.butPrevGroupId)){self.butPrevGroup=jQuery("#"+args.butPrevGroupId);}
if(self._isDefined(args.butNextGroupId)){self.butNextGroup=jQuery("#"+args.butNextGroupId);}
if(self._isDefined(args.butExamineId)){self.butExamine=jQuery("#"+args.butExamineId);}
if(self._isDefined(args.showTotalId)){self.showTotal=self._getElementsById(args.showTotalId);}
if(self._isDefined(args.showPageId)){self.showPage=self._getElementsById(args.showPageId);}
if(self._isDefined(args.showSourceId)){self.showSource=self._getElementsById(args.showSourceId);}
if(self._isDefined(args.showSourceCompositeId)){self.showSourceComposite=self._getElementsById(args.showSourceCompositeId);}
if(self._isDefined(args.playStatusId)){self.playStatus=self._getElementsById(args.playStatusId);}
if(self._isDefined(args.pvCounterId)){self.pvCounter=jQuery("#"+args.pvCounterId);}
if(!self._isDefined(args.hidePlayBut)){args.hidePlayBut=true;}
self.hidePlayBut=args.hidePlayBut?true:false;if(self._isDefined(args.altId)){self.alt=jQuery("#"+args.altId);}
self.defaultAlt=args.defaultAlt||"";self._eventBinding();self._catchKeyEvent();var goTo=0;if(self._isDefined(args.curUrl)&&args.curUrl!=""){self._debug("default url:"+args.curUrl);self.go(goTo,true);}else{if(self._isDefined(args.curId)){self._debug("default id:"+args.curId);for(var i=0;i<self.data.length;i++){if(self.data[i].id==args.curId){goTo=i;self.curId=self.data[i].id
break;}}}
self.go(goTo);}
self.screen.trigger("load");if(null!=self.butSpeedGroup){self.butSpeedGroup.find("a[@speed="+self.interval+"]").trigger("click");}
self._debug("init success");},go:function(index,noturn){var self=this;var toUrl=location.href;if(toUrl.indexOf("#id="+self.index)==-1){var pos=sUrl=toUrl.indexOf("#");if(pos==-1){toUrl+="#id="+self.index;}else{toUrl=toUrl.substr(0,pos+1)+"id="+self.index;}
location.href=toUrl;self._debug("change location to "+toUrl);}
var noturn=noturn||false;self._debug("want to go "+index);index=(!self._isDefined(index))||index<0?0:index;if(index>self.getTotal()-1){index=Number(self.getTotal())-1;}
self._debug("going to "+index+" in fact");self.index=index;var item=self.data[index];if(!self._isDefined(item)){self._debug("can't get item data of page "+index);if(self.index>0){self.go(self.index-1);}else{return;}}
if(!noturn){if(self.screen.attr("src")!=item.middlePicUrl){self.screen.attr("src",item.middlePicUrl);if(self.pvCounter!=null&&(self.index>0||(self.index==0&&null!=self.curId))){var countUrl="<img src=http://count5.pconline.com.cn/newcount/count.php?channel=703&screen="+screen.width+"*"+screen.height+"&refer="+escape(location.href)+" border=0 width=0 height=0 alt='' />";document.getElementById('span_count').innerHTML=countUrl;}}}
if(null!=self.butPrevLink){var prevItem=self.index>0?self.data[self.index-1]:self.data[self.getTotal()-1];var prevHerf=self.baseUrl+prevItem.productId+"_bigpicture"+prevItem.id+(self.tag!=""?"_tag"+self.tag:"")+".html";if(self.butPrevLink.attr("href")!=prevHerf){self.butPrevLink.attr("href",prevHerf);self._debug("change prev link to:"+prevHerf);}}
if(null!=self.butNextLink){var nextItem=self.index>=(self.getTotal()-1)?self.data[0]:self.data[self.index+1];var nextHerf=self.baseUrl+nextItem.productId+"_bigpicture"+nextItem.id+(self.tag!=""?"_tag"+self.tag:"")+".html";if(self.butNextLink.attr("href")!=nextHerf){self.butNextLink.attr("href",nextHerf);self._debug("change next link to:"+nextHerf);}}
self._showNumberRefresh();self._navRefresh(noturn);self.butPrev.attr("href","#id="+(self.index-1>0?self.index-1:0)+"");self.butNext.attr("href","#id="+(self.index+1)+"");self.butPrevGroup.attr("href","#id="+(self.index-4>0?self.index-4:0)+"");self.butNextGroup.attr("href","#id="+(self.index+5)+"");if(null!=self.butExamine&&self.butExamine.length>0){var bigPicUrl=null;if(noturn){bigPicUrl=self.screen.attr("src");}
bigPicUrl=bigPicUrl||item.bigPicUrl||item.middlePicUrl;self.butExamine.attr("href","http://www.pconline.com.cn/images/html/viewpic_pconline.htm?"+bigPicUrl+"&channel=703");}
if(null!=self.showSource){var source="";if("网友上传图片"==item.articleTag){source="网友上传";}else if(item.articleTitle!=''&&item.articleUrl!=''){source='<a href="'+item.articleUrl+'" target="_blank">'+item.articleTitle+'</a>';}
self.showSource.html(source);if(null!=self.showSourceComposite){if(source==""){self.showSourceComposite.hide();}else{self.showSourceComposite.show();}}}
if(null!=self.alt){var alt=item.alt||self.defaultAlt;self.alt.attr("title",alt);self.alt.html(alt);}
if(index==self.getTotal()-1){if(null!=self.timmer){self._debug("is the last one and player is running,stop it!");self.stop();}}},goInterval:function(){var self=this;self._debug("going by goInterval");if(!self.pauseFlag){self.go(self.index+1);}
window.clearInterval(self.timmer);if(self.playFlag){self.timmer=window.setInterval(function(){self.goInterval();},self.interval*1000);}},play:function(){var self=this;self._debug("play");if(self.playFlag){self._debug("palyer is running,return!");return;}
self.playFlag=true;if(self.hidePlayBut){self._debug("hide the play button");if(null!=self.butPlay){self.butPlay.hide();}
if(null!=self.playStatus){self.playStatus.hide();}
if(null!=self.butStop){self.butStop.show();}
if(null!=self.butPause){self.butPause.show();}}
if(null!=self.butSpeedGroup){self.butSpeedGroup.show();}
self.timmer=window.setInterval(function(){self.goInterval();},self.interval*1000);},pause:function(){var self=this;self._debug("pause,just call stop.");self.stop();},stop:function(){var self=this;self._debug("stop");self.playFlag=false;if(self.hidePlayBut){self._debug("show the play button");if(null!=self.butPlay){self.butPlay.show();}
if(null!=self.playStatus){self.playStatus.show();}
if(null!=self.butStop){self.butStop.hide();}
if(null!=self.butPause){self.butPause.hide();}}
if(null!=self.butSpeedGroup){self.butSpeedGroup.hide();}
window.clearInterval(self.timmer);self.timmer=null;},setSpeed:function(speed){var self=this;speed=speed||self._defaultInterval;self._debug("begin to set speed:"+speed);self.interval=Number(speed);self._debug("set interval to cookie");jQuery.cookie(self._cookiePrefix+"interval",self.interval,{"expires":999999999,"domain":"pconline.com.cn","path":"/"});if(self.playFlag){self._debug("player is running,rePlay!");self.stop();self.play();}
self._debug("set speed success!");},prev:function(){var self=this;if(self.index==0){self.go(Number(self.getTotal())-1);return;}
self.go(self.index-1);},next:function(){var self=this;if(self.index==self.getTotal()-1){self.go(0);return;}
self.go(Number(self.index)+1);},prevGroup:function(){var self=this;if(self.index==0){alert("已经是第一组了");return;}
self.go(Number(self.index)-Number(self.navSize));},nextGroup:function(){var self=this;var goTo=self.index+self.navSize;if(goTo>=self.getTotal()-self.navSize){goTo=self.getTotal()-self.navSize}
if(self.index>=goTo){alert("已经是最后一组了");return;}
self.go(goTo);},getIndex:function(){var self=this;return self.index||0;},getTotal:function(){var self=this;return self.data.length||0;},_isDefined:function(variable){return typeof(variable)!="undefined"},_eventBinding:function(){var self=this;self._bindingButClick(self.butPlay,function(){self.play();},true);self._bindingButClick(self.butStop,function(){self.stop();},true);self._bindingButClick(self.butPause,function(){self.pause();},true);if(null!=self.butSpeedGroup){self.butSpeedGroup.find("a").click(function(){self.butSpeedGroup.find("a").removeClass("focus")
self.setSpeed(Number(jQuery(this).attr("speed")));jQuery(this).addClass("focus");});}
self._bindingButClick(self.butPrev,function(){self.prev();},true);self._bindingButClick(self.butNext,function(){self.next();},true);self._bindingButClick(self.butPrevGroup,function(){self.prevGroup();},true);self._bindingButClick(self.butNextGroup,function(){self.nextGroup();},true);if(null!=self.butExamine){self.butExamine.hover(function(){if(self.playFlag){self.pauseFlag=true;}},function(){if(self.playFlag){self.pauseFlag=false;}});}
var nowDirection=null;self.screen.hover(function(){if(self.playFlag){self.pauseFlag=true;}},function(){if(self.playFlag){self.pauseFlag=false;}
jQuery(this).css('cursor','auto').attr('title','').attr('alt','');}).mousemove(function(event){event=event||window.event;var direction=(event.pageX-jQuery(this).offset().left)/jQuery(this).width()>0.5;if(nowDirection!=direction){if(direction){jQuery(this).css('cursor','url(images/arrow-r.cur),auto').attr('title','点击跳到下一张>>').attr('alt','点击跳到下一张>>');}else{jQuery(this).css('cursor','url(images/arrow-l.cur),auto').attr('title','<<点击跳到上一张').attr('alt','<<点击跳到上一张');}
self._debug("change direction to "+(direction?"next":"prev"));nowDirection=direction;}}).mouseout(function(){nowDirection=null;self._debug("clear direction");}).click(function(event){var direction=(event.pageX-jQuery(this).offset().left)/jQuery(this).width()>0.5;if(direction){self.next();}else{self.prev();}}).load(function(){var img=self.screen.get(0);var height="";var width="";self.screen.css("height","").css("width","");self._debug("pic:"+img.width+"x"+img.height);if(parseInt(img.width)>500){width=500;height=parseInt((500/img.width)*img.height);}else{if(parseInt(img.height)>500){width=parseInt((500/img.height)*img.width);height=500;}}
self.screen.css("height",height).css("width",width);self._debug("style:"+height+"x"+width);});},_navRefresh:function(nocheck){var self=this;if(null==self.nav){return;}
var nocheck=nocheck||false;var html="";var begin=Number(self.index)-1;if(begin<0){begin=0;}
var end=begin+Number(self.navSize);if(end>self.getTotal()-1){end=self.getTotal();begin=end-self.navSize;if(begin<0){begin=0;}}
var item=null;for(var i=begin;i<end;i++){item=self.data[i];html+=nocheck?"<li>":"<li"+(i==self.index?" class=\"focus\"":"")+">";html+=" <a href=\"#id="+i+"\" idx="+i+" target=\"_self\"><img src=\""+item.thumbPicUrl+"\""+(self._isDefined(item.alt)&&""!=item.alt?"alt=\""+item.alt+"\"":"")+" /></a>";html+="</li>";}
self.nav.html(html);self.nav.find("a").click(function(){self.go(Number(jQuery(this).attr("idx")));});},_showNumberRefresh:function(){var self=this;if(null!=self.showTotal){self.showTotal.html(self.getTotal());}
if(null!=self.showPage){self.showPage.html(self.index+1);}},_bindingButClick:function(but,func,unbind){var self=this;if(null!=but&&but.length>0){if(true==unbind){but.unbind("click");}
but.click(function(){func.call();});}},_catchKeyEvent:function(){var self=this;jQuery("body").keydown(function(event){event=event||window.event;var url=null;switch(event.keyCode){case 33:var prevItem=self.index>0?self.data[self.index-1]:self.data[self.data.length-1];url=self.baseUrl+prevItem.productId+"_bigpicture"+prevItem.id+(self.tag!=""?"tag_"+self.tag:"")+".html";break;case 37:self.prev();break;case 34:var nextItem=self.index<self.data.length-1?self.data[self.index+1]:self.data[0];url=self.baseUrl+nextItem.productId+"_bigpicture"+nextItem.id+(self.tag!=""?"tag_"+self.tag:"")+".html";break
case 39:self.next();break;}
if(url!=null){location.href=url;}});},_getElementsById:function(ids){if(ids.indexOf(",")!=-1){ids=ids.replace(",",",#");}
return jQuery("#"+ids);},_filter:function(picFilter){var self=this;self._debug("being to filter");if(!self._isDefined(picFilter)||null==picFilter||""==picFilter){self._debug("filter is empty,return");return;}
self._debug("filter by "+picFilter);var tempFilter=picFilter.split("|");var data=new Array();var item=null;Outer:for(var i=0;i<self.data.length;i++){item=self.data[i];for(var j=0;j<tempFilter.length;j++){var kay_val=tempFilter[j].split(":");if(eval("!self._isDefined(item."+kay_val[0]+") || item."+kay_val[0]+".indexOf('"+kay_val[1]+"')==-1")){continue Outer;}}
data.push(item);}
self._debug("filter success,"+self.data.length+" to "+data.length);self.data=data;},console:null,consoleId:"autoPlayerConsole",isDebug:false,_debug:function(str){var self=this;if(!self.isDebug){return;}
if(null==self.console){if(jQuery("#"+self.consoleId).length<=0){jQuery("html").append("<div id='"+self.consoleId+"'></div>");}
self.console=jQuery("#"+self.consoleId);}
self.console.append("["+new Date()+"]:"+str+"<br />");},_destory:function(){}}