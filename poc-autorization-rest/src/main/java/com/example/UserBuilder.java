package com.example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class UserBuilder {
	
	@SuppressWarnings("serial")
	public List<User> users = new ArrayList<User>() {
		{
			add(new User("user1", "1"));
			add(new User("user2", "2"));
			add(new User("user3", "3"));
		}
	};
}
