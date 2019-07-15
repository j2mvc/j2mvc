
(function( $, undefined ) {
	var t = null;

	/**
	 * 大图轮播
	 */
	$.fn.rotationMutli = function(options){
		var _this = this;
		var _root = $(this),docWidth = 0,docHeight = 0;
		var _rotationContainer = $('<div class="rotation-mutli"></div>'); 
		var _handlers = $('<div class="handlers"></div>');
		var defaults = {
				full:true,
				rotationItems:[],
				current:0,
				handlersStyle:'',
				handlersAlign:'',
				mainWidth:900,// 中心区域宽度
				sleep:5000,// 切换时间
				data:[{
					backgroundImage:'',// 背景图片
					bgcolor:'transparent',// 背景色
					items:[{
						left:0,// x坐标
						top:0,// y坐标
						inMode:'left',// 进入方式:left：左进，right：右进，top：上方进入，bottom：下方进入
						outMode:'right',// 出方式 :left：左出，right：右出，top：上方出，bottom：下方出
						body:'',// 主要内容
						sorter:1,// 次序
						inDelay:100, // 进入前延时，当前页显示后多少毫秒进入
						outDelay:100// 退出前延时，提前多少毫秒出
					}]
				}],
				finished:function(){}
		};
		var methods = {
				create:function(){
					_this = this;
					_root.append(_rotationContainer);
					docWidth = _root.width();
					docHeight = _root.height();
					docWidth = docWidth < _this.mainWidth ? _this.mainWidth:docWidth;
					_rotationContainer.width(docWidth);
					_rotationContainer.height(docHeight);
					_this.createItems();
					_this.createHandlers();
					_this.show(0);
					_this.current ++;
					_this.play();
					if(_this.full == true){
						_this.resize();
					}
				},
				play:function(){
					t = setInterval(_this.start,_this.sleep);
				},
				start:function(){
					if(_this.current == _this.data.length)
						_this.current = 0;
					_this.show(_this.current);
					_this.current ++;
				},
				resize:function(){
					function resize(){
						var width = $(document).width();
						width = width < _this.mainWidth ? _this.mainWidth:width;
						_rotationContainer.width(width);
						if(_this.handlersAlign &&( _this.handlersAlign=='align-center'|| _this.handlersAlign=='center')){
							_handlers.css({
								'left':((width - _handlers.width() )/2) + 'px'
							});
						}
					}
					$(window).resize(function(){
						resize();
					});
					resize();
				},
				createItems:function(){
					$.grep(_this.data,function(d){
						var _item = $('<div class="rotation-mutli-item"></div>');
						_item.width(docWidth);
						_item.height(docHeight);
						if(d.backgroundImage){
							var img = $('<img src="'+d.backgroundImage+'" style="position:absolute;z-index:-1;"/>');
							img.width(docWidth);
							img.height(docHeight);
							_item.append(img);
							if(_this.full == true)
							$(window).resize(function(){
								var width = $(window).width();
								var height = $(window).height();
								width = width>docWidth?width:docWidth;
								img.width(width);
//								img.height(height);
//								_item.css({
//									'background-image':'url('+d.backgroundImage+')',
//									'background-repeat':'no-repeat',
//									'background-position':'top center'
//								});
							})
//							_item.css({
//								'background-image':'url('+d.backgroundImage+')',
//								'background-repeat':'no-repeat',
//								'background-position':'top center'
//							});
							if(d.bgcolor){
								_item.css({
									'background-color':'#'+d.bgcolor
								});
							}
						}else{
							if(d.bgcolor){
								_item.css({
									'background':'#'+d.bgcolor
								});
							}
						}
						var _itemmain = $('<div class="item-main"></div>');
						_itemmain.css({
							'width':_this.mainWidth+'px',
							'height':docHeight+'px'
						});
						var innerItems = [];
						if(d.items && $.isArray(d.items)){
							$.grep(d.items,function(item){
								var innerItem = _this.wrapperInner(_itemmain, item);
								_itemmain.append(innerItem.doc());
								innerItems.push(innerItem);
							});
						}
						_item.append(_itemmain);
						_this.rotationItems.push({
							_item:_item,
							innerItems:innerItems
						});
						_rotationContainer.append(_item);
					});
				},
				wrapperInner:function(_itemmain,data){
					var innerItem =  new InnerItem({
						data:data,
						parentWidth:_this.docWidth,
						parentHeight:docHeight
						}).create();
					return innerItem;
				},
				/**
				 * 创建页面索引号
				 */
				createHandlers:function(){
					if(_this.data.length > 0){
						_handlers.html('');
						for(var i=0;i<_this.data.length ;i++){
							var handler = $('<a class="handler '+(_this.handlersStyle?_this.handlersStyle:'')+'"></a>');
							_handlers.append(handler);
							_this.handlerEvent(handler,i);
						}
						if(_this.handlersAlign &&( _this.handlersAlign=='align-center'|| _this.handlersAlign=='center')){
							_handlers.css({
								'left':((_root.innerWidth() - _handlers.width() )/2) + 'px'
							});
						}
						_rotationContainer.append(_handlers);
					}
				},
				/**
				 * handler事件
				 */
				handlerEvent:function (handler,i){
					handler.hover(function(){
						$(this).addClass('hover');
						clearInterval(t);
						_this.current = i+1;
						clearInterval(t);
						_this.show(_this.current,true);
					},function(){
						$(this).removeClass('hover');
						_this.play();
					});
				},
				/**
				 * 显示指定item
				 * 
				 * @param item
				 */
				show:function(current) {

					if(current < 0){
						current = _this.data.length -1;
					}
					if(current > _this.data.length - 1){
						current = 0;
					}
					_rotationContainer.find('.rotation-mutli-item').fadeTo(500,0.5);
					_rotationContainer.find('.rotation-mutli-item:not(:eq('+(current)+'))').css({'z-index':991});
					_rotationContainer.find('.rotation-mutli-item:eq('+(current)+')').fadeTo(1000,1);//.fadeIn(3000);// 
					_rotationContainer.find('.rotation-mutli-item:eq('+(current)+')').css({'z-index':992});
//					_rotationContainer.find('.rotation-mutli-item:eq('+(current)+')').fadeOut();
//					_rotationContainer.find('.rotation-mutli-item:not(:eq('+(current)+'))').fadeOut(500);

					
					$.grep(_this.rotationItems,function(item,i){
						if(i == current){
							$.grep(item.innerItems,function(innerItem){
								innerItem.playIn();
							});
						}else{
							$.grep(item.innerItems,function(innerItem){
								innerItem.playOut();
							});
						}
					});
					
					_handlers.find('.handler').removeClass('selected');			
					_handlers.find('.handler:eq('+current+')').addClass('selected');
				}
				
		};
		InnerItem = function(options){
			var _this = this;
			var _doc = $('<div class="inner-item"></div>');
			return $.extend({
				create:function(){
					_this = this;
					_doc.html(_this.data.body);
					_doc.hide();
					return _this;
				},
				doc:function(){
					return _doc;
				},
				playIn:function(){
					var speed = _this.data.inSpeed && _this.data.inSpeed > 0?_this.data.inSpeed:10;
					function playIn(){
						if(_this.data.inMode == 'left'){
							_doc.css({
								'left':(- _doc.width() )+'px',
								'top':_this.data.top+'px'
							});
						}else if(_this.data.inMode == 'right'){
							_doc.css({
								'left':_this.parentWidth+'px',
								'top':_this.data.top+'px'
							});
						}else if(_this.data.inMode == 'top'){
							_doc.css({
								'left':_this.data.left+'px',
								'top':(- _doc.height() )+'px'
							});
						}else if(_this.data.inMode == 'bottom'){
							_doc.css({
								'left':_this.data.left+'px',
								'top':_this.parentHeight+'px'
							});
						}else{
							_doc.css({
								'display':'none',
								'left':_this.data.left+'px',
								'top':_this.data.top+'px'
							});
						}
						if(_this.data.inMode == 'left' || _this.data.inMode == 'right' 
							|| _this.data.inMode == 'top' || _this.data.inMode == 'bottom'){
							_doc.show();
							_doc.stop().animate({
								'left':_this.data.left+'px',
								'top':_this.data.top+'px'
							}, {
								speed : speed,
								queue : true
							});
						}else if(_this.data.inMode == 'fade' ){
							var fadeInSpeed = _this.data.fadeInSpeed && _this.data.fadeInSpeed> 0?_this.data.fadeInSpeed:3000; 
							_doc.fadeIn(fadeInSpeed);
						}else{
							_doc.show();
						}
					}
					if(_this.data.inDelay && _this.data.inDelay > 0){
						setTimeout(function(){
							playIn();
						},_this.data.inDelay);
					}else{
						playIn();
					}
				},
				playOut:function(){
					var speed = _this.data.outSpeed && _this.data.outSpeed > 0?_this.data.outSpeed:20;
					function playOut(){
						if(_this.data.outMode == 'left'){
							_doc.stop().animate({
								'left':(- _doc.width() )+'px',
								'top':_this.data.top+'px'
							}, {
								speed : speed,
								queue : true
							});
						}else if(_this.data.outMode == 'right'){
							_doc.stop().animate({
								'left':_this.parentWidth+'px',
								'top':_this.data.top+'px'
							}, {
								speed :speed,
								queue : true
							});
						}else if(_this.data.outMode == 'top'){
							_doc.stop().animate({
								'left':_this.data.left+'px',
								'top':(- _doc.height() )+'px'
							}, {
								speed : speed,
								queue : true
							});
						}else if(_this.data.outMode == 'bottom'){
							_doc.stop().animate({
								'left':_this.data.left+'px',
								'top':_this.parentHeight+'px'
							}, {
								speed : speed,
								queue : true
							});
						}else if(_this.data.outMode == 'fade'){
							var fadeOutSpeed = _this.data.fadeOutSpeed && _this.data.fadeOutSpeed> 0?_this.data.fadeOutSpeed:3000; 
							_doc.fadeOut(fadeOutSpeed);
						}else{
							_doc.hide();
						}
					}
					if(_this.data.outDelay && _this.data.outDelay > 0){
						setTimeout(function(){
							playOut();
						},_this.data.outDelay);
					}else{
						playOut();
					}
				}
			},{
				parentWidth:0,
				parentHeight:0,
				data:{}
			},options);
		};
		return $.extend(methods,defaults,options).create();
	};
})(jQuery);
		