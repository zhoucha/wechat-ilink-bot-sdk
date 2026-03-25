package com.wechat.ilink.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 二维码响应
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrcodeResponse extends ApiResponse {

    @JsonProperty("qrcode")
    private String qrcode;

    @JsonProperty("qrcode_img_content")
    private String qrcodeImgContent;

    public QrcodeResponse() {
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getQrcodeImgContent() {
        return qrcodeImgContent;
    }

    public void setQrcodeImgContent(String qrcodeImgContent) {
        this.qrcodeImgContent = qrcodeImgContent;
    }

    @Override
    public String toString() {
        return "QrcodeResponse{qrcode='" + qrcode + "', success=" + isSuccess() + "}";
    }
}
