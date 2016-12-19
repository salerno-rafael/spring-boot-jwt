package com.example;

import java.security.Key;

import javax.crypto.SecretKey;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

@SpringBootApplication
public class PocAutorizationApplication {

	private final String key =  "teste";

	public static void main(String[] args) {
		// SpringApplication.run(PocAutorizationApplication.class, args);
		PocAutorizationApplication poc = new PocAutorizationApplication();
		poc.desCompactJws(poc.compactJws(new User("user", "password").toString()));
		
	}

	public String compactJws(String user) {
		String compactJws = Jwts.builder()
				.setSubject(user)
				.signWith(SignatureAlgorithm.HS512, key)
				.compact();

		System.out.println("compactJws = "+compactJws);

		return compactJws;
	}

	public String desCompactJws(String compactJws) {
		String desCompactJws = Jwts.parser()
		.setSigningKey(key)
		.parseClaimsJws(compactJws)
		.getBody()
		.getSubject();
		
		System.out.println("desCompactJws = "+ desCompactJws);
		
		return desCompactJws;
	}

}
