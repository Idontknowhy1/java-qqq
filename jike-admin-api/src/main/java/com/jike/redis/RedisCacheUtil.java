package com.jike.redis;

import com.alibaba.fastjson.JSON;
import com.jike.config.AppConfig;
import com.jike.utils.StaticMethodGetBean;
import com.jjs.common.HashMapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisCacheUtil {

    private static StringRedisTemplate stringRedisTemplate = StaticMethodGetBean.getBean(StringRedisTemplate.class);
    private static AppConfig appConfig = StaticMethodGetBean.getBean(AppConfig.class);

    /**
     * 根据key删除缓存
     * @param key
     * @return	true:成功
     * 		false:失败
     */
    public static boolean del(String... key) {
        log.debug(" delete key :{}", key.toString());
        try {
            if (isClose() || isEmpty(key)) {
                return false;
            }
            Set<String> keySet = new HashSet<>();
            for (String str : key) {
                keySet.add(buildKey(str));
            }
            stringRedisTemplate.delete(keySet);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 根据key删除缓存
     * @param key
     * @return	true:成功
     * 		false:失败
     */
    public static boolean delPattern(String key) {
        log.debug(" delete Pattern keys :{}", key);
        try {
            if (isClose() || isEmpty(key)) {
                return false;
            }
            key = buildKey(key);
            stringRedisTemplate.delete(stringRedisTemplate.keys(key));
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 删除一组key值
     * @param keys
     * @return	true:成功
     * 		false:失败
     */
    public static boolean del(Set<String> keys) {
        log.debug(" delete keys :{}", keys.toString());
        try {
            if (isClose() || isEmpty(keys)) {
                return false;
            }
            Set<String> keySet = new HashSet<>();
            for (String str : keys) {
                keySet.add(buildKey(str));
            }
            stringRedisTemplate.delete(keySet);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 设置过期时间
     * @param key	缓存key
     * @param seconds	过期秒数
     * @return	true:成功
     * 		false:失败
     */
    public static boolean setExp(String key, long seconds) {
        log.debug(" setExp key :{}, seconds: {}", key, seconds);
        try {
            if (isClose() || isEmpty(key) || seconds > 0) {
                return false;
            }
            key = buildKey(key);
            return stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 查询过期时间
     * @param key	缓存key
     * @return	秒数
     */
    public static Long getExpire(String key) {
        log.debug(" getExpire key :{}", key);
        try {
            if (isClose() || isEmpty(key)) {
                return 0L;
            }
            key = buildKey(key);
            return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0L;
    }



    /**
     * 缓存存入key-value
     * @param key	缓存键
     * @param value	缓存值
     * @return	true:成功
     * 		false:失败
     */
    public static boolean setString(String key, String value) {
        log.debug(" setString key :{}, value: {}", key, value);
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 缓存存入key-value
     * @param key	缓存键
     * @param value	缓存值
     * @param seconds	秒数
     * @return	true:成功
     * 		false:失败
     */
    public static boolean setString(String key, String value, long seconds) {
        log.debug(" setString key :{}, value: {}, timeout:{}", key, value, seconds);
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 不存在则缓存存入key-value
     * @param key	缓存键
     * @param value	缓存值
     * @return	true:成功
     * 		false:失败
     */
    public static boolean setIfAbsent(String key, String value) {
        log.debug(" setIfAbsent key :{}, value: {}", key, value);
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 不存在则缓存存入key-value
     * @param key	缓存键
     * @param value	缓存值
     * @param seconds	秒数
     * @return	true:成功
     * 		false:失败
     */
    public static boolean setIfAbsent(String key, String value, long seconds) {
        log.debug(" setIfAbsent key :{}, value: {}, timeout:{}", key, value, seconds);
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            return Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                    .setIfAbsent(key, value, seconds, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    /**
     * 根据key取出String value
     * @param key	缓存key值
     * @return	String	缓存的String
     */
    public static String getString(String key) {
        log.debug(" getString key :{}", key);
        try {
            if (isClose() || isEmpty(key)) {
                return null;
            }
            key = buildKey(key);
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }


    public static boolean setObj(String key, Object object){
        return setObj(key,object,0);
    }
    public static boolean setObj(String key, Object object, long seconds){
        try {
            if (isClose() || isEmpty(key) || isEmpty(object)) {
                return false;
            }
            key = buildKey(key);
            if(seconds > 0){
                stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(object), seconds, TimeUnit.SECONDS);
            } else {
                stringRedisTemplate.opsForValue().set(key,JSON.toJSONString(object));
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static <T> T getObj(String key, Class<T> clazz){
        try {
            if (isClose() || isEmpty(key)) {
                return null;
            }
            key = buildKey(key);
            String json = stringRedisTemplate.opsForValue().get(key);
            return JSON.parseObject(json,clazz);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }



    /**
     * List操作
     * @param key
     * @param list
     * @return
     */
    public static boolean setList(String key, List list){
        return setList(key,list,0);
    }
    public static boolean setList(String key, List list,long seconds){
        try {
            if (isClose() || isEmpty(key) || isEmpty(list)) {
                return false;
            }
            key = buildKey(key);
            List<String> valueList = new ArrayList<>();
            for(Object obj:list){
                if(obj instanceof String){
                    valueList.add((String) obj);
                } else {
                    valueList.add(JSON.toJSONString(obj));
                }
            }
            stringRedisTemplate.opsForList().rightPushAll(key,valueList);
            if (seconds > 0) {
                stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static List<String> getList(String key){
        try {
            if (isClose() || isEmpty(key)) {
                return null;
            }
            key = buildKey(key);
            return stringRedisTemplate.opsForList().range(key,0,-1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
    public static List getList(String key,Class clazz){
        List value = new ArrayList<>();
        List<String> list = RedisCacheUtil.getList(key);
        for (String json: list) {
            value.add(JSON.parseObject(json,clazz));
        }
        return value;
    }
    public static boolean leftPush(String key, String value){
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            stringRedisTemplate.opsForList().leftPush(key,value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static boolean rightPush(String key, String value){
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            stringRedisTemplate.opsForList().rightPush(key,value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


    /**
     * 对Set操作
     * @return
     */
    public static boolean setSet(String key, Set<String> set,long seconds){
        try {
            if (isClose() || isEmpty(key) || isEmpty(set)) {
                return false;
            }
            key = buildKey(key);
            List<String> valueList = new ArrayList<>();
            for (String value : set) {
                stringRedisTemplate.opsForSet().add(key, value);
            }
            if (seconds > 0) {
                stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static boolean addToSet(String key, Set<String> set){
        try {
            if (isClose() || isEmpty(key) || isEmpty(set)) {
                return false;
            }
            key = buildKey(key);
            List<String> valueList = new ArrayList<>();
            for (String value : set) {
                stringRedisTemplate.opsForSet().add(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static boolean addToSet(String key, String value){
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            stringRedisTemplate.opsForSet().add(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static boolean removeFromSet(String key, Set<String> set){
        try {
            if (isClose() || isEmpty(key) || isEmpty(set)) {
                return false;
            }
            key = buildKey(key);
            List<String> valueList = new ArrayList<>();
            for (String value : set) {
                stringRedisTemplate.opsForSet().remove(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static boolean removeFromSet(String key, String value){
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            stringRedisTemplate.opsForSet().remove(key, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static boolean isMemberInSet(String key, String value){
        try {
            if (isClose() || isEmpty(key) || isEmpty(value)) {
                return false;
            }
            key = buildKey(key);
            return stringRedisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
    public static Set<String> memebersInSet(String key) {
        try {
            if (isClose() || isEmpty(key)) {
                return null;
            }
            key = buildKey(key);
            return stringRedisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 去的缓存中的最大值并+1
     * @param key	缓存key值
     * @return	long	缓存中的最大值+1
     */
    public static long incr(String key) {
        try {
            if (isClose() || isEmpty(key)) {
                return 0;
            }
            key = buildKey(key);
            return stringRedisTemplate.opsForValue().increment(key, 1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }
    public static long decr(String key) {
        try {
            if (isClose() || isEmpty(key)) {
                return 0;
            }
            key = buildKey(key);
            return stringRedisTemplate.opsForValue().increment(key, -1);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }


    @SuppressWarnings("rawtypes")
    private static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            String str = obj.toString();
            if ("".equals(str.trim())) {
                return true;
            }
            return false;
        }
        if (obj instanceof List) {
            List<Object> list = (List<Object>) obj;
            if (list.isEmpty()) {
                return true;
            }
            return false;
        }
        if (obj instanceof Map) {
            Map map = (Map) obj;
            if (map.isEmpty()) {
                return true;
            }
            return false;
        }
        if (obj instanceof Set) {
            Set set = (Set) obj;
            if (set.isEmpty()) {
                return true;
            }
            return false;
        }
        if (obj instanceof Object[]) {
            Object[] objs = (Object[]) obj;
            if (objs.length <= 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 匹配符合正则的key
     * @param patternKey
     * @return key的集合
     */
    public static Set<String> keys(String patternKey) {
        log.debug(" keys key :{}", patternKey);
        try {
            if (isClose() || isEmpty(patternKey)) {
                return Collections.emptySet();
            }
            return stringRedisTemplate.keys(patternKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptySet();
    }

    /**
     * 获取整个hash对象
     * @param key
     * @return
     */
    public static Map<Object, Object> hashEntries(String key) {
        try {
            if (isClose() || isEmpty(key)) {
                return Collections.emptyMap();
            }
            String buildKey = buildKey(key);
            return stringRedisTemplate.opsForHash().entries(buildKey);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * 检查缓存是否开启
     * @return	true:已关闭
     * 		false:已开启
     */
    public static boolean isClose() {
        return false;
    }

    /**
     * 构建缓存key值
     * @param key	缓存key
     * @return
     */
    private static String buildKey(String key) {
        // 已经包含Key了，就不在加了
        if (key.contains(":dev:") || key.contains(":test:") || key.contains(":prod:")) {
            return key;
        }
        return appConfig.getEnv() + ":" + key;
    }

    public static Object getConfigInfo(String configKey) {
        String key = "app:config:global";
        key = buildKey(key);
        String json = RedisCacheUtil.getString(key);
        HashMap hashMap = HashMapUtil.hashMapFromJson(json);
        if (hashMap == null) {
            return null;
        }
        return hashMap.get(configKey);
    }
}
