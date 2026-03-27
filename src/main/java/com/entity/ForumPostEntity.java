package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.lang.reflect.InvocationTargetException;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("forum_post")
public class ForumPostEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public ForumPostEntity() {
		
	}
	
	public ForumPostEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * id
	 */
	@TableId
	private Long id;
	/**
	 * user_id (normalized)
	 */
	@TableField("user_id")
	private Long userId;

	/**
	 * 保持旧前端字段名兼容：getUserid/setUserid 仅代理到 userId
	 * （不再为旧列 `userid` 保留任何可用于 SQL 的字段映射）。
	 */
	/**
	 * 
	 */
	@TableField(exist = false)
	private String yonghuzhanghao;
	/**
	 * 
	 */
	@TableField(exist = false)
	private String yonghuxingming;
	/**
	 * 
	 */
	@TableField(exist = false)
	private String touxiang;
	/**
	 * 
	 */
					
	private String title;
	
	/**
	 * ?	 */
					
	private String introduction;
	
	/**
	 * 
	 */
					
	private String picture;
	
	/**
	 * 
	 */
					
	private String content;

	/**
	 * ?	 */
	@TableField(exist = false)
	private Integer thumbsupnum;
	
	
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
	 * user_id setter
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	/**
	 * user_id getter
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * ?	 */
	public void setYonghuzhanghao(String yonghuzhanghao) {
		this.yonghuzhanghao = yonghuzhanghao;
	}
	/**
	 * ?	 */
	public String getYonghuzhanghao() {
		return yonghuzhanghao;
	}
	/**
	 * ?	 */
	public void setYonghuxingming(String yonghuxingming) {
		this.yonghuxingming = yonghuxingming;
	}
	/**
	 * ?	 */
	public String getYonghuxingming() {
		return yonghuxingming;
	}
	/**
	 * ?	 */
	public void setTouxiang(String touxiang) {
		this.touxiang = touxiang;
	}
	/**
	 * ?	 */
	public String getTouxiang() {
		return touxiang;
	}
	/**
	 * ?	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * ?	 */
	public String getTitle() {
		return title;
	}
	/**
	 * ?	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
	/**
	 * ?	 */
	public String getIntroduction() {
		return introduction;
	}
	/**
	 * ?	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}
	/**
	 * ?	 */
	public String getPicture() {
		return picture;
	}
	/**
	 * ?	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * ?	 */
	public String getContent() {
		return content;
	}

	/**
	 * 
	 */
	public void setThumbsupnum(Integer thumbsupnum) {
		this.thumbsupnum = thumbsupnum;
	}

	/**
	 * 
	 */
	public Integer getThumbsupnum() {
		return thumbsupnum;
	}

}