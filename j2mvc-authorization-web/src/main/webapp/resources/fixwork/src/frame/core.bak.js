/**
 * 框架类库
 */
(function( $, undefined ) {
	
	STYLES = {
			FRAME_TOP:'frame-top',
			FRAME_TOP_NAVBAR:'frame-top-navbar',
			FRAME_MAIN:'frame-main',
			FRAME_MAIN_LEFT:'frame-main-left',
			FRAME_MAIN_CENTER:'frame-main-center',
			FRAME_MAIN_RIGHT:'frame-main-right',
			FRAME_FOOTER:'frame-footer'
	};
	
	var settings = null,
		_frameset	= null,
		_top = $('<div id="'+STYLES.FRAME_TOP+'"></div>'),
		_navbar = $('<div id="'+STYLES.FRAME_TOP_NAVBAR+'"></div>'),
		_main = $('<div class="'+STYLES.FRAME_MAIN+'"></div>'),
		_mainframes = [],
		_footer = $('<div id="'+STYLES.FRAME_FOOTER+'"></div>');

	function initFrame(target,source){
		if ( typeof source === "string" && $.trim(source).indexOf('url:') == 0 ) {
			source = $.trim(source);
			var url = source.substring(4,source.length);
			target.attr('src',url);
			target.attr('frameborder','no');
			target.attr('allowTransparency','true');
		}
	}

	function onbeforeunload() {
		// 用户点击浏览器右上角关闭按钮或是按alt+F4关闭
		if (event.clientX > document.body.clientWidth && event.clientY < 0
				|| event.altKey) {
			// alert("点关闭按钮");
			// window.event.returnValue="确定要退出本页吗?";
		}
		// 用户点击任务栏，右键关闭。s或是按alt+F4关闭
		else if (event.clientY > document.body.clientHeight || event.altKey) {
			// alert("任务栏右击关闭");
			// window.event.returnValue="确定要退出本页吗?";
		}
		// 其他情况为刷新
		else { // alert("刷新页面");
		}
	}
	var DispClose = true;
	function CloseEvent() {
		if (DispClose) {
			return "是否离开当前页面?";
		}
	}

	function UnLoadEvent() {
		DispClose = false;
		// 在这里处理关闭页面前的动作
	}
	/** 获取数字 */
	function getNumeric(v){
		var re = /[^a-zA-Z\d\u4e00-\u9fa5,.!?()，。．；;？]/g;
		return parseInt(v.replace(re,""));
	}	
	/** 初始化HTML */
	function initHtml(source,response) {		
		if ( typeof source === "string" && $.trim(source).indexOf('url:') == 0 ) {
			source = $.trim(source);
			var url = source.substring(4,source.length);
			$.ajax({
					url: url,
                    type: "get",
					sourceType: "json",
					success: function( result ) {
						if(result!=null)
							response(result);
					},
					error: function() {
					}
				});
		} else {
			if(source!=null)
				response(source);
		};
	};

	/**
	 * 框架
	 */
	$.fn.frame = function(options){
		_frameset = $(this);
	
		settings = $.extend($.fn.frame.methods,$.fn.frame.defaults,options); 
		
		this.each(function(){
			settings.create();
		});
	};

	/**
	 * 创建顶部导航
	 */
	$.fn.frame.navbar = function(){
		return $.extend({
			create:function(){
				var _this = this;
				
				if($.isArray(settings.navbar)){
					$.grep(settings.navbar,function(item,i){
						if(_navbar.find('#'+item.id).size() < 1)
							_this.createNav(item);
					});
					_top.append(_navbar);
				}
			},
			createNav:function(nav){
				var _nav = $('<a></a>');
				_nav.attr('id',nav.id);
				_nav.attr('href',nav.link);
				_nav.html(nav.label);
				_navbar.append(_nav);
			}
		});
	};

	/**
	 * 创建主内容区
	 */
	$.fn.frame.mainItem = function(){
		var _itemId = new Date().getTime()+"_"+(Math.floor(Math.random()*999+1));
		var _dom = $('<div id="'+_itemId+'"></div>');
		var _left = $('<iframe class="'+STYLES.FRAME_MAIN_LEFT+'"></iframe>'),
			_center = $('<iframe class="'+STYLES.FRAME_MAIN_CENTER+'"></iframe>'),
			_right = $('<iframe class="'+STYLES.FRAME_MAIN_RIGHT+'"></iframe>');
		return $.extend({
				create:function(){
					this.createLeft();
					this.createCenter();
					this.createRight();
					_dom.append(_left);
					_dom.append(_center);
					_dom.append(_right);
					this.onloadIframe(_left);
					this.onloadIframe(_center);
					this.onloadIframe(_right);					
					
					_main.append(_dom);
					_mainframes.push(_dom);
					
					this.initResize();
				},
				onloadIframe:function(iframe){
					var _this = this;
					iframe.load(function(){
						var _body = $(this).contents().find('body');
						if( _body){
							if($.trim(_body.css('background-color').toLowerCase()) == '#ffffff')
								_body.css('background-color','transparent');
							var text = _body.text();
							if($.trim(text).length< 1){
								$(this).remove();
							}
						}
						_this.initResize();
					});
				},
				createLeft:function(){
					initFrame(_left,settings.frameMain.leftSource);
				},
				createCenter:function(){
					initFrame(_center,settings.frameMain.centerSource);
				},
				createRight:function(){
					initFrame(_right,settings.frameMain.rightSource);
				},
				initResize:function(){
					var _this = this;
					$(window).load(function(){
						_this.resizeHeight();
						_this.resizeWidth();
					});					
					$(window).resize(function(){
						_this.resizeHeight();
						_this.resizeWidth();
					});					
				},
				resizeHeight:function(){
					var h = $(document).height() - _dom.offset().top;
					if (_frameset.find('#'+STYLES.FRAME_FOOTER).size()>0)
						h = _footer.offset().top - _dom.offset().top;
					_dom.height(h);			
					_left.height(h);			
					_center.height(h);				
					_right.height(h);	
				},
				resizeWidth:function(){
					var w = $(document).innerWidth();
					if (_dom.find('.'+STYLES.FRAME_MAIN_LEFT).size()>0
							&& _dom.find('.'+STYLES.FRAME_MAIN_RIGHT).size()>0){
						w = w - _left.outerWidth() - _right.outerWidth();						
					}else if (_dom.find('.'+STYLES.FRAME_MAIN_LEFT).size()>0 
							&& _dom.find('.'+STYLES.FRAME_MAIN_RIGHT).size()<1){
						w = w - _left.outerWidth();	
					}	 
					w = w - getNumeric(_center.css('margin-left'));
					w = w - getNumeric(_center.css('margin-right'));
					w = w - getNumeric(_center.css('padding-left'));
					w = w - getNumeric(_center.css('padding-right'));
					w = w - getNumeric(_center.css('border-left-width'));
					w = w - getNumeric(_center.css('border-right-width'));
					_center.width(w);
				}
		});
	};
	/**
	 * 方法
	 */
	$.fn.frame.methods = {
			create:function(){
				this.createTop();				
				this.createFooter();
				_frameset.append(_top);
				_frameset.append(_main);
				_frameset.append(_footer);	
				$.fn.frame.mainItem().create();
			},
			createTop:function(){
				initHtml(settings.topSource,function(result){
					_top.html(result);
					$.fn.frame.navbar().create();
				});
			},
			createFooter:function(){
				initHtml(settings.footerSource,function(result){
					_footer.html(result);
				});
			}
	};
	
	/**
	 * 属性
	 */
	$.fn.frame.defaults = {
			topSource		:null,				
			navbar		:null,				
			frameMain		:{
				leftSource:null,
				centerSource:null,
				rightSource:null
			},
			footerSource  :null
	};
})(jQuery);