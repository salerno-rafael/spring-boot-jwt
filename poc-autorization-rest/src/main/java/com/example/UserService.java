package com.example;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class UserService {
	
	@Autowired
	private Authentication authentication;
	
	@Autowired
	private UserBuilder userBuilder;
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletResponse response,@RequestBody User user) throws IOException {
		System.out.println(user);
		String token = authentication.compactJws(new ObjectMapper().writeValueAsString(user));
		response.setHeader("X-Auth-Token", token);
		
		return token ;
	}

	@RequestMapping(value = "/user", method = RequestMethod.GET)
	public String user(HttpServletRequest request) throws IOException {
		System.out.println(request.getHeader("X-Auth-Token"));
		return authentication.desCompactJws(request.getHeader("X-Auth-Token"));
	}

	@RequestMapping(value = "/validation/token", method = RequestMethod.POST)
	public boolean validationToken(HttpServletRequest request) throws IOException {
		return true;
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public List<User> listUsers(HttpServletRequest request,HttpServletResponse response) throws Exception {
		return userBuilder.users;		
	}
	
}
