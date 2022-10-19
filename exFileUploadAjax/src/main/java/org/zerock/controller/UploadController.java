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
	@GetMapping("/uploadAjax")
	public void uploadAjax() {
		log.info("upload ajax");
	}
	
	@PostMapping("/uploadAjaxAction")
	public void uploadAjaxPost(MultipartFile[] uploadFile) {
		// ���޵� ������ MultipartFile Ÿ�� ������ ����.
		// MultipartFile Ÿ�� �޼ҵ��
		// - getName() : �Ķ������ �̸�, input �±��� �̸�
		// - getOrigianlFileName() : ���ε� �Ǵ� ���� �̸�
		// - isEmpty() : ������ �������� �ʴ� ��� true
		// - getSize() : ���ε�Ǵ� ������ ũ��
		// - getBytes() : byte[]�� ���� ������ ��ȯ
		// - getInputStream() : ���� �����Ϳ� ����� InputStream�� ��ȯ
		// - transferTo(File file) : ������ ����
		log.info("update ajax post............");
		
		String uploadFolder = "C:\\upload";
		
		for(MultipartFile multipartFile : uploadFile) {
			log.info("------------------------------");
			log.info("Upload File Name : " + multipartFile.getOriginalFilename());
			log.info("Upload File Size : " + multipartFile.getSize());
			
			String uploadFileName = multipartFile.getOriginalFilename();
			
			uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1);
			
			log.info("only file name : " + uploadFileName);
			
			File saveFile = new File(uploadFolder, uploadFileName);
			
			try {
				multipartFile.transferTo(saveFile);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
}
