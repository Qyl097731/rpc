package com.nju.rpc.client;

import com.nju.codec.Decoder;
import com.nju.codec.Encoder;
import com.nju.rpc.Request;
import com.nju.rpc.Response;
import com.nju.rpc.ServiceDescriptor;
import com.nju.transporter.TransportClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @description 调用远程服务的代理类
 * @date 2023/4/9 20:58
 * @author: qyl
 */
@Slf4j
public class RemoteInvocationHandler implements InvocationHandler {
    private final Class target;
    private Encoder encoder;
    private Decoder decoder;
    private TransportSelector selector;

    public RemoteInvocationHandler(Class clazz, Encoder encoder, Decoder decoder, TransportSelector selector) {
        this.target = clazz;
        this.encoder = encoder;
        this.decoder = decoder;
        this.selector = selector;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Request request = new Request ();
        ServiceDescriptor descriptor = ServiceDescriptor.from (target, method);
        request.setService (descriptor);
        request.setParameters (args);
        Response<?> resp = invokeRemote (request);
        if (resp == null || resp.getCode () != HttpStatus.OK_200) {
            throw new IllegalStateException ("fail to invoke remote");
        }
        return resp.getData ();
    }

    private Response<?> invokeRemote(Request request) {
        TransportClient client = null;
        Response<?> response;
        try {
            client = selector.select ();
            byte[] bytes = encoder.encode (request);
            InputStream input = client.write (new ByteArrayInputStream (bytes));
            byte[] inBytes = IOUtils.readFully (input, input.available ());
            response = decoder.decode (inBytes, Response.class);
        } catch (IOException e) {
            log.info ("invoke remote Error....." + e.getMessage ());
            response = new Response<> ();
            response.setCode (HttpStatus.INTERNAL_SERVER_ERROR_500);
            response.setMessage ("invoke remote Error... " + e.getMessage ());
        } finally {
            if (client != null){
                selector.release (client);
            }
        }
        return response;
    }
}
