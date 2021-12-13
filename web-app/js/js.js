<!--

function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
//-->


   function lib_bwcheck(){ //Browsercheck (needed)
	this.ver=navigator.appVersion
	this.agent=navigator.userAgent
	this.dom=document.getElementById?1:0
	this.opera5=(navigator.userAgent.indexOf("Opera")>-1 && document.getElementById)?1:0
	this.ie5=(this.ver.indexOf("MSIE 5")>-1 && this.dom && !this.opera5)?1:0; 
	this.ie6=(this.ver.indexOf("MSIE 6")>-1 && this.dom && !this.opera5)?1:0;
	this.ie4=(document.all && !this.dom && !this.opera5)?1:0;
	this.ie=this.ie4||this.ie5||this.ie6
	this.mac=this.agent.indexOf("Mac")>-1
	this.ns6=(this.dom && parseInt(this.ver) >= 5) ?1:0; 
	this.ns4=(document.layers && !this.dom)?1:0;
	this.bw=(this.ie6 || this.ie5 || this.ie4 || this.ns4 || this.ns6 || this.opera5)
	return this
}

var bw=lib_bwcheck()
var speed = 30
var loop, timer
function makeObj(obj,nest){
    nest=(!nest) ? "":'document.'+nest+'.'
	this.el=bw.dom?document.getElementById(obj):bw.ie4?document.all[obj]:bw.ns4?eval(nest+'document.'+obj):0;
  	this.css=bw.dom?document.getElementById(obj).style:bw.ie4?document.all[obj].style:bw.ns4?eval(nest+'document.'+obj):0;
	this.scrollHeight=bw.ns4?this.css.document.height:this.el.offsetHeight
	this.clipHeight=bw.ns4?this.css.clip.height:this.el.offsetHeight
	this.up=goUp;this.down=goDown;
	this.moveIt=moveIt; this.x=0; this.y=0;
    this.obj = obj + "Object"
    eval(this.obj + "=this")
    return this
}
var px = bw.ns4||window.opera?"":"px";

function moveIt(x,y){
	this.x = x
	this.y = y
	this.css.left = this.x+px
	this.css.top = this.y+px
}

function goDown(move){
	if (this.y>-this.scrollHeight+oCont.clipHeight){
		this.moveIt(0,this.y-move)
			if (loop) setTimeout(this.obj+".down("+move+")",speed)
	}
}

function goUp(move){
	if (this.y<0){
		this.moveIt(0,this.y-move)
		if (loop) setTimeout(this.obj+".up("+move+")",speed)
	}
}

function scroll(speed){
	if (scrolltextLoaded){
		loop = true;
		if (speed>0) oScroll.down(speed)
		else oScroll.up(speed)
	}
}
function noScroll(){
	loop = false
	if (timer) clearTimeout(timer)
}
var scrolltextLoaded = false
function scrolltextInit(){
	oCont = new makeObj('divScrollTextCont')
	oScroll = new makeObj('divText','divScrollTextCont')
	oScroll.moveIt(0,0)
	oCont.css.visibility = "visible"
	scrolltextLoaded = true
}

if (bw.bw) onload = scrolltextInit