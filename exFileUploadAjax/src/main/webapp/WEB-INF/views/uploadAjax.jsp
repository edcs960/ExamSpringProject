<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
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
}

.uploadResult ul li img{
	width: 20px;
}
</style>
</head>
<body>
	<h1>Upload with Ajax</h1>
	<div class="uploadDiv">
		<input type="file" name="uploadFile" multiple="multiple">
	</div>
	<button id="uploadBtn">Upload</button>
	
	<div class="uploadResult">
		<ul>
		
		</ul>
	</div>
	
	<script src="https://code.jquery.com/jquery-3.3.1.min.js"
			integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
			crossorigin="anonymous"></script>
			
	<script type="text/javascript">
		$(document).ready(function(){
			var regex = new RegExp("(.*?)\.(exe|sh|zip|alz)$");
			var maxSize = 5242880;
			var cloneObj = $(".uploadDiv").clone(); // input태그 복사
			var uploadResult = $(".uploadResult ul");
			
			function showUploadedFile(uploadResultArr){
				var str = "";
				
				$(uploadResultArr).each(function(i, obj){
					if(!obj.image){
						var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);
						
						str += "<li><a href='/download?fileName="+fileCallPath+"'><img src='/resources/img/attach.png'>"+obj.fileName+"</a></li>";
					}else{
						// 파일 경로 인코딩
						var fileCallPath = encodeURIComponent(obj.uploadPath + "/s_" + obj.uuid + "_" + obj.fileName);
						
						str += "<li><img src='/controller/display?fileName="+fileCallPath+"'></li>";
					}
				});
				
				uploadResult.append(str);
			}
			
			function checkExtension(fileName, fileSize){
				if(fileSize >= maxSize){
					alert("파일 사이즈 초과");
					return false;
				}
				
				if(regex.test(fileName)){
					alert("해당 종류의 파일은 업로드할 수 없습니다.");
					return false;
				}
				return true;
			}
			
			$("#uploadBtn").on("click", function(e){
				// jQuery로 파일 업로드 시 FormData라는 객체를 이용
				// 브라우저마다 제약이 존재하므로 주의해야함.
				// FormData는 가상의 form 태그와 비슷
				var formData = new FormData();
				
				var inputFile = $("input[name='uploadFile']");
				
				var files = inputFile[0].files;
				
				console.log(files);
				
				for(var i = 0; i<files.length;i++){
					if(!checkExtension(files[i].name, files[i].size)){
						return false;
					}
					
					formData.append("uploadFile", files[i]);
				}
				
				// Ajax를 이용해 FormData를 전송하려면 processData와 contentType을 반드시 false로 지정해야 전송이됨.
				// UploadController로 데이터를 전달하고, 컨트롤러에서 MultiPartFile 타입을 이용해서 첨부파일 데이터 처리
				$.ajax({
					url:'/controller/uploadAjaxAction',
					processData: false,
					contentType: false,
					data: formData,
					type:'post',
					dataType:'json',
					success: function(result){
						console.log(result);
						
						showUploadedFile(result);
						
						$(".uploadDiv").html(cloneObj.html()); // input태그 위치에 다시 추가
					}
				});
			});
		});
	</script>
</body>
</html>