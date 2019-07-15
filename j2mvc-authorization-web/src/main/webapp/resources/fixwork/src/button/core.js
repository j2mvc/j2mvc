
/**
 * button按钮
 * @param $
 * @param undefined
 */
(function( $, undefined ) {
	
	$.fn.button = function(options){
	
		var _this = null;
		var _target = $(this);
		
		var hover = null,blur = null,click = null;

		var _outer = $('<span class="button-outer"></span>');
		var _inner = $('<span class="button-inner"></span>');
		var _label = $('<span class="button-label"></span>');
		var _icon = $('<i class="icon"></i>');
		
		var methods = {
			create:function(){
				_this = this; 

				var iconAlign = _this.iconAlign || _target.attr('icon-align');
				if(iconAlign == 'right'){
					_label.html('<label class="label">'+_target.text()+'</label>');
					_label.append(_icon);
				}else{
					_label.append(_icon);
					_label.append($('<label class="label">'+_target.text()+'</label>'));
				}
				var icon = _this.icon || _target.attr('icon');
				if(typeof icon != undefined && $.trim(icon) !=''){
					_label.css({'padding-left':0});
					_icon.addClass(icon);
				}else{
					_icon.hide();
				}
				_inner.append(_label);		
				_outer.append(_inner);

				if(_target[0].nodeName == 'BUTTON' && window.ActiveXObject){
					_target.width(_target.width() + 50);
				}
				_target.html('');
				_target.append(_outer);
				if(!_target.hasClass('button'))
					_target.addClass('button');

				// 无圆角
				var cornerLeft = _this.cornerLeft || _target.attr('corner-left');
				var cornerRight = _this.cornerRight || _target.attr('corner-right');
				var cornerTop = _this.cornerTop || _target.attr('corner-top');
				var cornerBottom = _this.cornerBottom || _target.attr('corner-bottom');
				if(cornerLeft == 'n' || cornerLeft == 'no'|| cornerLeft == 'none' || cornerLeft == false)
					_target.addClass('corner-left-no');
				if(cornerRight == 'n' || cornerRight == 'no'|| cornerRight == 'none' || cornerRight == false)
					_target.addClass('corner-right-no');
				if(cornerTop == 'n' || cornerTop == 'no'|| cornerTop == 'none' || cornerTop == false)
					_target.addClass('corner-top-no');
				if(cornerBottom == 'n' || cornerBottom == 'no'|| cornerBottom == 'none' || cornerBottom == false)
					_target.addClass('corner-bottom-no');
				
				// 无边框
				var borderLeft = _this.borderLeft || _target.attr('border-left');
				var borderRight = _this.borderRight || _target.attr('border-right');
				var borderTop = _this.borderTop || _target.attr('border-top');
				var borderBottom = _this.borderBottom || _target.attr('border-bottom');
				if(borderLeft == 'n' || borderLeft == 'no'|| borderLeft == 'none' || borderLeft == false)
					_target.addClass('border-left-no');
				if(borderRight == 'n' || borderRight == 'no'|| borderRight == 'none' || borderRight == false)
					_target.addClass('border-right-no');
				if(borderTop == 'n' || borderTop == 'no'|| borderTop == 'none' || borderTop == false)
					_target.addClass('border-top-no');
				if(borderBottom == 'n' || borderBottom == 'no'|| borderBottom == 'none' || borderBottom == false)
					_target.addClass('border-bottom-no');
				
				if(!_target.hasClass('disabled'))
					_this.initEvents();
				return _this;
			},
			setDisable:function(disable){
				if(disable){
					_target.addClass('disabled');
					_this.clearEvents();
				}else{
					_target.removeClass('disabled');
					_this.initEvents();
				}
			},
			setLabel:function(label){
				if(typeof label != undefined && $.trim(label) !=''){
					_label.find('.label').html(label);
				}
			},
			setText:function(text){
				if(typeof text != undefined && $.trim(text) !=''){
					_label.find('.label').html(text);
				}
			},
			setIcon:function(icon){
				if(typeof icon != undefined && $.trim(icon) !=''){
					_label.css({'padding-left':0});
					_icon.addClass(icon);
				}else{
					_icon.hide();
				}
			},
			clearEvents:function(){
				_target.unbind('hover',hover);
				_target.unbind('click',click);
			},
			initEvents:function(){
				hover = function(){
					_this.hover();
				};
				blur = function(){
					_this.blur();
				};
				click = function(){
					_this.click();
				};
				_target.hover(hover,blur);
				_target.click(click);
			},
			hover:function(){
				_inner.addClass('hover');
				_outer.addClass('hover');
				_label.addClass('hover');
				_icon.addClass('hover');
			},
			blur:function(){
				_inner.removeClass('hover');
				_outer.removeClass('hover');
				_label.removeClass('hover');
				_icon.removeClass('hover');
			}
		};
		
		var defaults = {
				cornerLeft:'',// 有左圆角，n:no:none:false无
				cornerRight:'', // 右圆角
				cornerTop:'',// 上圆角
				cornerBottom:'',// 下圆角
				borderLeft:'',// 有左边框，n:no:none:false无
				borderRight:'', // 右边框
				borderTop:'',// 上边框
				borderBottom:'',// 下边框
				iconAlign:'left',
				click:function(){}				
		};
		return $.extend(methods,defaults,options).create();
	};
})(jQuery);