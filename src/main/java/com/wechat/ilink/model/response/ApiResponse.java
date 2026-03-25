package com.wechat.ilink.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * API 响应基类
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {

    @JsonProperty("ret")
    private Integer ret;

    @JsonProperty("errcode")
    private Integer errCode;

    @JsonProperty("errmsg")
    private String errMsg;

    public ApiResponse() {
    }

    /**
     * 判断响应是否成功
     */
    public boolean isSuccess() {
        return (ret == null || ret == 0) && (errCode == null || errCode == 0);
    }

    /**
     * 获取错误信息
     */
    public String getErrorInfo() {
        if (!isSuccess()) {
            StringBuilder sb = new StringBuilder();
            if (ret != null && ret != 0) {
                sb.append("ret=").append(ret);
            }
            if (errCode != null && errCode != 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("errcode=").append(errCode);
            }
            if (errMsg != null) {
                if (sb.length() > 0) sb.append(", ");
                sb.append("errmsg=").append(errMsg);
            }
            return sb.toString();
        }
        return "";
    }

    // Getters and Setters

    public Integer getRet() {
        return ret;
    }

    public void setRet(Integer ret) {
        this.ret = ret;
    }

    public Integer getErrCode() {
        return errCode;
    }

    public void setErrCode(Integer errCode) {
        this.errCode = errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        if (isSuccess()) {
            return "ApiResponse{success=true}";
        }
        return "ApiResponse{success=false, " + getErrorInfo() + "}";
    }
}
