package com.yashconsulting.eams;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
		"spring.jpa.properties.hibernate.jdbc.time_zone=UTC"
})
class EnterpriseAssetManagementApplicationTests {

	@Test
	void contextLoads() {
	}
}
