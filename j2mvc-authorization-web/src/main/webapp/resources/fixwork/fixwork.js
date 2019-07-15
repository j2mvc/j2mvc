
(function( $, undefined ) {
	
	/** 键盘*/
	KEY = { 
			ESC : 27,
		    TAB : 9,
		    DELETE_NUMBER : 110,
			ENTER : 13,
		    UP : 38,
			DOWN : 40,
			RIGHT : 39,
			LEFT : 37,
			END : 35,
			HOME : 36,
			PAGE_UP : 33,
			PAGE_DOWN : 34,
			SPACE : 32,
			DELETE : 46,
			INSERT : 45,
			BACKSPACE : 8};

	/** 过滤键*/
	KEY_FILTERS = [KEY.UP,KEY.TAB,KEY.ESC,KEY.DOWN,KEY.RIGHT,KEY.LEFT,KEY.END,KEY.HOME,KEY.PAGE_UP,KEY.PAGE_DOWN,KEY.ENTER];

	/** 样式*/
	STYLES = {			  
			WIN : "autocomplete-win",
			ROW : "autocomplete-win-row",
			ROW_FOCUS : "focus",
			ROW_LABEL : "row-label",
			ROW_DESCRIPTION: "row-discription",
			ROW_ICON : "row-icon",
			ROW_LEFT : "row-left",
			ROW_RIGHT : "row-right",
			// 以下为HTML显示值时样式
			EDIT_TABLE: "autocomplete-edittable",
			EDIT_TABLE_TEXT: "text-field",
			EDIT_TABLE_TEXT_ERROR: "error-format",
			EDIT_TABLE_TEXT_EDIT: "text-field-on-edit",
			EDIT_TABLE_EDIT: "edit-field"};

	/** 其它常量 */
	CONSTANTS = {
			VALUE_TYPE_HTML : "html"	
	};
	
	/** 全局变量 */
	var lastQ = '-1',					// 最后搜索关键字
		settings = {}, 					// 配置
		events = null, 					// 事件
		target = null, 					// 引用对象
		nodeName = null, 				// 引用对象节点名称
		table = null,					// HTML编辑域
		win = null; 					// 弹出框对象
	var selectValues = new Array();		// 选择的值
	
	var size = {};

	function test(tag,s){
		$('#test123').text("tag:"+s);
	}
	/** 正则 */
	function escapeRegex ( value ) {
		return value.replace(/[\-\[\]{}()*+?.,\\\^$|#\s]/g, "\\$&");
	}
	/** 筛选 */
	function filter(array, term) { 
		var matcher = new RegExp( escapeRegex(term), "i" );
		return $.grep( array, function(value) {
			return matcher.test( value.label || value.value || value );
		});
	};
	/** 获取数字 */
	function getNumeric(v){
		var re = /[^a-zA-Z\d\u4e00-\u9fa5,.!?()，。．；;？]/g;
		v = v.replace(re,"");
		return v.match(/\d+/)?parseInt(v):0;
	}	
	/** 获取光标所在位置 */
	function getCurposition(){ 
		var el = target[0];
        var pos = 0;
        if(document.selection){                
        		el.focus();       						 
        		pos = document.selection.createRange();	
        }//火狐下标准    
        else if(el.selectionStart || el.selectionStart == '0'){
        		pos = el.selectionStart;  //获取焦点前坐标
        }
        return pos;
	}
	/** 获取起始字符 */
	function getStartChars(chars){ // 起始字符
		chars = (chars == undefined || chars == 'undefined')?'':chars;
		var o = chars.lastIndexOf(settings.separate);
		if(o != -1 && chars.length > o){
			chars = chars.substring(o+1,chars.length);
		}
		return chars;
	}
	/** 获取终止字符 */
	function getEndChars(chars){ // 终止字符
		chars = (chars == undefined || chars == 'undefined')?'':chars;
		var o = chars.indexOf(settings.separate);
		if(o != -1){
			chars = chars.substring(0,o);
		}
		return chars;
	}
	/** 初始化数据源 */
	function initSource() {
		// 初始源
		var array, url;
		if ( $.isArray(settings.source) ) {
			// 数组
			array = settings.source;
			settings.data = function( request, response ) {
				response( filter( array, request.term ) );
			};
		} else if ( typeof settings.source === "string" ) {
			url = settings.source;
			settings.data = function( request, response ) {
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
		} else {
			settings.data = settings.source;
		}
	};
	/** 初始化选中回调 */
	function initSelect() {
		settings._select = settings.select;
	};
	/** 还原格式化文本*/
	function parseFormat(v){// 
		v = v.replace(eval('/(['+settings.format.left+']+)\s*/g'),''); 
		v = v.replace(eval('/(['+settings.format.right+']+)\s*/g'),''); 
		return v;
	}		
	/** 替换重复分隔符*/
	function deleRepeat(v){//
		return v.replace(eval('/(['+settings.separate+']+)\s*/g'),settings.separate);
	}
	/** 过滤选择项*/
	function filterSelect(source){	
		var bool = false;
		$.grep(selectValues,function(item){
			if (item.value == source.value && item.description == source.description){
				bool = true;
				return;	
			}
		});
		return bool;
	}
	/** 过滤关键词*/
	function qFilter(d){
		var v = (nodeName === 'input')?target.val():target.html();
		v = parseFormat(v);
		v = v.toLowerCase();
		var filters = v.split(settings.separate);
		if(typeof d === 'string')
		if($.inArray(d.toLowerCase(), filters) != -1){
			return true;
		}else{
			return false;
		}
	}
	/** 获取关键词*/
	function getQ(){
		var q = ( nodeName === 'input')?target.val():target.html();
		if(settings.valueType && settings.valueType === 'html')
			q = target.text();
		else if(settings.multiple){// 多值
			var pos = getCurposition();				
			var startChars = getStartChars(q.substring(0,pos));
			var endChars = getEndChars(q.substring(pos,q.length));
			q = startChars + endChars;
			q = parseFormat(q);
			//ttt +='pos='+pos+' startChars='+startChars+' endChars='+endChars;
		}
		//test('getQ',ttt);
		return q;
	}
	/** 查询 */
	function query(){
		var q = $.trim(getQ());//
		if(q == settings.separate ){
			q = '';
			if(nodeName === 'div')
				target.html('');
			else if(nodeNode === 'input')
				target.val('');
			return;
		}else if(q.length > settings.separate.length
				&& q.substring(q.length-settings.separate.length)== settings.separate){
			win.select();
			return;
		}	
		if(lastQ == q && win.isShow())
			return;
		lastQ = $.trim(q);//
		win.clear(); // 清空HTML		
		settings.data( { term: q }, settings._response );
	}
	function clearSeparate(v){
		return v.replace(eval('/(['+settings.separate+']+)\s*/g'),'');
	}
	/** 插入值 */
	function insertValue(value){	
		var v = nodeName === 'input'?target.val():target.html();
		if(settings.multiple){
			var sep = settings.separate;
			var pos = getCurposition();	
			// 插入到分隔符中
			var start = v.substring(0,pos).lastIndexOf(sep);
			var end = v.substring(pos,v.length).indexOf(sep) + pos;
			start = start !=-1?start:0;
			var sv = start > 0 ? v.substring(0,start) +sep + settings.format.left:settings.format.left;
			var ev = end > start ? settings.format.right + sep + v.substring(end,v.length):"";
			v = sv + clearSeparate(value) + (ev == ''?settings.format.right + sep:ev);
			v = deleRepeat(v);
			if(nodeName === 'input')
				target.val(v);
		}else{
			if(nodeName === 'input')
				target.val(settings.format.left + clearSeparate(value) + settings.format.right);
		}
	}
	/** 更新值 */
	function updateValues(id,value){		
		var index = getIndex(id);
		if(index>-1){
			value = {
				id:id,
				value:value.value,
				description:value.description,
				icon:value.icon
			};
			selectValues.splice(index,1,value);
			settings.getValues(selectValues);
		}
	}
	/** 设置值 */
	function setValues(selectItem){
		if(settings.valueType && settings.valueType === 'html'){
			/** 插入HTML值 */
			if(table){
				var id = createId();
				var call = settings._select(selectItem);
				var display = call.display;
				var selectValue = {
						id:id,
						value:call.value.value,
						description:call.value.description,
						icon:call.value.icon
				};
				selectValues.push(selectValue);	
				table.appendText(display,id);
				settings.getValues(selectValues);
			}
		}else if(settings.multiple){
			insertValue(selectItem.value);
		}else{
			if(nodeName === 'input')
				target.val(selectItem.value);
		}	
		lastQ = '-1';
	}

	/**
	 * 创建ID
	 */
	function createId(){
		var timestamp = new Date().getTime();
		return timestamp + "_"+ Math.random() ;
	}
	
	//jQuery.unique(array)删除重复元素
	/**
	 * 删除值
	 */
	function deleteValue(id){
		selectValues.splice(getIndex(id),1);
		settings.getValues(selectValues);
	}
	/**
	 * 根据ID查看元素在数组中的位置
	 */
	function getIndex(id){
		var index = -1;
		$.grep(selectValues,function(item,i){
			if (item.id == id){
				index = i;
				return;	
			}
		});
		return index;
	}
	
	$.fn.autocomplete = function(options){
		settings = $.extend($.fn.autocomplete.methods, $.fn.autocomplete.defaults, options);
		
		target = $(this);
		size = {
				width  : $(this).innerWidth(),
				height  : $(this).innerHeight(),
				top  : $(this).offset().top,
				left  : $(this).offset().left
				//pt : getNumeric($(this).css('padding-top')),
				//pb : getNumeric($(this).css('padding-bottom')),
				//pl : getNumeric($(this).css('padding-left')),
				//pr : getNumeric($(this).css('padding-right'))
			};

		
		if(settings.valueType && settings.valueType === 'html'){
			table = $.fn.autocomplete.edittable().create();
			target = table.getEditer();
		}
		
		nodeName = target[0].nodeName.toLowerCase();

		win = $.fn.autocomplete.win();
		
		events = $.fn.autocomplete.events();
		
		return this.each(function(){
			settings.create();
		});
	};
	/**
	 * 弹出框
	 */
	$.fn.autocomplete.win = function(){
		var _win = $('<div class="'+ STYLES.WIN +'"></div>');// 弹出窗体
		return $.extend({
			create:function(){
				if(settings.valueType && settings.valueType === 'html'){
					target.parent().after(_win);
				}else{
					target.after(_win);
				}
			},
			createRow : function(len,i,label,description,icon){// 创建行
				var row = $('<div class="'+STYLES.ROW+'"></div>');
				var rowLabel = null,rowDescription = null,rowIcon = null;
				if(!("undefined" == typeof label ) && label != ''){
					rowLabel = $('<div class="'+STYLES.ROW_LABEL +(settings.rowDisplay == 'inline'?' row-inline':'') + '"></div>');
					rowLabel.html(settings.valuePrefix+label+settings.valueSubfix);
				}
				if(!("undefined" == typeof description) && description != ''){
					rowDescription = $('<div class="'+STYLES.ROW_DESCRIPTION+(settings.rowDisplay == 'inline'?' row-inline':'') + '"></div>');
					rowDescription.html(description);
				}
				if(!("undefined" == typeof icon) && icon != ''){
					rowIcon = $('<img class="'+STYLES.ROW_ICON+' '+settings.iconScale +'"/>');
					rowIcon.attr('src',icon);
				}
				if(rowIcon != null){
					var rowLeft = $('<div class="'+STYLES.ROW_LEFT+'"></div>');
					var rowRight = $('<div class="'+STYLES.ROW_RIGHT+'"></div>');				
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
				if(settings.rowDisplay == 'inline'){
					row.append($('<div style="clear:both"></div>'));
				}
				
				if(i == len-1)
					row.addClass('last');
				_win.append(row);
			},
			isShow : function(){
				return _win.css('display') != 'none';
			},
			show : function(){			
				if(_win.text().length > 0 ){
					this.resize();
					if(!_win.is('visible'))
						_win.slideDown();
				}else{
					_win.hide();
				};
			},
			hide : function(){
				_win.slideUp();
			},
			clear : function(){
				_win.html('');
			},
			resize : function(){
				if(table && settings.valueType && settings.valueType === 'html'){
					var _table = table.getTable();
					size.top = _table.offset().top-1;
					size.left = _table.offset().left;					
				}else{
					size.top = target.offset().top;
					size.left = target.offset().left;
				}
				_win.css({
					'position':'absolute',
					'height':'',
					'left':size.left+'px',
					'z-index':9999
				});
				var width = settings.width;
				if(width && (typeof width ==='string') && width.match(/^(\d+)$/)){
					_win.width(parseInt(width));
				}else if(typeof width ==='number'){
					_win.width(width);
				}else{
					_win.width(size.width);
				};	
				this.setHeight();
				var docH = $(window).height();
				var winH = _win.height();
				var wTop = size.top+size.height+1;
				if(table && table.getTable())
					wTop = size.top+table.getTable().height()+1;
				if((winH + wTop) > docH)
					wTop = size.top - winH;	
				_win.css({'top':wTop+'px'});
			},
			setHeight : function(){
				if(_win.height() >= settings.maxHeight)
					_win.height(settings.maxHeight);
				else
					_win.height('auto');
			},
			getWindow:function(){
				return _win;
			}
			
		});
	};
	/**
	 * 子插件，编辑域，值显示方式为HTML时调用
	 */
	$.fn.autocomplete.edittable = function(){
		var _table = $('<div class="'+STYLES.EDIT_TABLE+'"></div>');
		var _editer = $('<div class="'+STYLES.EDIT_TABLE_EDIT+'"></div>');	
		return $.extend({
			create :function(){
				_editer.attr('contenteditable','true');		
				_table.append(_editer);
				target.after(_table);
				target.hide();
				if(settings.focus)
					_editer.focus();
				this.resizeTable();
				this.resizeEditer();
				this.initEvents();
				return this;
			},
			appendText:function(html,id){
				var _texter = $('<div class="'+STYLES.EDIT_TABLE_TEXT+'"></div>');	
				_texter.html(html+settings.separate);
				_texter.attr('id',id);
				if(settings.valueRegexp && !_texter.text().match(settings.valueRegexp)){
					_texter.addClass(STYLES.EDIT_TABLE_TEXT_ERROR);
				}
				events.tableTextFieldEvents(_texter);
				this.setHeight(_texter);
				_editer.html('');
				_editer.before(_texter);	
				this.resizeTable();
				this.resizeEditer();
				_editer.focus();
				events.tableEditerEvents();
			},
			resizeTable : function(){
				_table.width(size.width);
				if(_table.height() < (size.height))
					this.setHeight(_table);
				else 
					_table.css({'height':'auto'});
			},
			resizeEditer : function(){
				this.setHeight(_editer);
			},
			setHeight:function(tg){
				var h = size.height;
				tg.css({
					'height':h+'px',
					'line-height':h+'px'
				});
			},
			initEvents : function(){
				var _this = this;
				_table.click(function(){
					_this._onClick();
				});
			},
			_onClick : function(){
				if(_table.find('.'+STYLES.EDIT_TABLE_TEXT_EDIT).size() > 0){
				}
				else{
					_editer.focus();
				}
			},
			selectText:function(target){
				_table.find('.'+STYLES.EDIT_TABLE_TEXT).removeClass('selected');
				target.addClass('selected');
				target.focus();
			},
			resetTextEdit:function(_texter){
				if(!_texter){
					this.resetAllTextEdit();
					return;
				}else if($.trim(_texter.text()).length == 0){
					this.removeTexter(_texter);					
				}else if(_texter.hasClass(STYLES.EDIT_TABLE_TEXT_EDIT)){
					var text = _texter.text();
					text = clearSeparate(text);
					
					var value = settings.formatTexter(text);
					updateValues(_texter.attr('id'),value);
					
					_texter.addClass(STYLES.EDIT_TABLE_TEXT);
					_texter.removeClass(STYLES.EDIT_TABLE_TEXT_EDIT);
					_texter.removeClass('selected');
					_texter.attr('contenteditable','false');
								
					var html = deleRepeat(_texter.html());
					if(html.substring(html.length - settings.separate.length,html.length) != settings.separate ){
						html += settings.separate;
					}
					_texter.html(html);
					if(settings.valueRegexp && !text.match(settings.valueRegexp)){
						_texter.addClass(STYLES.EDIT_TABLE_TEXT_ERROR);
					}else{
						_texter.removeClass(STYLES.EDIT_TABLE_TEXT_ERROR);
					}
				}
			},
			resetAllTextEdit:function(){
				_table.find('.'+STYLES.EDIT_TABLE_TEXT_EDIT+',.'+STYLES.EDIT_TABLE_TEXT).each(function(){
					if($.trim($(this).text()).length == 0){
						this.removeTexter(_texter);	
					}else{
						if(!$(this).hasClass(STYLES.EDIT_TABLE_TEXT))
							$(this).addClass(STYLES.EDIT_TABLE_TEXT);
						if($(this).hasClass(STYLES.EDIT_TABLE_TEXT_EDIT))
							$(this).removeClass(STYLES.EDIT_TABLE_TEXT_EDIT);
						$(this).removeClass('selected');
						$(this).attr('contenteditable','false');

						if(settings.valueRegexp && !$(this).text().match(settings.valueRegexp)){
							if(!$(this).hasClass(STYLES.EDIT_TABLE_TEXT_ERROR))
								$(this).addClass(STYLES.EDIT_TABLE_TEXT_ERROR);
						}else{
							if($(this).hasClass(STYLES.EDIT_TABLE_TEXT_ERROR))
								$(this).removeClass(STYLES.EDIT_TABLE_TEXT_ERROR);
						}
					}
				});
			},
			resetOtherTextEdit:function(_texter){
				_table.find('.'+STYLES.EDIT_TABLE_TEXT_EDIT).each(function(){
					if(_texter && $(this) != _texter ){
						$(this).addClass(STYLES.EDIT_TABLE_TEXT);
						$(this).removeClass(STYLES.EDIT_TABLE_TEXT_EDIT);
						$(this).removeClass('selected');
						$(this).attr('contenteditable','false');
					}
				});
			},
			onTextEdit:function(_texter){// 编辑
				this.resetOtherTextEdit(_texter);
				_texter.removeClass(STYLES.EDIT_TABLE_TEXT);
				_texter.addClass(STYLES.EDIT_TABLE_TEXT_EDIT);
				_texter.attr('contenteditable','true');
				_texter.find('div').attr('contenteditable','true');
				_texter.focus();
			},
			removeSelectTexter:function(){
				var _this = this;
				_table.find('.'+STYLES.EDIT_TABLE_TEXT).each(function(){
					if($(this).hasClass('selected') && !$(this).hasClass(STYLES.EDIT_TABLE_TEXT_EDIT)){
						_this.removeTexter($(this));
					}
				});
			},
			removeTexter:function(_texter){
				deleteValue(_texter.attr('id'));
				_texter.remove();
			},
			getTable:function(){
				return _table;
			},
			getEditer:function(){
				return _editer;
			}
			
		});
	};

	/**
	 * 事件
	 */
	$.fn.autocomplete.events =  function(){
		var _win = win.getWindow();
		return $.extend({
			/**
			 * 创建对象,
			 * 值域可以是input,也可以是textarea.
			 * 创建弹出框对象
			 */
			 fieldEvents : function(){// 输入框事件
				var _this = this;
				target.keydown(function(e){
					_this._onKeydown(e);
				});
				target.keyup(function(e){
					_this._onKeyup(e);
					//setTimeout(_this._onKeyup(e),settings.delay);
				});
				target.blur(function(){
					_this._onBlur();
				});
				target.click(function(e){
				});
			},
			winEvents : function(){ // 弹出框事件
				var _this = this;
				_win.find('.'+STYLES.ROW).hover(function(){
					_this._hover($(this));
				});
				_win.find('.'+STYLES.ROW).click(function(){
					_this._click($(this));
				});
				_win.hover(function(){
					
				},function(){
					_this._exitWin();
				});
			},
			tableEditerEvents:function(_texter){// 文本事件
				var _editer = table.getEditer();
				_editer.focus(function(){
					table.resetAllTextEdit();	
				});
			},
			tableTextFieldEvents:function(_texter){// 文本事件
				_texter.hover(function(){
					$(this).addClass('hover');
				},function(){
					$(this).removeClass('hover');
				}).click(function(){
					table.selectText($(this));
					return false;
				}).dblclick(function(){
					table.onTextEdit(_texter);
				}).blur(function(){
					table.resetTextEdit(_texter);
				});
				_texter.keydown(function(e){
					switch (e.keyCode) {
					case KEY.ENTER:
						table.resetTextEdit(_texter);
						table.getEditer().focus();
						return false; 
						break;
						default:
							break;
					}
				});
				
			},
			_onKeydown : function(e){
				// 键盘事件
				switch(e.keyCode){
					case KEY.DOWN:
						this._down();
						e.preventDefault();
						break;
					case KEY.UP:
						this._up();
						e.preventDefault();
						break;
					case KEY.ENTER:
						this._enter();
						e.preventDefault();
						break;
					case KEY.BACKSPACE:// 删除		
						if(table){
							var _texter = target.prev();						
							if(_texter.hasClass('selected')){
								table.removeTexter(_texter);
							}else if($.trim(target.text()) == '' && _texter.size()>0){
								table.selectText(_texter);
							}
						}
						break;
					case KEY.ESC:
						if(table)
							table.resetAllTextEdit();	
						break;
					case KEY.TAB:
						if(table)
							table.resetAllTextEdit();	
						break;
					default:
						break;
				}			
			},
			_onKeyup : function(e){
				// 查询
				if($.inArray(e.keyCode, KEY_FILTERS) == -1){	
					if(e.keyCode === KEY.BACKSPACE){
						if(target.text().length>0)
							query();	
					}else
						query();
				}else if(e.keyCode == KEY.DOWN && !win.isShow()){
					query();
				}
				e.preventDefault();
			},
			_onBlur : function(){
				win.hide();
			},
			_down : function(){// 光标键向下			
				if(_win.find('.'+STYLES.ROW).size() == 0)
					return false;
				var firstRow = _win.find('.'+STYLES.ROW+":first");
				var focusRow = _win.find('.'+STYLES.ROW+'.'+STYLES.ROW_FOCUS);
				var next = focusRow.next();
				if(focusRow.size() == 0){
					_win.scrollTop(0);
					firstRow.addClass(STYLES.ROW_FOCUS);
				}
				else if(next.size() == 0){
					_win.scrollTop(0);
					_win.find('.'+STYLES.ROW).removeClass(STYLES.ROW_FOCUS);
					firstRow.addClass(STYLES.ROW_FOCUS);
				}else{
					this._scrollTo(focusRow.next());
					_win.find('.'+STYLES.ROW).removeClass(STYLES.ROW_FOCUS);
					next.addClass(STYLES.ROW_FOCUS);
				}
			},
			_up : function(){// 光标键向上
				if(_win.find('.'+STYLES.ROW).size() == 0)
					return false;
				var lastRow = _win.find('.'+STYLES.ROW+":last");
				var focusRow = _win.find('.'+STYLES.ROW+'.'+STYLES.ROW_FOCUS);
				var prev = focusRow.prev();
				if(prev.size() == 0){
					this._scrollTo(lastRow);
					_win.find('.'+STYLES.ROW).removeClass(STYLES.ROW_FOCUS);
					lastRow.addClass(STYLES.ROW_FOCUS);
				}else{
					this._scrollTo(prev);
					_win.find('.'+STYLES.ROW).removeClass(STYLES.ROW_FOCUS);
					prev.addClass(STYLES.ROW_FOCUS);
				}
			},
			_scrollTo : function(row){
				var rowTop = row.offset().top;
				var rowHeight = row.height();
				var winHeight = _win.height();
				if (rowTop + rowHeight > winHeight){
					var h = _win.scrollTop() + rowTop + rowHeight - winHeight;
					_win.scrollTop(h);
				}else if(rowTop - rowHeight <= winHeight){
					var h = _win.scrollTop() - rowHeight;
					_win.scrollTop(h);
				}
			},
			_enter : function(){// 鼠标经过
				var focusRow = _win.find('.'+STYLES.ROW+'.'+STYLES.ROW_FOCUS);
				this.select(focusRow);
			},
			_hover : function(row){// 鼠标经过
				_win.find('.'+STYLES.ROW).removeClass(STYLES.ROW_FOCUS);
				row.addClass(STYLES.ROW_FOCUS);
			},
			_exitWin : function(){// 鼠标移出窗体
				_win.hide();
			},
			_click : function(row){
				row.addClass(STYLES.ROW_FOCUS);
				this.select(row);
			},
			reset : function(){
				_win.find('*').removeClass('focus');
				lastQ = '-1';
			},
			select : function(row){
				var value = row?row.find('.'+STYLES.ROW_LABEL).text():'';
				var description = row?row.find('.'+STYLES.ROW_DESCRIPTION).text():'';
				value = value.replace(eval('/([<>;]+)\s*/g'),'');
				var icon = row?row.find('.'+STYLES.ROW_ICON).attr('src'):"";
				var selectItem = null;
				if(value.length > 0){
					selectItem = {
							value:value,
							description:description,
							icon:icon
					};
				}else{
					selectItem = {
							value:clearSeparate(table?table.getEditer().text():target.val()),
							description:'',
							icon:''
					};
				}
				if(selectItem!=null && selectItem.value.length>0)
					setValues(selectItem);
				win.hide();
				this.reset();
			}
		});
	};
	/**
	 * 方法
	 */
	$.fn.autocomplete.methods = {
			create : function(){			
				if(settings.valueType && settings.valueType === 'html')
					$(document).keydown(function(e){
						if(e.target.nodeName.toLowerCase() === 'input' || e.target.nodeName.toLowerCase()=== 'textarea')
							return;
						switch (e.keyCode) {
						case KEY.DELETE:
							table.removeSelectTexter();
							break;
						case KEY.BACKSPACE:
							if(!target.is(":focus") && table.getTable().find('.'+STYLES.EDIT_TABLE_TEXT_EDIT).size()  === 0){
								table.removeSelectTexter();
								//if(target.val() == '' && table.getTable().find('.'+STYLES.EDIT_TABLE_TEXT).size()  === 0)
									return false;
							}
							break;
						case KEY.DELETE_NUMBER:
							table.removeSelectTexter();
							break;
							default:
								break;
						}
					});				
				win.create();
				win.hide();	
				initSource();
				initSelect();
				events.fieldEvents();
				if(this.loadValues){
					this.loadValues(function(selectItems){
						if($.isArray(selectItems))
							$.grep(selectItems,function(selectItem){
								setValues(selectItem);
							});
					});
				}
			},
			_response : function(source){
				var len = source.length;
				for(var i=0; i<len; i++){
					var d = source[i];
					var label = null,description = null,icon = null;
					if ( typeof d === "string" ) {
						label = d;
					}else{
						label = d.label || d.value || d.name,
						icon = d.icon,
						description = d.description;
					}
					if(settings.valueType && settings.valueType === 'html' && table){
						var sv = {
							value:d.label || d.value || d.name,
							description:d.description || '',
							icon:d.icon || ''
						};
						
						if(!filterSelect(sv))
							win.createRow(len,i,label,description,icon);
					}else if(!qFilter(label))
						win.createRow(len,i,label,description,icon);
				}	
				win.show();
				events.winEvents();
			},
			_select : function(html){
				return html;
			}
	};
	/**
	 * 设置
	 */
	$.fn.autocomplete.defaults = {
			focus:false,
			multiple	:false,							// 多选	
			valueType	:'default',					// 值显示类型,html或默认// HTML方式使用
			valueRegexp	: null,						// 值正则表达式格式
			getValues	: null,						// 获取值// HTML方式使用
			formatTexter: null,						// 格式化编辑文本值
			separate	:",",						// 分隔符
			url			: null,						// 远程url
			source		: null,						// 数据，为数组
			select		: null,						// 选中回调
			data			: null,					// 显示数据回调
			width		:'auto',					// 宽度
			maxHeight	: 320,						// 高度
			iconScale	:'16x16',					// 图标宽高
			response	:null,						// 响应
			delay		: 300,						// 延时
			format		:{							// 格式化
				left:'',
				right:''
			},
			rowDisplay	:'',						// 行显示方式，默认换行,inline：不换行
			valueSubfix :'',						// 后缀
			valuePrefix :''						// 前缀
	};
	
})(jQuery);
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
						_icon.html('<img class="'+config.iconScale +'" src="'+(config.iconPrefix && config.iconPrefix.length>0?config.iconPrefix:'')+item.icon+'"/>');
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
				label:'',
				value:'',
				width:'auto',// 宽
				maxHeight:200// 高
		};
		config = $.extend(methods,defaults,options);
		return config.create();
	};
})(jQuery);

