package com.entity.vo;

import com.entity.DiscussDailyRecommendationEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
 

/**
 * 姣忔棩鎺ㄨ崘璇勮琛?
 * 鎵嬫満绔帴鍙ｈ繑鍥炲疄浣撹緟鍔╃被 
 * 锛堜富瑕佷綔鐢ㄥ幓闄や竴浜涗笉蹇呰鐨勫瓧娈碉級
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public class DiscussDailyRecommendationVO  implements Serializable {
	private static final long serialVersionUID = 1L;

	 			
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

