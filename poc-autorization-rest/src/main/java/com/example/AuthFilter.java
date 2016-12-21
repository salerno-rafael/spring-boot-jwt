package com.example;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuthFilter implements Filter{

	@Autowired
	private Authentication authentication;

	@Autowired
	private UserBuilder userBuilder;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		if(request.getRequestURI().contains("login")) {
			chain.doFilter(request, response);
			return;
		}
		try {
			
			if(getToken(request.getHeader("X-Auth-Token"))){
				response.setHeader("X-Auth-Token", request.getHeader("X-Auth-Token"));
				chain.doFilter(request, response);
				return;
			}
			
			response.sendError(HttpStatus.UNAUTHORIZED.value());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpStatus.UNAUTHORIZED.value());
		}
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

		Optional<User> userValid = userBuilder.users.stream().
				filter(x-> x.getUser().equals(out.getUser())&& x.getPass().equals(out.getPass()))
				.findFirst();

		return userValid.isPresent();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}



	@Override
	public void destroy() {
	}

}