(function( $, undefined ) {
	var timeCache = [];			// 延时内存

	/** 获取数字 */
	function getNumeric(v){
		var re = /[^a-zA-Z\d\u4e00-\u9fa5,.!?()，。．；;？]/g;
		v = v.replace(re,"");
		return v.match(/\d+/)?parseInt(v):0;
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
			return response(source);
		}
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
	 * 菜单menu >> 布局为横排，可以靠左或靠右
	 * 菜单menu分为组和项。
	 * 项可以只有一层，也可以有多层，多层则以树的形式展现。
	 * 
	 */
	$.fn.menus = function(options){
		var msw = 0;
		var _root = $(this),
			_menus = _root.find('.menus:first'),
			_content = _root.find('.menus-content:first'),
			_menusOuter = _root.find('.menus-outer:first'),
			_panel =  _root.find('.menuspanel:first');
			_showHide = _root.find('.showhide:first');
		var methods = {
				/**
				 * 创建
				 */
				onload:function(){
					if(_menus.size() <1){
						var _this = this;
						_menus = $('<div class="menus"></div>'),
						_content = $('<div class="menus-content"></div>'),
						_menusOuter = $('<div class="menus-outer"></div>'),
						_panelleft = $('<td valign=top></td>'),
						_panelright = $('<td valign=top></td>'),
						_paneltr = $('<tr></tr>'),
						_panel = $('<table class="menuspanel"></table>');
						_showHide = $('<div class="showhide"></div>');
						_panel.addClass(_this.theme);

						
						// 向页面添加菜单节点
						_root.addClass('menuspanel');
						_menusOuter.append(_menus);

						// 菜单位置
						var menuOffset = _this.menuLayout.offset;
						if(menuOffset == 'right'){// 标签栏偏移右侧
							_content.addClass('float-left');	
							_panelleft.append(_content);
							_panelright.append(_menusOuter);		
						}else{// 标签栏偏移左侧
							_content.addClass('float-right');	
							_panelleft.append(_menusOuter);
							_panelright.append(_content);
						}
						_paneltr.append(_panelleft);
						_paneltr.append(_panelright);
						_panel.append(_paneltr);
						_panel.append(_showHide);

						// 菜单宽度
						msw = _this.menuLayout.menusWidth;
						if(msw){
							_menusOuter.width(msw);
							_menusOuter.parent().width(msw);
						}else{
							msw = _menusOuter.width();
						}
						// 调整尺寸
						_this.resize();		
						_root.append(_panel);
						
						// 配置数据
						this.setSource();
					}
					this.showHideButtonClick();
				},
				showHideButtonClick:function(){
					var _this = this;
					_showHide.click(function(){
						var _t = _menusOuter.parent();
						if(_t.css('display') != 'none'){
							_t.hide();
							_t.width(0);
						}else{
							_t.show();
							_t.width(msw);
						};
						_this.showHideButton($(this),_t);
						_this.resize(_this.resizeResponse);
					});
				},
				/**
				 * 显示隐藏按钮
				 */
				showHideButton:function(obj,t){
					_showHide = obj ||  _showHide; 
					var _t = t || _menusOuter.parent();
					var w = _t.width();
					var h = _t.height();
					var shw = _showHide.width();
					_showHide.height(h);
					if(w > shw){
						// 隐藏按钮
						_showHide.addClass('hide');
						_showHide.removeClass('show');
						var left = w - shw;
						_showHide.css({
							'left':left+'px'
						});
					}else{
						// 显示按钮
						_showHide.addClass('show');
						_showHide.removeClass('hide');
						_showHide.css({
							'left':'0px'
						});
					}
				},
				/**
				 * 配置数据源
				 */
				setSource:function(){
					var _this = this;
					initSource(this.source,function(result){	
						_this.source = result;
						// 调整标签内容栏尺寸				
						_this.createMenus();
						_this.ext = _this.complete(_content);// 回调，将内容节点输出到外部
						_this.afterComplete();
					});
				},
				create:function(){
					this.onload();
					return this;
				},
				createMenus:function(source){
					var _this = this;
					var source = this.source;
					if($.isArray(source) && source.length > 0){
						$.grep(source,function(data,i){
							var id = data.id;
							var label = data.label;
							var items = data.items;
							var _menu = _this.createMenu(items);
							var _group = $('<div class="menu-group"></div>');
							if(data.showTitle){
								// 显示菜单组标题
								var _title = $('<div class="menu-group-title"></div>');
								_title.html(label);
								if(typeof data.loaded === "function"){
									// 加载组标题结束
									data.loaded(_title,function(data,depth,complete){
										// 如果返回菜单项数据，添加菜单项
										var menu = _this.createMenuItem(data,depth);
										_menu.append(menu.get());
										if(typeof complete === 'function'){
											complete(menu);
										}
									});
								}
								_group.append(_title);
							}
							_group.attr('id',id);
							if(_menu){
								_group.append(_menu);
							}
							_menus.append(_group);
						});
					}
				},
				/**
				 * 创建菜单
				 */ 
				createMenu:function(items,depth){
					var _this = this;
					var _menu = $('<ul></ul>');
					if($.isArray(items) && items.length > 0){
						depth = depth || 0;
						depth ++;
						$.grep(items,function(data,i){
							// 创建菜单项
							var menu = _this.createMenuItem(data,depth);
							_menu.append(menu.get());
						});
					}
					return _menu;
				},
				/**
				 * 创建菜单项
				 */ 
				createMenuItem:function(data,depth){
					var _this = this;
					return Menu({
						depth:depth,
						data:data,
						paddingStep:_this.paddingStep,
						recursive:function(items,depth){
							return _this.createMenu(items,depth);
						},
						setSelectStyle:function(id){
							_this.setSelectStyle(id);
						},
						select:function(){
							//data.select(_this.ext);
							if(!data.noclick){
								data.select(_this.ext);
							}
						},
						hover:function(menu){
							//  经过事件
							if(typeof data.hover === "function")
								data.hover(menu,_this.ext);
						},
						blur:function(menu){
							// 鼠标离开
							if(typeof data.blur  === "function")
								data.blur(menu,_this.ext);
						}
					}).create();
				},
				getItem:function(mid){
					var source = this.source;
					var mi = null;
					if($.isArray(source))
					$.grep(source,function(data,i){
						var items = data.items;
						if($.isArray(items)){
							$.grep(items,function(item){
								if(item && item.id == mid){
									mi = item;
									return;
								}
							});
						}
						if(mi != null)
							return;
					});
					return mi;
				},
				/**
				 * 选中
				 * mid 菜单项ID
				 * q 地址栏参数
				 */
				selectMenu:function(mid,q,reload){
					if(mid && mid !=''){
						var mi = this.getItem(mid);
						if(mi != null && this.ext != null){
							mi.select(this.ext,q,reload);
						}else{
							mid = this.select(0,0);
						}
					}else{
						mid = this.select(0,0);
					}					
					// 设置样式
					this.setSelectStyle(mid);
				},
				/**
				 * 选中
				 * source 数据
				 * gindex 菜单组序号
				 * mindex 菜单项序号
				 */
				select:function(gindex,mindex){
					var mid = '';
					var source = this.source;
					if($.isArray(source) && source.length > 0){
						var _this = this;
						$.grep(source,function(data,i){
							var items = data.items;
							if($.isArray(items) && i == gindex){
								if(items.length > mindex){
									var item = items[mindex];
									mid = item.id;
									item.select(_this.ext);
								}
								return;
							}
						});
					}
					return mid;
				},
				// 设置选中样式
				setSelectStyle:function(mid){
					_panel.find('.menu').removeClass('select');
					_panel.find('#menu'+mid).addClass('select');
				},
				resize:function(response){
					this.resizeResponse = response;
					var width = _root.innerWidth() - _menusOuter.parent().outerWidth();
					_content.width(getRealWidth(_content,width));
					this.setHeight(_menusOuter);
					this.setHeight(_content);	
					this.setHeight(_panel);	
					_panel.width(_root.innerWidth());
					this.showHideButton();
					if(response && typeof response == "function"){
						response(this.ext);
					}					
				},
				setHeight:function(_target){
					var height = getRealHeight(_target,_root.innerHeight());
					_target.height(height);
				}
				
		};
		var defaults = {
				paddingStep:12,
				resizeResponse:function(){},// 尺寸调整响应
				source:[{// 组数组
						id:-1,	// 组id
						label:'', // 组显示文本
						showTitle:true,// 显示组标题
						loaded:function(){},// 加载结束
						hover:function(){}, // 经过
						blur:function(){}, // 移出
						select:function(){},// 选中
						items:[
							{// 菜单项
								id:-1,
								label:'', // 显示文本
								href:'', // 链接
								icon:'', // 图标
								hover:function(){},// 经过回调
								blur:function(){}, // 离开回调
								select:function(){}, // 选中回调
								items:[] // 子菜单列表，可以实现树形
							}]
					}],
				menuLayout:{				// 标签布局
					offset:'left',		// 标签栏位置，默认为左侧，还可输入right
					menusWidth:200		// 标签栏宽度，只有设置居左或居右显示有效，默认为父容器宽度
				},
				complete:function(){},	// 加载菜单结束，返回内容HTML节点，HTML节点可以创建不同对象重新传入类
			 	ext : null,				// 从菜单回调后创建的对象，菜单点击事件调用此对象
			 	afterComplete:function(){// 创建不同对象重新传入类结束回调，通常会在设置默认选中项时调用
			 		
			 	}
		};
		/**
		 * 菜单项
		 */
		var Menu = function(options){
			var _item = $('<li></li>');
			var _icon = $('<img class="icon" border=0 >');
			var _label = $('<a class="label"></a>');
			var _div = $('<div class="menu"></div>');
			var _this = this;
			return $.extend({
				create:function(){
					_this = this;
					_div.append(_icon);
					_div.append(_label);
					_item.append(_div);	
					_this.recreate();
					return _this;
				},
				recreate:function(){
					var paddingLeft = (_this.depth*_this.paddingStep) + 'px';
					_div.css({'padding-left':paddingLeft});
					_item.attr('id',_this.data.id);
					_div.attr('id','menu'+_this.data.id);
					
					var className = _this.data.className;
					if(className && className!=''){
						_div.addClass(className);
						// 点击事件
						_div.click(function(){
							// 设置样式
							_this.setSelectStyle(_this.data.id);
							_this.select();
						});
					}
					
					_this.setIcon();

					_label.html(_this.data.label);
					_this.createChildren();
					
					_this._click();
					_this._hover();

				},
				createChildren:function(){
					var items = _this.data.items;
					if(items && $.isArray(items) && items.length >0){
						// 如果有下级菜单，
						// 移交给根函数递归创建
						var _UL = _this.recursive(items,_this.depth);
						if(_UL){
							_UL.hide();
							_item.append(_UL);
							// 添加打开收起按钮，子菜单列表默认为收起状态
							var _folding = $('<a class="folding"></a>');
							_folding.addClass('collapse');
							_folding.click(function(){
								_UL.toggle();
								if(_folding.hasClass('collapse')){
									_folding.removeClass('collapse');
									_folding.addClass('expand');
								}else{
									_folding.addClass('collapse');
									_folding.removeClass('expand');
								}
							});
							if(!_label.prev().hasClass('folding')){
								_folding.insertBefore(_label);
							};
						}
					};
				},
				remove:function(){
					_item.remove();
				},
				_click:function(){
					// 点击事件
					if(_label.next().size() > 0){
						_label.click(function(){
							// 设置样式
							_this.setSelectStyle(_this.data.id);
							_this.select();
						});
					}else{
						_div.click(function(){
							// 设置样式
							_this.setSelectStyle(_this.data.id);
							_this.select();
						});
					}
				},
				_hover:function(){
					//  经过事件
					_div.hover(function(){
						$(this).addClass('hover');
						_this.hover(_this);
					},function(){
						// 鼠标离开
						$(this).removeClass('hover');
						_this.blur(_this);
					});
				},
				hide:function(){
					_menu.hide();
				},
				show:function(){
					
				},
				setIcon:function(){
					// 图标
					var icon = this.data.icon;
					if(icon && icon!=''){
						_icon.attr('src',icon);
						_icon.css({'display':'block'});
						_icon.show();
					}else{
						_icon.hide();
					}
				},
				setHref:function(){
					// 链接
					if(this.data.href){
						_label.attr('href',_this.data.href);
					}
				},
				exists:function(){
					return _parent.find('li#'+_this.data.id);
				},
				get:function(){
					return _item;
				},
				getIcon:function(){
					return _icon;
				},
				getLabel:function(){
					return _label;
				},
				getDiv:function(){
					return _div;
				}
			},{
				depth:0,
				data:null,
				ext:null,
				paddingStep:12,
				recursive:function(){},
				select:function(){},
				hover:function(){},
				blur:function(){}
			},options);
		};
		return $.extend(methods,defaults,options).create();
	};
})(jQuery);
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
	/** 获取数字 */
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
		if ( typeof source === "string" &&
				( $.trim(source).indexOf('url:') == 0 ||  $.trim(source).indexOf('url:') == 0 )) {
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
					if(this.tabNone == true){
						_tabsOuter.hide();
					}
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
					var _this = this;
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
					_this.resizeFrame();
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
//					alert(_content.offset().left  + " "+_couter.offset().left)
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
/*!
 * 多级树
 * Copyright 2014 杨大江
 */

(function( $, undefined ) {
	$.fn.tree = function(options) {
		var defaults = {
				type				:"GET",					// 异步获取类型
				dataType 		:"JSON",					// 异步返回数据类型
				ajax				:true,					// 开启异步
				ajaxUrl			:"",						// 异步URL
				gid				:'',						// 组ID
				checkeds			:[],						// 选中的数组
				checkboxName	:	'menu_id',				// checkbox名称
				idInput			:'pid',					// ID节点名称
				names			:'names',				// 显示名称节点名称
				containerClass	:'fixwork-plugins-tree',	// 容器样式
				toggleClass		:'tree-item-toggle',		// 条目关闭打开按钮样式
				itemClass		:'tree-item',			// 条目样式
				css3Url			:'',
				complete			:function(){}			// 加载结束调用
		};
		var DOM = $(this);
		var methods = {
				init:function(){
					DOM.addClass(config.containerClass);
					DOM.get(0).style.cssText += 'behavior: ' + config.css3Url;
					var gID = config.gid !=''?gid:DOM.attr('id');
					config.initChildren(DOM,'',gID);
				},
				loading:function(li){
					li.append($('<div class="loading"></div>'));
				},
				finish:function(li){
					li.find('.loading').remove();
					config.complete();
				},
				error:function(message){
				},
				event:function(li){
					$(li).find('a.'+config.toggleClass+', a.'+config.itemClass).click(function(){
						var ul = $(li).find('ul:first');
						var toggle = $(li).find('a.'+config.toggleClass+':first');
						var ff = toggle.next();
						var a = $(li).find('a.'+config.itemClass+':first');
						var id = a.attr('tag');
						// ul.css("display") == "none"
						if(ul.size()>0 && ul.is(':visible')){
							toggle.removeClass('opened');
							toggle.addClass('closed');
							ff.removeClass('folder');
							ff.addClass('folder-closed');
							ul.slideUp(300);
						}else if(ul.size()>0 && !ul.is(':visible')){
							toggle.addClass('opened');
							toggle.removeClass('closed');
							ff.removeClass('folder-closed');
							ff.addClass('folder');
							ul.slideDown(500);
						}else{
							toggle.addClass('opened');
							toggle.removeClass('closed');
							ff.removeClass('folder-closed');
							ff.addClass('folder');
							// 异步加载
							config.initChildren($(li),id,'');
						}
					});
				},
				getChecked:function(){
					// 获取选中项
					var value = new Array();
					var index = 0;
					$.each(DOM.find(':checkbox'),function(i,item){	
						if($(item).attr('checked') == 'checked'){
							value[index]= $(item).val(); 
							index ++;
						}
					});
					return value;
				},
				getNamesText:function(a,names){
					names = a.text() + " &gt;&gt; " + names;
					var pa = a.parent('ul').prev('a');
					if(pa.size()>0){
						names = config.getNamesText(a,names);
					}
					return names;
				},
				selected:function(a){
					//sID = a.attr('id');
				},
				setChecked:function(checkbox){
					$.grep(config.checkeds,function(value){
						if(checkbox.val() == value){
							checkbox.attr('checked','checked');
						}
					});
				},
				initChildren:function(parent,id,gid){
					var data = {'id':id,'gid':gid};
					// 提交
					$.ajax({
				        type: config.type,
				        dataType:config.dataType, 
				        async:false, 
				        url: config.ajaxUrl,
				        data:data,
				        beforeSend:function(){
				        	config.loading($(parent));
				        },
				        success: function(data) {
					        	if(data.error){
					        		//config.error(data.error.message);
						        	config.finish($(parent));
					        	}else{
					        		var ul = $('<ul></ul>');
					        		for(var i=0;i<data.length;i++){
					        			var item = data[i];
					        			var li = $('<li></li>');
					        			var toggle = $('<a class="tree-item-toggle"></a>');
					        			var ff = $('<a></a>');
					        			var checkbox = $('<input type="checkbox" value="'+item.id+'" name="'+config.checkboxName+'"></a>');
					        			config.setChecked(checkbox);
					        			var a = $('<a class="tree-item"></a>');
					        			if(item.exists_children == true){
					        				toggle.addClass('closed');
					        				ff.addClass('folder-closed');
					        			}
					        			else{
					        				toggle.addClass('opened');
					        				ff.addClass('file');
					        			}
					        			toggle.attr('tag',item.id);
					        			a.attr('tag',item.id);
					        			a.attr('pid',item.parent?item.parent.id:"");
					        			a.attr('gid',item.menuGroup?item.menuGroup.id:"");
	//				        			a.attr('href',item.uri);
	//				        			a.attr('icon',config.iconPrefix && config.iconPrefix.length>0?config.iconPrefix+item.icon:item.icon);
					        			a.attr('sorter',item.sorter);
					        			a.html(
					        					(item.icon?"<span class='icon'><img src='"+(config.iconPrefix && config.iconPrefix.length>0?config.iconPrefix:'')+item.icon+"' width=16 height=16/></span>":"")+
					        					item.name +
					        					(item.description?' <font color=#999>('+item.description+')</font>':'')+
					        					(item.descri?' <font color=#999>('+item.descri+')</font>':'')+
					        					(item.address?' <font color=#999>访问地址：('+item.address+')</font>':'')+
					        					(item.alias?' <font color=#999>别名：'+item.alias+'</font>':''));
					        			$(li).append($(checkbox));
					        			$(li).append($(toggle));
					        			$(li).append($(ff));
					        			$(li).append($(a));
					        			$(ul).append($(li));
					        			config.event($(li));				        			
					        		}
					        		$(parent).append($(ul));
						        	config.finish($(parent));
				        		};
				        },
				        error: function(XMLHttpRequest, textStatus, errorThrown) {
				        	config.finish($(parent));
				        }
				    });
				}
		};
		var config = $.extend(methods, defaults, options);
		
		return this.each(function() {  
			return config.init();
		});
	};
})(jQuery);

//(function(){
/**
 * 1、异步加载内容，并改变地址栏。
 * 2、后退操作时，获取保存的网页信息，并回调操作。
 * window.location.pathname // 项目路径
 * window.location.host // 主机
 * window.location.hostname // 主机名
 * window.location.search // url串
 */

	urlState = {
		// 检查浏览器是否支持
		enable:function(){
			if (window.history.pushState)        
				return true;
	    		return false;
		},
		// 改变地址栏并保存
		pushState : function(params,uri,title){
			var state = {
					title:title,
					params:params,
					uri:uri
			};
			var url = uri + this.createSearch(params);
			window.history.pushState(state, title || document.title, url);
		},
		// 创建参数串
		createSearch:function(params){
			if($.isArray(params) && params.length>0){
				var search = '?';	
				$.grep(params,function(param){
					if(param.value && param.value!=null && param.value != '')
						search += (search == '?'?'':'&')+ param.name+"="+param.value;
				});
				return search;
			}
		},
		// 获取URL参数串
		getSearch:function(){
	        return document.location.search;			
		},
		// 解析当前URL,并执行回调
		parseUrl :function(response){
			var uri = window.location.pathname;
			var search = window.location.search;
			this.parse(uri,search,response);
		},
		// 解析URL参数
		parse:function(uri,search,response){
			search = search.replace(/\?/g,'');
			var query = search.split('&');
			var params = [];
			$.grep(query,function(arr){
				var param = arr.split('=');
				if(param.length>1){
					params.push({
						name:param[0],
						value:param[1]
					});
				}
			});
			response({
				uri:uri,
				params:params
			});
		},
		// 获取参数值
		getParameter:function(name,data){
			var value = '';
			$.grep(data,function(param){
				if(param.name = name){
					value = param.value;
					return;
				}
			});
			return value;
		},
		// 后退
		back:function(){
			history.go(-1);
		},
		// 状态监听，执行响应函数，响应函数由外部调用
		stateListener:function(response){
			if (window.history.pushState)   
				window.addEventListener('popstate', function(e){
					  if (history.state){
					    var state = e.state;
					    response(state);
					 }
				}, false);
		}
	};	
//});
/*!
 * 横向多级菜单
 * Copyright 2014 杨大江
 */
(function( $, undefined ) {
	$.fn.hmenu = function(options) {
		var defaults = {
				showNum			:4,								// 显示的单元数
				itemWidth		:120,							// 单元宽度
				itemHeight		:200,							// 单元高度
				type			:"GET",							// 异步获取类型
				dataType 		:"JSON",						// 异步数据类型
				groupsData		:"",							// 组异步参数
				groupsUrl		:"",							// 组异步URL
				menusUrl		:"",							// 项异步URL
				typeGroup		:0,								// 类型-组
				typeMenu		:1,								// 类型-项
				names			:'names',						// 显示名称
				groupInput		:'gid',							// 组隐藏域
				menuInput		:'pid',					   	 	// 项隐藏域
				itemClass		:'horizontal-menu-item',		// 项样式
				domClass		:'horizontal-menu-dom',			// 根节点样式
				containerClass	:'horizontal-menu-container',	// 容器样式
				css3Url			:''
		};
		var sGid = '',
			sMid = '',
			namesText = new Array(),
			DOM = $('<div></div>'),
			_THIS = $(this),
			nodes = new Array();
		var methods = {
				resize:function(){			
					var size = DOM.find('.'+config.itemClass).size();
					DOM.width(size*config.itemWidth+size);
					DOM.height(config.itemHeight);
					if(size>config.showNum){
						_THIS.width(config.itemWidth * config.showNum + config.showNum);
					}else{
						_THIS.width(config.itemWidth * size + size);
					}
				},
				init:function(){
					_THIS.html(DOM);
					_THIS.addClass(config.containerClass);
					_THIS.get(0).style.cssText += 'behavior: ' + config.css3Url;
					DOM.addClass(config.domClass);
					
					var loadId = $('input[name='+config.menuInput+']').val();
					var loadNames = $('#'+config.names).html();
					if(config.groupsUrl != ''){
						config.groups();
					}else{
						var index = 0;
						config.removeNames(index);
						namesText[index] = "";
						config.selectedStyle($(this),index);			
						// 点击菜单组
						config.selected();
						config.menus(sMid,index);
						$('input[name='+config.menuInput+']').val(loadId);
						$('#'+config.names).html(loadNames);
					}
				},
				loading:function(){
				},
				success:function(index){
					config.remove(index);
					DOM.append(nodes[index]);
					config.resize();
				},
				removeNames:function(index){
					for(var i=index;i < nodes.length;i++){
						namesText.splice(i, 1);
					}
				},
				remove:function(index){
					for(var i=index;i < nodes.length;i++){
						DOM.find('.'+config.itemClass+':eq('+index+')').remove();
					}
					config.resize();
				},
				error:function(message){
				},
				click:function(a,index,type){
					$(a).click(function(){
						var _this = $(this);
						if(type == config.typeGroup){
							sGid = _this.attr('id');
							sMid = '';
						}else{
							sMid = _this.attr('id');
						}
						config.removeNames(index);
						namesText[index] = _this.text();
						config.selectedStyle(_this,index);			
						// 点击菜单组
						config.selected();
						config.menus(sMid,index+1);
					});
				},
				selectedStyle:function(obj,index){
					for(var i=index;i<nodes.length;i++){
						$(nodes[i]).find('a').removeClass('selected');
					}
					obj.addClass('selected');
				},
				selected:function(){					
					$('input[name='+config.groupInput+']').val(sGid);
					$('input[name='+config.menuInput+']').val(sMid);
					var _namesText = '';
					$.each(namesText,function(i,item){
						_namesText += (_namesText !=''? " &gt;&gt; ":'') + item;
					});
					$('#'+config.names).html(_namesText);
				},
				groups:function(){
					$.ajax({
				        type: config.type,
				        dataType:config.dataType, 
				        async:false, 
				        url: config.groupsUrl,
				        data:config.groupsData,
				        beforeSend:function(){
				        	config.loading();
				        },
				        success: function(data) {
					        	if(data.error){
					        		config.remove(0);
					        	}else{
					        		nodes[0] = $('<div class="'+config.itemClass+' first"></div>');
					        		var array = $.isArray(data)?data:data.result;
					        		if(array &&  $.isArray(array))
					        		for(var i=0;i<array.length;i++){
					        			var group = array[i];
					        			var a = $('<a></a>');
					        			a.attr('id',group.id);
					        			a.attr('position',group.position);
					        			a.attr('sorter',group.sorter);
					        			a.text(group.name);
					        			config.click(a,0,config.typeGroup);
					        			$(nodes[0]).append($(a));				        			
					        		}	
					        		if(config.itemWidth>0)
					        			$(nodes[0]).width(config.itemWidth);
					        		if(config.itemHeight>0)
					        			$(nodes[0]).height(config.itemHeight);
					        		config.success(0);
					        	};
				        },
				        error: function(XMLHttpRequest, textStatus, errorThrown) {
				        	alert(errorThrown);
				        }
				    });
				},
				menus:function(id,index){
					var data = id !=''?{'id':id}:{'gid':sGid};
					// 提交
					$.ajax({
				        type: config.type,
				        dataType:config.dataType, 
				        async:false, 
				        url: config.menusUrl,
				        data:data,
				        beforeSend:function(){
				        	config.loading();
				        },
				        success: function(data) {
					        	if(data.error){
					        		//config.error(data.error.message);
					        		config.remove(index);
					        	}else{
					        		nodes[index] = $('<div class="'+config.itemClass+'"></div>');
					        		var array = $.isArray(data)?data:data.result;
					        		if(array &&  $.isArray(array))
					        		for(var i=0;i<array.length;i++){
					        			var menu = array[i];
					        			var a = $('<a></a>');
					        			a.attr('id',menu.id);
					        			a.attr('pid',menu.parent?menu.parent.id:"");
					        			a.attr('gid',menu.menuGroup?menu.menuGroup.id:"");
	//				        			a.attr('href',menu.uri);
	//				        			a.attr('icon',menu.icon);
					        			a.attr('sorter',menu.sorter);
					        			a.html(menu.name);
					        			config.click(a,index,config.typeMenu);
					        			$(nodes[index]).append($(a));				        			
					        		}
					        		if(config.itemWidth>0)
					        			$(nodes[index]).width(config.itemWidth);
					        		if(config.itemHeight>0)
					        			$(nodes[index]).height(config.itemHeight);
					        		config.success(index);
				        		};
				        },
				        error: function(XMLHttpRequest, textStatus, errorThrown) {
				        }
				    });
				}
		};
		var config = $.extend(methods, defaults, options);
		
		return this.each(function() {  
			return config.init();
		});
	};
})(jQuery);

