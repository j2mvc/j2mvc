
(function( $, undefined ) {
	$.fn.slider =function(options){
		var _root = $(this);
		var _target = null;
		
		var defaults = {
				slideLayout:"horizontal", // 默认横向滚动，可以纵向滚动vertical
				resizeNodeName:["img"],	  // 需要继承父节点尺寸的子节点,默认为一个img
				_target:null			  // 滚动的节点
		};
		var scrollers = [];
		
		var methods = {
				create:function(){
					
				},resize:function(){
					
				}		
		}
		var Content = function(options){
			var _content = null,_this = null,_resizeNodes = [];
			return $.extend({
				create:function(){
					_this = this;
					_content = this._content;
					_resizeNodes = this._resizeNodes;
					return this;
				},
				resizeFrame:function(width,height){
					width = width || _content.innerWidth();
					height = height || _content.innerHeight();
					$.grep(_resizeNodes,function(_node){
						_node.width(width);
						_node.height(height);
					});
				},
				hide:function(){
					_content.hide();
				},
				show:function(anim,response){
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
					if(response){
						response();
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
							//_this.xScrollTo(0);
						}
					}
				},
				/**
				 * 设置节点到指定x坐标
				 */
				xScrollTo:function(x){
					if(_this.anim)
						_this._parent.stop().animate({'left':x+'px'},{speed:'50',queue:false});
					else
						_this._parent.css({'left':x+'px'});
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
					if(_this.anim)
						_this._parent.stop().animate({'top':y+'px'},{speed:'50',queue:false});
					else
						_this._parent.css({'top':y+'px'});
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
				_resizeNodes:[]
			},options).create();
		};
		
		
	};
})(jQuery);