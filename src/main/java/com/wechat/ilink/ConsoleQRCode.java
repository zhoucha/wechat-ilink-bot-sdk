package com.wechat.ilink;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class ConsoleQRCode {

    public static void main(String[] args) {
        String content = "https://www.example.com"; // 二维码内容
        int width = 40; // 二维码宽度 (模块数)
        int height = 40; // 二维码高度 (模块数)

        try {
            // 1. 设置编码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            // 2. 生成二维码矩阵
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 3. 打印到控制台
            // 注意：控制台字体通常高宽比不是 1:1，使用两个空格可能看起来更像正方形
            String blackChar = "█";
            String whiteChar = "  ";

            for (int y = 0; y < matrix.getHeight(); y++) {
                for (int x = 0; x < matrix.getWidth(); x++) {
                    if (matrix.get(x, y)) {
                        System.out.print(blackChar);
                    } else {
                        System.out.print(whiteChar);
                    }
                }
                System.out.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}