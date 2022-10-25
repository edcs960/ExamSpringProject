package org.zerock.security;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.extern.log4j.Log4j2;

// �н����� ���ڵ� ����
// �н������ ������ �ܹ������� ��ȣȭ
// PasswordEncoder ���� Ŭ������ �پ��� ��ȣȭ �޼ҵ带 ����
// - BcryptPasswordEncoder
// - Argon2PasswordEncoder
// - Pbkdf2PasswordEncoder
// - ScryptPasswordEncoder

// PasswordEncoder ���� Ŭ�������� �����ϴ� �޼ҵ带 ����ϸ� �ش� �н����� ���ڴ� �ڵ鷯�� �ȸ�����.
@Log4j2
public class CustomNoOpPasswordEncoder implements PasswordEncoder{
	// ��ȣȭ�� �� ���
	@Override
	public String encode(CharSequence rawPassword) { 
		log.warn("before encode : " + rawPassword);
		return rawPassword.toString();
	}

	// ����ڿ��Լ� �Է¹��� �н����带 ��
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) { 
		log.warn("matches : " + rawPassword + " : " + encodedPassword);
		return rawPassword.toString().equals(encodedPassword);
	}
}
