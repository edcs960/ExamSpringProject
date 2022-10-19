package org.zerock.controller;

import java.io.File;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class UploadController {
	@GetMapping("/uploadForm")
	public void uploadForm() {
		log.info("upload form");
	}
	
	@PostMapping("/uploadFormAction")
	public void uploadFormPost(MultipartFile[] uploadFile, Model model) {
		// 전달된 파일은 MultipartFile 타입 변수에 저장.
		// MultipartFile 타입 메소드들
		// - getName() : 파라미터의 이름, input 태그의 이름
		// - getOrigianlFileName() : 업로드 되는 파일 이름
		// - isEmpty() : 파일이 존재하지 않는 경우 true
		// - getSize() : 업로드되는 파일의 크기
		// - getBytes() : byte[]로 파일 데이터 변환
		// - getInputStream() : 파일 데이터와 연결된 InputStream을 반환
		// - transferTo(File file) : 파일의 저장
		String uploadFolder = "C:\\upload";
		
		for(MultipartFile multipartFile : uploadFile) {
			log.info("------------------------------");
			log.info("Upload File Name : " + multipartFile.getOriginalFilename());
			log.info("Upload File Size : " + multipartFile.getSize());
			
			File saveFile = new File(uploadFolder, multipartFile.getOriginalFilename());
			
			try {
				multipartFile.transferTo(saveFile);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
}
