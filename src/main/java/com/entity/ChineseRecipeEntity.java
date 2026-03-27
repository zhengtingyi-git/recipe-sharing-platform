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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.beanutils.BeanUtils;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.FieldFill;
import com.baomidou.mybatisplus.enums.IdType;


/**
 * 中式美食
 * 数据库通用操作实体类（普通增删改查）
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("recipe")
public class ChineseRecipeEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public ChineseRecipeEntity() {
		
	}
	
	public ChineseRecipeEntity(T t) {
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
	 * 菜品名称
	 */
	@TableField("dish_name")
	private String caipinmingcheng;
	
	/**
	 * 菜系
	 */
	@TableField("cuisine")
	private String caixi;
	
	/**
	 * 图片
	 */
	@TableField("image")
	private String tupian;
	
	/**
	 * 菜品类型
	 */
	@TableField("dish_type")
	private String caipinleixing;
	
	/**
	 * 材料
	 */
	@TableField("ingredients")
	private String cailiao;
	
	/**
	 * 烹饪方法
	 */
	@TableField("cooking_method")
	private String pengrenfangfa;
	
	/**
	 * 用户id
	 */
	@JsonProperty("userid")
	@TableField("user_id")
	private Long userId;
	
	/**
	 * 用户账号
	 */
	@TableField(exist = false)			
	private String yonghuzhanghao;
	
	/**
	 * 用户昵称
	 */
	@TableField(exist = false)			
	private String yonghuxingming;
	
	/**
	 * 发布者头像（从用户表带出，不存库）
	 */
	@TableField(exist = false)
	private String touxiang;
	
	/**
	 * 是否审核
	 */
	@JsonProperty("sfsh")
	@TableField("audit_status")
	private String auditStatus;
	
	/**
	 * 审核回复
	 */
	@JsonProperty("shhf")
	@TableField("audit_reply")
	private String auditReply;
	
	/**
	 * 赞
	 */
	@TableField(exist = false)			
	private Integer thumbsupnum;
	
	/**
	 * 点击次数
	 */
	@JsonProperty("clicknum")
	@TableField("view_count")
	private Integer viewCount;

	/**
	 * 用户交互收藏数（运行时统计，不存库）
	 */
	@TableField(exist = false)
	private Integer userInteractionsNum;

	/**
	 * 来源类型（用于兼容合并前的分类：foreign_recipe/chinese_recipe）
	 */
	@JsonProperty("recipetype")
	@TableField("source_type")
	private String sourceType;
	
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	@JsonProperty("addtime")
	@TableField("created_at")
	private Date createdAt;

	@JsonProperty("addtime")
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
	 * 设置：菜品名称
	 */
	public void setCaipinmingcheng(String caipinmingcheng) {
		this.caipinmingcheng = caipinmingcheng;
	}
	/**
	 * 获取：菜品名称
	 */
	public String getCaipinmingcheng() {
		return caipinmingcheng;
	}
	/**
	 * 设置：菜系
	 */
	public void setCaixi(String caixi) {
		this.caixi = caixi;
	}
	/**
	 * 获取：菜系
	 */
	public String getCaixi() {
		return caixi;
	}
	/**
	 * 设置：图片
	 */
	public void setTupian(String tupian) {
		this.tupian = tupian;
	}
	/**
	 * 获取：图片
	 */
	public String getTupian() {
		return tupian;
	}
	/**
	 * 设置：菜品类型
	 */
	public void setCaipinleixing(String caipinleixing) {
		this.caipinleixing = caipinleixing;
	}
	/**
	 * 获取：菜品类型
	 */
	public String getCaipinleixing() {
		return caipinleixing;
	}
	/**
	 * 设置：材料
	 */
	public void setCailiao(String cailiao) {
		this.cailiao = cailiao;
	}
	/**
	 * 获取：材料
	 */
	public String getCailiao() {
		return cailiao;
	}
	/**
	 * 设置：烹饪方法
	 */
	public void setPengrenfangfa(String pengrenfangfa) {
		this.pengrenfangfa = pengrenfangfa;
	}
	/**
	 * 获取：烹饪方法
	 */
	public String getPengrenfangfa() {
		return pengrenfangfa;
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
	@JsonProperty("userid")
	public Long getUserId() {
		return userId;
	}
	/**
	 * 设置：用户账号
	 */
	public void setYonghuzhanghao(String yonghuzhanghao) {
		this.yonghuzhanghao = yonghuzhanghao;
	}
	/**
	 * 获取：用户账号
	 */
	public String getYonghuzhanghao() {
		return yonghuzhanghao;
	}
	/**
	 * 设置：用户昵称
	 */
	public void setYonghuxingming(String yonghuxingming) {
		this.yonghuxingming = yonghuxingming;
	}
	/**
	 * 获取：用户昵称
	 */
	public String getYonghuxingming() {
		return yonghuxingming;
	}
	/**
	 * 设置：发布者头像
	 */
	public void setTouxiang(String touxiang) {
		this.touxiang = touxiang;
	}
	/**
	 * 获取：发布者头像
	 */
	public String getTouxiang() {
		return touxiang;
	}
	/**
	 * 设置：是否审核
	 */
	public void setAuditStatus(String auditStatus) {
		this.auditStatus = auditStatus;
	}
	/**
	 * 获取：是否审核
	 */
	@JsonProperty("sfsh")
	public String getAuditStatus() {
		return auditStatus;
	}
	/**
	 * 设置：审核回复
	 */
	public void setAuditReply(String auditReply) {
		this.auditReply = auditReply;
	}
	/**
	 * 获取：审核回复
	 */
	@JsonProperty("shhf")
	public String getAuditReply() {
		return auditReply;
	}
	/**
	 * 设置：赞
	 */
	public void setThumbsupnum(Integer thumbsupnum) {
		this.thumbsupnum = thumbsupnum;
	}
	/**
	 * 获取：赞
	 */
	public Integer getThumbsupnum() {
		return thumbsupnum;
	}
	/**
	 * 设置：点击次数
	 */
	public void setViewCount(Integer viewCount) {
		this.viewCount = viewCount;
	}
	/**
	 * 获取：点击次数
	 */
	@JsonProperty("clicknum")
	public Integer getViewCount() {
		return viewCount;
	}

	/**
	 * 设置：用户交互收藏数
	 */
	public void setUserInteractionsNum(Integer userInteractionsNum) {
		this.userInteractionsNum = userInteractionsNum;
	}

	/**
	 * 获取：用户交互收藏数
	 */
	public Integer getUserInteractionsNum() {
		return userInteractionsNum;
	}

	/**
	 * 获取：来源类型
	 */
	@JsonProperty("recipetype")
	public String getSourceType() {
		return sourceType;
	}

	/**
	 * 设置：来源类型
	 */
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}

