package com.jike.redis;

import com.jike.utils.StaticMethodGetBean;
import org.redisson.Redisson;
import org.redisson.api.RLock;

public class LockUtil {
    private static Redisson redisson = StaticMethodGetBean.getBean(Redisson.class);

    public static RLock getLock(String lockId) {
        return redisson.getLock("lock:"+lockId);
    }
}
