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
	/** 获取数字?\d */
	function getNumeric(v){
		var re = /[^a-zA-Z\d\u4e00-\u9fa5,.!?()，。．；;？]/g;
		v = v.replace(re,"");
		return v.match(/\d+/)?parseInt(v):0;
	}	
	function getRandomColor(){
	    var x = 999999;
	    var y = 333333;
	    return '#'+parseInt(Math.random()* (x - y + 1) + y);
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
	 * 根据index获取元素
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
	 * 根据ID查看元素
	 */
	function getItemById(id,array){
		var result = null;
		$.grep(array,function(item){
			if (item.id == id){
				result = item;
				return;	
			}
		});
		return result;
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
	 * 获取当前选中的scroller index
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
	/**
	 * 获取当前选中的scroller
	 */
	function getSelected(array){
		var scroller = -1;
		$.grep(array,function(item,i){
			if (item.tab.isSelect()){
				scroller = item;
				return;	
			}
		});
		return scroller;
	}
	/** 初始化HTML */
	//var initHtmlRequest = null;
	function initHtml(source,response) {
		if ( typeof source === "string" && $.trim(source).indexOf('url:') == 0 ) {
			source = $.trim(source);
			var url = source.substring(4,source.length);
			//if(initHtmlRequest!=null){
			//	initHtmlRequest.abort();
			//}
			initHtmlRequest = $.ajax({
					url: url,
                    type: "get",
					sourceType: "json",
					success: function( result ) {
						if(result!=null)
							response(result);
					}
				});
		} else {
			if(source!=null)
				response(source);
		};
	};

	/** 初始化iframe */
	function initFrame(target,source){
		if ( typeof source === "string" && $.trim(source).indexOf('url:') == 0 ) {
			source = $.trim(source);
			var url = source.substring(4,source.length);
			target.attr('src',url);
		}
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
			response(source);
		}
	}
	/**
	 * 标签页
	 */
	$.fn.tabHost = function(options){
		var _root = $(this);
		var _header = $('<div class="tabs-header"></div>'),
			_footer = $('<div class="tabs-footer"></div>'),
			_tabs =  $('<div class="tabs"></div>'),
			_contents = $('<div class="contents"></div>'),
			_tabsOuter = $('<div class="tabs-outer"></div>'),
			_contentsOuter = $('<div class="contents-outer"></div>'),
			_panel = $('<div class="tabhostpanel"></div>');
		var methods = {
				// 创建标签页模块，根据不同的标签偏移布局设置，设置不同的布局
				create:function(){
					var _this = this;
					var tabOffset = _this.tabLayout.offset;
					var tsw = _this.tabLayout.tabsWidth;
						_tabsOuter.append(_tabs);
						_contentsOuter.append(_contents);
						_panel.append(_header);		
					if(this.tabNone == true)
						_tabsOuter.hide();
					if(tabOffset == 'top' ){// 标签栏偏移顶部			
						_panel.append(_tabsOuter);
						_panel.append($('<div style="clear:both"></div>'));
						_panel.append(_contentsOuter);
					}else if(tabOffset == 'left'){// 标签栏偏移左侧
						if(tsw)
							_tabsOuter.width(tsw);
						_contentsOuter.addClass('vertical');
						_contentsOuter.css({'float':'left'});
						_panel.append(_tabsOuter);
						_panel.append(_contentsOuter);
					}else if(tabOffset == 'right'){// 标签栏偏移右侧
						if(tsw)
							_tabsOuter.width(tsw);
						_contentsOuter.addClass('vertical');	
						_contentsOuter.css({'float':'left'});	
						_panel.append(_contentsOuter);
						_panel.append(_tabsOuter);		
					}else if(tabOffset == 'bottom'){// 标签栏偏移底部
						_panel.append(_contentsOuter);
						_panel.append($('<div style="clear:both"></div>'));
						_panel.append(_tabsOuter);
					}
					this.createHeader();
					this.createFooter();
					this.setTheme();
					_panel.append(_footer);
					_root.append(_panel);
					_header.after($('<div style="clear:both"></div>'));
					_footer.before($('<div style="clear:both"></div>'));
					return _this;
				},
				// 设置主题
				setTheme:function(){
					_panel.addClass(this.theme);
					_tabsOuter.addClass(this.theme);
					_contentsOuter.addClass(this.theme);
					_header.addClass(this.theme);
					_footer.addClass(this.theme);
				},
				load:function(id,reload,response){
					var _this = this;
					initSource(this.source,function(result){
						_this.createScrollers(result);
						// 调整标签内容栏尺寸
						_this.resize();
						var index = id && id!=''? getIndex(id,_this.scrollers):0;
						index = index>-1?index:0;
						var scoller = _this.show(index,false, reload || _this.reload);
						if(typeof response == 'function'){
							response(scoller);
						}
					});
				},
				createHeader:function(){
					if(this.header)
					initHtml(this.header,function(result){
						_header.html(result);
					});
				},
				createFooter:function(){
					if(this.footer)
						initHtml(this.footer,function(result){
							_footer.html(result);
						});
				},
				getTag:function(){
					return _root;
				},
				/**
				 * 从外部调用创建
				 */
				loadExt:function(data,reload){
					var index = this.createTab(data,reload);
					this.resize();
					this.show(index,this.anim,reload);
				},
				/**
				 * 创建滚动标签页
				 */
				createScrollers:function(source){
					var _this = this;
					if($.isArray(source) && source.length > 0){
						$.grep(source,function(item,i){
							_this.createTab(item);
						});
					}
				},
				/**
				 * 创建标签栏,
				 * 标签栏应先加载,标签按钮点击事件发生时,激活内容区数据加载并显示
				 */
				createTab:function(data,reload){
					var id = data.id;
					var index = getIndex(id,this.scrollers);
					if(index == -1){
						var scroller = {
								id:id,
								tab:Tab({
										_parent:_tabs,
										data:data.tab || '',
										id:id
									}),
								content:Content({
										_parent:_contents,
										id:id,
										globalanim:this.anim // 全局动画，若为false,content不采用滚
									}),
								data:data
							};
						this.tabClick(scroller);
						this.scrollers.push(scroller);
						return getIndex(id,this.scrollers);
					}else if(reload){
						// 刷新tab及内容
						var scroller = getItem(index,this.scrollers);
						scroller.data = data;
						// 刷新tab
						scroller.tab.data = data.tab;
						scroller.tab.setIcon();
						scroller.tab.setLabel();
						scroller.tab.setExtra();
						// 刷新内容
						this.tabClick(scroller);
					}
					return index;
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
						if(_this.anim === true){
							// 滚动显示
							_this.showScroller(scroller,_this.anim);
						}else{
							_this.showScrollerNoanim(scroller);
						}
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
					if(index < 0)
						return;
					var isShow = scroller.content.isShow();
					var prevShowid = scroller.prevShowid;
					scroller.tab.getTag().remove();
					scroller.content.getTag().remove();
					scrollers.splice(index,1);
					//var showindex = getSelectIndex(scrollers);
					if(isShow){
						// 如果当前正在显示中，删除后上一个显示的条目获得显示
						if(prevShowid && prevShowid!=null){
							index = getIndex(prevShowid,scrollers);
						}
						this.show(index,this.anim);
					}//else if(showindex == index){
						// 当前显示项序号大于删除的序号，显示显示中的条目，不要有滚动动画
					//	this.show(showindex,false);
					//}
					this.setDeleTag();
				},
				/**
				 * 删除指定ID标签，同时删除内容
				 * @param scroller
				 */
				removeById:function(id,label){
					var scrollers = this.scrollers;
					var index = getIndex(id,scrollers);
					var scroller = getItem(index,scrollers);
					if(!scroller || scroller == null || index < 0 ||
							(label && label != null && label != '' 
								? scroller.tab.data.label != label:""))
						return;
					var isShow = scroller.content.isShow();
					var prevShowid = scroller.prevShowid;
					scroller.tab.getTag().remove();
					scroller.content.getTag().remove();
					scrollers.splice(index,1);
					if(isShow){
						// 如果当前正在显示中，删除后上一个显示的条目获得显示
						if(prevShowid && prevShowid!=null){
							index = getIndex(prevShowid,scrollers);
						}
						this.show(index,this.anim);
					}
					this.setDeleTag();
				},
				/**
				 * 显示指定序号
				 * @param index
				 * @param anim
				 */
				show:function(index,anim,reload){
					var scrollers = this.scrollers;
					index = index >-1 ?index:0;
					index = index > scrollers.length-1?scrollers.length-1:index;
					scroller = getItem(index,scrollers);
					if(this.anim){
						this.showScroller(scroller, anim,reload);
					}else{
						this.showScrollerNoanim(scroller,reload);
					}
					return scroller;
				},
				/**
				 * 显示指定标签页
				 * @param scroller
				 * @param anim
				 */
				showScroller:function(scroller,anim,reload){
					if(scroller){
						this.setPrevShowid(scroller);
						unSelectTab(this.scrollers);
						scroller.tab.select();
						// 加载内容并显示
						if(scroller.content.getTag().html() == ''){
							scroller.content.setData(scroller.data.content,reload);
							if(scroller.data.select)
								scroller.data.select(scroller);
						}
						this.resize();
						scroller.content.show(anim);
						if(this.showResponse){
							// 显示的回调函数
							// 如无此方法,通常会在尺寸发生调整,多个标签切换时有问题
							// 因为该标签页显示后,应该要重新调整该标签页内所有HTML节点.
							// 而子节点,在此类中无法获取,故需要在调用类时定义回调函数
							this.showResponse(scroller);
						}
					}
					this.setDeleTag();
				},
				// 无动画的显示
				showScrollerNoanim:function(scroller,reload){
					if(scroller){
						$.grep(this.scrollers,function(scrol){
							if(scrol.id != scroller.id){
								scrol.content.hide();
							}
						});
						this.setPrevShowid(scroller);
						unSelectTab(this.scrollers);
						scroller.tab.select();
						// 加载内容并显示
						if(scroller.content.getTag().html() == '' || reload){
							scroller.content.setData(scroller.data.content,reload);
							if(scroller.data.select){
								scroller.data.select(scroller);
							}
						}
						this.resizeSelected(scroller);
						scroller.content.show();
						if(this.showResponse){
							// 显示的回调函数
							// 如无此方法,通常会在尺寸发生调整,多个标签切换时有问题
							// 因为该标签页显示后,应该要重新调整该标签页内所有HTML节点.
							// 而子节点,在此类中无法获取,故需要在调用类时定义回调函数
							this.showResponse(scroller);
						}
					}
					this.setDeleTag();
				},
				setPrevShowid:function(scroller){
					var prevShowid = getShowid(this.scrollers);
					if(prevShowid!=null){
						scroller.prevShowid = prevShowid;
					}
				},
				/**
				 * 清除timeout内存
				 */
				clearTimecache:function(){
					$.grep(timeCache,function(t){
						clearTimeout(t);
					});
				},
				setDeleTag:function(){
					var scrollers = this.scrollers;
					// 如果当前只有一个scroller,隐藏序号为0的tab删除按钮
					if(scrollers.length == 1){
						var defaultScroller = scrollers[0];
						if(defaultScroller && defaultScroller!=null){
							defaultScroller.tab.removeDeleTag();
						}
					}else{
						// 显示序号为0的tab删除按钮,此项加入，选择第一个tab会卡。
//						var defaultScroller = scrollers[0];
//						if(defaultScroller && defaultScroller!=null){
//							defaultScroller.tab.appendDeleTag();
//							defaultScroller.tab.setHover();
//							this.tabClick(defaultScroller);
//						}
					}
				},
				resize:function(response){
					var _this = this;
					//var t = setTimeout(function(){
					this._resize();		
					if(typeof response == "function"){
						// 调整尺寸后的回调函数
						response(getSelected(_this.scrollers));
					}
					//},1);
					//timeCache.push(t);
				}
				,//
				/**
				 * 调整内容尺寸,需要先在内容加载前执行
				 */
				_resize:function(){
					var _this = this;
					var scrollers = this.scrollers;
					var tos = this.tabLayout.offset;
					var tsw = this.tabLayout.tabsWidth;			
					
					var width = _root.innerWidth();
					this.resizeHeader(width);
					this.resizeFooter(width);
					
					if(tos == 'top'){
						var height = this.getTBCHeight();
						this.resizeTB(width,height);					
						$.grep(scrollers,function(scroller){
							_this.resizeContent(scroller, tos, tsw, width, height);
						});
					}else if(tos == 'left' || tos == 'right'){
						var tsh = this.getHorizontalHeight(_tabsOuter);
						var csh = this.getHorizontalHeight(_contentsOuter);// 相对内容区外层
						var csw = this.getHorizontalWidth(_tabsOuter, _contentsOuter); // 相对内容区外层
						this.resizeLR(tsw,tsh,csw,csh);
						$.grep(scrollers,function(scroller){
							_this.resizeContent(scroller, tos, tsw, csw, csh);
						});		
					}else if(tos == 'bottom'){
						var height = this.getTBCHeight();
						this.resizeTB(width,height);
						$.grep(scrollers,function(scroller){
							_this.resizeContent(scroller, tos, tsw, width, height);
						});
					}
				},
				resizeContent:function(scroller,tos,tsw,width,height){
					var content = scroller.content;
					var _content = scroller.content.getTag();
					scroller.tab.setOffset(tos,tsw);
					height = getRealHeight(_content,height);
					width = getRealWidth(_content,width);
					_content.height(height);
					_content.width(width);
					content.resizeFrame(width,height);
				},
				resizeHeader:function(width){
					if(_header){
						if(_header.html() == ''){
							_header.hide();
						}
						_header.width(getRealWidth(_header,width));
					}
				},
				resizeFooter:function(width){
					if(_footer){
						if(_footer.html() == ''){
							_footer.hide();
						}
						_footer.width(getRealWidth(_footer,width));
					}
				},
				resizeTB:function(width,height){
					_tabsOuter.width(getRealWidth(_tabsOuter,width));
					_contentsOuter.width(getRealWidth(_contentsOuter,width));
					_contentsOuter.height(getRealHeight(_contentsOuter,height));
				},
				resizeLR:function(tsw,tsh,csw,csh){	
					_tabsOuter.width(tsw);	
					_tabs.width(tsw);
					_tabs.height(tsh);			
					_tabsOuter.height(tsh);	
					_contents.width(csw);
					_contentsOuter.width(csw);
					_contentsOuter.height(csh);
				},
				/**
				 * 调整选中的标签页尺寸
				 */
				resizeSelected:function(scroller){
					var tos = this.tabLayout.offset;
					var tsw = this.tabLayout.tabsWidth;			
					
					var width = _root.innerWidth();
					this.resizeHeader(width);
					this.resizeFooter(width);
					
					if(tos == 'top'){
						var height = this.getTBCHeight();
						this.resizeTB(width,height);		
						this.resizeContent(scroller, tos, tsw, width, height);
					}else if(tos == 'left' || tos == 'right'){
						var tsh = this.getHorizontalHeight(_tabsOuter);
						var csh = this.getHorizontalHeight(_contentsOuter);// 相对内容区外层
						var csw = this.getHorizontalWidth(_tabsOuter, _contentsOuter); // 相对内容区外层
						this.resizeLR(tsw,tsh,csw,csh);
						this.resizeContent(scroller, tos, tsw, csw, csh);
					}else if(tos == 'bottom'){
						var height = this.getTBCHeight();
						this.resizeTB(width,height);
						this.resizeContent(scroller, tos, tsw, width, height);
					}
				},				
				/**
				 * 标签栏偏移顶部和底部布局内容高度
				 */
				getTBCHeight:function(){
					var height = _root.innerHeight();
					height -= (_header && _header.size()>0 && _header.css('display') != 'none'?_header.outerHeight():0);
					height -= (_footer && _footer.size()>0 && _footer.css('display') != 'none'?_footer.outerHeight():0);
					height -= (_tabsOuter && _tabsOuter.size()>0 && _tabsOuter.css('display') != 'none'?_tabsOuter.outerHeight():0);
					height = getRealHeight(_contentsOuter,height);
					return height;
				},
				/**
				 * 横向布局高度
				 * @param _target
				 * @returns {Number}
				 */
				getHorizontalHeight:function(_tagert){
					var height = _root.innerHeight();
					height -= (_footer && _footer.size()>0 && _footer.css('display') != 'none'?_footer.outerHeight():0);
					height -= (_header && _header.size()>0 && _header.css('display') != 'none'?_header.outerHeight():0);		
					height = getRealHeight(_tagert,height);	
					return height;
				},
				/**
				 * 横向布局宽度
				 * @param _tabsOuter
				 * @param _contentsOuter
				 * @returns {Number}
				 */
				getHorizontalWidth:function(){
					var width = _root.innerWidth() - _tabsOuter.outerWidth();
					width = getRealWidth(_contentsOuter,width);
					return width;
				}
				
		};
		var defaluts = {
				tabNone:false,
				reload:false,
				theme:'default',
				anim:false,
				header:{},
				footer:{},
				source:[{
					id:1,
					tab:{},
					content:{
						source:'',
						type:'html'	// 内容的类型，可以是frame框架页/html
					}
				}],	
				tabLayout:{				// 标签布局
					offset:'top',		// 标签栏位置，默认为顶部
					tabsWidth:200,		// 标签栏宽度，只有设置居左或居右显示有效，默认为父容器宽度
					scroll:true			// 允许滚动
				},
			 	scrollers : [],			// 滚动内容数组
			 	finished:function(scroller){}   // 加载完成调用函数 ,返回 
		};

		/**
		 * 标签
		 */
		var Tab = function(options){
			var _tab = $('<div class="tab" style="float:left"></div>');
			var _dele = $('<span class="delete"></span>');
			var _icon = $('<img/>');
			var _a = $('<a class="label"></a>');
			return $.extend({
				create:function(){
					var data = this.data;
					_tab.attr('id','tab'+this.id);
					// 支持图标显示
					if(data.icon){
						this.setIcon();
						_tab.append(_icon);
					}
					// 标签文本
					if(data.label){
						this.setLabel();
						_tab.append(_a);
					}
					if(data.extra){
						this.setExtra();
						_tab.append(_dele);
					}
					this.setHover();
					this._parent.append(_tab);
					return this;
				},
				setIcon:function(){
					_icon.attr('src',this.data.icon);
				},
				setLabel:function(){
					_a.html(this.data.label);
				},
				setExtra:function(){
					_tab.addClass('extra');
				},
				appendDeleTag:function(){
					if(_tab.find('.delete').size()<1){
						if(this.data.extra){
							_tab.addClass('extra');
							_tab.append(_dele);
						}
					}
				},
				removeDeleTag:function(){
					_tab.removeClass('extra');
					_tab.find('.delete').remove();
				},
				setHover:function(){
					_tab.hover(function(){
						_tab.addClass('hover');
					},function(){
						_tab.removeClass('hover');
					});
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
							'float':'left'
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
			var _iframe = _content.find('iframe');
			var _this = null;
			return $.extend({
				create:function(){
					_this = this;
					_content.attr('id','content'+this.id);
//					_content.css({
//						'background':getRandomColor()
//					});
					this._parent.append(_content);
					return this;
				},
				setData:function(data,reload){
					if(typeof data == 'string'){
						initHtml(data,function(result){
							_content.html(result);
						});
					}else if(data){
						if(data.type == 'html')
							initHtml(data.source,function(result){
								_content.html(result);
							});
						else if(data.type == 'frame'){
							if(_iframe.size()<1){
								//scrolling=no 
								_iframe = $('<iframe class="content-iframe" frameBorder=0 border=0 allowTransparency=true  charset="utf-8"></iframe>');
								_content.append(_iframe);
								initFrame(_iframe,data.source);
							}else if(reload){
								initFrame(_iframe,data.source);
							}
							this.onloadIframe();
						}
					}
					
				},
				resizeFrame:function(width,height){
					width = width || _content.innerWidth();
					height = height || _content.innerHeight();
					_content.css({"overflow":'hidden'});
					_iframe.width(width);
					_iframe.height(height);
				},
				onloadIframe:function(){
					if(_iframe && _iframe.size()>0){
						_iframe.load(function(){ 
							if(_iframe[0].contentWindow != null){
								var _body = _iframe.contents().find('body');
								if( _body && _body.css('background-color')){
									if($.trim(_body.css('background-color').toLowerCase()) == '#ffffff')
										_body.css('background-color','transparent');
									_body.css({
										'padding':'0px',
										'margin':'0px',
										'border':'0px'
									});
									//_iframe.height(_body[0].scrollHeight);
								}	
							}
						});
					}
					this.resizeFrame();
				},
				hide:function(){
					_content.hide();
				},
				show:function(anim,response){
					_content.addClass('loading');
					if(this.globalanim){
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
					}else{
						// 不滚动
						_content.show();
					}
					if(response){
						response();
					}
					timeCache.push(setTimeout(function(){
						_content.removeClass('loading');
					},5000));
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
							//_this.xScrollTo(0);
						}
					}
				},
				/**
				 * 设置节点到指定x坐标
				 */
				xScrollTo:function(x){
					//var t = setTimeout(function(){
						if(_this.anim)
							_this._parent.stop().animate({'left':x+'px'},{speed:'50',queue:false});
						else
							_this._parent.css({'left':x+'px'});
					//},1);
					//timeCache.push(t);
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
					//var t = setTimeout(function(){
						if(_this.anim)
							_this._parent.stop().animate({'top':y+'px'},{speed:'50',queue:false});
						else
							_this._parent.css({'top':y+'px'});
					//},1);
					//timeCache.push(t);
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
				anim:true,
				globalanim:false
			},options).create();
		};
		return $.extend(methods,defaluts,options).create();
	};
	
})(jQuery);