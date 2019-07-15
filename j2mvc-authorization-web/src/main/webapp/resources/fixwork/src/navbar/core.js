/**
 * navbar导航
 * @param $
 * @param undefined
 */
(function( $, undefined ) {

	$.fn.nav = function(){
		var _target = $(this);
		var _navOuter = $('<span class="nav-outer"></span>');
		var _navInner = $('<span class="nav-inner"></span>');
		var _navLabel = $('<span class="nav-label"></span>');
		return $.extend({
			create:function(){
				_this = this;
				_navLabel.html(_target.text());
				_navInner.append(_navLabel);		
				_navOuter.append(_navInner);

				_target.html('');
				_target.append(_navOuter);
				if(!_target.hasClass('nav'))
					_target.addClass('nav');
				_target.hover(function(){
					$(this).addClass('hover');
				},function(){
					$(this).removeClass('hover');
				})	;
				return _this;
			}
		});
	};
	
	$.fn.navbar = function(options){
	
		var _this = null;
		var _target = $(this);
		

		var _outer = $('<div class="navbar-outer"></div>');
		var _inner = $('<div class="navbar-inner"></div>');
		
		var methods = {
			create:function(){
				_this = this;
				_target.find('a').each(function(){
					$(this).nav().create();
				});
				return _this;
			}
		};
		
		
		var defaults = {			
		};
		return $.extend(methods,defaults,options).create();
	};
})(jQuery);