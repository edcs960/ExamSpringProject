package org.zerock.domain;

import lombok.Data;

@Data
public class BoardAttachVO {
	// 파일 관련 데이터
	private String uuid;
	private String uploadPath;
	private String fileName;
	private boolean fileType;
	
	// 게시글 번호
	private Long bno;
}
