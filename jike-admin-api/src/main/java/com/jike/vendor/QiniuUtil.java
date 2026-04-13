package com.jike.vendor;

import com.jike.constant.AppConst;
import com.jike.redis.RedisCacheUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.util.Auth;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class QiniuUtil {

    // 七牛文档 https://developer.qiniu.com/kodo/1239/java#5

    public HashMap getUpdateToken() {
        String upToken = getToken();
        Object qiniuPrefix = RedisCacheUtil.getConfigInfo("qiniuPrefix");
        return new HashMap() {{
            put("token",upToken);
            put("prefix",(qiniuPrefix == null ? "" : qiniuPrefix.toString()));
        }};
    }

    /**
     * 获取Token
     * @return
     */
    private String getToken() {
        Auth auth = getAuth();
        String upToken = auth.uploadToken(AppConst.QINIU_BUCKET);
        return upToken;
    }

    // 普通删除(暂未使用以下方法，未测试)
    public void deleteImage(String key) {
        Auth auth = getAuth();
        // 实例化一个BucketManager对象
        Configuration cfg = new Configuration(Region.region2());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            // 调用delete方法移动文件
            Response response = bucketManager.delete(AppConst.QINIU_BUCKET, key);
        } catch (QiniuException e) {
            // 捕获异常信息
            Response r = e.response;
            log.error("删除七牛图片失败 ",e);
        }
    }

    // 批量删除文件
    public void deleteFiles(List<String> list) {
        // list一次数量不能超过1000个

        Auth auth = getAuth();
        // 实例化一个BucketManager对象
        Configuration cfg = new Configuration(Region.region2());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            String[] keys = list.toArray(new String[0]);
            BucketManager.BatchOperations batchOperations = new BucketManager.BatchOperations();
            batchOperations.addDeleteOp(AppConst.QINIU_BUCKET, keys);
            bucketManager.batch(batchOperations);
        } catch (QiniuException ex) {
//            System.err.println(ex.response.toString());
        }
    }

    private Auth getAuth() {
        Auth auth = Auth.create(AppConst.QINIU_ACCESS_KEY, AppConst.QINIU_SECRET_KEY);
        return auth;
    }

    @Getter
    @Setter
    public static class UploadResult {
        String url;
        String key;
        boolean success;
        String msg;
    }
}
