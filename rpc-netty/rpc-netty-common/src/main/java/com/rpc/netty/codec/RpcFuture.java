package com.rpc.netty.codec;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 异步返回，阻塞客户，尽量不影响服务器吞吐量
 *
 * @author asus
 */
@Data
@Slf4j
public class RpcFuture implements Future<Object> {
    private RpcResponse response;
    private RpcRequest request;
    private Sync sync = new Sync ();

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException ();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException ();
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        RpcResponse r;
        return ((r = response) == null ? waitingGet (true) : r);
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        boolean success = sync.tryAcquireNanos (1, timeout);
        RpcResponse r = response;
        if (success) {
            return r == null ? null : r.getResult ();
        } else {
            throw new TimeoutException (String.format ("request {} fail to get response in time ",
                    request.getServiceId (),
                    request.getServiceDescriptor ().getMethod ()));
        }
    }

    private Object waitingGet(boolean interruptible) throws InterruptedException {
        if (interruptible) {
            sync.acquireInterruptibly (1);
        } else {
            sync.acquire (1);
        }
        RpcResponse r;
        return ((r = response) == null) ? null : r.getResult ();
    }

    public void done(RpcResponse response) {
        this.response = response;
        sync.release (1);
    }

    private class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;
        private final int done = 0;
        private final int running = 1;

        @Override
        protected boolean tryAcquire(int ignored) {
            return getState () == done;
        }

        @Override
        protected boolean tryRelease(int ignored) {
            if (getState () == running){
                return compareAndSetState (running,done);
            }else{
                return false;
            }
        }
    }
}
