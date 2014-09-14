function imgSlides()
{
	this.slides = new Array();
	this.add = function(slide){
		var i = this.slides.length;
		this.slides[i] = slide;
	}
	this.getSmallPics = function(){
		var pics = "";
		for(var i=0;i<this.slides.length;i++)
		{
			pics += this.slides[i].smallpic+"|";
		}
		if(pics.length>0)
		{
			pics = pics.substring(0,pics.length-1);
		}
		return pics;
	}
	this.getBigPics = function(){
		var pics = "";
		for(var i=0;i<this.slides.length;i++)
		{
			pics += this.slides[i].bigpic+"|";
		}
		if(pics.length>0)
		{
			pics = pics.substring(0,pics.length-1);
		}
		return pics;
	}
	this.getLinks = function(){
		var links = "";
		for(var i=0;i<this.slides.length;i++)
		{
			links += this.slides[i].link+"|";
		}
		if(links.length>0)
		{
			links = links.substring(0,links.length-1);
		}
		return escape(links);
	}
	this.getTitles = function(){
		var texts = "";
		for(var i=0;i<this.slides.length;i++)
		{
			texts += this.slides[i].title+"|";
		}
		if(texts.length>0)
		{
			texts = texts.substring(0,texts.length-1);
		}
		return texts;
	}
	this.getDes = function(){
		var texts = "";
		for(var i=0;i<this.slides.length;i++)
		{
			texts += this.slides[i].des+"|";
		}
		if(texts.length>0)
		{
			texts = texts.substring(0,texts.length-1);
		}
		return texts;
	}
}

function imgInfo(smallpic,bigpic,link,title,des)
{
	this.smallpic = smallpic;
	this.bigpic = bigpic;
	this.link = link;
	this.title = title;
	this.des = des;
}