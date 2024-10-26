package com.example.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

	PasswordEncoder passwordEncoder;

	UserRepository userRepository;

	@NonFinal
	@Value("${jwt.signing-key}")
	String jwtSigningKey;

	@NonFinal
	@Value("${jwt.issuer}")
	String jwtIssuer;

	@NonFinal
	@Value("${jwt.expire-time}")
	String jwtExpireTime;

	public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!authenticated)
			throw new AppException(ErrorCode.UNAUTHORIZED);

		String token = generateToken(request.getUsername());

		return AuthenticationResponse.builder().authenticated(true).token(token).build();
	}

	private String generateToken(String username) {

		JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

		int expireTime = Integer.parseInt(jwtExpireTime);

		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(username).issuer(jwtIssuer).issueTime(new Date())
				.expirationTime(new Date(Instant.now().plus(expireTime, ChronoUnit.HOURS).toEpochMilli())).build();

		Payload payload = new Payload(claimsSet.toJSONObject());

		JWSObject jwsObject = new JWSObject(header, payload);

		try {
			jwsObject.sign(new MACSigner(jwtSigningKey.getBytes()));
			return jwsObject.serialize();
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
		}
	}

	public IntrospectResponse introspectToken(IntrospectRequest request) throws JOSEException, ParseException {
		String token = request.getToken();

		JWSVerifier jwsVerifier = new MACVerifier(jwtSigningKey.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		boolean isValid = signedJWT.verify(jwsVerifier);

		var expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		boolean isExpired = expireTime.after(new Date());

		return IntrospectResponse.builder().isValid(isValid && isExpired).build();
	}
}
