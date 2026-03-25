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
 * ?
 * 
 * @author 
 * @email 
 * @date 2022-04-09 17:21:19
 */
@TableName("forum_post_comment")
public class ForumPostCommentEntity<T> implements Serializable {
	private static final long serialVersionUID = 1L;


	public ForumPostCommentEntity() {
		
	}
	
	public ForumPostCommentEntity(T t) {
		try {
			BeanUtils.copyProperties(this, t);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * id
	 */
	@TableId
	private Long id;
	@TableField("post_id")
	private Long postId;

	@TableField("user_id")
	private Long userId;

	@TableField(exist = false)
	private String displayName;

	@TableField("comment_content")
	private String commentContent;

	@TableField("comment_image")
	private String commentImage;

	@TableField("reply_content")
	private String replyContent;

	@JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat
	@TableField("created_at")
	private Date createdAt;

	// Legacy aliases for compatibility with old frontend/backend fields
	@TableField(exist = false)
	private Long refid;
	@TableField(exist = false)
	private Long userid;
	@TableField(exist = false)
	private String nickname;
	@TableField(exist = false)
	private String content;
	@TableField(exist = false)
	private String cpicture;
	@TableField(exist = false)
	private String reply;
	@TableField(exist = false)
	private Date addtime;

	public Date getAddtime() {
		return addtime != null ? addtime : createdAt;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
		if (this.createdAt == null) {
			this.createdAt = addtime;
		}
	}

	public Date getCreatedAt() {
		return createdAt != null ? createdAt : addtime;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
		if (this.addtime == null) {
			this.addtime = createdAt;
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * id
	 */
	public void setRefid(Long refid) {
		this.refid = refid;
		if (this.postId == null) {
			this.postId = refid;
		}
	}
	/**
	 * id
	 */
	public Long getRefid() {
		return refid != null ? refid : postId;
	}
	/**
	 * id
	 */
	public void setUserid(Long userid) {
		this.userid = userid;
		if (this.userId == null) {
			this.userId = userid;
		}
	}
	/**
	 * id
	 */
	public Long getUserid() {
		return userid != null ? userid : userId;
	}
	/**
	 * 
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
		if (this.displayName == null) {
			this.displayName = nickname;
		}
	}
	/**
	 * 
	 */
	public String getNickname() {
		return nickname != null ? nickname : displayName;
	}
	/**
	 * ?
	 */
	public void setContent(String content) {
		this.content = content;
		if (this.commentContent == null) {
			this.commentContent = content;
		}
	}
	/**
	 * ?
	 */
	public String getContent() {
		return content != null ? content : commentContent;
	}
	/**
	 * ?
	 */
	public void setCpicture(String cpicture) {
		this.cpicture = cpicture;
		if (this.commentImage == null) {
			this.commentImage = cpicture;
		}
	}
	/**
	 * ?
	 */
	public String getCpicture() {
		return cpicture != null ? cpicture : commentImage;
	}
	/**
	 * ?
	 */
	public void setReply(String reply) {
		this.reply = reply;
		if (this.replyContent == null) {
			this.replyContent = reply;
		}
	}
	/**
	 * ?
	 */
	public String getReply() {
		return reply != null ? reply : replyContent;
	}

	public Long getPostId() {
		return postId != null ? postId : refid;
	}
	public void setPostId(Long postId) {
		this.postId = postId;
		if (this.refid == null) {
			this.refid = postId;
		}
	}

	public Long getUserId() {
		return userId != null ? userId : userid;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
		if (this.userid == null) {
			this.userid = userId;
		}
	}

	public String getDisplayName() {
		return displayName != null ? displayName : nickname;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		if (this.nickname == null) {
			this.nickname = displayName;
		}
	}

	public String getCommentContent() {
		return commentContent != null ? commentContent : content;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
		if (this.content == null) {
			this.content = commentContent;
		}
	}

	public String getCommentImage() {
		return commentImage != null ? commentImage : cpicture;
	}
	public void setCommentImage(String commentImage) {
		this.commentImage = commentImage;
		if (this.cpicture == null) {
			this.cpicture = commentImage;
		}
	}

	public String getReplyContent() {
		return replyContent != null ? replyContent : reply;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
		if (this.reply == null) {
			this.reply = replyContent;
		}
	}

}

