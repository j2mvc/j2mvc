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
				iconPrefix			:"",					// 图片前缀
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
					        	}else if(data && data.result){
					        		var ul = $('<ul></ul>');
					        		for(var i=0;i<data.result.length;i++){
					        			var item = data.result[i];
					        			var li = $('<li></li>');
					        			var toggle = $('<a class="'+toggleClass+'"></a>');
					        			var ff = $('<a></a>');
					        			var checkbox = $('<input type="checkbox" value="'+item.id+'" name="'+config.checkboxName+'"></a>');
					        			config.setChecked(checkbox);
					        			var a = $('<a class="'+itemClass+'"></a>');
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
	//				        			a.attr('icon',config.iconPrefix && config.iconPrefix.length>0?config.iconPrefix+item.icon:'');
					        			a.attr('sorter',item.sorter);
					        			a.html(item.name +
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
