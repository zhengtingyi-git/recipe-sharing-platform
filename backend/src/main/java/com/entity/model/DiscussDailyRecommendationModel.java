package com.entity.model;

import com.entity.DiscussDailyRecommendationEntity;

import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
 

/**
 * 姣忔棩鎺ㄨ崘璇勮琛?
 * 鎺ユ敹浼犲弬鐨勫疄浣撶被  
 *锛堝疄闄呭紑鍙戜腑閰嶅悎绉诲姩绔帴鍙ｅ紑鍙戞墜鍔ㄥ幓鎺変簺娌＄敤鐨勫瓧娈碉紝 鍚庣涓€鑸敤entity灏卞鐢ㄤ簡锛?
 * 鍙栬嚜ModelAndView 鐨刴odel鍚嶇О
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
public class DiscussDailyRecommendationModel  implements Serializable {
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

