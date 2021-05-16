package com.ingemark.webshop;

import static org.assertj.core.api.Assertions.assertThat;

import com.ingemark.webshop.controller.ProductController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("/application-integration-test.properties")
class WebshopApplicationTest {

	@Autowired
	private ProductController controller;

	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

}
