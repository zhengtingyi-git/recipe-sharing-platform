package com.entity.vo;

import java.io.Serializable;
 

/**
 * 用户
 * 手机端接口返回实体辅助类 
 * （主要作用去除一些不必要的字段）
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public class UserVO implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
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
