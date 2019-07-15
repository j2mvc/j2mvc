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
		},
	};
//});