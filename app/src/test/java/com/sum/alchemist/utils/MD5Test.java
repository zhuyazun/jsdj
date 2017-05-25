package com.sum.alchemist.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Created by Qiu on 2016/11/14.
 */
@RunWith(JUnit4.class)
public class MD5Test{
    @Test
    public void testGetStringMD5(){
        String sign = CommonUtil.getSign("18700919015", 111);
        System.out.print(sign);
        assertEquals(sign, "b7a9507236b31b3d9823eed827db7652");

    }

    @Test
    public void testPatter(){
        String result = StringUtil.filter("<p style=\"text-align: center;\"><span style=\"font-size: 20px;\"><strong>测试</strong></span></p><ol class=\" list-paddingleft-2\" style=\"list-style-type: decimal;\"><li><p><span style=\"font-size: 20px;\"><strong>123</strong></span></p></li><li><p><span style=\"font-size: 20px;\"><strong>123213</strong></span></p></li></ol>", "<.*?>");
        Assert.assertEquals(result, "测试123123213");
    }

}