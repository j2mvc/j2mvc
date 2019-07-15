/**
 * 标签页框架tabframe
 * 组件:
 * 1:header 头部,不必须
 * 2:tabs 必须
 * 3:tabs.contents 滚动内容区
 * 5:footer 底部,不必须
 */
(function( $, undefined ) {

	var timeCache = [];			// 延时内存
	
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
		var re = /[^a-zA-Z\u4e00-\u9fa5,.!?()，。．；;？]/g;
		v = v.replace(re,"");
		return v.match(/\d+/)?parseInt(v):0;
	}	
	function getRandomColor(){
	    var x = 999999;
	    var y = 888888;
	    return '#eee';//+parseInt(Math.random()* (x - y + 1) + y);
	}
	/**
	 * 根据ID查看元素在数组中的位置
	 */
	function getIndex(id,array){
		var index = -1;
		$.grep(array,function(item,i){
			if (item.id == id){
				index = i;
				return;	
			}
		});
		return index;
	}
	/**
	 * 根据ID查看元素在数组中的位置
	 */
	function getItem(index,array){
		var arr = null;
		$.grep(array,function(item,i){
			if (index == i){
				arr = item;
				return;	
			}
		});
		return arr;
	}

	/**
	 * 设置tab不选中
	 */
	function unSelectTab(array){
		$.grep(array,function(item,i){
			item.tab.unSelect();
		});
	}
	/**
	 * 获取当前显示的scroller的ID
	 */
	function getShowid(array){
		var id = null;
		$.grep(array,function(item,i){
			if (item.tab.isSelect()){
				id = item.id;
				return;	
			}
		});
		return id;
	}
	/**
	 * 获取当前选中的scroller
	 */
	function getSelectIndex(array){
		var index = -1;
		$.grep(array,function(item,i){
			if (item.tab.isSelect()){
				index = i;
				return;	
			}
		});
		return index;
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
	 * 标签页
	 */
	$.fn.tabs = function(options){
		var _root = $(this);
		var methods = {
				// 创建标签页模块，根据不同的标签偏移布局设置，设置不同的布局
				create:function(){
					var _this = this,_header = null,_footer = null;
					var _tabs = $('<div class="tabs"></div>');
					var _contents = $('<div class="contents"></div>');
					var _tabsOuter = $('<div class="tabs-outer"></div>');
					var _contentsOuter = $('<div class="contents-outer"></div>');
					var _tabspanel = $('<div class="tabspanel"></div>');	
					_tabsOuter.append(_tabs);
					_contentsOuter.append(_contents);	

					var tabOffset = this.tabLayout.offset;
					var tsw = this.tabLayout.tabsWidth;
					if(tabOffset == 'top' ){// 标签栏偏移顶部			
						_tabspanel.append(_tabsOuter);
						_tabspanel.append($('<div style="clear:both"></div>'));
						_tabspanel.append(_contentsOuter);
					}else if(tabOffset == 'left'){// 标签栏偏移左侧
						if(tsw)
							_tabs.parent().width(tsw);
						_contentsOuter.addClass('vertical');
						_contentsOuter.css({'float':'right'});
						_tabspanel.append(_tabsOuter);
						_tabspanel.append(_contentsOuter);
					}else if(tabOffset == 'right'){// 标签栏偏移右侧
						if(tsw)
							_tabs.parent().width(tsw);
						_contentsOuter.addClass('vertical');	
						_contentsOuter.css({'float':'left'});	
						_tabspanel.append(_contentsOuter);
						_tabspanel.append(_tabsOuter);		
					}else if(tabOffset == 'bottom'){// 标签栏偏移底部
						_tabspanel.append(_contentsOuter);
						_tabspanel.append($('<div style="clear:both"></div>'));
						_tabspanel.append(_tabsOuter);
					}
					_root.append(_tabspanel);
					this.createScrollers(_tabs,_contents);
					// 调整标签内容栏尺寸
					this.resize(_tabs,_contents,_header,_footer);
					$(window).resize(function(){
						_this.resize(_tabs,_contents,_header,_footer);
					});
					_this.show(0,false);
					return _this;
				},
				/**
				 * 创建滚动标签页
				 */
				createScrollers:function(_tabs,_contents,_header,_footer){
					var _this = this;
					var array = this.source ? this.source:"";
					if($.isArray(array) && array.length > 0){
						$.grep(array,function(item,i){
							_this.createTabs(_tabs,_contents,item);
						});
					}
				},
				/**
				 * 创建标签栏,
				 * 标签栏应先加载,标签按钮点击事件发生时,激活内容区数据加载并显示
				 */
				createTabs:function(_tabs,_contents,data){
					var id = data.id;
					if(getIndex(id,this.scrollers) == -1){
						var scroller = {
								id:id,
								tab:Tab({
										_parent:_tabs,
										data:data.tab || '',
										id:id
									}),
								content:Content({
										_parent:_contents,
										id:id
									}),
								data:data
							};
						this.tabClick(scroller);
						this.scrollers.push(scroller);
					}
				},
				/**
				 * 标签点击事件，切换内容
				 * @param _tabs
				 * @param _contents
				 * @param scroller
				 */
				tabClick:function(scroller){
					var _this = this;
					var tab = scroller.tab;
					tab.getTag().click(function(){	
						var index = getIndex(scroller.id,_this.scrollers);
						_this.show(index,_this.anim);
					});
					tab.getDeleTag().click(function(){
						_this.remove(scroller);
					});
				},
				/**
				 * 删除标签，同时删除内容
				 * @param scroller
				 */
				remove:function(scroller){
					var scrollers = this.scrollers;
					var index = getIndex(scroller.id,scrollers);
					var isShow = scroller.content.isShow();
					var prevShowid = scroller.prevShowid;
					scroller.tab.getTag().remove();
					scroller.content.getTag().remove();
					scrollers.splice(index,1);
					var showindex = getSelectIndex(scrollers);
					if(isShow){
						// 如果当前正在显示中，删除后上一个显示的条目获得显示
						if(prevShowid && prevShowid!=null){
							index = getIndex(prevShowid,scrollers);
						}
						this.show(index,this.anim);
					}else if(showindex >= index){
						// 当前显示项序号大于删除的序号，显示显示中的条目，不要有滚动动画
						this.show(showindex,false);
					}
					// 如果当前只有一个scroller,隐藏序号为0的tab删除按钮
					if(scrollers.length == 1){
						var defaultScroller = getItem(0,scrollers);
						if(defaultScroller && defaultScroller!=null){
							defaultScroller.tab.getDeleTag().hide();
						}
					}else{
						// 显示序号为0的tab删除按钮
						var defaultScroller = getItem(0,scrollers);
						if(defaultScroller && defaultScroller!=null){
							defaultScroller.tab.getDeleTag().show();
						}
					}
				},
				/**
				 * 显示指定序号
				 * @param index
				 */
				show:function(index,anim){
					var scrollers = this.scrollers;
					index = index >-1 ?index:0;
					index = index > scrollers.length-1?scrollers.length-1:index;
					var scroller = getItem(index,scrollers);
					if(scroller){
						var prevShowid = getShowid(scrollers);
						if(prevShowid!=null){
							scroller.prevShowid = prevShowid;
						}
						unSelectTab(this.scrollers);
						scroller.tab.select();
						// 加载内容并显示
						if(scroller.content.getTag().text() == ''){
							scroller.content.setData(scroller.data.content);
							if(scroller.data.complete)
								scroller.data.complete(scroller);
						}
						scroller.content.show(anim);
					}
				},//
				/**
				 * 调整内容尺寸,需要先在内容加载前执行
				 */
				resize:function(_tabs,_contents,_header,_footer){
					var scrollers = this.scrollers;
					var _this = this;				
					var tabOffset = this.tabLayout.offset;
					var tabsWidth = this.tabLayout.tabsWidth;
					var _tabsOuter = _tabs.parent();
					var _contentsOuter = _contents.parent();
					
					if(tabOffset == 'top'){
						var height = this.getTopOffsetContentsHeight(_tabsOuter,_contentsOuter,_header,_footer);
						var width = _contentsOuter.outerWidth();
						_contentsOuter.height(height);
						$.grep(scrollers,function(scroller){
							var _content = scroller.content.getTag();
							scroller.tab.setOffset(tabOffset,tabsWidth);
							height = _this.getRealHeight(_content,height);
							width = _this.getRealWidth(_content,width);
							_content.height(height);
							_content.width(width);
						});
					}else if(tabOffset == 'left' || tabOffset == 'right'){		
						_tabsOuter.width(tabsWidth);	
						_tabs.width(tabsWidth);
						var tabsHeight = this.getHorizontalHeight(_tabsOuter,_header,_footer);
						var contentsHeight = this.getHorizontalHeight(_contentsOuter,_header,_footer);// 相对内容区外层
						var contentsWidth = this.getHorizontalWidth(_tabsOuter, _contentsOuter); // 相对内容区外层
						_tabs.height(tabsHeight);			
						_tabsOuter.height(tabsHeight);	
						_contents.width(contentsWidth);
						_contentsOuter.width(contentsWidth);
						_contentsOuter.height(contentsHeight);
						$.grep(scrollers,function(scroller){
							var _content = scroller.content.getTag();							
							scroller.tab.getTag().width(tabsWidth);	
							_content.height(contentsHeight);
							_content.width(contentsWidth);
							scroller.tab.setOffset(tabOffset,tabsWidth);
						});		
					}else if(tabOffset == 'bottom'){
						var height = this.getBootomOffsetContentsHeight(_tabsOuter,_contentsOuter,_header,_footer);
						var width = _contentsOuter.width();
						//_contentsOuter.height(height);
						$.grep(scrollers,function(scroller){
							var _content = scroller.content.getTag();
							scroller.tab.setOffset(tabOffset,tabsWidth);
							height = _this.getRealHeight(_content,height);
							width = _this.getRealWidth(_content,width);
							_content.height(height);
							_content.width(width);
						});
					}	
				},				
				/**
				 * 标签栏偏移顶部布局内容高度
				 * @param _tabsOuter
				 * @param _contentsOuter
				 * @param _footer
				 * @param tabOffset
				 */
				getTopOffsetContentsHeight:function(_tabsOuter,_contentsOuter,_header,_footer){
					height = _root.innerHeight() - (_header && _header.size()>0 && _header.css('display') != 'none'?_header.outerHeight():0);
					height -= (_footer && _footer.size()>0 && _footer.css('display') != 'none'?_footer.outerHeight():0);
					height -= (_tabsOuter && _tabsOuter.size()>0?_tabsOuter.outerHeight():0);
					height = this.getRealHeight(_contentsOuter,height);
					//$('#test').html('_rootHeight='+_root.innerHeight()+' _tabsOuter='+_tabsOuter.outerHeight() + ' height='+height + ' _contentsOuter.height='+_contentsOuter.outerHeight());
					return height;
				},
				/**
				 * 标签栏偏移底部布局内容高度
				 * @param _tabsOuter
				 * @param _contentsOuter
				 * @param _header
				 * @param _footer
				 */
				getBootomOffsetContentsHeight:function(_tabsOuter,_contentsOuter,_header,_footer){
					var height = _root.innerHeight() - _tabsOuter.outerHeight();
					height -= (_footer && _footer.size()>0 && _footer.css('display') != 'none'?_footer.outerHeight():0);
					height -= (_header && _header.size()>0 && _header.css('display') != 'none'?_header.outerHeight():0);
					return height;
				},
				/**
				 * 横向布局高度
				 * @param _target
				 * @param _footer
				 * @returns {Number}
				 */
				getHorizontalHeight:function(_tagert,_header,_footer){
					var height = _root.innerHeight() - (_header && _header.size()>0 && _header.css('display') != 'none'?_header.outerHeight():0);
					height -= (_footer && _footer.size()>0 && _footer.css('display') != 'none'?_footer.outerHeight():0);
					height -= (_header && _header.size()>0 && _header.css('display') != 'none'?_header.outerHeight():0);		
					height = this.getRealHeight(_tagert,height);	
					return height;
				},
				/**
				 * 横向布局宽度
				 * @param _tabsOuter
				 * @param _contentsOuter
				 * @returns {Number}
				 */
				getHorizontalWidth:function(_tabsOuter,_contentsOuter){
					var width = _root.innerWidth() - _tabsOuter.outerWidth();
					$('#test2').html('rw='+_root[0].scrollWidth+' s='+_root[0].offsetWidth);
					width = this.getRealWidth(_contentsOuter,width);
					return width;
				},
				getRealWidth:function(_target,width){
					width -= getNumeric(_target.css('border-left-width'));
					width -= getNumeric(_target.css('border-right-width'));
					width -= getNumeric(_target.css('margin-left'));
					width -= getNumeric(_target.css('margin-right'));
					width -= getNumeric(_target.css('padding-left'));
					width -= getNumeric(_target.css('padding-right'));
					return width;
				},
				getRealHeight:function(_target,height){
					height -= getNumeric(_target.css('border-bottom-width'));
					height -= getNumeric(_target.css('border-top-width'));
					height -= getNumeric(_target.css('margin-bottom'));
					height -= getNumeric(_target.css('margin-top'));
					height -= getNumeric(_target.css('padding-bottom'));
					height -= getNumeric(_target.css('padding-top'));
					return height;
				}
				
		};
		var defaluts = {
				anim:true,
				// 数据源，可以是静态内容，也可以通setSource动态设置
				source:[{
					id:1,
					tab:{},
					content:{}
				}],	
				tabLayout:{				// 标签布局
					offset:'top',		// 标签栏位置，默认为顶部
					tabsWidth:200,		// 标签栏宽度，只有设置居左或居右显示有效，默认为父容器宽度
					scroll:true,		// 允许滚动
					scrollbar:false 	// 是否显示滚动条
				},
				setSource:null,			// 调用时自定义请求，并返回数据到source
			 	scrollers : []			// 滚动内容数组
		};

		/**
		 * 标签
		 */
		var Tab = function(options){
			var _tab = $('<div class="tab" style="float:left"></div>');
			var _dele = $('<span class="delete"></span>');
			return $.extend({
				create:function(){
					var data = this.data;
					_tab.attr('id','tab'+this.id);
					// 支持图标显示
					if(data.icon){
						var _icon = $('<img/>');
						_icon.attr('src',data.icon);
						_tab.append(_icon);
					}
					// 标签文本
					if(data.label){
						var _a = $('<a class="label"></a>');
						_a.html(data.label);
						_tab.append(_a);
					}
					_tab.append(_dele);
					this._parent.append(_tab);
					return this;
				},
				select:function(){
					_tab.addClass('select');
				},			
				unSelect:function(){
					_tab.removeClass('select');
				},
				/**
				 * 设置偏移方式
				 */
				setOffset:function(tabOffset,tabsWidth){
					var _tabOuter = this._parent.parent();
					if(tabOffset == 'left'){
						_tab.addClass('offset-left');
						_tab.find('.tab').width(tabsWidth);	
						_tabOuter.css({
							'float':'left'
						});//.addClass('offset-left');// 标签栏居左
					}else if(tabOffset == 'right'){
						_tab.addClass('offset-right');
						_tab.width(tabsWidth);
						_tabOuter.css({
							'float':'right'
						});//.addClass('offset-right'); // 标签栏居右
					}
				},
				getTag:function(){
					return _tab;
				},
				getDeleTag:function(){
					return _dele;
				},
				isSelect:function(){
					return _tab.hasClass('select');
				}
			},{
				_parent:null,
				data:null
			},options).create();
		};
		/**
		 * 内容
		 */
		var Content = function(options){
			var _content = $('<div class="content"></div>');
			var _this = null;
			return $.extend({
				create:function(){
					_this = this;
					_content.attr('id','content'+this.id);
					_content.css({
						'background':getRandomColor()
					});
					this._parent.append(_content);
					return this;
				},
				setData:function(data){
					initHtml(data,function(result){
						_content.html(result);
					});
				},
				show:function(anim){
					this.anim = anim;
					// content-outer的可见宽度
					var _couter = this._parent.parent();
					if(_couter.hasClass('vertical')){
						// 纵向滑动显示
						this.yShow();
					}else{
						// 横向滑动显示
						this.xShow();
					}
				},
				/**
				 * 横向滑动显示
				 */
				xShow:function(){				
					// content-outer的可见宽度
					var _couter = this._parent.parent();
					var csow = this._parent.parent().outerWidth();
					// contents总宽度
					var csw = this._parent[0].scrollWidth;
					// contents的x坐标, 将会移动
					var x = this._parent.offset().left - _couter.offset().left;
					// 当前content的x坐标
					var cx = _content.offset().left - _couter.offset().left;

					//$('#test').html('csow='+csow+' csw='+csw+ ' cx='+cx+' _='+_content.outerWidth()+' _='+_content[0].scrollWidth);
					if(cx < 0){
						// 当前坐标小于0，坐标向右偏移
						x = x - cx>0?0:x-cx;
						_this.xScrollTo(x);
					}else if(cx > 0){
						// contents右侧宽度
						var crw = csw + x ; 
						var cw = _content.outerWidth();// 当前content的宽度	
						if(cx + cw >= csow){ 
							// 如果当前content的x坐标+当前content的宽度大于content-outer宽度，向左偏移隐藏到content的坐标
							x = x - ((cx + cw) - csow);
							_this.xScrollTo(x);
						}else if(crw < csow && x<0){
							// 全部显示
							// 内层显示宽度小于外层宽度，且内层x坐标小于0
							// 向右偏移
							x = x + (csow-crw);
							_this.xScrollTo(x);
						}else{
							_this.xScrollTo(0);
						}
					}
				},
				/**
				 * 设置节点到指定x坐标
				 */
				xScrollTo:function(x){
					var t = setTimeout(function(){
						if(_this.anim)
							_this._parent.animate({'left':x+'px'},300);
						else
							_this._parent.css({'left':x+'px'});
					},1);
					timeCache.push(t);
				},
				/**
				 * 横向滑动显示
				 */
				yShow:function(){
					// content-outer的可见高度
					var _couter = this._parent.parent();
					var csoh = _couter.outerHeight();
					// contents总高度
					var csh = this._parent[0].scrollHeight;
					// contents的y坐标, 将会移动
					var y = this._parent.offset().top - _couter.offset().top;
					// 当前content的y坐标
					var cy = _content.offset().top - _couter.offset().top;
					var zero = getNumeric(this._parent.css('border-top-width')); 
					$('#test').html('zero='+zero);
					if(cy < zero){
						// 当前坐标小于zero，坐标向下偏移
						y = y - cy>zero?zero:y-cy;
						_this.yScrollTo(y);
					}else {
						// contents底部高度
						var cbh = csh + y ; 
						if(cy >= csoh){ 
							// 如果当前content的y坐标大于content-outer高度，
							// 向上偏移
							y = y - cy;
							_this.yScrollTo(y);
						}else if(cbh < csoh && y<zero){
							// 全部显示
							// 内层显示宽度小于外层宽度，且内层x坐标小于0
							// 向右偏移
							y = y + (csoh-cbh);
							_this.yScrollTo(y);
						}else if(cy >zero){
							_this.yScrollTo(zero);
						}
					}
				},
				/**
				 * 设置节点到指定y坐标
				 */
				yScrollTo:function(y){
					var t = setTimeout(function(){
						if(_this.anim)
							_this._parent.animate({'top':y+'px'},300);
						else
							_this._parent.css({'top':y+'px'});
					},1);
					timeCache.push(t);
				},
				hide:function(){
					_content.css({
						'z-index':98
					});
				},
				getTag:function(){
					return _content;
				},
				/**
				 * 是否正在显示
				 */
				isShow:function(){
					// content-outer的可见宽度
					var _couter = this._parent.parent();
					if(_couter.hasClass('vertical')){
						// 纵向是否显示
						return this.yIsShow();
					}else{
						// 横向是否显示
						return this.xIsShow();
					}
				},
				/**
				 * 是否正在显示
				 */
				xIsShow:function(){
					// content-outer的可见宽度
					var _couter = this._parent.parent();
					// 当前content的x坐标
					var cx = _content.offset().left - _couter.offset().left;	
					return cx == 0;
				},
				/**
				 * 是否正在显示
				 */
				yIsShow:function(){
					// content-outer的可见宽度
					var _couter = this._parent.parent();
					// 当前content的x坐标
					var cy = _content.offset().top - _couter.offset().top;	
					var zero = getNumeric(this._parent.css('border-top-width')); 
					return cy == zero;
				}
			},{
				_parent:null,
				anim:true
			},options).create();
		};
		return $.extend(methods,defaluts,options).create();
	};
	
})(jQuery);