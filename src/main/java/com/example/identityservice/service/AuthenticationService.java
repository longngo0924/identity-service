package com.example.identityservice.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.identityservice.dto.request.AuthenticationRequest;
import com.example.identityservice.dto.request.IntrospectRequest;
import com.example.identityservice.dto.request.LogoutRequest;
import com.example.identityservice.dto.request.RefreshRequest;
import com.example.identityservice.dto.response.AuthenticationResponse;
import com.example.identityservice.dto.response.IntrospectResponse;
import com.example.identityservice.dto.response.LogoutResponse;
import com.example.identityservice.entity.InvalidToken;
import com.example.identityservice.entity.User;
import com.example.identityservice.exception.AppException;
import com.example.identityservice.exception.ErrorCode;
import com.example.identityservice.repository.InvalidTokenRepository;
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

	UserRepository userRepository;

	InvalidTokenRepository invalidTokenRepository;

	@NonFinal
	@Value("${jwt.signing-key}")
	String jwtSigningKey;

	@NonFinal
	@Value("${jwt.issuer}")
	String jwtIssuer;

	@NonFinal
	@Value("${jwt.expire-time}")
	String jwtExpireTime;

	@NonFinal
	@Value("${jwt.refreshable-time}")
	String jwtRefreshableTime;

	public AuthenticationResponse authenticateUser(AuthenticationRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

		boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

		if (!authenticated)
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		String accessToken = generateToken(user, false);
		String refreshToken = generateToken(user, true);

		return AuthenticationResponse.builder().authenticated(true).access(accessToken).refresh(refreshToken).build();
	}

	private String generateToken(User user, boolean isRefresh) {

		JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

		int expireTime = Integer.parseInt(jwtExpireTime);
		int refreshableTime = Integer.parseInt(jwtRefreshableTime);

		Date expiryTimeEpochMilli = new Date(Instant.now().plus(expireTime, ChronoUnit.HOURS).toEpochMilli());
		Date refreshableTimeEpochMilli = new Date(Instant.now().plus(refreshableTime, ChronoUnit.HOURS).toEpochMilli());

		String scopes = isRefresh ? "REFRESH_TOKEN" : buildScope(user);

		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(user.getUsername()).issuer(jwtIssuer)
				.issueTime(new Date()).claim("scope", scopes).jwtID(UUID.randomUUID().toString())
				.expirationTime(isRefresh ? refreshableTimeEpochMilli : expiryTimeEpochMilli).build();

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

	private String buildScope(User user) {
		StringJoiner stringJoiner = new StringJoiner(" ");

		if (!CollectionUtils.isEmpty(user.getRoles())) {
			user.getRoles().forEach(role -> {
				stringJoiner.add("ROLE_" + role.getName());
				if (!CollectionUtils.isEmpty(role.getPermissions())) {
					role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
				}
			});
		}

		return stringJoiner.toString();
	}

	public IntrospectResponse introspectToken(IntrospectRequest request) throws JOSEException, ParseException {
		String token = request.getToken();

		boolean isValid = false;
		try {
			isValid = verifyToken(token);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return IntrospectResponse.builder().isValid(isValid).build();
	}

	public boolean verifyToken(String token) throws JOSEException, ParseException {
		JWSVerifier jwsVerifier = new MACVerifier(jwtSigningKey.getBytes());
		SignedJWT signedJWT = SignedJWT.parse(token);
		boolean isValid = signedJWT.verify(jwsVerifier);

		var expireTime = signedJWT.getJWTClaimsSet().getExpirationTime();

		boolean isNotExpired = expireTime.after(new Date());

		boolean isLogout = invalidTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID());

		return isValid && isNotExpired && !isLogout;
	}

	public LogoutResponse logout(LogoutRequest request) throws JOSEException, ParseException {
		if (!verifyToken(request.getToken()))
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		SignedJWT signedJWT = SignedJWT.parse(request.getToken());
		String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
		Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
		InvalidToken invalidToken = InvalidToken.builder().id(jwtId).expiryTime(expiryTime).build();
		invalidTokenRepository.save(invalidToken);

		return LogoutResponse.builder().logout(true).build();
	}

	public AuthenticationResponse refresh(RefreshRequest request) throws JOSEException, ParseException {
		boolean isValid = verifyToken(request.getToken());
		if (!isValid)
			throw new AppException(ErrorCode.UNAUTHENTICATED);

		SignedJWT signedJWT = SignedJWT.parse(request.getToken());

		String scopes = signedJWT.getJWTClaimsSet().getClaim("scope").toString();

		if (!"REFRESH_TOKEN".equals(scopes))
			throw new AppException(ErrorCode.ACCESS_DENINED);

		String username = signedJWT.getJWTClaimsSet().getSubject();
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

		String accessToken = generateToken(user, false);
		String refreshToken = generateToken(user, true);

		logout(LogoutRequest.builder().token(request.getToken()).build());

		return AuthenticationResponse.builder().authenticated(true).access(accessToken).refresh(refreshToken).build();
	}
}
