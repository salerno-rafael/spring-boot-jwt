package com.example;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

	@Autowired
    private TestRestTemplate restTemplate;

	private String tokenTest="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ7XCJ1c2VyXCI6XCJ1c2VyMVwiLFwicGFzc1wiOlwiMVwifSJ9.Ee0SHfJzTk80KfHzoXCSk5h8yMJgldpJs-QfT1u4eocpTBA9qNypIaKWOtNTKO1S2MJxn-Dcw3ypRDTziDnjGw";
	
	@Test
    public void loginTest() {
        ResponseEntity<String> responseEntity = 
        	restTemplate.postForEntity("/login", new User("user1", "1"), String.class);
        String token = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(tokenTest, token);
    }
	
	@Test
    public void getUserTest() {
		 ResponseEntity<User> responseEntity = 
		        	restTemplate.getForEntity("/{token}/user", User.class, tokenTest);
		        	
		        User user = responseEntity.getBody();
		        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		        assertEquals(user.getUser(), "user1");
	}

	@Test
    public void validationTest() {
		 ResponseEntity<Boolean> responseEntity = 
		        	restTemplate.getForEntity("/{token}/validation", Boolean.class, tokenTest);
		        	
		 boolean validation = responseEntity.getBody();
		        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		        assertTrue(validation);
	}
}
