package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RestController
public class UserService {

	@SuppressWarnings("serial")
	private ArrayList<User> users = new ArrayList<User>() {
		{
			add(new User("user1", "1"));
			add(new User("user2", "2"));
			add(new User("user3", "3"));
		}
	};

	private final String key = "teste";

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(@RequestBody User user) throws IOException {
		System.out.println(user);
		ObjectMapper mapper = new ObjectMapper();
		return compactJws(mapper.writeValueAsString(user));
	}

	@RequestMapping(value = "/{token}/user", method = RequestMethod.GET)
	public String user(@PathVariable String token) throws IOException {
		System.out.println(token);
		return desCompactJws(token);
	}

	@RequestMapping(value = "/{token}/validation", method = RequestMethod.GET)
	public boolean validation(@PathVariable String token) throws IOException {
		return validationToken(token);
	}

	private boolean validationToken(String token) throws JsonParseException, JsonMappingException, IOException {
		String user = desCompactJws(token);
		User out =  new ObjectMapper().readValue(user, User.class);
		Optional<User> userValid = users.stream().
				filter(x-> x.getUser().equals(out.getUser())&& x.getPass().equals(out.getPass()))
				.findFirst();
		return userValid.isPresent();
	}

	public String compactJws(String user) {
		String compactJws = Jwts.builder().setSubject(user).signWith(SignatureAlgorithm.HS512, key).compact();

		System.out.println("compactJws = " + compactJws);

		System.out.println(desCompactJws(compactJws));
		return compactJws;
	}

	public String desCompactJws(String compactJws) {
		String desCompactJws = Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject();

		System.out.println("desCompactJws = " + desCompactJws);

		return desCompactJws;
	}
}
