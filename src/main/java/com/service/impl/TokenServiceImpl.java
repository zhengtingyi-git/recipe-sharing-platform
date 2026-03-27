
package com.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.entity.TokenEntity;
import com.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * JWT 签发与解析（替代原 token 表存储）。
 */
@Service("tokenService")
public class TokenServiceImpl implements TokenService {

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expire-hours:1}")
	private int expireHours;

	@Override
	public String generateToken(Long userid, String username, String tableName, String role) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.HOUR_OF_DAY, expireHours);
		Date exp = cal.getTime();
		return Jwts.builder()
				.claim("userid", userid)
				.claim("username", username)
				.claim("tablename", tableName)
				.claim("role", role)
				.setIssuedAt(new Date())
				.setExpiration(exp)
				.signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes(StandardCharsets.UTF_8))
				.compact();
	}

	@Override
	public TokenEntity getTokenEntity(String token) {
		if (token == null || token.trim().isEmpty()) {
			return null;
		}
		try {
			Claims claims = Jwts.parser()
					.setSigningKey(jwtSecret.getBytes(StandardCharsets.UTF_8))
					.parseClaimsJws(token.trim())
					.getBody();
			Object uid = claims.get("userid");
			if (uid == null) {
				return null;
			}
			long userId = uid instanceof Number ? ((Number) uid).longValue() : Long.parseLong(uid.toString());
			TokenEntity te = new TokenEntity();
			te.setUserId(userId);
			te.setUsername(claims.get("username", String.class));
			te.setTablename(claims.get("tablename", String.class));
			te.setRole(claims.get("role", String.class));
			te.setExpiratedtime(claims.getExpiration());
			return te;
		} catch (Exception e) {
			return null;
		}
	}
}
