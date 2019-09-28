/*
 * *******************************************************************************
 *         文 件：Root.java     模 块：app      项 目：onewechat
 *         当前修改时间：2019年09月28日 11:08:46
 *         上次修改时间：2019年09月28日 10:58:59
 *         作者：liutaos@qq.com        Copyright (c) 2019
 * *******************************************************************************
 */

package com.auto.onewechat.tools;

public class Root {

    private String code;

    private String msg;

    private User user;

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return this.user;
    }

}
