/**
 * 弹出框
 * 调用方式：
 * $(target).dialog() // 普通对话框
 * $(target).dialog.tip() // 提示框
 * $(target).dialog.combox() // 选择框
 * $(target).dialog.alert() // 确定框
 * $(target).dialog.comfirm() // 确认框
 * $.dialog() 
 */
;(function( $, window, undefined ){
	
	var fixdialog = function(options){
		alert(fixdialog.settings.width)
		return $.extend(methods,settings,options);
	};
	fixdialog.methods = {
			
	};
	fixdialog.settings = {
			/** 
			 * 相对父层位置
			 * left:居左
			 * center:完全居中
			 * right:居右
			 * vertical_center:垂直居中
			 * horizontal_center:横向居中
			 */
			align:'center',
			width:'auto',// 宽度
			height:'auto',// 高度
			title:"对话框",// 标题，如果为false,则不显示标题
			content:"",// 内容,如果以url:开头，则为ifrme框架内容
			max:false,// 最大化
			min:false,// 最小化
			lock:false// 锁屏
	};
	
	fixdialog.tip = function(){
		alert('tip'+fixdialog.settings.align)
	};

	fixdialog.alert = function(){
		alert('alert')
	};

	fixdialog.comfirm = function(){
		alert('comfirm')
	};

	$.dialog = fixdialog;
	
})(jQuery);