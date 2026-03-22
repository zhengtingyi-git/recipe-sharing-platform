package com.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.IdType;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 用户关注表
 */
@TableName("guanzhu")
public class GuanzhuEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    public GuanzhuEntity() {}

    public GuanzhuEntity(T t) {
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

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFollowerId() { return followerId; }
    public void setFollowerId(Long followerId) { this.followerId = followerId; }
    public Long getFollowedId() { return followedId; }
    public void setFollowedId(Long followedId) { this.followedId = followedId; }
    public Date getAddtime() { return addtime; }
    public void setAddtime(Date addtime) { this.addtime = addtime; }
}
