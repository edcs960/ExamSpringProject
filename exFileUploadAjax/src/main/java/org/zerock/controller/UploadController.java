package org.zerock.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.domain.AttachFileDTO;

import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;

@Controller
@Log4j2
public class UploadController {
	// '년/월/일' 폴더명 생성
	private String getFolder() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String str = sdf.format(date);
		
		return str.replace("-", File.separator);
	}
	
	private boolean checkImageType(File file) {
		try {
			String contentType = Files.probeContentType(file.toPath());
			
			return contentType.startsWith("image");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		log.info("upload ajax");
	}
	
	@PostMapping(value="/uploadAjaxAction", produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ResponseEntity<List<AttachFileDTO>> uploadAjaxPost(MultipartFile[] uploadFile) {
		// 전달된 파일은 MultipartFile 타입 변수에 저장.
		// MultipartFile 타입 메소드들
		// - getName() : 파라미터의 이름, input 태그의 이름
		// - getOrigianlFileName() : 업로드 되는 파일 이름
		// - isEmpty() : 파일이 존재하지 않는 경우 true
		// - getSize() : 업로드되는 파일의 크기
		// - getBytes() : byte[]로 파일 데이터 변환
		// - getInputStream() : 파일 데이터와 연결된 InputStream을 반환
		// - transferTo(File file) : 파일의 저장
		

		List<AttachFileDTO> list = new ArrayList<>(); // 브라우저에 전달할 리스트 객체 생성
		String uploadFolder = "C:\\upload";
		
		// 폴더 생성
		String uploadFolderPath = getFolder();
		File uploadPath = new File(uploadFolder,uploadFolderPath);
		
		if(uploadPath.exists() == false) {
			uploadPath.mkdirs();
		}
		
		// 파일 체크
		for(MultipartFile multipartFile : uploadFile) {
			AttachFileDTO attachDTO = new AttachFileDTO(); // 해당 업로드 파일 데이터를 저장할 객체 생성
			
			// 파일 이름 설정
			String uploadFileName = multipartFile.getOriginalFilename();
			uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1);
			log.info("only file name : " + uploadFileName);
			
			attachDTO.setFileName(uploadFileName); // 객체에 업로드 파일 이름 설정
			
			UUID uuid = UUID.randomUUID();
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			
			
			try {
				// 파일 저장
				File saveFile = new File(uploadPath, uploadFileName);
				multipartFile.transferTo(saveFile);
				
				attachDTO.setUuid(uuid.toString()); // 객체에 UUID 설정
				attachDTO.setUploadPath(uploadFolderPath); // 객체에 업로드 경로 설정
				
				if(checkImageType(saveFile)) {
					attachDTO.setImage(true); // 객체에 이미지 여부 설정
					
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));
					
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);
					
					thumbnail.close();
				}
				
				list.add(attachDTO); // 브라우저에 전달할 리스트 객체에 추가
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<>(list, HttpStatus.OK); // 브라우저에 리스트 객체 전달
	}
	
	// 문자열로 파일의 경로가 포함된 fileName을 파라미터로 받고 byte 배열을 전송
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(String fileName){
		log.info("fileName : " + fileName);
		
		File file = new File("c:\\upload\\"+fileName);
		
		log.info("file : " + file);
		
		ResponseEntity<byte[]> result = null;
		
		try {
			HttpHeaders header = new HttpHeaders();
			
			// byte 배열로 이미지 파일을 전송할 때 브라우저에 보내주는 MIME 타입이 파일의 종류에 따라 달라짐.
			// probeContentType 메소드를 이용해 적절한 MIME타입 데이터를 Http 헤더 메시지에 포함할 수 있도록 처리
			header.add("Content-Type", Files.probeContentType(file.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// 파일을 다운 받기 위해 MIME 타입을 application/octet-stream으로 설정
	// 사용자가 사용중인 브라우저를 @RequestHeader를 통해 HTTP 헤더 메시지의 User-Agent 데이터를 가져옴
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@RequestHeader("User-Agent") String userAgent, String fileName){
		
		// 업로드한 파일 경로 가져오기
		Resource resource = new FileSystemResource("C:\\upload\\" + fileName);
		
		// 업로드한 파일이 있는지 없는지 체크
		if(resource.exists() == false) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);// 없으면 not_found 상태 리턴
		}
		
		// 파일이름 가져오기
		String resourceName = resource.getFilename();
		
		// UUID 제거
		String resourceOriginalName = resourceName.substring(resourceName.indexOf("_")+1); 
		
		HttpHeaders headers = new HttpHeaders();
		
		try {
			String downloadName = null;
			
			// 브라우저별 인코딩 처리
			if(userAgent.contains("Trident")) {
				log.info("IE browser");
				
				downloadName = URLEncoder.encode(resourceOriginalName,"UTF-8").replaceAll("\\", " ");
			}
			else if(userAgent.contains("Edge browser")) {
				log.info("Edge browser");
				
				downloadName = URLEncoder.encode(resourceOriginalName,"UTF-8");
			}
			else {
				log.info("Chrome browser");
				
				downloadName = new String(resourceOriginalName.getBytes("UTF-8"),"ISO-8859-1");
			}
			
			log.info("downloadName" + downloadName);
			
			// 헤더 설정
			// Content-Disposition을 통해 파일 이름 문자열 처리할 때 한글 깨지는 문제 막기 위해 사용
			// filename에 다운로드할 파일 설정
			headers.add("Content-Disposition", "attachment; filename=" + downloadName);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
	}
	
	@PostMapping("/deleteFile")
	@ResponseBody
	public ResponseEntity<String> deleteFile(String fileName, String type){
		
		log.info("deleteFile : " + fileName);
		
		File file;
		
		try {
			file = new File("C:\\upload\\" + URLDecoder.decode(fileName,"UTF-8"));
			
			file.delete();
			
			if(type.equals("image")) {
				String largeFileName = file.getAbsolutePath().replace("s_","");
				log.info("largeFileName" + largeFileName);
				file = new File(largeFileName);
				file.delete();
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>("deleted", HttpStatus.OK);
	}
}
