package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@SuppressWarnings("serial")
	private List<User> users = new ArrayList<User>() {
		{
			add(new User("user1", "1"));
			add(new User("user2", "2"));
			add(new User("user3", "3"));
		}
	};
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(HttpServletResponse response,@RequestBody User user) throws IOException {
		System.out.println(user);
		
		String token = authentication.compactJws(new ObjectMapper().writeValueAsString(user));
		response.setHeader("X-Auth-Token", token);
		
		return token ;
	}

	@RequestMapping(value = "/{token}/user", method = RequestMethod.GET)
	public String user(@PathVariable String token) throws IOException {
		System.out.println(token);
		return authentication.desCompactJws(token);
	}

	@RequestMapping(value = "/{token}/validation", method = RequestMethod.GET)
	public boolean validation(@PathVariable String token) throws IOException {
		return validationToken(token);
	}
	
	@RequestMapping(value = "/users", method = RequestMethod.POST)
	public List<User> listUsers(HttpServletRequest request,HttpServletResponse response) throws Exception {
		if(getToken(request.getHeader("X-Auth-Token"))){
			response.setHeader("X-Auth-Token", request.getHeader("X-Auth-Token"));
			return users;
		}
		return new ArrayList<User>();		
	}
	
	
	private boolean getToken(String token) throws Exception{
		if(token != null && !token.isEmpty() && validationToken(token))
			return true; 
	  
		return false;
	}
	
	private boolean validationToken(String token) throws JsonParseException, JsonMappingException, IOException {
		String user = authentication.desCompactJws(token);
		
		if(user.equals("Invalid Token")) return false;
		
		User out =  new ObjectMapper().readValue(user, User.class);
		Optional<User> userValid = users.stream().
				filter(x-> x.getUser().equals(out.getUser())&& x.getPass().equals(out.getPass()))
				.findFirst();
		return userValid.isPresent();
	}
	
}
