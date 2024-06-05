package com.RM.manageSystem.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    /**
     * 將輸入字串轉換為MD5散列字串。
     *
     * @param input 需要加密的字串
     * @return 加密後的MD5字串
     */
    public static String getMD5(String input) {
        try {
            // 創建MD5散列演算法的 MessageDigest 實例
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 更新要計算的內容
            md.update(input.getBytes());
            // 完成散列計算並返回結果
            byte[] digest = md.digest();
            // 將byte數組轉換為16進位字串
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            // MD5演算法不存在時拋出異常
            throw new RuntimeException("MD5 algorithm not available.", e);
        }
    }

    /**
     * 將byte數組轉換為16進位形式的字串。
     *
     * @param bytes 要轉換的byte數組
     * @return 轉換後的16進位字串
     */
    private static String bytesToHex(byte[] bytes) {
        // 使用 StringBuilder 構建最終的 16 進位字串
        StringBuilder hexString = new StringBuilder();
        // 遍歷 byte 數組，將每個 byte 轉換為 16 進位的形式
        for (int i = 0; i < bytes.length; i++) {
            // 對 byte 值進行 & 0xFF 操作以去除符號位，然後轉換成 16 進位字串
            String hex = Integer.toHexString(0xff & bytes[i]);
            // 如果轉換後的 16 進位字串只有一位，則在前面補 '0'
            if (hex.length() == 1) {
                hexString.append('0');
            }
            // 將處理後的 16 進位字串追加到 StringBuilder 中
            hexString.append(hex);
        }
        // 將最終構建的字串返回
        return hexString.toString();
    }
}
