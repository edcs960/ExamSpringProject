package org.zerock.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.log4j.Log4j2;

// 패스워드 인코딩 관련
// 패스워드는 무조건 단방향으로 암호화
// PasswordEncoder 구현 클래스에 다양한 암호화 메소드를 지원
// - BcryptPasswordEncoder
// - Argon2PasswordEncoder
// - Pbkdf2PasswordEncoder
// - ScryptPasswordEncoder

// PasswordEncoder 구현 클래스에서 지원하는 메소드를 사용하면 해당 패스워드 인코더 핸들러를 안만들어도됨.
@Log4j2
public class CustomNoOpPasswordEncoder implements PasswordEncoder{
	// 암호화할 때 사용
	@Override
	public String encode(CharSequence rawPassword) { 
		log.warn("before encode : " + rawPassword);
		return rawPassword.toString();
	}

	// 사용자에게서 입력받은 패스워드를 비교
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) { 
		log.warn("matches : " + rawPassword + " : " + encodedPassword);
		return rawPassword.toString().equals(encodedPassword);
	}
}
