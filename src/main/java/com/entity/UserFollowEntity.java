package com.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户关注表
 */
@TableName("user_follow")
public class UserFollowEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public UserFollowEntity() {}

    public UserFollowEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关注者用户id */
    private Long followerId;
    /** 被关注者用户id */
    private Long followedId;

    /**
     * 规范化字段：created_at
     */
    @TableField("created_at")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date createdAt;

    /**
     * 兼容字段：对外仍可读取 addtime，但底层已经切到 created_at
     */
    @TableField(exist = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }
    public Long getFollowedId() { return followedId; }
    public void setFollowedId(Long followedId) { this.followedId = followedId; }
    public Date getAddtime() { return addtime != null ? addtime : createdAt; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
    public Date getCreatedAt() { return createdAt != null ? createdAt : addtime; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
