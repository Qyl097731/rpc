package com.nju.codec.impl;

import com.nju.codec.Encoder;
import com.nju.codec.TestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @description 编码器测试
 * @date 2023/4/8 21:07
 * @author: qyl
 */
class JSONEncoderTest {

    @Test
    void encode() {
        Encoder encoder = new JSONEncoder ();
        TestBean bean = new TestBean ("qyl", 22);
        byte[] bytes = encoder.encode (bean);
        Assertions.assertNotNull (bytes);
    }
}
