
package com.service;

import com.entity.TokenEntity;

/**
 * 登录令牌：使用 JWT，不再读写 token 数据表。
 */
public interface TokenService {

	String generateToken(Long userid, String username, String tableName, String role);

	TokenEntity getTokenEntity(String token);
}
