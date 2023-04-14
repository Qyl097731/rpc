package com.netty.rpc;

import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

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
@Slf4j
public class NettyServerTest {
    private static NettyClient client;

    @BeforeAll
    static void setUp() {
        client = new NettyClient ();
    }

    @AfterAll
    static void tearDown() {
        client = null;
    }

    @Test
    void testResponseTimeWithoutResponse() {
        long start = System.currentTimeMillis ();
        UserService service = client.getProxy (UserService.class);
        service.sayHello ();
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (100, 100));
        log.info ("sync call total-time-cost:{}ms", cost);
    }

    @Test
    void testResponseTimeWithResponse() {
        long start = System.currentTimeMillis ();
        UserService service = client.getProxy (UserService.class);
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
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        try {
                            final UserService service = client.getProxy (UserService.class);
                            service.sayHello ();
                        } catch (Exception ex) {
                            System.out.println(ex.toString());
                        }
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                log.error (e.getMessage());
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
        final int threadNum = 10;
        final int requestNum = 50;
        Thread[] threads = new Thread[threadNum];
        long start = System.currentTimeMillis ();
        for (int i = 0; i < threadNum; ++i) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < requestNum; i++) {
                        try {
                            final UserService service = client.getProxy (UserService.class);
                            service.sayHello ();
                        } catch (Exception ex) {
                            System.out.println(ex.toString());
                        }
                    }
                }
            });
            threads[i].start();
        }
        for (int i = 0; i < threads.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                log.error (e.getMessage());
            }
        }
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (1000.0, 1000.0)); // 判断响应时间是否在预期范围内
        log.info ("Sync call total-time-cost:{}ms, req/s={}", cost, threadNum * requestNum / cost * 1000);
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
