package com.netty.rpc;

import com.netty.rpc.service.AnnotationService;
import com.netty.rpc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

/**
 * @description 测试类
 * @date 2023/4/13 20:22
 * @author: qyl
 */
@Slf4j
public class NettyServerTest {
    private static NettyClient client;
    private static NettyServer server;

    @BeforeAll
    static void setUp() throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        server = new NettyServer ();
        client = new NettyClient ();
    }

    @AfterAll
    static void tearDown() {
        server.stop ();
        client = null;
    }

    @Test
    void testResponseTimeWithoutResponse() {
        long start = System.currentTimeMillis ();
        UserService service = client.getProxy (UserService.class, "");
        service.sayHello ();
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (160, 160));
        log.info ("sync call total-time-cost:{}ms", cost);
    }

    @Test
    void testResponseTimeWithResponse() {
        long start = System.currentTimeMillis ();
        UserService service = client.getProxy (UserService.class, "");
        List<Integer> response = service.selectAll ();
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (200, 200));
        assertThat ("Response length", response.size () == 3);
        log.info ("sync call total-time-cost:{}ms", cost);
    }

    /**
     * 模拟查询，查看并发场景下QPS
     *
     * @throws BrokenBarrierException
     * @throws InterruptedException
     */
    @Test
    void testQPSWithoutRealPool() {
        final int threadNum = 1;
        final int requestNum = 50;
        Thread[] threads = new Thread[threadNum];
        long start = System.currentTimeMillis ();
        for (int i = 0; i < threadNum; ++i) {
            threads[i] = new Thread (new Runnable () {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        try {
                            final UserService service = client.getProxy (UserService.class, "");
                            service.sayHello ();
                        } catch (Exception ex) {
                            System.out.println (ex.toString ());
                        }
                    }
                }
            });
            threads[i].start ();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join ();
            } catch (InterruptedException e) {
                log.error (e.getMessage ());
            }
        }
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (1000.0, 1000.0)); // 判断响应时间是否在预期范围内
        log.info ("Sync call total-time-cost:{}ms, req/s={}", cost, threadNum * requestNum / cost * 1000);
    }

    /**
     * 模拟查询，查看并发场景下QPS
     *
     * @throws BrokenBarrierException
     * @throws InterruptedException
     */
    @Test
    void testQPSWithRealPool() {
        final int threadNum = 100;
        final int requestNum = 10000;
        Thread[] threads = new Thread[threadNum];
        long start = System.currentTimeMillis ();
        for (int i = 0; i < threadNum; ++i) {
            threads[i] = new Thread (new Runnable () {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        try {
                            final UserService service = client.getProxy (UserService.class, "");
                            service.sayHello ();
                        } catch (Exception ex) {
                            System.out.println (ex.toString ());
                        }
                    }
                }
            });
            threads[i].start ();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join ();
            } catch (InterruptedException e) {
                log.error (e.getMessage ());
            }
        }
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (10000.0, 10000.0)); // 判断响应时间是否在预期范围内
        log.info ("Sync call total-time-cost:{}ms, req/s={}", cost, threadNum * requestNum / cost * 1000);
    }

    /**
     * 测试是否调用了正确的version
     */
    @Test
    void testCallCorrectServiceVersion() {
        AnnotationService serviceVersion2 = client.getProxy (AnnotationService.class, "2.0");
        Assertions.assertEquals (2, serviceVersion2.getVersion ());

        AnnotationService serviceVersion1 = client.getProxy (AnnotationService.class, "1.0");
        Assertions.assertEquals (1, serviceVersion1.getVersion ());
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
