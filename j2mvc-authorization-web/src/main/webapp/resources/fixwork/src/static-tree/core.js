/*!
 * 多级树
 * Copyright 2014 杨大江
 */

(function( $, undefined ) {
	$.fn.tree = function(options) {
		var defaults = {
				containerClass	:'fixwork-static-tree',	// 容器样式
				toggleClass		:'tree-item-toggle',	// 条目关闭打开按钮样式
				itemClass		:'tree-item',			// 条目样式
				css3Url			:'',
				complete		:function(){}			// 加载结束调用
		};
		var DOM = $(this);
		var methods = {
				init:function(){
					DOM.addClass(config.containerClass);
					DOM.get(0).style.cssText += 'behavior: ' + config.css3Url;
					DOM.find('li').each(function(){
						config.event($(this));
					});
				},
				_click:function(toggle,ul){
					var ff = toggle.next();
					if(ul.size()>0 && ul.is(':visible')){
						toggle.removeClass('opened');
						toggle.addClass('closed');
						ff.removeClass('folder');
						ff.addClass('folder-closed');
						ul.slideUp(300);
					}else if(ul.size()>0 && !ul.is(':visible')){
						toggle.addClass('opened');
						toggle.removeClass('closed');
						ff.removeClass('folder-closed');
						ff.addClass('folder');
						ul.slideDown(500);
					}else{
						toggle.addClass('opened');
						toggle.removeClass('closed');
						ff.removeClass('folder-closed');
						ff.addClass('folder');
					}
				},
				event:function(li){
					var ul = $(li).find('ul:first');
					var toggle = $(li).find('a.'+config.toggleClass+':first');
					if(toggle.size() == 0){
						toggle = $('<a class="'+config.toggleClass+'"></a>');
	        			$(li).prepend($(toggle));
					}	
					toggle.click(function(){
						config._click(toggle,ul);
					});
					config._click(toggle,ul);
				}
		};
		var config = $.extend(methods, defaults, options);
		
		return this.each(function() {  
			return config.init();
		});
	};
})(jQuery);
