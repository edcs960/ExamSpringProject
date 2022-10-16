<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../includes/header.jsp" %>

<script type="text/javascript">
$(document).ready(function(){
	var operFrom = $("#operForm");
	
	$("button[data-oper='modify']").on("click",function(e){
		operForm.attr("action","/board/modify").submit();
	});
	
	$("button[data-oper='list']").on("click",function(e){
		operForm.find("#bno").remove();
		operForm.attr("action","/board/list");
		operForm.submit();
	});
});
</script>

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
					
					<button data-oper='modify' class="btn btn-default" onclick="location.href='/board/modify'">Modify</button>
					<button data-oper='list' class="btn btn-info" onclick="location.href='/board/list'">List</button>
					
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

<%@ include file="../includes/footer.jsp" %>
