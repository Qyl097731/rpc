package com.netty.rpc;

import com.netty.rpc.service.AnnotationService;
import com.netty.rpc.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.TimeUnit;

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
    private static NettyServer[] cluster;


    @BeforeAll
    static void setUp() throws Exception {
        cluster = new NettyServer[]{
                new NettyServer ("127.0.0.1", 3000),
                new NettyServer ("127.0.0.1", 3001),
                new NettyServer ("127.0.0.1", 3002)
        };
        client = new NettyClient ();
    }

    @AfterAll
    static void tearDown() {
        for (NettyServer server : cluster) {
            server.stop ();
        }
        cluster = null;
        client = null;
    }

    /**
     * 测试单个请求，并且无响应数据时的响应时间
     */
    @Test
    void testResponseTimeWithoutResponse() {
        long start = System.currentTimeMillis ();
        UserService service = client.getProxy (UserService.class, "");
        service.sayHello ();
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (250, 250));
        log.info ("sync call total-time-cost:{}ms", cost);
    }

    /**
     * 测试单个请求，并且有响应数据时的响应时间
     */
    @Test
    void testResponseTimeWithResponse() {
        long start = System.currentTimeMillis ();
        UserService service = client.getProxy (UserService.class, "");
        List<Integer> response = service.selectIds ();
        long end = System.currentTimeMillis ();
        double cost = end - start;
        assertThat ("Response time", cost, closeTo (250, 250));
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
     * 模拟查询，查看并发场景下无返回数据QPS
     *
     * @throws BrokenBarrierException
     * @throws InterruptedException
     */
    @Test
    void testQPSForNoResponseWithRealPool() {
        final int threadNum = 10;
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
        assertThat ("Response time", cost, closeTo (10 * 1000, 10 * 1000)); // 判断响应时间是否在预期范围内
        log.info ("Sync call total-time-cost:{}ms, req/s={}", cost, threadNum * requestNum / cost * 1000);
    }

    /**
     * 模拟查询，查看并发场景下有大型返回数据QPS
     *
     * @throws BrokenBarrierException
     * @throws InterruptedException
     */
    @Test
    void testQPSForResponseWithRealPool() {
        final int threadNum = 10;
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
                            service.selectUsers ();
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
        assertThat ("Response time", cost, closeTo (20000.0, 20000.0)); // 判断响应时间是否在预期范围内
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

    /**
     * 测试负载均衡策略，是否选择正确的服务节点(除了LRU）
     */
    @Test
    void testRoadBalanceWhetherChooseCorrectlyExcludeLru() {
        UserService service = client.getProxy (UserService.class, "");
        for (int i = 0; i < 10; i++) {
            service.sayHello ();
        }
    }

    /**
     * 测试LRU负载均衡策略，是否选择正确的服务节点(LRU）
     */
    @Test
    void testRoadBalanceLRUWhetherChooseCorrectly() throws Exception {
        UserService service = client.getProxy (UserService.class, "");
        for (int i = 0; i < 4; i++) {
            service.sayHello ();
            if (i == 2){
                new NettyServer ("127.0.0.1", 3004);
                TimeUnit.MILLISECONDS.sleep (200);
            }
        }
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
