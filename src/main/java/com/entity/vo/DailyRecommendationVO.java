package com.entity.vo;

import com.entity.DailyRecommendationEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
 

/**
 * 姣忔棩鎺ㄨ崘
 * 鎵嬫満绔帴鍙ｈ繑鍥炲疄浣撹緟鍔╃被 
 * 锛堜富瑕佷綔鐢ㄥ幓闄や竴浜涗笉蹇呰鐨勫瓧娈碉級
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public class DailyRecommendationVO  implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
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
		
	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat 
	private Date fabushijian;
		
	/**
	 * 璧?
	 */
	
	private Integer thumbsupnum;
				
	
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
			
}

