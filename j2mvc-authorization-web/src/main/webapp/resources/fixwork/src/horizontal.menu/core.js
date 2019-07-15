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
