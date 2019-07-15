
(function( $, undefined ) {
	$.fn.slider =function(options){
		var _root = $(this);
		
		var defaults = {
				//slideLayout:"horizontal", // 默认横向滑动，可以纵向滚动vertical
				input:'',					// 对应的input
				type:'default',				// 默认，drag:可拖动
				upper:10,					// 上限
				lower:1,					// 下限
				step:1,						// 步长
				plusminus:false,			// 允许自定义加减
				data:[],					// 数据
				value:'',					// 默认值 
				itemPadding:10,			    // 内边距
				mid_display: true,			// 中间显示
				plus_button:false,			// 增加按钮
				minus_button:false,			// 减少按钮
				show_input:false,			// 显示值
				setValue:function(response){},		// 外部改变值
				getValue:function(){},		// 获取值
				onItemClick:function(){}
		};
		var _container = $('<div class="slider-container"></div>');
		var _sliderRange = $('<div class="slider-range"></div>');
		var _sliderThrough = $('<div class="slider-through"></div>');
		var _sliderThroughCover = $('<div class="slider-through-cover"></div>');
		var _sliderText = $('<div class="slider-text"></div>');

		var _sliderDragerTip = $('<div class="slider-drager-tip"></div>');
		var _sliderDrager = $('<div class="slider-drager"></div>');
		var _arrow = $('<div class="arrow"></div>');
		var _arrowUp = $('<a class="up"></a>');
		var _arrowDown = $('<a class="down"></a>');
		
		var width,_this=null;
		
		var methods = {
				create:function(){
					_this = this;
					_root.html('');
					_sliderRange.append(_sliderText);
					if(_this.type == 'drag'){
						// 拖动DIV
						_sliderRange.append(_sliderThrough);
						_sliderRange.append(_sliderThroughCover);
						_sliderDrager.append(_sliderDragerTip);
						_sliderRange.append(_sliderDrager);
					}
					_container.append(_sliderRange);
					// 加减按钮
					if(_this.plusminus == true){
						_arrowUp.hover(function(){
							$(this).addClass('hover');
						},function(){
							$(this).removeClass('hover');
						});
						_arrowDown.hover(function(){
							$(this).addClass('hover');
						},function(){
							$(this).removeClass('hover');
						});
						_arrow.append(_arrowUp);
						_arrow.append(_arrowDown);
						_container.append(_arrow);
						if(typeof _this.input == 'object' && _this.input.size() > 0){
							_this.inputBlur();
							_this.arrowClick();
						}
					}
					
					_root.append(_container);

					_container.find('div').bind("contextmenu",function(){return false;});  
					_container.find('div').bind("selectstart",function(){return false;});  
					_container.find('div').keydown(function(){return key(arguments[0])});
					
					_this.setValue(_this.response);
					_this.load();
					return _this;
				},
				load:function(){
					$.grep(_this.data,function(d){
						var _itemText = $('<div class="item"></div>');
						_itemText.css({
							'padding-left':_this.itemPadding+'px',
							'padding-right':_this.itemPadding+'px',
							'text-align':_this.textAlign
						});
						if(d.label){
							_itemText.append($('<span class="label" '
									+(_this.textFloat != ''?' style="float:'+d.textFloat+'"':'')
									+'>'+d.label+'</span>'));
						}
						if(d.value){
							_itemText.append($('<input type="hidden" value="'+d.value+'" />'));
						}
						if(d.width){
							_itemText.width(d.width);
						}
						if(_this.type != 'drag'){
							_this.itemClick(_itemText,d);
						}
						_sliderText.append(_itemText);
					});
					if(_this.type == 'drag'){
						_this.coverClick();
						_this.dragger();
					}
					if(_this.value != ''){
						_this.response(_this.value);
					}
				},
				loadValue:function(value){
					_this.response(value);
				},
				itemClick:function(item,data){
					item.click(function(){
						_this.itemSelect($(this),data);
			            _this.onItemClick();
					});
				},
				coverClick:function(){
					_sliderThroughCover.click(function(e){
						var _ex = e.pageX;
				        var _x = _ex - _sliderRange.offset().left; 
			            _this.slideX(_x,_ex);
			            _this.onItemClick();
			            return false;
					});
				},
				coverClickDrag:function(){
					item.click(function(e){
						var _ex = e.pageX;
				        var _x = _ex - _sliderRange.offset().left; 
			            _this.slideX(_x,_ex);
			            _this.onItemClick();
			            return false;
					});
				},
				/*
				 * 拖动
				 */
				dragger:function(){
					var _move=false;//移动标记  
					var _x = _sliderDrager.offset().left;
					_sliderDrager.click(function(){   
				        }).mousedown(function(e){  
				        _move=true;  
				        _x= e.pageX - parseInt(_sliderDrager.css("left"));  
				        _sliderDrager.fadeTo(20, 0.7);//点击后开始拖动并透明显示 
				    });  
				    $(document).mousemove(function(e){  
				        if(_move){  
				        	var _ex = e.pageX;
				            var x = _ex - _x;//移动时根据鼠标位置计算控件左上角的绝对位置  
				            _this.slideX(x,_ex);
				        }  
				        return false;
				    }).mouseup(function(){  
					    _move=false;  
					    _sliderDrager.fadeTo("fast", 1);//松开鼠标后停止移动并恢复成不透明  
				    });  
						
				},
				slideX:function(x,_ex){
					var _dw = _sliderRange.width() - _sliderDrager.width();
		            if(x >= 0 && x <= _dw){
			            _sliderThrough.width(x);
			            _sliderDrager.css({left:x});//控件新位置  
			            _this.labelSelect(_ex);
			            
			            // 计算值
			            var r = x / _dw;// 当前x相对总宽的比例
			            var t = _this.upper / _this.step;// 总份数
			            var ct = r * t;// 当前的份数
			            var v = Math.round(ct * _this.step);
			            v = v < _this.lower ? _this.lower:v;
			            v = v % _this.step > 0 ? v - (v % _this.step):v;
			            _sliderDragerTip.html(v);
			            _this.value = v;
						_this.getValue(v);
		            }
				},
				slideXFromV:function(v){
					if(v < _this.lower)
						v = _this.lower;
					if(v > _this.upper)
						v = _this.upper;

					var _dw = _sliderRange.width() - _sliderDrager.width();
					// 根据值计算x坐标
		            var r = v / _this.upper;// 当前值相对最大值的比例
		            var x = r * _dw;// 当前值相对总宽度的X
		            _sliderThrough.width(x);
		            _sliderDrager.css({left:x});//控件新位置  
		            _this.labelSelect(x + _sliderRange.offset().left);
		            _sliderDragerTip.html(v);
		            _this.value = v;
					_this.getValue(v);
				},		
				response:function(value){
					if(typeof value != undefined){
						if( _this.type == 'drag'){
							_this.slideXFromV(value);
						}else {
							$.grep(_this.data,function(d,i){
								if(d.value == value){
									_this.itemSelect(_sliderText.find('.item:eq('+i+')'),d);
								}
							});
						}
					}
				},
				inputBlur:function(){
					// input事件
					_this.input.blur(function(){
						_this.response($(this).val());
					});
				},
				arrowClick:function(){
					// 箭头点击事件
					_arrowUp.click(function(){
						var val = _this.input.val();
						val = /([\d]+)$/.test(val)?val:0;
						_this.response(parseInt(val)+parseInt(_this.step));
					});
					_arrowDown.click(function(){
						var val = _this.input.val();
						val = /([\d]+)$/.test(val)?val:_this.lower;
						_this.response(parseInt(val)-parseInt(_this.step));
					});
				},
				labelSelect:function(x){
					_sliderRange.find('.item').each(function(){
						var label = $(this).find('.label');
						if(x >= label.offset().left){
							$(this).addClass('through-select');
						}else{
							$(this).removeClass('through-select');
						}
					});
				},
				itemSelect:function(item,data){
					var index = item.index();
					_sliderText.find('.item').each(function(i,it){
						var _i = $(it).index();
						if(_i <= index){
							$(it).addClass('select');
							_this.getValue(data);
				            _this.value = data.value;
						}else{
							$(it).removeClass('select');
						}
					});
				}
				
		};
		
		

		return $.extend(methods, defaults, options).create();
	};
})(jQuery);