package com.nju.transporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @description 对网络请求处理
 * @date 2023/4/8 21:39
 * @author: qyl
 */
public interface RequestHandler {
    /**
     * 请求处理
     * @param input
     * @param output
     */
    void onRequest(InputStream input, OutputStream output) throws IOException;
}
