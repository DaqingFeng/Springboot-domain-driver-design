package com.aida.util;

import org.junit.Test;

import com.infrastructure.common.CharsetUtil;
import com.infrastructure.common.StreamUtil;

import static org.junit.Assert.assertEquals;
import java.io.InputStream;


public class StreamUtilTest  {


    @Test
    public void testStream2Byte() throws Exception {
        String str="中文";
        InputStream in = StreamUtil.byte2InputStream(str.getBytes(CharsetUtil.UTF_8));
        byte[]  bt =StreamUtil.stream2Byte(in);
        assertEquals(str,new String(bt));

    }

    @Test
    public void testInputStream2Byte() throws Exception {
        String str="中文";
        InputStream in = StreamUtil.byte2InputStream(str.getBytes(CharsetUtil.UTF_8));
        byte[]  bt =StreamUtil.inputStream2Byte(in);
        assertEquals(str,new String(bt));
    }

    @Test
    public void testByte2InputStream() throws Exception {
        String str ="中文";
        InputStream in = StreamUtil.byte2InputStream(str.getBytes(CharsetUtil.UTF_8));
        String result = StreamUtil.streamToString(in);
        assertEquals(result,str);
    }


}