package com.ken.wms.domain;

/**
 * @author 302294马丽丽
 * @since 2017/9/4 16:08
 */

public class UrlAuthority {
    //url权限id
    private String id;
    //请求名称
    private String actionName;
    //请求描述
    private String actionDesc;
    //请求地址
    private String actionParam;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionDesc() {
        return actionDesc;
    }

    public void setActionDesc(String actionDesc) {
        this.actionDesc = actionDesc;
    }

    public String getActionParam() {
        return actionParam;
    }

    public void setActionParam(String actionParam) {
        this.actionParam = actionParam;
    }

    @Override
    public String toString() {
        return "UrlAuthority{" +
                "id='" + id + '\'' +
                ", actionName='" + actionName + '\'' +
                ", actionDesc='" + actionDesc + '\'' +
                ", actionParam='" + actionParam + '\'' +
                '}';
    }
}
