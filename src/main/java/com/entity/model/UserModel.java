package com.entity.model;

import java.io.Serializable;

/**
 * 用户
 * 接收传参的实体类
 *（实际开发中配合移动端接口开发手动去掉些没用的字段， 后端一般用entity就够用了）
 * 取自ModelAndView 的model名称
 */
public class UserModel implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 密码
     */
    private String password;

    /**
     * 性别
     */
    private String gender;

    /**
     * 联系方式
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setYonghuxingming(String yonghuxingming) {
        this.nickname = yonghuxingming;
    }

    public String getYonghuxingming() {
        return nickname;
    }

    public void setMima(String mima) {
        this.password = mima;
    }

    public String getMima() {
        return password;
    }

    public void setXingbie(String xingbie) {
        this.gender = xingbie;
    }

    public String getXingbie() {
        return gender;
    }

    public void setTouxiang(String touxiang) {
        this.avatar = touxiang;
    }

    public String getTouxiang() {
        return avatar;
    }
}
