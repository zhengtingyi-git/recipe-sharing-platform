package com.entity.vo;

import java.io.Serializable;

/**
 * 美食论坛评论表
 * 手机端接口返回实体辅助类 
 * （主要作用去除一些不必要的字段）
 */
public class DiscussnewsVO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	private Long userid;

	/**
	 * 用户名
	 */
	private String nickname;

	/**
	 * 评论内容
	 */
	private String content;

	/**
	 * 回复内容
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

