
(function( $, undefined ) {
	
	$.fn.fixed = function(options){
		var DOM = $(this);
		var settings = $.extend({
			init:function(){
				var _this = this;
				_this.target.scroll(function(){
					var fixedHeight = DOM.outerHeight() + _this.fixedHeight;
					if($(this).scrollTop() >= fixedHeight ){
						var w = _this.target.innerWidth();
						DOM.css({
							"position":"fixed",
							"top":"0px",
							"width":w+"px"
						});
					}else{
						DOM.css({
							"position":"",
							"width":"auto"
						});
					}
				});	
			}
		},{
			target:null,
			fixedHeight:0
		}, options);
		
		this.each(function(){
			settings.init();
		});
	};
	
})(jQuery);