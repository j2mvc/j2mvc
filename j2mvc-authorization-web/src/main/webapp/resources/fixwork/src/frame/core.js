/**
 * 框架类库
 */
(function( $, undefined ) {
	
	STYLES = {
			FRAME_TOP:'frame-top',
			FRAME_TOP_NONAVBAR:'nonavbar',
			FRAME_TOP_NAVBAR:'frame-top-navbar',
			FRAME_MAIN:'frame-main',
			FRAME_MAIN_LEFT:'frame-main-left',
			FRAME_MAIN_LEFT_MENUS:'frame-menus',
			FRAME_MAIN_LEFT_MENU:'frame-menu',
			FRAME_MAIN_LEFT_MENU_TITLE:'frame-menu-title',
			FRAME_MAIN_CENTER:'frame-main-center',
			FRAME_MAIN_CENTER_FRAME:'frame-main-center-frame',
			FRAME_MAIN_RIGHT:'frame-main-right',
			FRAME_MAIN_RIGHT_FRAME:'frame-main-right-frame',
			FRAME_FOOTER:'frame-footer'
	};
	var ie = window.navigator.appVersion.toLowerCase().match(/msie/gi);
	var timeCache = [];
	var _frameset	= null,	// frame组,类型HTML节点
		navbar = null,		// navbar对象
		leftScroll = null,	// 导航栏的左侧滚动按钮,点击会向右滚动
		rightScroll = null, // 导航栏的右侧滚动按钮,点击会向右滚动
		_top = $('<div id="'+STYLES.FRAME_TOP+'"></div>'), // 顶部节点
		_navbarinner = $('<div class="navbar-inner"></div>');// 导航栏内层节点
		_navbar = $('<div id="'+STYLES.FRAME_TOP_NAVBAR+'"></div>'),// 导航栏节点
		_main = $('<div class="'+STYLES.FRAME_MAIN+'"></div>'),// 主内容节点
		_mainframes = [],// 主内容对象数组,保存主内容对象.支撑主内容页面切换
		_footer = $('<div id="'+STYLES.FRAME_FOOTER+'"></div>');// 底部

	/** 初始化iframe */
	function initFrame(target,source){
		if ( typeof source === "string" && $.trim(source).indexOf('url:') == 0 ) {
			source = $.trim(source);
			var url = source.substring(4,source.length);
			target.attr('src',url);
			target.attr('frameborder','no');
			target.attr('allowTransparency','true');
		}
	}
	/** 删除首位指定符号*/
	function delePrefix(v,regexp){//
		v = $.trim(v);
		if(v.indexOf(regexp) == 0 )
			return v.substring(1,v.length);
		return v;
	}
	/** 替换重复*/
	function deleRepeat(v,regexp){//
		return v.replace(eval('/(['+regexp+']+)\s*/g'),regexp);
	}
	/** 获取数字 */
	function getNumeric(v){
		var re = /[^a-zA-Z\d\u4e00-\u9fa5,.!?()，。．；;？]/g;
		v = v?v.replace(re,""):"0";
		v = v.match(/\d+/);
		return parseInt(v);
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
	 * 如果导航数据为空，返回。
	 * 如果导航为数组，且长度大于0，遍历导航数据，创建导航项。
	 * 导航单击事件，激活frame获取主内容方法.
	 */
	$.fn.frame.navbar = function(){
		var _this = null;
		return $.extend({
			create:function(){
				_this = this;
				if(settings.navbar)
				if($.isArray(settings.navbar) && settings.navbar.length > 0){
					$.grep(settings.navbar,function(item,i){
						if(_navbarinner.find('#'+item.id).size() < 1){
							_this.createNav(item,i == settings.navbar.length -1,i ==0);	
						}					
					});
					_navbar.append(_navbarinner);
					_top.removeClass(STYLES.FRAME_TOP_NONAVBAR);
					_top.append(_navbar);
				}else{
					_top.addClass(STYLES.FRAME_TOP_NONAVBAR);
				}
				return this;
			},
			get:function(){
				return _top.find('#'+STYLES.FRAME_TOP_NAVBAR);
			},
			remove:function(){
				_top.remove('#'+STYLES.FRAME_TOP_NAVBAR);
				
			},
			createExtra:function(){
				_navbar.append(_navbarinner);
				_top.removeClass(STYLES.FRAME_TOP_NONAVBAR);
				_top.append(_navbar);
				return this;
			},
			createNav:function(nav,last,first){
				var _nav = $('<a class="nav"></a>');
				_nav.attr('id',nav.id);
				//_nav.attr('href',settings.contextPath+nav.uri);
				if(last)
					_nav.addClass('last');
				else if(first)
					_nav.addClass('first');
				_nav.html(nav.label);
				_nav.click(function(){
					_this._onclick($(this).attr('id'));
				});
				_navbarinner.append(_nav);
			},
			_onclick:function(navId){		
				this.select(navId);
				settings.loadContents(navId);
			},
			select:function(id){
				if(_navbarinner.find('#'+$.trim(id)) && !_navbarinner.find('#'+$.trim(id)).hasClass('select')){
					_navbarinner.find('a').removeClass('select');
					if(id && $.trim(id).length>0)
						_navbarinner.find('#'+$.trim(id)).addClass('select');
				}
			},
			/**
			 * 监听导航数量,导航条目小于1,则移除导航,
			 * 导航数量为0和为1时,调整主内容区顶端坐标以及高度
			 */ 
			listenNavSize:function(){
				if(_navbar.find('a.nav').size()>0){
					_top.removeClass(STYLES.FRAME_TOP_NONAVBAR);
				}else{
					navbar.remove();
					_top.addClass(STYLES.FRAME_TOP_NONAVBAR);
				}
				if(_navbar.find('a.nav').size() == 0 || _navbar.find('a.nav').size() == 1){
					var mainDoms = _main.find('.frame-main-inner');
					$.grep(mainDoms,function(dom){
						if($(dom).css('display')!='none'){
							var id = $(dom).attr('id');
							$.grep(_mainframes,function(item){
								if(item.id == id){
									item.content.initResize();
									return ;
								}	
							});
							return;
						}
					});
				}
			},
			/**
			 * 调整导航栏尺寸
			 */
			resize:function(){
				var nbiw = _navbarinner[0].scrollWidth;
				var nbx = _navbar.offset().left;
				var nbw = _navbar.outerWidth();
				var w = _main.outerWidth();
				rsw = rightScroll?rightScroll.outerWidth() - 2 :2;
				var scrollWidth = (nbiw > nbw?nbiw:nbw) + nbx + rsw + 20;
				if(scrollWidth  >= w){
					if(rightScroll){
						nbw = w - rsw - nbx;
					}else if(leftScroll){
						nbw = nbw - leftScroll.offset().left - leftScroll.outerWidth();
					}	
					_navbar.width(nbw-20);
				}else{
					_navbar.css({'width':'auto'});
				}	
				this.setScrollX();
			},
			/**
			 * 计算导航按钮坐标
			 */
			setScrollX:function(){
				var nbt = _navbar.offset().top;
				var nbl = _navbar.offset().left;
				var nbw = _navbar.outerWidth();
				var tll = leftScroll.offset().left;
				var tlw = leftScroll.outerWidth();
				tll = (nbl - tlw);
				var trl = (nbl + nbw + 2);	
				leftScroll.css({'left':tll+'px','top':nbt+'px'});
				rightScroll.css({'left':trl+'px','top':nbt+'px'});
			},
			/**
			 * 构造滚动按钮
			 */
			createScroll:function(){
				if(!leftScroll || leftScroll.size() == 0){
					leftScroll = $('<a class="navbar-scroll left-to"></a>');
					_navbar.before(leftScroll);
				}
				if(!rightScroll || rightScroll.size() == 0){
					rightScroll = $('<a class="navbar-scroll right-to"></a>');
					_navbar.after(rightScroll);					
				}	
				// 调整导航尺寸
				this.resize();
				this.autoScroll(leftScroll,rightScroll);
				this.linstenScroll(leftScroll,rightScroll);
			},
			/**
			 * 自动滚动
			 * 计算导航栏内层节点宽度,实现选中项在可见区域内.
			 * 同时设置按钮的可见属性
			 */
			autoScroll:function(leftScroll,rightScroll){
				var _this = this;
				// 选中项
				var selectNav = _navbarinner.find('a.nav.select');
				if(selectNav.size() < 1)
					return;
				// 外层宽度
				var nbw = _navbar.outerWidth();	
				// 内层宽度
				var nbiw = _navbarinner[0].scrollWidth - 1;
				// 内层x
				var x = _navbarinner.offset().left - _navbar.offset().left;
				// 选中项x
				var snx = selectNav.offset().left - _navbar.offset().left;
				//$('#test').html(new Date().getTime()+': autoScroll >> snx='+snx+ ' x='+x + ' nbiw+x='+(nbiw + x)+' nbw='+nbw);
				if(snx < 0){
					// 内层坐标向右偏移sx
					x = x - snx>0?0:x-snx;
					_this.scrollTo(x);
				}else{
					// 内层右侧宽度
					var rw = nbiw + x ; 
					var snW = selectNav[0].scrollWidth;// 选择项的宽度	
					if(snx + snW >= nbw){ 
						// 如果选中项的x坐标+选择项的宽度大于外层宽度，向左偏移隐藏到选中项的坐标
						x = x - ((snx + snW) - nbw);
						_this.scrollTo(x);
					}else if(rw < nbw && x<0){
						// 全部显示
						// 内层显示宽度小于外层宽度，且内层x坐标小于0
						// 向右偏移
						x = x + (nbw-rw);
						_this.scrollTo(x);
					}else if(x>0){
						x = 0;
						_this.scrollTo(x);
					}
				}
				if(x>=0){
					leftScroll.hide();
				}else{
					leftScroll.show();
				}
				if(nbiw + x  > nbw){
					// 右侧显示+隐藏大于导航宽度
					rightScroll.show();
				}else{
					rightScroll.hide();
				}
			},
			/**
			 * 监听左侧按钮点击事件
			 * 点击向右滚动
			 */
			linstenScroll:function(leftScroll,rightScroll){
				var _this = this;
				// 左边滚动按钮向右滚动
				leftScroll.click(function(e){
					rightScroll.show();
					var x = _navbarinner.position().left;
					if(x < 0){
						// 获取将要显示的nav
						var nbx = _navbar.offset().left;
						var nav = _this.getScrolltoNav(nbx,'right');
						if(nav){
							var nx = nav.offset().left;// 导航x
							x = x + ( nbx - nx);								
						}
						if(x >= -1){
							x = 0;
							leftScroll.hide();
						}
						_this.scrollTo(x);
					}
					e.preventDefault();
				});
				/**
				 * 监听右侧滚动按钮事件
				 * 点击向左滚动
				 */ 
				rightScroll.click(function(){
					// 计算:nbw=200 nbiw=300 nbix=-10 rw=300+(-10)=290 x = -10-(290-200)
					var x =  _navbarinner.position().left;// 内层x坐标
					var nbiw =  _navbarinner[0].scrollWidth;
					var nbw = _navbar.outerWidth(); // 外层宽度
					var rw = nbiw+x; // 显示的宽度+右侧隐藏的宽度
					if(rw > nbw){
						// 获取将要显示的nav
						var nav = _this.getScrolltoNav(nbw,'left');
						if(!nav){
							x = x - (rw - nbw);
						}else{
							var nx = nav.offset().left - _navbar.offset().left;// 导航x
							var nw = nav[0].scrollWidth;// 导航宽度
							x = x - ((nx + nw) - nbw);// 内层x - 右侧未显示的导航宽度
						}
						if(nbiw+x <= nbw){
							rightScroll.hide();
						}
						_this.scrollTo(x);
					}	
					leftScroll.show();
					e.preventDefault();
				});
			},
			/**
			 * 设置导航栏内层节点到指定x坐标
			 */
			scrollTo:function(x){
				var t = setTimeout(function(){
					//_navbarinner.animate({'left':x+'px'},20);
					_navbarinner.css({'left':x+'px'});
				},1);
				timeCache.push(t);
			},
			/**
			 * 获取将要显示的nav
			 * @param left
			 */
			getScrolltoNav:function(x,to){				
				var navs = _navbarinner.find('a.nav');
				var nbx = _navbar.offset().left;
				// 大于0的请求// 向左滚动
				if(to == 'left'){
					for(var i=0;i<navs.length;i++){
						var nav = $(navs[i]);
						var nx = nav.offset().left - nbx;// 导航x
						var nw = nav[0].scrollWidth;// 导航宽度
						if(nx + nw > x){
							return nav;
						}	
					}
				}
				// 小于0的请求,向右滚动
				if(to == 'right'){
					for(var i=navs.length-1;i>=0;i--){
						var nav = $(navs[i]);
						var nx = nav.offset().left;// 导航x
						if(nx < x){
							return nav;
						}	
					}
				}
			}
			
		});
	};

	/**
	 * 创建额外的可删导航
	 * 点击导航时，根据导航id获取关联导航ID和关联菜单ID。
	 */
	$.fn.frame.extraNav = function(){
		return $.extend({
			create:function(nav){
				if(_navbarinner.find('#'+nav.id).size() < 1){
					var _nav = $('<a class="nav extra"></a>');
					var _delea = $('<span></span>');
					_nav.addClass('extra');
					_nav.attr('id',nav.id);
					//_nav.attr('href',settings.contextPath+nav.uri);
					_nav.append(_delea);
					_nav.append(nav.label);
					_nav.click(function(){						
						var id = $(this).attr('id').split("-");
						var navId = id[0];
						var menuId = id[1];
						settings.loadContents(navId,menuId,true);// true为需要添加可删除导航
					});
					_delea.click(function(){
						/**
						 * 删除导航,如果导航为选中状态,
						 * 获取当前显示的主内容区域的上一个显示ID,
						 * 加载上一个区域
						 */
						if(_nav.hasClass('select')){
							_navbarinner.find('a:last').addClass('last');
							var show = settings.getMainShow();

							if(show.prevId && show.prevId !=null){				
								var prevId = show.prevId.split("-");
								var navId = prevId.length>0?prevId[0]:'';
								var menuId = prevId.length>1?prevId[1]:'';
								$('#test2').html(new Date().getTime() + ' prevId:'+prevId+" navId="+navId);
								
								settings.loadContents(navId,menuId,true);
							}else{
								if(ie){
									var id = _nav.attr('id').split("-");
									settings.loadContents(id[0]);
								}else{
									//history.go(-1);
								}
							}
						}else{
							if(navbar.get().size()>0){
								navbar.createScroll();
							}
						}						
						_nav.remove();
						navbar.listenNavSize();
					});
					_navbarinner.find('.last').removeClass('last');
					_nav.addClass('last');
					_navbarinner.append(_nav);
					navbar.listenNavSize();
				}
				_navbarinner.find('.select').removeClass('select');
				_navbarinner.find('#'+nav.id).addClass('select');
				return this;
			}
		});
	};
	/**
	 * 创建菜单
	 * 遍历菜单组，以及菜单项，创建左侧菜单。
	 * 菜单点击事件激活frame获取主内容方法。
	 * 传递参数为menuId,和navId.
	 * 主内容Center和right区域将会更改。
	 * 导航栏通常会新增扩展导航，且是可以移除的。
	 */
	$.fn.frame.menus = function(){
		var _menus = $('<div class="'+STYLES.FRAME_MAIN_LEFT_MENUS+'"></div>');
		return $.extend({
			create:function(){
				var _this = this;	
				var sources = settings.contents.leftSource;
				if($.isArray(sources) && sources.length > 0){
					$.grep(sources,function(source,i){
						_this.createMenu(source);						
					});
				}
				return _menus;
			},
			createMenu:function(source){
				var _this = this;	
				var _menu = $('<ul class="'+STYLES.FRAME_MAIN_LEFT_MENU+'"></ul>');
				var group = source.group;
				if(group){
					var name = group.name;
					var _li = $('<li></li>');
					var _div = $('<div class="'+STYLES.FRAME_MAIN_LEFT_MENU_TITLE+'"></div>');
					if(name && name.length >0)
						_div.html(name);
					_li.append(_div);
					_menu.append(_li);
				}
				var items = source.items;
				if($.isArray(items) && items.length > 0){
					$.grep(items,function(item,i){
						var _item = _this.createItem(item);
						_menu.append(_item);
					});
					_menus.append(_menu);
				}
			},
			createItem:function(item){
				var _item = $('<li></li>');
				var _a = $('<a></a>');		
				var currentMenu = settings.contents.currentMenu;
				if(currentMenu && currentMenu.id == item.id)
					_a.addClass('select');
				else
					_a.removeClass('select');					
				_a.attr('id',item.id);
				//_a.attr('href',settings.contextPath+item.uri);
				_a.html(item.name);
				_a.click(function(){
					var navId = settings.contents?settings.contents.currentNav?settings.contents.currentNav.id:"":"";
					settings.loadContents(navId,item.id,true);
				});
				_item.append(_a);
				return _item;
			}
		});
	};
	/**
	 * 创建主内容区
	 * 包含左侧菜单，中间主内容。右侧广告。
	 */
	$.fn.frame.main = function(options){
		var _this = null;
		var _dom = $('<div class="frame-main-inner"></div>');
		var _left = $('<div class="'+STYLES.FRAME_MAIN_LEFT+'"></div>'),
			_center = $('<div class="'+STYLES.FRAME_MAIN_CENTER+'"></div>'),
			_right = $('<div class="'+STYLES.FRAME_MAIN_RIGHT+'"></div>');
		return $.extend({
				create:function(){
					_this = this;
					
					_this.createLeft();
					_this.createCenter();
					_this.createRight();	
					
					_dom.attr('id',_this.id);
					if(_left.text().length>0)
						_dom.append(_left);

					_dom.append(_center);
					_dom.append(_right);
								
					return _this;
				},
				recreate:function(){
					_this.createCenter();
					_this.createRight();
					_this.selectMenu();
				},
				get:function(){
					return _dom;
				},
				hide:function(){
					_dom.hide();
				},
				show:function(){
					_dom.show();
				},
				/**
				 * 选择菜单项,设置菜单项选中样式
				 */
				selectMenu:function(){
					var id = settings.contents?settings.contents.currentMenu?settings.contents.currentMenu.id:"":"";
					_left.find('a').removeClass('select');
					if(id && id != '')
						_left.find('#'+id).addClass('select');			
				},
				/**
				 * 加载iframe
				 * 遍历iframe所在父节点的所有iframe,获取当前显示中iframe的父节点ID,命名为prevId
				 * 如果即将显示的iframe父节确实可见的,则将prevId设置为即将显示的iframe父节点的prevId属性的值
				 */
				onloadIframe:function(iframe){
					var frames = iframe.parent().find('iframe');
					var prevId = '';
					iframe.load(function(){ 
						$.grep(frames,function(item){
							var fm = $(item);
							if(fm.css('display') != 'none'){
								var id = fm.attr('id');
								if(id.indexOf('mcframe_') == 0 ){
									id = id.replace('mcframe_','');
								}else if(id.indexOf('mrframe_') == 0 ){
									id = id.replace('mrframe_','');
								}
								prevId = fm.attr('id');
								//$('#test2').html(new Date().getTime() + ' id:'+id +' iframe.id='+iframe.attr('id'));
								fm.hide();
								return;
							}
						});
						_this._onloadIframe($(this),prevId);
					});
					//if(!ie && iframe && iframe.size()>0 && iframe[0].contentWindow != null){
						//_this._onloadIframe(iframe,prevId);
					//}
					_this.resizeWidth();
				},
				_onloadIframe:function(iframe,prevId){
					if(_this._bodyIsNull(iframe)){
						iframe.hide();
						iframe.parent().hide();
					}else{
						iframe.parent().show();
						//$('#test2').html($('#test2').html() + ' prevId:'+prevId);
						iframe.parent().attr('prevId',prevId);
						iframe.show();
					}
				},
				_bodyIsNull:function(iframe){
					if(!(iframe && iframe.size()>0 && iframe[0].contentWindow != null))
						return true;
					var _body = iframe.contents().find('body');
					if( _body){
						if($.trim(_body.css('background-color').toLowerCase()) == '#ffffff')
							_body.css('background-color','transparent');
						var text = _body.text();
						_body.css({
							'padding':'0px',
							'margin':'0px'
						});
						return ($.trim(text) == '');
					}	
				},
				createLeft:function(){
					var _menus = $.fn.frame.menus().create();					
					_left.append(_menus);
				},
				createCenter:function(){
					// 导航ID与菜单ID分隔符为"-"
					var frameId = 'mcframe_'+_this.id+'-'+_this.menuId;
					var _centerFrame = _center.find('#'+frameId);
					if(_centerFrame.size()<1){
						_centerFrame = $('<iframe class="'+STYLES.FRAME_MAIN_CENTER_FRAME+'"></iframe>');
						_centerFrame.attr('id',frameId);
						_centerFrame.attr('name',frameId);
						_center.append(_centerFrame);
						initFrame(_centerFrame,settings.contents.centerSource);
					}
					_this.onloadIframe(_centerFrame);
				},
				setMenuId:function(menuId){
					_this.menuId = menuId;
				},
				getMenuId:function(){
					return _this.menuId;
				},
				createRight:function(){
					var frameId = 'mrframe_'+_this.id+'_'+_this.menuId;
					var _rightFrame = _right.find('#'+frameId);
					if(_rightFrame.size() < 1){
						_rightFrame = $('<iframe class="'+STYLES.FRAME_MAIN_RIGHT_FRAME+'"></iframe>');
						_rightFrame.attr('id',frameId);
						_rightFrame.attr('name',frameId);
						_right.append(_rightFrame);
						initFrame(_rightFrame,settings.contents.rightSource);
					}
					_this.onloadIframe(_rightFrame);	
				},
				initResize:function(){
					var t = setTimeout(function(){
						_this.resizeHeight();
						_this.resizeWidth();				
					},1);
					timeCache.push(t);

					$(window).load(function(){
						_this.resizeHeight();
						_this.resizeWidth();
					});			
					$(window).resize(function(){
						_this.resizeHeight();
						_this.resizeWidth();
						if(navbar){
							navbar.createScroll();	
						}
					});	
				},
				resizeHeight:function(){
					// 获取底端scrollTop()>=$(document).height()-$(window).height()
					var h = window.innerHeight - _dom.offset().top;
					h = h - getNumeric(_center.css('border-bottom-width'));
					h = h - getNumeric(_center.css('border-top-width'));
					h = h - getNumeric(_center.css('margin-bottom'));
					h = h - getNumeric(_center.css('margin-top'));
					h = h - getNumeric(_center.css('padding-bottom'));
					h = h - getNumeric(_center.css('padding-top'));
					if (_frameset.find('#'+STYLES.FRAME_FOOTER).size()>0 && _footer.css('display') != 'none')
						h = _footer.offset().top - _dom.offset().top;
					//$('#test').html('resizeHeight >>t=' + _dom.offset().top +' wH='+window.innerHeight);
					_dom.height(h);			
					_left.height(h);			
					_center.height(h);						
					_right.height(h);
					_center.find('iframe').height(h);	
					_right.find('iframe').height(h);	
				},
				isVisible:function(obj){
					return (obj.size()>0  && obj.css('display') != 'none') || obj.size() <1;
				},
				resizeWidth:function(){
					var w = $(window).innerWidth();
					var _leftNode = _dom.find('.'+STYLES.FRAME_MAIN_LEFT);
					var _rightNode = _dom.find('.'+STYLES.FRAME_MAIN_RIGHT).find('iframe');
					if (_this.isVisible(_leftNode) && _this.isVisible(_rightNode)){
						w = w - _left.outerWidth() - _right.outerWidth();
					}else if (_this.isVisible(_leftNode) && !_this.isVisible(_rightNode)){
						w = w - _left.outerWidth();	
					}	 
					w = w - getNumeric(_center.css('margin-left'));
					w = w - getNumeric(_center.css('margin-right'));
					w = w - getNumeric(_center.css('padding-left'));
					w = w - getNumeric(_center.css('padding-right'));
					w = w - getNumeric(_center.css('border-left-width'));
					w = w - getNumeric(_center.css('border-right-width'));
					_center.width(w);
					_center.find('iframe').width(w);
				}
		},{id:null,menuId:null},options);
	};
	/**
	 * 方法
	 */
	$.fn.frame.methods = {
			loadContents:function(navId,menuId,extra){	
				var _this = this;
				var request = {navId:navId,menuId:menuId,extra:extra};
				// 主内容
				$('#test').html('request >> navId='+navId+' menuId='+menuId,' extra='+extra);
				this.getContents(request,function(data){
					if(data){
						settings.contents.id = data.id;
						settings.contents.leftSource = data.leftSource;
						settings.contents.centerSource = data.centerSource;
						settings.contents.rightSource = data.rightSource;
						settings.contents.currentMenu = data.currentMenu;
						settings.contents.currentNav = data.currentNav;
						navId = data.currentNav?data.currentNav.id:navId;
						menuId = data.currentMenu?data.currentMenu.id:menuId;						
						_this.createMain(data.id,menuId);
						if(!ie)
							_this.refreshState(data.currentNav.id,data.currentMenu.id,data.currentMenu.name,data.extraNav && data.extraNav.id);
						if(navbar){
							navbar.select(data.id);
						}
						if(data.extraNav && data.extraNav.id && settings.extraNav){
							if(navbar.get().size()<1)
								navbar = navbar.createExtra();
							// 创建可删除的导航
							$.fn.frame.extraNav().create(data.extraNav);							
						}
						if(navbar.get().size()>0){
							navbar.createScroll();
						}
					}
				});
				this.clearTimecache();			
			},
			loadNavbar:function(){
				navbar = $.fn.frame.navbar();
				// 导航
				if(this.getNavbar)
				this.getNavbar({},function(data){
						if(data){
							settings.navbar = data;
							navbar.create();
						}
					});
				this.clearTimecache();
			},
			stateListener:function(){
				var _this = this;
				window.addEventListener('popstate', function(e){
					  if (history.state){
					    var state = e.state;
					    _this.load(state.url);	
					    
					 }
				}, false);
			},
			refreshState:function(navId,menuId,title,extra){
				this.queryParams = {
						navId : navId,
						menuId: menuId
				};
				var url = settings.contextPath + '?nav_id='+navId  + "&menu_id="+menuId+(extra?"&extra=true":"");
				var state = {
					    title: title,
					    url: url
					};
				window.history.pushState(state, document.title, url);
				//history.pushState( null, null,url); 
			},
			load:function(url){
				var _this = this;
				var t = setTimeout(function(){
					if(url)
						_this.parseUrl(url);
					var navId = _this.queryParams?_this.queryParams.navId:'';
					var menuId = _this.queryParams?_this.queryParams.menuId:'';
					var extra = _this.queryParams?_this.queryParams.extra:'';
					_this.loadContents(navId,menuId,extra);
					_this.loadNavbar();	
				},1);	
				timeCache.push(t);
			},
			create:function(){
				this.load();
				if(!ie)
					this.stateListener();
				this.createTop();
				this.createFooter();
				
				_frameset.append(_top);
				_frameset.append(_main);			
				_frameset.append(_footer);
			},
			/**
			 * 创建主内容区
			 */
			createMain:function(id,menuId){
				var bool = true;
				$.grep(_mainframes,function(item){
					if(item.id == id){
						// 显示原框架页
						bool = false;
						item.content.setMenuId(menuId);
						item.content.show();
						item.content.recreate();
						return false;
					}else{
						item.content.hide();
					}	
				});
				if(bool){
					// 创建新的框架页
					var main = $.fn.frame.main({id:id,menuId:menuId}).create();	
					var _dom = main.get();
					_main.append(_dom);
					var _mainframe = {
							id:id,
							content:main
					};
					_mainframes.push(_mainframe);		
					main.initResize();
				}
			},
			/**
			 * 创建顶部
			 */
			createTop:function(){
				initHtml(settings.topSource,function(result){
					_top.html(result);
				});
			},
			/**
			 * 创建底部
			 */
			createFooter:function(){
				initHtml(settings.footerSource,function(result){
					_footer.html(result);
					_footer.show();
				});
			},
			/**
			 * 解析URL
			 */
			parseUrl :function(url){
				var _this = this;
				var queryString = url.substring(url.indexOf('\?'),url.length);
				var params = queryString.split('&');
				$.grep(params,function(item){
					var param = item.split('=');
					if(param.length > 0){
						if(param[0] == 'nav_id'){
							_this.queryParams.navId = param[1];  
						}
						else if(param[0] == 'menu_id')   
							_this.queryParams.menuId = param[1];  
					}			
				});
			},
			/**
			 * 清除timeout内存
			 */
			clearTimecache:function(){
				$.grep(timeCache,function(t){
					clearTimeout(t);
				});
			},
			/**
			 * 获取显示中的主内容区ID,及prevId
			 */
			getMainShow:function(){
				var frames = _main.find('iframe');
				var show = {};
				$.grep(frames,function(item){
					var fm = $(item);
					if(fm.css('display') != 'none'){
						var id = fm.attr('id');
						if(id.indexOf('mcframe_') == 0 ){
							id = id.replace('mcframe_','');
						}else if(id.indexOf('mrframe_') == 0 ){
							id = id.replace('mrframe_','');
						}
						var prevId = fm.parent().attr('prevId');
						if(prevId && prevId.indexOf('mcframe_') == 0 ){
							prevId = prevId.replace('mcframe_','');
						}else if(prevId && prevId.indexOf('mrframe_') == 0 ){
							prevId = prevId.replace('mrframe_','');
						}
						show = {
								id:id,
								prevId:prevId
						};
						return;
					}
				});
				return show;
			}
	};
	
	/**
	 * 属性
	 */
	$.fn.frame.defaults = {
			contextPath	:null,	
			queryParams :null,
			uri			:null,
			topSource	:null,			
			getNavbar	:null,				
			getContents	:null,	
			extraNav	:false,
			navbar		:null,			
			contents		:{
				prevId:null,
				id:'10001',
				leftSource:null,
				centerSource:null,
				rightSource:null,
				currentMenu:null,
				currentNav:null
			},
			footerSource  :null
	};
})(jQuery);