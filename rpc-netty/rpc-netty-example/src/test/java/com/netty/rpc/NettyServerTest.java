package com.netty.rpc;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

/**
 * @description 测试类
 * @date 2023/4/13 20:22
 * @author: qyl
 */
public class NettyServerTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testResponseTimeWithoutResponse(){
        long start = System.currentTimeMillis();
        NettyClient client = new NettyClient ();
        UserService service = client.getProxy (UserService.class);
        service.sayHello ();
        long end = System.currentTimeMillis ();
        assertThat("Response time", (double) (end - start), closeTo (100,100));
    }

    @Test
    void testResponseTimeWithResponse(){
        long start = System.currentTimeMillis();
        NettyClient client = new NettyClient ();
        UserService service = client.getProxy (UserService.class);
        List<Integer> response = service.selectAll ();
        long end = System.currentTimeMillis ();
        assertThat ("Response length",response.size() == 3);
        assertThat("Response time", (double) (end - start), closeTo (300,100));
    }

    @Test
    void testQPSWithoutExecutorPool() throws BrokenBarrierException, InterruptedException {
        int threadNum = 200;
        CyclicBarrier barrier = new CyclicBarrier (threadNum + 1);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadNum; i++) {
            new Thread(() -> {
                try {
                    barrier.await ();
                    NettyClient client = new NettyClient ();
                    UserService service = client.getProxy (UserService.class);
                    service.sayHello ();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException (e);
                }
            }).start();
        }
        long end = System.currentTimeMillis ();
        barrier.await ();
        assertThat("Response time", (double) (end - start), closeTo(1000.0, 1000.0)); // 判断响应时间是否在预期范围内
    }

    @Test
    void stop() {
    }

    @Test
    void isStarted() {
    }

    @Test
    void register() {
    }
}
