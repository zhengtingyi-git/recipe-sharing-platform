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
 * 姣忔棩鎺ㄨ崘
 * 鏁版嵁搴撻€氱敤鎿嶄綔瀹炰綋绫伙紙鏅€氬鍒犳敼鏌ワ級
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("remencaipin")
public class DailyRecommendationEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public DailyRecommendationEntity() {
		
	}
	
	public DailyRecommendationEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 涓婚敭id
	 */
	@TableId
	private Long id;
	/**
	 * 鏍囬
	 */
					
	private String biaoti;
	
	/**
	 * 灏侀潰
	 */
					
	private String fengmian;
	
	/**
	 * 鍐呭璇︽儏
	 */
					
	private String neirongxiangqing;
	
	/**
	 * 鍙戝竷鏃堕棿
	 */
				
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
	@DateTimeFormat 		
	private Date fabushijian;
	
	/**
	 * 璧?
	 */
					
	private Integer thumbsupnum;
	/**
	 * 鑿滅郴锛堥潪鏁版嵁搴撳瓧娈碉紝鐢ㄤ簬姣忔棩鎺ㄨ崘灞曠ず锛?
	 */
	@TableField(exist = false)
	private String caixi;
	
	/**
	 * 鏉ユ簮琛紙chinese_recipe/foreign_recipe锛夛紝鐢ㄤ簬鍓嶇璺宠浆璇︽儏锛岄潪鏁版嵁搴撳瓧娈?
	 */
	@TableField(exist = false)
	private String sourceType;
	
	
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	private Date addtime;

	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 璁剧疆锛氭爣棰?
	 */
	public void setBiaoti(String biaoti) {
		this.biaoti = biaoti;
	}
	/**
	 * 鑾峰彇锛氭爣棰?
	 */
	public String getBiaoti() {
		return biaoti;
	}
	/**
	 * 璁剧疆锛氬皝闈?
	 */
	public void setFengmian(String fengmian) {
		this.fengmian = fengmian;
	}
	/**
	 * 鑾峰彇锛氬皝闈?
	 */
	public String getFengmian() {
		return fengmian;
	}
	/**
	 * 璁剧疆锛氬唴瀹硅鎯?
	 */
	public void setNeirongxiangqing(String neirongxiangqing) {
		this.neirongxiangqing = neirongxiangqing;
	}
	/**
	 * 鑾峰彇锛氬唴瀹硅鎯?
	 */
	public String getNeirongxiangqing() {
		return neirongxiangqing;
	}
	/**
	 * 璁剧疆锛氬彂甯冩椂闂?
	 */
	public void setFabushijian(Date fabushijian) {
		this.fabushijian = fabushijian;
	}
	/**
	 * 鑾峰彇锛氬彂甯冩椂闂?
	 */
	public Date getFabushijian() {
		return fabushijian;
	}
	/**
	 * 璁剧疆锛氳禐
	 */
	public void setThumbsupnum(Integer thumbsupnum) {
		this.thumbsupnum = thumbsupnum;
	}
	/**
	 * 鑾峰彇锛氳禐
	 */
	public Integer getThumbsupnum() {
		return thumbsupnum;
	}
	/**
	 * 璁剧疆锛氳彍绯?
	 */
	public void setCaixi(String caixi) {
		this.caixi = caixi;
	}
	/**
	 * 鑾峰彇锛氳彍绯?
	 */
	public String getCaixi() {
		return caixi;
	}
	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

}


