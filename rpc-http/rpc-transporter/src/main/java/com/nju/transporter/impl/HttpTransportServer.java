package com.nju.transporter.impl;

import com.nju.transporter.RequestHandler;
import com.nju.transporter.TransportServer;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @description Http传输服务器
 * @date 2023/4/8 22:58
 * @author: qyl
 */
@Slf4j
public class HttpTransportServer implements TransportServer {
    private RequestHandler handler;
    private Server server;

    @Override
    public void start() throws Exception {
        server.start ();
    }

    @Override
    public void init(int port, RequestHandler handler) {
        this.handler = handler;
        this.server = new Server (port);

        // 通过servlet接收请求
        ServletContextHandler ctx = new ServletContextHandler ();
        // 处理网络请求的抽象 所有请求处理最后都是通过handler.onRequest进行处理
        ServletHolder holder = new ServletHolder (new RequestServlet ());
        ctx.addServlet (holder,"/*");
        server.setHandler (ctx);
    }

    @Override
    public void stop() throws Exception {
        server.stop ();
    }

    class RequestServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            log.info ("收到  client 请求.....");
            InputStream in = req.getInputStream ();
            OutputStream out = resp.getOutputStream ();
            if (handler != null){
                handler.onRequest (in,out);
            }
            out.flush ();
        }
    }
}
