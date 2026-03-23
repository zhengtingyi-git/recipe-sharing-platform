package com.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录态信息（由 JWT 解析得到；不再映射数据库 token 表）。
 */
public class TokenEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long userid;

	private String username;

	private String tablename;

	private String role;

	private String token;

	private Date expiratedtime;

	private Date addtime;

	public Long getUserid() {
		return userid;
	}

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getToken() {
		return token;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getExpiratedtime() {
		return expiratedtime;
	}

	public void setExpiratedtime(Date expiratedtime) {
		this.expiratedtime = expiratedtime;
	}

	public Date getAddtime() {
		return addtime;
	}

	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public TokenEntity(Long userid, String username, String tablename, String role, String token, Date expiratedtime) {
		this.userid = userid;
		this.username = username;
		this.tablename = tablename;
		this.role = role;
		this.token = token;
		this.expiratedtime = expiratedtime;
	}

	public TokenEntity() {
	}
}
