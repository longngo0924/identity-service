package com.example.identityservice.configuration;

import java.text.ParseException;
import java.util.Objects;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import com.example.identityservice.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJwtDecoder implements JwtDecoder {

	@Autowired
	private AuthenticationService authenticationService;

	private NimbusJwtDecoder nimbusJwtDecoder = null;

	@Value("${jwt.signing-key}")
	private String jwtSigningKey;

	@Override
	public Jwt decode(String token) throws JwtException {
		boolean isValid = false;
		try {
			isValid = authenticationService.verifyToken(token);
		} catch (JOSEException | ParseException e) {
			log.error(e.getMessage());
			throw new JwtException(e.getMessage());
		}

		if (!isValid)
			throw new JwtException("Invalid token");

		if (Objects.isNull(nimbusJwtDecoder)) {
			SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSigningKey.getBytes(), "HS512");

			this.nimbusJwtDecoder = NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512)
					.build();
		}

		return nimbusJwtDecoder.decode(token);
	}

}
