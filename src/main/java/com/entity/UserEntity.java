package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;


/**
 * 用户
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("user")
public class UserEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public UserEntity() {
		
	}
	
	public UserEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主键id
	 */
	@TableId
	private Long id;
	/**
	 * 用户账号
	 */
	private String username;
	
	/**
	 * 用户昵称
	 */
	private String nickname;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 性别
	 */
	private String gender;
	
	/**
	 * 联系方式
	 */
					
	private String phone;
	
	/**
	 * 头像
	 */
	private String avatar;
	
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	@TableField("created_at")
	private Date createdAt;

	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：用户账号
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取：用户账号
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置：用户昵称
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * 获取：用户昵称
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * 设置：密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * 获取：密码
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * 设置：性别
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}
	/**
	 * 获取：性别
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * 设置：联系方式
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * 获取：联系方式
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * 设置：头像
	 */
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	/**
	 * 获取：头像
	 */
	public String getAvatar() {
		return avatar;
	}

	public Date getAddtime() {
		return createdAt;
	}
	public void setAddtime(Date addtime) {
		this.createdAt = addtime;
	}

	public void setYonghuzhanghao(String yonghuzhanghao) {
		this.username = yonghuzhanghao;
	}
	public String getYonghuzhanghao() {
		return username;
	}
	public void setYonghuxingming(String yonghuxingming) {
		this.nickname = yonghuxingming;
	}
	public String getYonghuxingming() {
		return nickname;
	}
	public void setMima(String mima) {
		this.password = mima;
	}
	public String getMima() {
		return password;
	}
	public void setXingbie(String xingbie) {
		this.gender = xingbie;
	}
	public String getXingbie() {
		return gender;
	}
	public void setTouxiang(String touxiang) {
		this.avatar = touxiang;
	}
	public String getTouxiang() {
		return avatar;
	}

}
