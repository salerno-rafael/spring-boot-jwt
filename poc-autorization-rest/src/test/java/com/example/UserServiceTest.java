package com.example;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
		ResponseEntity<String> responseEntity = restTemplate.postForEntity("/login", new User("user1", "1"), String.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(tokenTest, responseEntity.getBody());
		assertEquals(tokenTest, responseEntity.getHeaders().get("X-Auth-Token").get(0));
	}

	@Test
	public void getUserTestFail() {
		ResponseEntity<User> responseEntity = restTemplate.getForEntity("/user", User.class, tokenTest);
		assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
	}

	@Test
	public void getUserTestSuccess() {
		ResponseEntity<User> responseEntity = restTemplate.exchange("/user", HttpMethod.GET,  setHeader(tokenTest), User.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(responseEntity.getBody().getUser(), "user1");
	}

	
	@Test
	public void getUserTestInvalidUser() {
		ResponseEntity<String> responsToken = restTemplate.postForEntity("/login", new User("xyz", "1111"), String.class);
		ResponseEntity<User> responseEntity = restTemplate.exchange("/user", HttpMethod.GET,  setHeader(responsToken.getBody()), User.class);

		assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
	}

	@Test
	public void validationTokenTest() {

		ResponseEntity<Boolean> responseEntity = restTemplate.postForEntity("/validation/token",setHeader(tokenTest),Boolean.class);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertTrue(responseEntity.getBody());
	}

	@Test
	public void listUsersTestSuccess() {
		ResponseEntity<User[]> responseEntity =   restTemplate.postForEntity("/users",setHeader(tokenTest),User[].class);

		List<User> users = Arrays.asList(responseEntity.getBody());

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertEquals(users.size(),3);
		assertEquals(tokenTest, responseEntity.getHeaders().get("X-Auth-Token").get(0));
	}

	@Test
	public void listUsersTestFail() {
		ResponseEntity<Object> responseEntity =   restTemplate.postForEntity("/users",setHeader("11313123"),Object.class);

		assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
	}
	
	private HttpEntity<String> setHeader(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("X-Auth-Token",token);
		HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
		return entity;
	}

}
