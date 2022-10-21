<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../includes/header.jsp" %>

<script type="text/javascript" src="/resources/js/reply.js"></script>

<script>
	// 원본 이미지 보기
	function showImage(fileCallPath){
		//alert(fileCallPath);
		
		$(".bigPictureWrapper").css("display","flex").show();
		
		$(".bigPicture")
			.html("<img src='/display?fileName="+fileCallPath+"'>")
			.animate({width : '100%', height:'100%'}, 1000);
	}
	
	// 첨부파일 관련
	$(document).ready(function(){
		
		(function(){
			var bno = '<c:out value="${board.bno}"/>';
			
			$.getJSON("/board/getAttachList", {bno:bno}, function(arr){
				console.log(arr);
				
				var str = "";
				
				$(arr).each(function(i, attach){
					if(attach.fileType){
						var fileCallPath = encodeURIComponent(attach.uploadPath + "/s_" + attach.uuid + "_" + attach.fileName);
						
						str += "<li data-path='"+attach.uploadPath+"' data-uuid='"+attach.uuid+"' " 
								+ "data-filename='"+attach.fileName+"' data-type='"+attach.fileType+"'><div>"
								+ "<img src='/display?fileName="+fileCallPath+"'>"
								+ "</div></li>";
					}
					else{
						str += "<li data-path='"+attach.uploadPath+"' data-uuid='"+attach.uuid+"' " 
								+ "data-filename='"+attach.fileName+"' data-type='"+attach.fileType+"'><div>"
								+ "<span> "+attach.fileName+"</span><br/>"
								+ "<img src='/resources/img/attach.png'>"
								+ "</div></li>";
					}
				});
				
				$(".uploadResult ul").html(str);
			});
		})();
		
		// 첨부파일 클릭 이벤트
		$(".uploadResult").on("click","li", function(e){
			console.log("view imgae");
			
			var liObj = $(this);
			
			var path=encodeURIComponent(liObj.data("path") + "/" + liObj.data("uuid") + "_" + liObj.data("filename"));
			
			if(liObj.data("type")){
				showImage(path.replace(new RegExp(/\\/g),"/"));
			}else{
				self.location="/download?fileName="+path;
			}
		})
		
		$(".bigPictureWrapper").on("click", function(e){
			$(".bigPicture").animate({width:'0%', height: '0%'},1000);
			setTimeout(function(){
				$(".bigPictureWrapper").hide();
			},1000);
		});
	});
</script>

