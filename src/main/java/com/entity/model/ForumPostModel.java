package com.entity.model;

import com.entity.ForumPostEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
 

/**
 * 
 *   
 * entity?
 * ModelAndView odel
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public class ForumPostModel  implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
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
	private Integer thumbsupnum;
				
	
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