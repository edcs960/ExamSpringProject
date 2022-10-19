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
		// ���޵� ������ MultipartFile Ÿ�� ������ ����.
		// MultipartFile Ÿ�� �޼ҵ��
		// - getName() : �Ķ������ �̸�, input �±��� �̸�
		// - getOrigianlFileName() : ���ε� �Ǵ� ���� �̸�
		// - isEmpty() : ������ �������� �ʴ� ��� true
		// - getSize() : ���ε�Ǵ� ������ ũ��
		// - getBytes() : byte[]�� ���� ������ ��ȯ
		// - getInputStream() : ���� �����Ϳ� ����� InputStream�� ��ȯ
		// - transferTo(File file) : ������ ����
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
