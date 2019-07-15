(function(){	
	jslib = {
			size : {
					getRealWidth :function (_target,width){
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
			}
	}
})(js);
