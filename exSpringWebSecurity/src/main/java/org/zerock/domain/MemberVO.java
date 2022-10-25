package org.zerock.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;

// 여러 개의 사용자 권한을 가질 수 있게 설계

@Data
public class MemberVO {
	private String userid;
	private String userpw;
	private String userName;
	private String enabled;
	
	private Date regDate;
	private Date updateDate;
	private List<AuthVO> authList;
}
