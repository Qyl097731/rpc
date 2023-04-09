package com.nju.rpc;

import com.nju.codec.Decoder;
import com.nju.codec.Encoder;
import com.nju.transporter.RequestHandler;
import com.nju.transporter.TransportServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpStatus;

/**
 * @description rpc服务器
 * @date 2023/4/9 18:19
 * @author: qyl
 */
@Slf4j
public class RpcServer {
    private RpcServerConfig config;
    private Encoder encoder;
    private Decoder decoder;
    private TransportServer transportServer;

    private RpcServiceManager manager;

    private ServiceInvoker invoker;

    public RpcServer(){
        this(new RpcServerConfig ());
    }

    public RpcServer(RpcServerConfig config) {
        this.config = config;
        this.encoder = ReflectionUtils.newInstance (config.getEncoderClass ());
        this.decoder = ReflectionUtils.newInstance (config.getDecoderClass ());
        this.transportServer = ReflectionUtils.newInstance (config.getTransportClass ());
        this.transportServer.init (config.getPort (), requestHandler ());
        this.manager = new RpcServiceManager ();
        this.invoker = new ServiceInvoker ();
    }

    public <T> void register(Class<T> object,T bean) {
        manager.register (object, bean);
    }

    public void start() throws Exception {
        transportServer.start ();
    }

    public void stop() throws Exception {
        transportServer.stop ();
    }

    private RequestHandler requestHandler() {
        return (input, output) -> {
            Response resp = new Response ();
            try {
                byte[] bytes = IOUtils.readFully (input, input.available ());
                Request request = decoder.decode (bytes, Request.class);
                log.info ("get request {}", request);
                RpcServiceInstance service = manager.lookup (request);
                Object r = invoker.invoke (service, request);
                resp.setCode (HttpStatus.OK_200);
                resp.setData (r);
            } catch (Exception e) {
                resp.setCode (HttpStatus.INTERNAL_SERVER_ERROR_500);
                resp.setMessage ("RpcServer error..." + e.getClass ().getName ());
            }
            byte[] encode = encoder.encode (resp);
            IOUtils.write (encode,output);
            output.flush ();
            log.info ("RpcServer finish...");
        };
    }
}
