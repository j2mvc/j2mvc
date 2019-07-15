
/**
 * combox选择器
 * @param $
 * @param undefined
 */
(function( $, undefined ) {

	$.fn.combox = function(options){
	
		var _this = null,config = null,box = null;
		var _target = $(this);

		var _combox = $('<div class="combox"></div>');
		var _header_outer = $('<div class="combox-header-outer"></div>');
		var _header_inner = $('<div class="combox-header-inner"></div>');
		var _header = $('<div class="combox-header"></div>');
		var _label = $('<span class="combox-label"></span>');
		var _button = $('<span class="combox-button"></span>');
		var _icon = $('<span class="combox-icon"></span>');
		
		var methods = {
			create:function(){
				_this = this;
				
				_label.html(this.label);

				_header.append(_icon);
				_header.append(_label);
				_header.append(_button);
				var icon = _this.icon || _target.attr('icon');
				if(typeof icon != undefined && $.trim(icon) !=''){
					_icon.html('<img class="'+config.iconScale +'" src="'+icon+'"/>');
					_label.css({'margin':'2px','padding':0});
					_icon.css({'margin':'2px'});
				}else{
					_icon.hide();
				}
				
				_header_inner.append(_header);
				_header_outer.append(_header_inner);

				_combox[0].className = _target[0].className+" combox";
				_combox.css({'position':'static'});
				_combox.append(_header_outer);

				// 无圆角
				var cornerLeft = this.cornerLeft || _target.attr('corner-left');
				var cornerRight = this.cornerRight || _target.attr('corner-right');
				var cornerTop = this.cornerTop || _target.attr('corner-top');
				var cornerBottom = this.cornerBottom || _target.attr('corner-bottom');
				if(cornerLeft == 'n' || cornerLeft == 'no'|| cornerLeft == 'none' || cornerLeft == false)
					_combox.addClass('corner-left-no');
				if(cornerRight == 'n' || cornerRight == 'no'|| cornerRight == 'none' || cornerRight == false)
					_combox.addClass('corner-right-no');
				if(cornerTop == 'n' || cornerTop == 'no'|| cornerTop == 'none' || cornerTop == false)
					_combox.addClass('corner-top-no');
				if(cornerBottom == 'n' || cornerBottom == 'no'|| cornerBottom == 'none' || cornerBottom == false)
					_combox.addClass('corner-bottom-no');

				// 无边框
				var borderLeft = this.borderLeft || _target.attr('border-left');
				var borderRight = this.borderRight || _target.attr('border-right');
				var borderTop = this.borderTop || _target.attr('border-top');
				var borderBottom = this.borderBottom || _target.attr('border-bottom');
				if(borderLeft == 'n' || borderLeft == 'no'|| borderLeft == 'none' || borderLeft == false)
					_combox.addClass('border-left-no');
				if(borderRight == 'n' || borderRight == 'no'|| borderRight == 'none' || borderRight == false)
					_combox.addClass('border-right-no');
				if(borderTop == 'n' || borderTop == 'no'|| borderTop == 'none' || borderTop == false)
					_combox.addClass('border-top-no');
				if(borderBottom == 'n' || borderBottom == 'no'|| borderBottom == 'none' || borderBottom == false)
					_combox.addClass('border-bottom-no');
				
				_target.after(_combox);
				_target.hide();
				
				box = Box({
					_combox:_combox,
					_header:_header
				});
				box.create();
				box.hide();	
				
				this.onload();
				
				this.initEvents();
				
				return this;
			},
			/**
			 * 重新设置数据
			 */
			recreate:function(source){
				this.source = source;
				this.initSource();
			},
			/** 初始化数据源 */
			initSource:function() {
				var source = this.source || {};
				if ($.isArray(source) ) {
					// 数组
					this.data = source;
				} else if ( typeof source === "string" ) {
					var url = source;
					this.data = function( request, response ) {
						$.ajax({
							url: url,
							source: request,
		                    type: "get",
							sourceType: "json",
							success: function( source ) {
								response( source );
							},
							error: function() {
								response( [] );
							}
						});
					};
				} else  if ( typeof source === "function" ) {
					source({},function(item){
						_this.data = item;
					});
				}
				if($.isArray(_this.data))
					$.grep(_this.data,function(d){
						var label = d.label || d.name,
							value = d.value;// || d.label || d.name;
						if(_this.value == value){
							_this.setValue({
								value:value,
								label:label
							});
							return;
						}
					});
			},
			onload:function(){
				this.initSource();
			},
			initEvents:function(){
				_header_outer.hover(function(){
					_this.hover();
				},function(){
					_this.blur();
				});
				
				_button.click(function(){
					_this.showBox();
				});
			},
			showBox:function(){
				box.clear(); // 清空HTML		
				var data = this.data;
				var len = data.length;
				for(var i=0; i<len; i++){
					var d = data[i];
					if(typeof d != undefined)
						if ( typeof d === "string" ) {
							box.createRow(len,i,d,d);
						}else{
							var label = d.label || d.name,
								value = d.value ;//|| d.label || d.name,
								icon = d.icon,
								description = d.description;
							box.createRow(len,i,label,value,description,icon);
						}
				}	
				box.initEvents();
				box.show();
			},
			hover:function(){
				_header_inner.addClass('hover');
				_header_outer.addClass('hover');
				_header.addClass('hover');
				_button.addClass('hover');
			},
			blur:function(){
				_header_inner.removeClass('hover');
				_header_outer.removeClass('hover');
				_header.removeClass('hover');
				_button.removeClass('hover');
			},
			/**
			 *  设置值 
			 */
			setValue:function(item){
				if(_target.val() != item.value && typeof config.onChange === 'function'){
					config.onChange(item);
				}else if(typeof config.onSelect === 'function'){
					config.onSelect(item);
				}
				_target.val(item.value);
				if(this.labelShowChange){
					if(!("undefined" == typeof item.icon) && item.icon != ''){
						_icon.html('<img class="'+config.iconScale +'" src="'+item.icon+'"/>');
					}
					_label.html(item.label);
				}
			},
			onchange:function(){
				
			}
		};

		/**
		 * 弹出框
		 */
		var Box = function(options){
			var _this = null;
			var _box = $('<div class="combox-box"></div>');// 弹出窗体
			return $.extend({
				create:function(){
					_this = this;
					this._header.after(_box);
				},
				createRow : function(len,i,label,value,description,icon){// 创建行
					var row = $('<div class="combox-box-row"></div>');
					var rowLabel = null,rowDescription = null,rowIcon = null;
					row.attr('value',value);
					if(!("undefined" == typeof label ) && label != ''){
						rowLabel = $('<span class="row-label"></span>');
						rowLabel.html(label);
					}
					if(!("undefined" == typeof description) && description != ''){
						rowDescription = $('<span class="row-description"></span>');
						rowDescription.html(description);
					}
					if(!("undefined" == typeof icon) && icon != ''){
						rowIcon = $('<img class="row-icon '+config.iconScale +'"/>');
						rowIcon.attr('src',icon);
					}
					if(rowIcon != null){
						var rowLeft = $('<span class="row-left"></span>');
						var rowRight = $('<span class="row-right"></span>');
						rowLabel.css({'margin':'0px','padding':0});
						rowIcon.css({'margin':'2px','padding':0});	
						rowLeft.css({'margin':'2px','padding':0});				
						rowLeft.append(rowIcon);
						if(rowLabel!=null)
							rowRight.append(rowLabel);
						if(rowDescription!=null)
							rowRight.append(rowDescription);	
						row.append(rowLeft);
						row.append(rowRight);
					}else{
						if(rowLabel!=null)
							row.append(rowLabel);
						if(rowDescription!=null)
							row.append(rowDescription);	
					}
					if(i == len-1)
						row.addClass('last');
					_box.append(row);
				},
				isShow : function(){
					return _box.css('display') != 'none';
				},
				/**
				 * 选定条目
				 */
				select : function(row){
					var value = row?row.attr('value'):'';
					var label = row?row.find('.row-label').text():'';
					var description = row?row.find('.row-description').text():'';
					var icon = row?row.find('.row-icon').attr('src'):"";
					var item = null;
					item = {
							value:value,
							label:label,
							description:description,
							icon:icon
					};
					if(item!=null)// && item.label.length>0 && item.value.length>0
						config.setValue(item);
					box.hide();
					this.reset();
				},
				/**
				 * 鼠标经过
				 */ 
				hover:function(row){
					_box.find('.combox-box-row').removeClass('focus');
					row.addClass('focus');
				},
				/**
				 * 鼠标移出
				 */ 
				blur:function(row){
				},
				click : function(row){
					row.addClass('focus');
					this.select(row);
				},
				reset : function(){
					_box.find('*').removeClass('focus');
				},
				initEvents:function(){
					_box.find('.combox-box-row').hover(function(){
						_this.hover($(this));
					});
					_box.find('.combox-box-row').click(function(){
						_this.click($(this));
					});
					_box.hover(function(){
						
					},function(){
						_this.hide();
					});
				},
				show : function(){		
					if(_box.text().length > 0 ){
						this.resize();
						if(!_box.is('visible'))
							_box.slideDown();
					}else{
						_box.hide();
					};
				},
				hide : function(){
					_box.slideUp();
				},
				clear : function(){
					_box.html('');
				},
				resize : function(){
					var size = {
						top : this._combox.offset().top+1,
						left : this._combox.offset().left,
						height : this._header.outerHeight(),
						width : this._header.outerWidth() - 2
					};
					_box.css({
						'position':'absolute',
						'left':size.left+'px',
						'z-index':999
					});
					var width = config.width;
					if(width && (typeof width ==='string') && width.match(/^(\d+)$/)){
						_box.width(parseInt(width));
					}else if(typeof width =='number'){
						_box.width(width);
					}else{
						_box.width(size.width);
					};	
					this.setHeight();
					var docH = $(window)[0].scrollHeight;
					var winH = _box.height();
					var wTop = size.top+size.height;
					if((winH + wTop) > docH)
						wTop = size.top - winH;	
					_box.css({'top':wTop+'px'});
				},
				setHeight : function(){
					if(_box.height() >= config.maxHeight)
						_box.height(config.maxHeight);
					else
						_box.height('auto');
				},
				getNode:function(){
					return _box;
				}
			},{_combox:_combox,_header:_header},options);
		};
		
		var defaults = {
				labelShowChange:false,
				cornerLeft:'',// 有左圆角，n:no:none:false无
				cornerRight:'', // 右圆角
				cornerTop:'',// 上圆角
				cornerBottom:'',// 下圆角
				borderLeft:'',// 有左边框，n:no:none:false无
				borderRight:'', // 右边框
				borderTop:'',// 上边框
				borderBottom:'',// 下边框
				data:[],
				source:[{
					label:'',// 文本
					value:'',// 值
					icon:'',// 图标
					description:''// 简短描述
					
				}],// 数据
				/**
				 * demo
				 * source:function( request, response){
					var url = "${path}/admin/rule/level/getList";
					$.ajax({
						url: url,
						dataType: "json",
				        async:false,   
				       	beforeSend:function(){
				        },
						success: function( data ) {
							response($.map( data.result, function( item ) {
								return {
									value: item.level+'',
									label: item.name+(item.alias && item.alias != ''?'('+item.alias+')':'')
								};
							}));
						}
					});
				}
				 */
				label:'',
				value:'',
				width:'auto',// 宽
				maxHeight:200// 高
		};
		config = $.extend(methods,defaults,options);
		return config.create();
	};
})(jQuery);