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
 * 姣忔棩鎺ㄨ崘璇勮琛?
 * 鏁版嵁搴撻€氱敤鎿嶄綔瀹炰綋绫伙紙鏅€氬鍒犳敼鏌ワ級
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("discussremencaipin")
public class DiscussDailyRecommendationEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public DiscussDailyRecommendationEntity() {
		
	}
	
	public DiscussDailyRecommendationEntity(T t) {
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
	 * 鍏宠仈琛╥d
	 */
					
	private Long refid;
	
	/**
	 * 鐢ㄦ埛id
	 */
					
	private Long userid;
	
	/**
	 * 鐢ㄦ埛鍚?
	 */
					
	private String nickname;
	
	/**
	 * 璇勮鍐呭
	 */
					
	private String content;
	
	/**
	 * 鍥炲鍐呭
	 */
					
	private String reply;
	
	
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
	 * 璁剧疆锛氬叧鑱旇〃id
	 */
	public void setRefid(Long refid) {
		this.refid = refid;
	}
	/**
	 * 鑾峰彇锛氬叧鑱旇〃id
	 */
	public Long getRefid() {
		return refid;
	}
	/**
	 * 璁剧疆锛氱敤鎴穒d
	 */
	public void setUserid(Long userid) {
		this.userid = userid;
	}
	/**
	 * 鑾峰彇锛氱敤鎴穒d
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
	 * 璁剧疆锛氱敤鎴峰悕
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * 鑾峰彇锛氱敤鎴峰悕
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * 璁剧疆锛氳瘎璁哄唴瀹?
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * 鑾峰彇锛氳瘎璁哄唴瀹?
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 璁剧疆锛氬洖澶嶅唴瀹?
	 */
	public void setReply(String reply) {
		this.reply = reply;
	}
	/**
	 * 鑾峰彇锛氬洖澶嶅唴瀹?
	 */
	public String getReply() {
		return reply;
	}

}

