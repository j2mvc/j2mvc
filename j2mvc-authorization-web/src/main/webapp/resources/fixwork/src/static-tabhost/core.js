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
	/**
	 * 标签页
	 */
	$.fn.tabhost = function(options){
		var _root = $(this);
		var _this = null;
		var _tabs = null,
			_contents = null;
		var methods = {
				create:function(){
					_this = this;
					_tabs = _root.find('.static-tabs'),
					_contents = _root.find('.static-contents');
					_this.loadScrollers();
					_this.show(0);
					return _this;
				},
				getTag:function(){
					return _root;
				},
				loadScrollers:function(){
					var tabs = _tabs.find('.tab');
					if(tabs.size()>0)
					tabs.each(function(i,tab){
						var _tab = $(tab);
						var id = "item_"+i;
						var index = getIndex(id,_this.scrollers);
						if(index == -1){
							var scroller = {
									id:id,
									tab:Tab({
										_tab:_tab,
										id:id
									}),
									content:Content({
										_content : _contents.find('.items-container:eq('+i+")"),
										id:id,
										globalanim:_this.anim 
									})
							};
							_this.tabHover(scroller);
							_this.scrollers.push(scroller);
						}
						
					});
				},
				/**
				 * 标签点击事件，切换内容
				 * @param _tabs
				 * @param _contents
				 * @param scroller
				 */
				tabHover:function(scroller){
					var _this = this;
					var tab = scroller.tab;
					tab.getTag().hover(function(){
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
				show:function(index,anim){
					var scrollers = this.scrollers;
					index = index >-1 ?index:0;
					index = index > scrollers.length-1?scrollers.length-1:index;
					scroller = getItem(index,scrollers);
					if(this.anim){
						this.showScroller(scroller, anim);
					}else{
						this.showScrollerNoanim(scroller);
					}
					return scroller;
				},
				/**
				 * 显示指定标签页
				 * @param scroller
				 * @param anim
				 */
				showScroller:function(scroller,anim){
					if(scroller){
						this.setPrevShowid(scroller);
						unSelectTab(this.scrollers);
						scroller.tab.select();
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
				showScrollerNoanim:function(scroller){
					if(scroller){
						$.grep(this.scrollers,function(scrol){
							if(scrol.id != scroller.id){
								scrol.content.hide();
							}
						});
						this.setPrevShowid(scroller);
						unSelectTab(this.scrollers);
						scroller.tab.select();
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
				}
				
		};
		var defaluts = {
				tabNone:false,
				anim:false,
			 	scrollers : [],			// 滚动内容数组
			 	finished:function(scroller){}   // 加载完成调用函数 ,返回 
		};

		/**
		 * 标签
		 */
		var Tab = function(options){
			var _tab = null;
			var _dele = $('<span class="delete"></span>');
			return $.extend({
				create:function(){
					_tab = this._tab;
					this.setHover();
					return this;
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
						_tab.parent().addClass('hover');
						_tab.addClass('hover');
					},function(){
						_tab.parent().removeClass('hover');
						_tab.removeClass('hover');
					});
				},
				select:function(){
					_tab.parent().addClass('select');
					_tab.addClass('select');
				},			
				unSelect:function(){
					_tab.parent().removeClass('select');
					_tab.removeClass('select');
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
				_tab:null
			},options).create();
		};
		/**
		 * 内容
		 */
		var Content = function(options){
			var _this = null;
			var _content = null;
			return $.extend({
				create:function(){
					_content = this._content;
					return this;
				},
				hide:function(){
					_content.hide();
				},
				show:function(anim,response){
					if(this.globalanim){
						this.anim = anim;
						// content-outer的可见宽度
						var _couter = _content.parent().parent();
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
				},
				/**
				 * 横向滑动显示
				 */
				xShow:function(){				
					// content-outer的可见宽度
					var _couter = _content.parent().parent();
					var csow = _content.parent().parent().outerWidth();
					// contents总宽度
					var csw = _content.parent()[0].scrollWidth;
					// contents的x坐标, 将会移动
					var x = _content.parent().offset().left - _couter.offset().left;
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
					if(_this.anim)
						_content.parent().stop().animate({'left':x+'px'},{speed:'50',queue:false});
					else
						_content.parent().css({'left':x+'px'});
				},
				/**
				 * 横向滑动显示
				 */
				yShow:function(){
					// content-outer的可见高度
					var _couter = _content.parent().parent();
					var csoh = _couter.outerHeight();
					// contents总高度
					var csh = _content.parent()[0].scrollHeight;
					// contents的y坐标, 将会移动
					var y = _content.parent().offset().top - _couter.offset().top;
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
					if(_this.anim)
						_this.parent().stop().animate({'top':y+'px'},{speed:'50',queue:false});
					else
						_this.parent().css({'top':y+'px'});
				},
				getTag:function(){
					return _content;
				},
				/**
				 * 是否正在显示
				 */
				isShow:function(){
					if(_this.xShow){
						// 横向显示
						return this.xIsShow();
					}else{
						// 纵向显示
						return this.yIsShow();
					}
				},
				/**
				 * 是否正在显示
				 */
				xIsShow:function(){
					// content-outer的可见宽度
					var _couter = _content.parent().parent();
					// 当前content的x坐标
					var cx = _content.offset().left - _couter.offset().left;	
					return cx == 0;
				},
				/**
				 * 是否正在显示
				 */
				yIsShow:function(){
					var _couter = _content.parent().parent();
					// 当前content的x坐标
					var cy = _content.offset().top - _couter.offset().top;	
					var zero = getNumeric(this._parent.css('border-top-width')); 
					return cy == zero;
				}
			},{
				_content:null,
				xShow:true,
				anim:true,
				globalanim:false
			},options).create();
		};
		return $.extend(methods,defaluts,options).create();
	};
	
})(jQuery);