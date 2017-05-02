package com.example.lgc.encrypt;

import org.junit.Test;

import java.net.URLDecoder;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    /**
     * URLEncoder 编码/解码测试
     */
    @Test
    public void URLEncodeTest() {
        //地址栏不允许中文,传递一些特殊字符 & ? ,而这些符号很容易和get提交上的转义符号冲突
        String url = "http://www.baidu.com?search=\"中文字符\"&name=uy?&cc&pwd=?ke&w";
        //将url进行分割,对相应的字符串进行URLEncode编码,然后再拼接,最后解码
        StringBuffer sb = new StringBuffer("http://www.baidu.com?");

        //通过URLDeCoder转码
        String searchEncode = URLEncoder.encode("search=\"中文字符\"");
        System.out.println("search=\"中文字符\"--编码后: " + searchEncode);
        String nameEncode = URLEncoder.encode("name=uy?&cc");
        System.out.println("name=uy?&cc--编码后: " + nameEncode);
        String pwdEncode = URLEncoder.encode("pwd=?ke&w");
        System.out.println("pwd=?ke&w--编码后: " + pwdEncode);

        //转码后拼接
        String encodeUrl = sb.append(searchEncode).append("&")
                .append(nameEncode).append("&")
                .append(pwdEncode).toString();
        System.out.println("初始的 url=" + url);
        System.out.println("通过URLDeCoder转码 encodeUrl=" + encodeUrl);

        //把转码的url 还原
        String decodeUrl = URLDecoder.decode(encodeUrl);
        System.out.println("通过URLDeCoder解码 decodeUrl=" + decodeUrl);
    }

}