package com.RM.manageSystem.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MD5UtilTest {

    @Test
    public void testGetMD5() {
        String password = "password";
        String md5 = MD5Util.getMD5(password);
        assertEquals("5f4dcc3b5aa765d61d8327deb882cf99", MD5Util.getMD5("password"));
    }
}
