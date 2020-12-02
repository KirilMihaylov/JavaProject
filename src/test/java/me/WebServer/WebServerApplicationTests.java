package me.WebServer;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import me.web_server.IUserDao;
import me.web_server.WebServerApplication;

@SpringBootTest(classes = { WebServerApplication.class })
class WebServerApplicationTests {
	@Test
	void contextLoads() {
		assertNull(null);
	}

	@Bean
	CommandLineRunner demo(IUserDao dao) {
		return (args) -> {
			dao.authenticateAdmin("user", new byte[] { 0 });
		};
	}
}
