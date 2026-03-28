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
 * 中式美食评论表
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("recipe_comment")
public class ChineseRecipeCommentEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public ChineseRecipeCommentEntity() {
		
	}
	
	public ChineseRecipeCommentEntity(T t) {
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
	 * 菜谱id（语义化命名，映射 recipe_comment.recipe_id）
	 */
	@TableField("recipe_id")
	private Long recipeId;
	
	/**
	 * 用户id（语义化命名，映射 recipe_comment.user_id）
	 */
	@TableField("user_id")
	private Long userId;
	
	/**
	 * 评论内容
	 */
					
	private String content;
	
	/**
	 * 回复内容
	 */
					
	private String reply;

	/**
	 * 来源类型（语义化命名，映射 recipe_comment.source_type）
	 */
	@TableField("source_type")
	private String sourceType;
	
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	@TableField("created_at")
	private Date createdAt;

	/**
	 * 兼容字段：评论发布者昵称（不存库，运行时/接口组装使用）
	 */
	@TableField(exist = false)
	private String nickname;

	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 设置：关联表id
	 */
	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}
	/**
	 * 获取：关联表id
	 */
	public Long getRecipeId() {
		return recipeId;
	}
	public Long getResourceId() {
		return recipeId;
	}
	public void setResourceId(Long resourceId) {
		this.recipeId = resourceId;
	}
	/**
	 * 设置：用户id
	 */
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	/**
	 * 获取：用户id
	 */
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：评论内容
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 获取：评论内容
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置：回复内容
	 */
	public void setReply(String reply) {
		this.reply = reply;
	}
	/**
	 * 获取：回复内容
	 */
	public String getReply() {
		return reply;
	}

	/**
	 * 获取：来源类型
	 */
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * 设置：来源类型
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	/**
	 * 兼容旧字段名：addtime -> createdAt
	 */
	public Date getAddtime() {
		return createdAt;
	}
	public void setAddtime(Date addtime) {
		this.createdAt = addtime;
	}

	/**
	 * 兼容旧字段名：refid -> recipeId
	 */
	public Long getRefid() {
		return recipeId;
	}
	public void setRefid(Long refid) {
		this.recipeId = refid;
	}

	/**
	 * 兼容旧字段名：userid -> userId
	 */
	public Long getUserid() {
		return userId;
	}
	public void setUserid(Long userid) {
		this.userId = userid;
	}

	/**
	 * 兼容旧字段名：recipetype -> sourceType
	 */
	public String getRecipetype() {
		return sourceType;
	}
	public void setRecipetype(String recipetype) {
		this.sourceType = recipetype;
	}

}

