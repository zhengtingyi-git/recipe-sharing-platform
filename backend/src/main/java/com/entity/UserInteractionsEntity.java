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
import com.fasterxml.jackson.annotation.JsonAlias;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 收藏表
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("user_interactions")
public class UserInteractionsEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public UserInteractionsEntity() {
		
	}
	
	public UserInteractionsEntity(T t) {
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
	 * 用户id
	 */
	@JsonAlias("userId")
	@TableField("user_id")
	private Long userid;
	
	/**
	 * 被交互资源id（forum_post / recipe 等多态资源）
	 */
	@JsonAlias("resourceId")
	@TableField("resource_id")
	private Long refid;
	
	/**
	 * 兼容返回字段：目标资源类型（数据库已移除 tablename）
	 */
	@TableField(exist = false)
	private String tablename;
	
	/**
	 * 兼容返回字段：目标资源名称（数据库已移除 name）
	 */
	@TableField(exist = false)
	private String name;
	
	/**
	 * 兼容返回字段：目标资源封面（数据库已移除 picture）
	 */
	@TableField(exist = false)
	private String picture;
	
	/**
	 * 类型(1:收藏,0:赞；历史数据可能为 21)
	 */
	@TableField("interaction_type")
	private String type;
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	@JsonAlias("createdAt")
	@TableField("created_at")
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Date getCreatedAt() {
		return addtime;
	}
	public void setCreatedAt(Date createdAt) {
		this.addtime = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：用户id
	 */
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	/**
	 * 获取：用户id
	 */
	public Long getUserid() {
		return userid;
	}
	public void setUserId(Long userId) {
		this.userid = userId;
	}
	public Long getUserId() {
		return userid;
	}
	/**
	 * 设置：收藏id
	 */
	public void setRefid(Long refid) {
		this.refid = refid;
	}
	/**
	 * 获取：收藏id
	 */
	public Long getRefid() {
		return refid;
	}
	public void setResourceId(Long resourceId) {
		this.refid = resourceId;
	}
	public Long getResourceId() {
		return refid;
	}
	/**
	 * 设置：表名
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	/**
	 * 获取：表名
	 */
	public String getTablename() {
		return tablename;
	}
	/**
	 * 设置：收藏名称
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * 获取：收藏名称
	 */
	public String getName() {
		return name;
	}
	/**
	 * 设置：收藏图片
	 */
	public void setPicture(String picture) {
		this.picture = picture;
	}
	/**
	 * 获取：收藏图片
	 */
	public String getPicture() {
		return picture;
	}
	/**
	 * 设置：类型(1:收藏,0:赞)
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * 获取：类型(1:收藏,0:赞)
	 */
	public String getType() {
		return type;
	}
}
