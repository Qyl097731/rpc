package com.nju.codec.impl;

import com.nju.codec.Decoder;
import com.nju.codec.Encoder;
import com.nju.codec.TestBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @description
 * @date 2023/4/8 21:26
 * @author: qyl
 */
class JSONDecoderTest {

    @Test
    void decode() {
        Decoder decoder = new JSONDecoder ();
        Encoder encoder = new JSONEncoder ();
        TestBean bean = new TestBean ("qyl", 22);
        byte[] bytes = encoder.encode (bean);

        TestBean testBean = decoder.decode (bytes, TestBean.class);
        Assertions.assertEquals ("qyl",testBean.getName ());
        Assertions.assertEquals (22,testBean.getAge ());
    }
}
