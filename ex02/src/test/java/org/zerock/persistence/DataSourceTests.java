package org.zerock.persistence;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zaxxer.hikari.HikariConfig;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("file:src/main/webapp/WEB-INF/spring/root-context.xml")
@Log4j2
public class DataSourceTests {
	@Setter(onMethod_ = @Autowired)
	private DataSource dataSource;
	
	@Test
	public void testConnection() {
		// 히카리CP에서 사용가능한 connection 객체 하나를 받아옴.
		try {
			Connection con = dataSource.getConnection();
			log.info(con);
		} catch (Exception e) {
			
		}
	}
}
