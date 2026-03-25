package com.entity.vo;

import java.io.Serializable;

/**
 * ?
 *  
 * 
 */
public class ForumPostCommentVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long userid;

	/**
	 * ?
	 */
	private String nickname;

	/**
	 * 
	 */
	private String content;

	/**
	 * 
	 */
	private String reply;

	public void setUserid(Long userid) {
		this.userid = userid;
	}

	public Long getUserid() {
		return userid;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getReply() {
		return reply;
	}
}

