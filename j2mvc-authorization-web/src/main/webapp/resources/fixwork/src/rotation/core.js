/**
 * 轮播
 * @auth 杨大江 2014-12-5
 * @param container 滑动容器
 * @param items 滑动条目
 */
function rotationPlay(container, uiItems,s,minWidth,handlersAlign,handlersStyle,callback,fade,handlersClass) {
	var _ie6 = window.ActiveXObject && !window.XMLHttpRequest;
	var _ie7 = navigator.userAgent.indexOf("MSIE 7.0")>0;
	var timeCache = [];
	// 定义横向条目数组
	var xItemArray = [];
	// 滑动容器宽度
	var containerWidth = container[0].scrollWidth;
	// 索引栏
	var handlers = {};
	// 条目总数
	var size = uiItems.size();
	// 条目数组
	var items = [];
	// 当前条目
	var current = 1;
	// 切换时间
	var sleep = s  && s >0?s:3000;
	// 滑动
	var inter = null;
	// 初始化
	init();
	/**
	 * 初始
	 */
	function init() {
		if(size>1){
			// 遍历条目，装载条目数组
			uiItems.each(function(i,item){
				if(i == 0){
					if(fade != true){
						$(item).before($(uiItems[size - 1]).clone());
						items.push($(item).prev());
					}
					items.push($(item));
				}else if( i == size - 1){
					if(fade != true){
						$(item).after($(uiItems[0]).clone());
						items.push($(item));
						items.push($(item).next());
					}else{
						items.push($(item));
					}
				}else{
					items.push($(item));
				}
			});
			size = items.length;
		}
		$.grep(items,function(item,i){
			xItemArray.push($(item));
		});
		if(size  > 1){
			if(fade == true){
				createFadeHandlers();
				show(0);
			}else{
				createHandlers();
				show(1);
			}
			slide();
		}
	}

	/** 清除缓存 */
	function clearTimeoutCache(){
		$.grep(timeCache,function(t){
			clearTimeout(t);
		});
	}

	/**
	 * 创建页面索引号
	 */
	function createFadeHandlers(){
		handlers = container.parent().find('.handlers');
		if(handlers.size() == 0){
			handlers = $('<div class="handlers '+(handlersAlign?handlersAlign:'')+' '+(handlersClass?handlersClass:'')+'"></div>');
			container.parent().append(handlers);
		}
		handlers.html('');
		for(var i=0;i<xItemArray.length;i++){
			var handler = $('<a class="handler '+(handlersStyle?handlersStyle:'')+'"></a>');
			//handler.html(i+1);
			handlers.append(handler);
			handlerEvent(handler,i);
		}
		if(handlersAlign &&( handlersAlign=='align-center'|| handlersAlign=='center')){
			handlers.css({
				'left':(container.parent().innerWidth() - handlers.outerWidth()) + 'px'
			});
		}
		if(callback){
			callback(handlers);
		}
	}
	/**
	 * 创建页面索引号
	 */
	function createHandlers(){
		handlers = container.parent().find('.handlers');
		if(handlers.size() == 0){
			handlers = $('<div class="handlers '+(handlersAlign?handlersAlign:'')+' '+(handlersClass?handlersClass:'')+'"></div>');
			container.parent().append(handlers);
		}
		handlers.html('');
		for(var i=0;i<xItemArray.length - 2;i++){
			var handler = $('<a class="handler '+(handlersStyle?handlersStyle:'')+'"></a>');
			//handler.html(i+1);
			handlers.append(handler);
			handlerEvent(handler,i);
		}
		if(handlersAlign &&( handlersAlign=='align-center'|| handlersAlign=='center')){
			handlers.css({
				'left':(container.parent().innerWidth() - handlers.outerWidth()) + 'px'
			});
		}
		if(callback){
			callback(handlers);
		}
	}
	/**
	 * handler事件
	 */
	function handlerEvent(handler,i){
		handler.hover(function(){
			if(fade == true){
				current = i;
			}else{
				current = i+1;
			}
			show(current,true);
		});
	}
	/**
	 * 自动切换
	 */
	function slide(){
		var AutoSlide = function (){
			show(current,true);
			current ++;
		};
		inter = setInterval(AutoSlide,sleep);
	}
	/**
	 * 设置选中项
	 * @param index
	 */
	function selectHandler(index){
		if(handlers!=null && handlers.size()>0){
			handlers.find('.handler').removeClass('select');
			handlers.find('.handler:eq('+index+')').addClass('select');
		}
	}
	/**
	 * 获取横向滑动的页面
	 */
	function getXItem(index) {
		var select = null;
		$.grep(xItemArray,function(item,i){
			if (index == i){
				select = item;
				return;	
			}
		});
		return select;
	}
	function showFade(index){
		clearInterval(inter);
		if(index >= size  ){
			current = 0;
			index = 0;
		}
		var item = getXItem(index);
		container.width(container.parent().width());
		container.find('.item').fadeTo(300,0.4);
		container.find('.item').css({
			'position':'absolute',
			'z-index':98
		});
		item.css({'z-index':99});
		item.fadeTo(500,1);;

		selectHandler(index);

		timeCache.push(setTimeout(function(){
			slide();
		},sleep));
	}
	/**
	 * 显示指定item
	 * 
	 * @param item
	 */
	function show(index,anim) {
		clearTimeoutCache();
		if(fade == true){
			showFade(index);
			return;
		}
		var item = getXItem(index);
		var Methods = {
			/**
			 * 横向滑动显示
			 */
			xShow : function() {
				// 容器可见宽度
				var displayWidth = container.parent().innerWidth();
				if(minWidth && minWidth >0 && displayWidth < minWidth )
					displayWidth = minWidth;
				// 容器相对于容器父层节点的当前x坐标
				var x = container.offset().left - container.parent().offset().left;
				// 目标条目item的x相对于容器坐标
				if(!item){
					return;
				}
				var itemX = item.offset().left - container.parent().offset().left;

				if (itemX < 0) {
					// 当前坐标小于0，坐标向右偏移
					x = x - itemX > 0 ? 0 : x - itemX;
					this.xScrollTo(x);
				} else if (itemX > 0) {
					// 容器右侧宽度
					var containerRW = containerWidth + x;
					var itemWidth = item.outerWidth();// 目标条目宽度
					if (itemX + itemWidth >= displayWidth) {
						// 如果目标条目的x坐标+目标条目的宽度大于显示宽度
						// 当前坐标再向左偏移隐藏到条目的坐标
						x = x - ((itemX + itemWidth) - displayWidth);
						this.xScrollTo(x);
					} else if (containerRW < displayWidth && x < 0) {
						// 全部显示
						// 内层显示宽度小于外层宽度，且内层x坐标小于0
						// 向右偏移
						x = x + (displayWidth - containerRW);
						this.xScrollTo(x);
					} else {
						// _this.xScrollTo(0);
					}
				}
			},
			ie6Show:function(){
				clearInterval(inter);
				container.find('.item').hide();
				item.show();// 
				if(index == 0){
					// 第一条，实际是最后一条
					timeCache.push(setTimeout(function(){
						show(size - 2,false);
						selectHandler(size - 3);
					},300));
				}else if(index == size -1){
					// 最后一条，实际是第一条
					timeCache.push(setTimeout(function(){
						current = 1;
						show(1,false);
						selectHandler(0);
					},300));
				}else{
					selectHandler(index - 1);
				}
				timeCache.push(setTimeout(function(){
					slide();
				},sleep));
			},
			/**
			 * 设置节点到指定x坐标
			 */
			xScrollTo : function(x) {
				if(anim){
					container.stop().animate({
						'left' : x + 'px'
					}, {
						speed : '50',
						queue : true
					});
				}else{
					container.stop().css({'left':x+'px'});
				}
				clearInterval(inter);
				if(index == 0){
					// 第一条，实际是最后一条
					timeCache.push(setTimeout(function(){
						show(size - 2,false);
						selectHandler(size - 3);
					},300));
				}else if(index == size -1){
					// 最后一条，实际是第一条
					timeCache.push(setTimeout(function(){
						current = 1;
						show(1,false);
						selectHandler(0);
					},300));
				}else{
					selectHandler(index - 1);
				}
				timeCache.push(setTimeout(function(){
					slide();
				},sleep));
			}
		};
		if(_ie6 || _ie7){
			Methods.ie6Show();
		}else{
			Methods.xShow();
		}
	}
}