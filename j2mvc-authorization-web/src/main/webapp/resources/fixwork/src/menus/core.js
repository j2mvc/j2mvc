
(function( $, undefined ) {
	var timeCache = [];			// 延时内存

	/** 获取数字 */
	function getNumeric(v){
		var re = /[^a-zA-Z\d\u4e00-\u9fa5,.!?()，。．；;？]/g;
		v = v.replace(re,"");
		return v.match(/\d+/)?parseInt(v):0;
	}	
	/**
	 * 初始化数据 
	 */
	function initSource(source,response){
		if ( typeof source === "function") {
			source(function(result){
				response(result);
			});
		}else{
			return response(source);
		}
	}

	function getRealWidth(_target,width){
		width -= getNumeric(_target.css('border-left-width'));
		width -= getNumeric(_target.css('border-right-width'));
		width -= getNumeric(_target.css('margin-left'));
		width -= getNumeric(_target.css('margin-right'));
		width -= getNumeric(_target.css('padding-left'));
		width -= getNumeric(_target.css('padding-right'));
		return width;
	}
	function getRealHeight(_target,height){
		height -= getNumeric(_target.css('border-bottom-width'));
		height -= getNumeric(_target.css('border-top-width'));
		height -= getNumeric(_target.css('margin-bottom'));
		height -= getNumeric(_target.css('margin-top'));
		height -= getNumeric(_target.css('padding-bottom'));
		height -= getNumeric(_target.css('padding-top'));
		return height;
	}

	/**
	 * 菜单menu >> 布局为横排，可以靠左或靠右
	 * 菜单menu分为组和项。
	 * 项可以只有一层，也可以有多层，多层则以树的形式展现。
	 * 
	 */
	$.fn.menus = function(options){
		var msw = 0;
		var _root = $(this),
			_menus = _root.find('.menus:first'),
			_content = _root.find('.menus-content:first'),
			_menusOuter = _root.find('.menus-outer:first'),
			_panel =  _root.find('.menuspanel:first');
			_showHide = _root.find('.showhide:first');
		var methods = {
				/**
				 * 创建
				 */
				onload:function(){
					if(_menus.size() <1){
						var _this = this;
						_menus = $('<div class="menus"></div>'),
						_content = $('<div class="menus-content"></div>'),
						_menusOuter = $('<div class="menus-outer"></div>'),
						_panelleft = $('<td valign=top></td>'),
						_panelright = $('<td valign=top></td>'),
						_paneltr = $('<tr></tr>'),
						_panel = $('<table class="menuspanel"></table>');
						_showHide = $('<div class="showhide"></div>');
						_panel.addClass(_this.theme);

						
						// 向页面添加菜单节点
						_root.addClass('menuspanel');
						_menusOuter.append(_menus);

						// 菜单位置
						var menuOffset = _this.menuLayout.offset;
						if(menuOffset == 'right'){// 标签栏偏移右侧
							_content.addClass('float-left');	
							_panelleft.append(_content);
							_panelright.append(_menusOuter);		
						}else{// 标签栏偏移左侧
							_content.addClass('float-right');	
							_panelleft.append(_menusOuter);
							_panelright.append(_content);
						}
						_paneltr.append(_panelleft);
						_paneltr.append(_panelright);
						_panel.append(_paneltr);
						_panel.append(_showHide);

						// 菜单宽度
						msw = _this.menuLayout.menusWidth;
						if(msw){
							_menusOuter.width(msw);
							_menusOuter.parent().width(msw);
						}else{
							msw = _menusOuter.width();
						}
						// 调整尺寸
						_this.resize();		
						_root.append(_panel);
						
						// 配置数据
						this.setSource();
					}
					this.showHideButtonClick();
				},
				showHideButtonClick:function(){
					var _this = this;
					_showHide.click(function(){
						var _t = _menusOuter.parent();
						if(_t.css('display') != 'none'){
							_t.hide();
							_t.width(0);
						}else{
							_t.show();
							_t.width(msw);
						};
						_this.showHideButton($(this),_t);
						_this.resize(_this.resizeResponse);
					});
				},
				/**
				 * 显示隐藏按钮
				 */
				showHideButton:function(obj,t){
					_showHide = obj ||  _showHide; 
					var _t = t || _menusOuter.parent();
					var w = _t.width();
					var h = _t.height();
					var shw = _showHide.width();
					_showHide.height(h);
					if(w > shw){
						// 隐藏按钮
						_showHide.addClass('hide');
						_showHide.removeClass('show');
						var left = w - shw;
						_showHide.css({
							'left':left+'px'
						});
					}else{
						// 显示按钮
						_showHide.addClass('show');
						_showHide.removeClass('hide');
						_showHide.css({
							'left':'0px'
						});
					}
				},
				/**
				 * 配置数据源
				 */
				setSource:function(){
					var _this = this;
					initSource(this.source,function(result){	
						_this.source = result;
						// 调整标签内容栏尺寸				
						_this.createMenus();
						_this.ext = _this.complete(_content);// 回调，将内容节点输出到外部
						_this.afterComplete();
					});
				},
				create:function(){
					this.onload();
					return this;
				},
				createMenus:function(source){
					var _this = this;
					var source = this.source;
					if($.isArray(source) && source.length > 0){
						$.grep(source,function(data,i){
							var id = data.id;
							var label = data.label;
							var items = data.items;
							var _menu = _this.createMenu(items);
							var _group = $('<div class="menu-group"></div>');
							if(data.showTitle){
								// 显示菜单组标题
								var _title = $('<div class="menu-group-title"></div>');
								_title.html(label);
								if(typeof data.loaded === "function"){
									// 加载组标题结束
									data.loaded(_title,function(data,depth,complete){
										// 如果返回菜单项数据，添加菜单项
										var menu = _this.createMenuItem(data,depth);
										_menu.append(menu.get());
										if(typeof complete === 'function'){
											complete(menu);
										}
									});
								}
								_group.append(_title);
							}
							_group.attr('id',id);
							if(_menu){
								_group.append(_menu);
							}
							_menus.append(_group);
						});
					}
				},
				/**
				 * 创建菜单
				 */ 
				createMenu:function(items,depth){
					var _this = this;
					var _menu = $('<ul></ul>');
					if($.isArray(items) && items.length > 0){
						depth = depth || 0;
						depth ++;
						$.grep(items,function(data,i){
							// 创建菜单项
							var menu = _this.createMenuItem(data,depth);
							_menu.append(menu.get());
						});
					}
					return _menu;
				},
				/**
				 * 创建菜单项
				 */ 
				createMenuItem:function(data,depth){
					var _this = this;
					return Menu({
						depth:depth,
						data:data,
						paddingStep:_this.paddingStep,
						recursive:function(items,depth){
							return _this.createMenu(items,depth);
						},
						setSelectStyle:function(id){
							_this.setSelectStyle(id);
						},
						select:function(){
							//data.select(_this.ext);
							if(!data.noclick){
								data.select(_this.ext);
							}
						},
						hover:function(menu){
							//  经过事件
							if(typeof data.hover === "function")
								data.hover(menu,_this.ext);
						},
						blur:function(menu){
							// 鼠标离开
							if(typeof data.blur  === "function")
								data.blur(menu,_this.ext);
						}
					}).create();
				},
				getItem:function(mid){
					var source = this.source;
					var mi = null;
					if($.isArray(source))
					$.grep(source,function(data,i){
						var items = data.items;
						if($.isArray(items)){
							$.grep(items,function(item){
								if(item && item.id == mid){
									mi = item;
									return;
								}
							});
						}
						if(mi != null)
							return;
					});
					return mi;
				},
				/**
				 * 选中
				 * mid 菜单项ID
				 * q 地址栏参数
				 */
				selectMenu:function(mid,q,reload){
					if(mid && mid !=''){
						var mi = this.getItem(mid);
						if(mi != null && this.ext != null){
							mi.select(this.ext,q,reload);
						}else{
							mid = this.select(0,0);
						}
					}else{
						mid = this.select(0,0);
					}					
					// 设置样式
					this.setSelectStyle(mid);
				},
				/**
				 * 选中
				 * source 数据
				 * gindex 菜单组序号
				 * mindex 菜单项序号
				 */
				select:function(gindex,mindex){
					var mid = '';
					var source = this.source;
					if($.isArray(source) && source.length > 0){
						var _this = this;
						$.grep(source,function(data,i){
							var items = data.items;
							if($.isArray(items) && i == gindex){
								if(items.length > mindex){
									var item = items[mindex];
									mid = item.id;
									item.select(_this.ext);
								}
								return;
							}
						});
					}
					return mid;
				},
				// 设置选中样式
				setSelectStyle:function(mid){
					_panel.find('.menu').removeClass('select');
					_panel.find('#menu'+mid).addClass('select');
				},
				resize:function(response){
					this.resizeResponse = response;
					var width = _root.innerWidth() - _menusOuter.parent().outerWidth();
					_content.width(getRealWidth(_content,width));
					this.setHeight(_menusOuter);
					this.setHeight(_content);	
					this.setHeight(_panel);	
					_panel.width(_root.innerWidth());
					this.showHideButton();	
					if(response && typeof response == "function"){
						response(this.ext);
					}				
				},
				setHeight:function(_target){
					var height = getRealHeight(_target,_root.innerHeight());
					_target.height(height);
				}
				
		};
		var defaults = {
				paddingStep:12,
				resizeResponse:function(){},// 尺寸调整响应
				source:[{// 组数组
						id:-1,	// 组id
						label:'', // 组显示文本
						showTitle:true,// 显示组标题
						loaded:function(){},// 加载结束
						hover:function(){}, // 经过
						blur:function(){}, // 移出
						select:function(){},// 选中
						items:[
							{// 菜单项
								id:-1,
								label:'', // 显示文本
								href:'', // 链接
								icon:'', // 图标
								hover:function(){},// 经过回调
								blur:function(){}, // 离开回调
								select:function(){}, // 选中回调
								items:[] // 子菜单列表，可以实现树形
							}]
					}],
				menuLayout:{				// 标签布局
					offset:'left',		// 标签栏位置，默认为左侧，还可输入right
					menusWidth:200		// 标签栏宽度，只有设置居左或居右显示有效，默认为父容器宽度
				},
				complete:function(){},	// 加载菜单结束，返回内容HTML节点，HTML节点可以创建不同对象重新传入类
			 	ext : null,				// 从菜单回调后创建的对象，菜单点击事件调用此对象
			 	afterComplete:function(){// 创建不同对象重新传入类结束回调，通常会在设置默认选中项时调用
			 		
			 	}
		};
		/**
		 * 菜单项
		 */
		var Menu = function(options){
			var _item = $('<li></li>');
			var _icon = $('<img class="icon" border=0 >');
			var _label = $('<a class="label"></a>');
			var _div = $('<div class="menu"></div>');
			var _this = this;
			return $.extend({
				create:function(){
					_this = this;
					_div.append(_icon);
					_div.append(_label);
					_item.append(_div);	
					_this.recreate();
					return _this;
				},
				recreate:function(){
					var paddingLeft = (_this.depth*_this.paddingStep) + 'px';
					_div.css({'padding-left':paddingLeft});
					_item.attr('id',_this.data.id);
					_div.attr('id','menu'+_this.data.id);
					
					var className = _this.data.className;
					if(className && className!=''){
						_div.addClass(className);
						// 点击事件
						_div.click(function(){
							// 设置样式
							_this.setSelectStyle(_this.data.id);
							_this.select();
						});
					}
					
					_this.setIcon();
					
					_label.html(_this.data.label);
					_this.createChildren();
					
					_this._click();
					_this._hover();

				},
				createChildren:function(){
					var items = _this.data.items;
					if(items && $.isArray(items) && items.length >0){
						// 如果有下级菜单，
						// 移交给根函数递归创建
						var _UL = _this.recursive(items,_this.depth);
						if(_UL){
							_UL.hide();
							_item.append(_UL);
							// 添加打开收起按钮，子菜单列表默认为收起状态
							var _folding = $('<a class="folding"></a>');
							_folding.addClass('collapse');
							_folding.click(function(){
								_UL.toggle();
								if(_folding.hasClass('collapse')){
									_folding.removeClass('collapse');
									_folding.addClass('expand');
								}else{
									_folding.addClass('collapse');
									_folding.removeClass('expand');
								}
							});
							if(!_label.prev().hasClass('folding')){
								_folding.insertBefore(_label);
							};
						}
					};
				},
				remove:function(){
					_item.remove();
				},
				_click:function(){
					// 点击事件
					if(_label.next().size() > 0){
						_label.click(function(){
							// 设置样式
							_this.setSelectStyle(_this.data.id);
							_this.select();
						});
					}else{
						_div.click(function(){
							// 设置样式
							_this.setSelectStyle(_this.data.id);
							_this.select();
						});
					}
					
				},
				_hover:function(){
					//  经过事件
					_div.hover(function(){
						$(this).addClass('hover');
						_this.hover(_this);
					},function(){
						// 鼠标离开
						$(this).removeClass('hover');
						_this.blur(_this);
					});
				},
				hide:function(){
					_menu.hide();
				},
				show:function(){
					
				},
				setIcon:function(){
					// 图标
					var icon = this.data.icon;
					if(icon && icon!=''){
						_icon.attr('src',icon);
						_icon.css({'display':'block'});
						_icon.show();
					}else{
						_icon.hide();
					}
				},
				setHref:function(){
					// 链接
					if(this.data.href){
						_label.attr('href',_this.data.href);
					}
				},
				exists:function(){
					return _parent.find('li#'+_this.data.id);
				},
				get:function(){
					return _item;
				},
				getIcon:function(){
					return _icon;
				},
				getLabel:function(){
					return _label;
				},
				getDiv:function(){
					return _div;
				}
			},{
				depth:0,
				data:null,
				ext:null,
				paddingStep:12,
				recursive:function(){},
				select:function(){},
				hover:function(){},
				blur:function(){}
			},options);
		};
		return $.extend(methods,defaults,options).create();
	};
})(jQuery);
		