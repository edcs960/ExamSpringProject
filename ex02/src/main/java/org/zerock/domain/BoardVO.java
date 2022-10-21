package org.zerock.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class BoardVO {
	// 게시글 관련 데이터
	private Long bno;
	private String title;
	private String content;
	private String writer;
	private Date regDate;
	private Date updateDate;
	
	// 댓글 개수
	private int replyCnt;
	
	// 파일 업로드 리스트
	private List<BoardAttachVO> attachList;
	
}