<script type="text/javascript">
$(document).ready(function(){
	var bnoValue = '<c:out value="${board.bno}"/>';
	var replyUL = $(".chat");
	
	var pageNum = 1;
	var replyPageFooter = $(".panel-footer");
	
	showList(1);
	
	/* 댓글 페이징 함수 */
	function showReplyPage(replyCnt){
		var endNum = Math.ceil(pageNum/10.0)*10;
		var startNum = endNum - 9;
		
		var prev = startNum != 1;
		var next = false;
		
		if(endNum * 10 >= replyCnt){
			endNum = Math.ceil(replyCnt/10.0);
		}
		
		if(endNum * 10 < replyCnt){
			next = true;
		}
		
		var str = "<ul class='pagination pull-right'>";
		
		if(prev){
			str += "<li class='page-item'><a class='page-link' href='"+(startNum-1)+"'>Previous</a></li>";
		}
		
		for(var i = startNum;i<=endNum; i++){
			var active = pageNum==i ? "active":"";
			
			str += "<li class='page-item"+active+" '><a class='page-link' href='"+i+"'>"+i+"</a></li>";
		}
		
		if(next){
			str+="<li class='page-item'><a class='page-link' href='"+(endNumm+1)+"'>next</a></li>";
		}
		
		str += "</ul></div>";
		
		console.log(str);
		
		replyPageFooter.html(str);
	}
	
	/* 댓글 리스트 게시글 페이지에 추가 */
	function showList(page){
		console.log("show list " + page);
		
		replyService.getList({bno:bnoValue, page:page||1}, function(replyCnt,list){
			console.log("replyCnt : " + replyCnt);
			console.log("list : " + list);
			console.log(list);
			
			if(page == -1){
				pageNum = Math.ceil(replyCnt/10.0);
				showList(pageNum);
				return;
			}
			
			var str="";
			if(list == null || list.length == 0){
				replyUL.html("");
				return;
			}
			
			for(var i = 0, len=list.length || 0 ; i<len;i++){
				str += "<li class='left clearfix' data-rno='"+list[i].rno+"'>";
				str += "	<div><div class='header'><strong class='primary-font'>["+list[i].rno+"] " +list[i].replyer+"</strong>";
				str += "	<small class='pull-right text-muted'>"+replyService.displayTime(list[i].replyDate)+"</small></div>";
				str += "	<p>"+list[i].reply+"</p></div></li>";
			}
			replyUL.html(str);
			
			showReplyPage(replyCnt);
		});
	}
	
	replyPageFooter.on("click","li a",function(e){
		e.preventDefault();
		console.log("page click");
		
		var targetPageNum = $(this).attr("href");
		
		console.log("targetPageNum : " + targetPageNum);
		
		pageNum = targetPageNum;
		
		showList(pageNum);
	});
	
	var modal = $(".modal");
	var modalInputReply = modal.find("input[name='reply']");
	var modalInputReplyer = modal.find("input[name='replyer']");
	var modalInputReplyDate = modal.find("input[name='replyDate']");
	
	var modalModBtn = $("#modalModBtn");
	var modalRemoveBtn = $("#modalRemoveBtn");
	var modalRegisterBtn = $("#modalRegisterBtn");
	
	$("#addReplyBtn").on("click", function(e){
		modal.find("input").val("");
		modalInputReplyDate.closest("div").hide();
		modal.find("button[id !='modalCloseBtn']").hide();
		
		modalRegisterBtn.show();
		
		$(".modal").modal("show")
	});
	
	/* 모달창 저장 버튼 이벤트 */
	modalRegisterBtn.on("click", function(e){
		var reply = {
				reply:modalInputReply.val(),
				replyer:modalInputReplyer.val(),
				bno:bnoValue
		};
		
		replyService.add(reply, function(result){
			alert(result);
			
			modal.find("input").val("");
			modal.modal("hide");
			
			showList(-1);
		});
	});
	
	/* 댓글 모달창 수정 이벤트 */
	modalModBtn.on("click", function(e){
		var reply = {rno:modal.data("rno"), reply:modalInputReply.val()};
		
		replyService.update(reply, function(result){
			alert(result);
			modal.modal("hide");
			showList(pageNum);
		});
	});
	
	/* 댓글 모달창 삭제 이벤트 */
	modalRemoveBtn.on("click", function(e){
		var rno = modal.data("rno");
		
		replyService.remove(rno, function(result){
			alert(result);
			modal.modal("hide");
			showList(pageNum);
		})
	});
	
	$(".chat").on("click", "li", function(e){
		var rno = $(this).data("rno");
		
		replyService.get(rno, function(reply){
			modalInputReply.val(reply.reply);
			modalInputReplyer.val(reply.replyer);
			modalInputReplyDate.val(replyService.displayTime(reply.replyDate)).attr("readonly", "readonly");
			modal.data("rno", reply.rno);
			
			//console.log(modal.data("rno"));
			
			modal.find("button[id != 'modalCloseBtn']").hide();
			modalModBtn.show();
			modalRemoveBtn.show();
			
			$(".modal").modal("show");
		});
	});
});
</script>

<script type="text/javascript">
$(document).ready(function(){
	var operForm = $("#operForm");
	
	$("button[data-oper='modify']").on("click",function(e){
		operForm.submit();
	});
	
	$("button[data-oper='list']").on("click", function(e) {
		operForm.find("#bno").remove()
        operForm.attr("action", "/board/list");
		operForm.submit();
    });

});
</script>

<style type="text/css">
.uploadResult{
	width : 100%;
	background-color : gray;
}

.uploadResult ul{
	display: flex;
	flex-flow: row;
	justify-content: center;
	align-items: center;
}

.uploadResult ul li {
	list-style: none;
	padding: 10px;
	align-content: center;
	text-align: center;
}

.uploadResult ul li img{
	width: 100px;
}

.uploadResult ul li span{
	color: white;
}

.bigPictureWrapper{
	position: absolute;
	display: none;
	justify-content: center;
	align-items: center;
	top: 0%;
	width: 100%;
	height: 100%;
	background-color: gray;
	z-index: 100;
	background: rgba(255,255,255,0.5);
}

