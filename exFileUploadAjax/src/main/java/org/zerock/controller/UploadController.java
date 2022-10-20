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
	// '��/��/��' ������ ����
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
		// ���޵� ������ MultipartFile Ÿ�� ������ ����.
		// MultipartFile Ÿ�� �޼ҵ��
		// - getName() : �Ķ������ �̸�, input �±��� �̸�
		// - getOrigianlFileName() : ���ε� �Ǵ� ���� �̸�
		// - isEmpty() : ������ �������� �ʴ� ��� true
		// - getSize() : ���ε�Ǵ� ������ ũ��
		// - getBytes() : byte[]�� ���� ������ ��ȯ
		// - getInputStream() : ���� �����Ϳ� ����� InputStream�� ��ȯ
		// - transferTo(File file) : ������ ����
		

		List<AttachFileDTO> list = new ArrayList<>(); // �������� ������ ����Ʈ ��ü ����
		String uploadFolder = "C:\\upload";
		
		// ���� ����
		String uploadFolderPath = getFolder();
		File uploadPath = new File(uploadFolder,uploadFolderPath);
		
		if(uploadPath.exists() == false) {
			uploadPath.mkdirs();
		}
		
		// ���� üũ
		for(MultipartFile multipartFile : uploadFile) {
			AttachFileDTO attachDTO = new AttachFileDTO(); // �ش� ���ε� ���� �����͸� ������ ��ü ����
			
			// ���� �̸� ����
			String uploadFileName = multipartFile.getOriginalFilename();
			uploadFileName = uploadFileName.substring(uploadFileName.lastIndexOf("\\")+1);
			log.info("only file name : " + uploadFileName);
			
			attachDTO.setFileName(uploadFileName); // ��ü�� ���ε� ���� �̸� ����
			
			UUID uuid = UUID.randomUUID();
			uploadFileName = uuid.toString() + "_" + uploadFileName;
			
			
			try {
				// ���� ����
				File saveFile = new File(uploadPath, uploadFileName);
				multipartFile.transferTo(saveFile);
				
				attachDTO.setUuid(uuid.toString()); // ��ü�� UUID ����
				attachDTO.setUploadPath(uploadFolderPath); // ��ü�� ���ε� ��� ����
				
				if(checkImageType(saveFile)) {
					attachDTO.setImage(true); // ��ü�� �̹��� ���� ����
					
					FileOutputStream thumbnail = new FileOutputStream(new File(uploadPath, "s_" + uploadFileName));
					
					Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnail, 100, 100);
					
					thumbnail.close();
				}
				
				list.add(attachDTO); // �������� ������ ����Ʈ ��ü�� �߰�
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new ResponseEntity<>(list, HttpStatus.OK); // �������� ����Ʈ ��ü ����
	}
	
	// ���ڿ��� ������ ��ΰ� ���Ե� fileName�� �Ķ���ͷ� �ް� byte �迭�� ����
	@GetMapping("/display")
	@ResponseBody
	public ResponseEntity<byte[]> getFile(String fileName){
		log.info("fileName : " + fileName);
		
		File file = new File("c:\\upload\\"+fileName);
		
		log.info("file : " + file);
		
		ResponseEntity<byte[]> result = null;
		
		try {
			HttpHeaders header = new HttpHeaders();
			
			// byte �迭�� �̹��� ������ ������ �� �������� �����ִ� MIME Ÿ���� ������ ������ ���� �޶���.
			// probeContentType �޼ҵ带 �̿��� ������ MIMEŸ�� �����͸� Http ��� �޽����� ������ �� �ֵ��� ó��
			header.add("Content-Type", Files.probeContentType(file.toPath()));
			result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	// ������ �ٿ� �ޱ� ���� MIME Ÿ���� application/octet-stream���� ����
	// ����ڰ� ������� �������� @RequestHeader�� ���� HTTP ��� �޽����� User-Agent �����͸� ������
	@GetMapping(value = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@RequestHeader("User-Agent") String userAgent, String fileName){
		
		// ���ε��� ���� ��� ��������
		Resource resource = new FileSystemResource("C:\\upload\\" + fileName);
		
		// ���ε��� ������ �ִ��� ������ üũ
		if(resource.exists() == false) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);// ������ not_found ���� ����
		}
		
		// �����̸� ��������
		String resourceName = resource.getFilename();
		
		// UUID ����
		String resourceOriginalName = resourceName.substring(resourceName.indexOf("_")+1); 
		
		HttpHeaders headers = new HttpHeaders();
		
		try {
			String downloadName = null;
			
			// �������� ���ڵ� ó��
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
			
			// ��� ����
			// Content-Disposition�� ���� ���� �̸� ���ڿ� ó���� �� �ѱ� ������ ���� ���� ���� ���
			// filename�� �ٿ�ε��� ���� ����
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
