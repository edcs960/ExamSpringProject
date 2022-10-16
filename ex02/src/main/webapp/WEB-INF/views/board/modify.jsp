<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../includes/header.jsp" %>



<div class="row">
	<div class="col-lg-12">
		<h1 class="page-header">Board Read</h1>
	</div>
	<!-- /.col-lg-12 -->
</div>
<!-- /.row -->

<div class="row">
	<div class="col-lg-12">
		<div class="panel panel-default">
			
			<div class="panel-heading">Board Read</div>
			<!-- /.panel-heading -->
			
			<form action="/board/modify" role="form" method="post">
				<input type="hidden" name="pageNum" value='<c:out value="${cri.pageNum}"/>'>
				<input type="hidden" name="amount" value='<c:out value="${cri.amount}"/>'>
				<input type="hidden" name="keyword" value='<c:out value="${cri.keyword}"/>'>
				<input type="hidden" name="type" value='<c:out value="${cri.type}"/>'>
				<div class="panel-body">
						<div class="form-group">
							<label>게시판 번호</label>
							<input class="form-control" name="bno" value='<c:out value="${board.bno}"/>' readonly="readonly">
						</div>
						
						<div class="form-group">
							<label>제목</label>
							<input class="form-control" name="title" value='<c:out value="${board.title}"/>'>
						</div>
						
						<div class="form-group">
							<label>내용</label> 
							<textarea class="form-control" name="content" rows="3"><c:out value="${board.content}"/></textarea>
						</div>
						
						<div class="form-group">
							<label>작성자</label>
							<input class="form-control" name="writer" value='<c:out value="${board.writer}"/>' readonly="readonly">
						</div>
						
						<div class="form-group">
							<label>작성일</label>
							<input class="form-control" name="regDate" value='<fmt:formatDate pattern="yyyy/mm/dd" value="${board.regDate}"/>' readonly="readonly">
						</div>
						
						<div class="form-group">
							<label>수정날짜</label>
							<input class="form-control" name="updateDate" value='<fmt:formatDate pattern="yyyy/mm/dd" value="${board.updateDate}"/>' readonly="readonly">
						</div>
						<!-- /.form-group -->
						
						<button type="submit" data-oper='modify' class="btn btn-default">Modify</button>
						<button type="submit" data-oper='remove' class="btn btn-danger">Remove</button>
						<button type="submit" data-oper='list' class="btn btn-info">List</button>
				</div>
				<!-- /.end panel-body -->
			
			</form>
			
		</div>
		<!-- /.end panel panel-default -->
	</div>
	<!-- /.end col-lg-12 -->
</div>
<!-- /.end row -->

<%@ include file="../includes/footer.jsp" %>

<script type="text/javascript">
$(document).ready(function(){
	var formObj = $("form");
	
	$('button').on("click", function(e){
		e.preventDefault();
		
		var operation = $(this).data("oper");
		
		console.log(operation);
		
		if(operation === 'remove'){
			formObj.attr("action","/board/remove");
		}
		else if(operation === 'list'){
			formObj.attr("action", "/board/list").attr("method", "get");
			var pageNumTag = $(input[name='pageNum']).clone();
			var amountTag = $(input[name='amount']).clone();
			var keywordTag = $(input[name='keyword']).clone();
			var typeTag = $(input[name='type']).clone();
			
			formObj.empty();
			formObj.append(pageNumTag);
			formObj.append(amountTag);
			formObj.append(keywordTag);
			formObj.append(typeTag);
		}
		formObj.submit();
	});
});
</script>