.bigPicture{
	position: relative;
	display:flex;
	justify-content: center;
	align-items: center; 
}

.bigPicture img{
	width: 600px;
}
</style>

<div class="row">
	<div class="col-lg-12">
		<h1 class="page-header">게시글</h1>
	</div>
	<!-- /.col-lg-12 -->
</div>
<!-- /.row -->

<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			
			<div class="panel-heading">게시글</div>
			<!-- /.panel-heading -->
			
			<div class="panel-body">
					<div class="form-group">
						<label>게시판 번호</label>
						<input class="form-control" name="bno" value='<c:out value="${board.bno}"/>' readonly="readonly">
					</div>
					
					<div class="form-group">
						<label>제목</label>
						<input class="form-control" name="title" value='<c:out value="${board.title}"/>' readonly="readonly">
					</div>
					
					<div class="form-group">
						<label>내용</label> 
						<textarea class="form-control" name="content" rows="3" readonly="readonly"><c:out value="${board.content}"/></textarea>
					</div>
					
					<div class="form-group">
						<label>작성자</label>
						<input class="form-control" name="writer" value='<c:out value="${board.writer}"/>' readonly="readonly">
					</div>
					<!-- /.form-group -->
					
					<button data-oper='modify' class="btn btn-default">수정하기</button>
					<button data-oper='list' class="btn btn-info">게시판으로 돌아가기</button>
					
					<form action="/board/modify" id='operForm' method="get">
						<input type="hidden" id="bno" name="bno" value='<c:out value="${board.bno}"/>'>
						<input type="hidden" name="pageNum" value='<c:out value="${cri.pageNum}"/>'>
						<input type="hidden" name="amount" value='<c:out value="${cri.amount}"/>'>
						<input type="hidden" name="keyword" value='<c:out value="${cri.keyword}"/>'>
						<input type="hidden" name="type" value='<c:out value="${cri.type}"/>'>
					</form>
			</div>
			<!-- /.end panel-body -->
			
		</div>
		<!-- /.end panel panel-default -->
	</div>
	<!-- /.end col-lg-12 -->
</div>
<!-- /.end row -->

<!-- 첨부파일 -->
<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			<div class="panel-heading">첨부파일</div>
			
			<div class="panel-body">
				<div class="uploadResult">
					<ul>
					</ul>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="bigPictureWrapper">
	<div class="bigPicture">
		
	</div>
</div>


<!-- 댓글 -->
<div class="row">
	<div class="col-lg-12">
		<!-- /.panel -->
		<div class="panel panel-default">
			<div class="panel-heading">
				<i class="fa fa-comments fa-fw"></i> 댓글
				<button id="addReplyBtn" class="btn btn-primary btn-xs pull-right">새 댓글 작성</button>
			</div>
		</div>
		<div class="panel-body">
			<ul class="chat">
				<!-- start reply -->
				
				<!-- end reply -->
			</ul>
			<!-- ./ end ul -->
		</div>
		<!-- ./ panel .chat-panel -->
		<div class="panel-footer">
		
		</div>
	</div>
</div>
<!-- ./ end row -->

<!-- 댓글 모달창 -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-dialog">
		<div class="modal-content">
			<!-- 모달 헤더 -->
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h4 class="modal-title" id="myModalLabel">댓글 달기</h4>
			</div>
			<!-- 모달 바디 -->
			<div class="modal-body">
				<div class="form-group">
					<label>댓글</label>
					<input class="form-control" name="reply" value="New Reply!!!!">
				</div>
				<div class="form-group">
					<label>댓글작성자</label>
					<input class="form-control" name="replyer" value="replyer">
				</div>
				<div class="form-group">
					<label>댓글 작성일</label>
					<input class="form-control" name="replyDate">
				</div>
			</div>
			<!-- 모달 푸터 -->
			<div class="modal-footer">
				<button id="modalModBtn" type="button" class="btn btn-warning">수정</button>
				<button id="modalRemoveBtn" type="button" class="btn btn-danger">삭제</button>
				<button id="modalRegisterBtn" type="button" class="btn btn-primary">저장</button>
				<button id="modalCloseBtn" type="button" class="btn btn-default" data-dismiss="modal">닫기</button>
			</div>
		</div>
	</div>
</div>

<%@ include file="../includes/footer.jsp" %>
