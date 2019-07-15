
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
			data		: null,					// 显示数据回调
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