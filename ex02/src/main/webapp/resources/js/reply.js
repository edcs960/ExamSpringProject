/**
 * 
 */
 
 console.log("Reply Module.............");
 
 var replyService = (function(){
 	
 	console.log("add.........");
 	function add(reply, callback, error){
 		console.log("add reply.............");
 		
 		$.ajax({
 			type : 'post',
 			url : '/replies/new',
 			data : JSON.stringify(reply),
 			contentType : "application/json; charset=utf-8",
 			success : function(result,status,xhr){
 				if(callback){
 					callback(result);
 				}
 			},
 			error : function(xhr, status, er){
 				if(error){
 					error(er);
 				}
 			}
 		})
 	}
 	
 	console.log("getList.........");
 	function getList(param, callback, error){
 		var bno = param.bno;
 		var page = param.page || 1;
 		
 		console.log(bno);
 		console.log(page);
 		 		
 		$.getJSON("/replies/pages/" + bno + "/" + page + ".json", 
 		function(data){
 			if(callback){
 				callback(data);
 			}
 		}).fail(function(xhr, status, err){
 			if(error){
 				error();
 			}
 		});
 	}
 	
 	return {
 		add : add,
 		getList : getList
 	};
 })();