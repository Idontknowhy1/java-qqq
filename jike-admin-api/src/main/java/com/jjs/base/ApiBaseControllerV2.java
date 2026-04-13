package com.jjs.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jjs.common.AppBaseServiceV2;
import com.jjs.common.ConvertUtil;
import com.jjs.common.DefaultMethod;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class ApiBaseControllerV2<T, VOT, DTOT> {

    /*
    获取Service
     */
    public AppBaseServiceV2<T, VOT, DTOT > getService(){ return null; }
    /*
    获取主键
     */
    public long getEntityKey(T obj) { return 0; }
    public long getDtoKey(DTOT obj) { return 0; }

    protected Class<VOT> getVoClass() { return null; }
    protected Class<DTOT> getDtoClass() { return null; }
    protected Class<T> getEntityClass() { return null; }

    @PostMapping("/save")
    public ApiResponse save(@RequestBody DTOT obj) throws Exception {
        if (!enableDefaultMethods().contains(DefaultMethod.SAVE)) {
            return ApiResponseUtil.getApiResponseMethodNotAllowed();
        }
        if (obj == null) {
            return ApiResponseUtil.getApiResponseFailure("参数异常");
        } else {
            //验证成功
            long id = getDtoKey(obj);
            T t = ConvertUtil.convertTo(obj, getEntityClass());

            if (id > 0) {
                this.getService().updateById(t);
            } else {
                this.getService().save(t);
            }
            T newObj = (T) this.getService().getById(getEntityKey(t));
            VOT vot = ConvertUtil.convertTo(newObj, getVoClass());

            return ApiResponseUtil.getApiResponseSuccess("保存成功", vot);
        }
    }

    @GetMapping("/delete")
    public ApiResponse delete(@RequestParam Integer id) throws Exception {
        if (!enableDefaultMethods().contains(DefaultMethod.DELETE)) {
            return ApiResponseUtil.getApiResponseMethodNotAllowed();
        }
        try {
            this.getService().removeById(id);
            return ApiResponseUtil.getApiResponseSuccess("删除成功",null);
        }catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    @GetMapping("/detail")
    public ApiResponse getDetail(@RequestParam String id) throws Exception {
        try {
            QueryWrapper<T> wrapper = new QueryWrapper<T>().eq("id", id);
            T obj = this.getService().getOne(wrapper);
            if (obj != null) {
                VOT vo = ConvertUtil.convertTo(obj, getVoClass());
                return ApiResponseUtil.getApiResponseSuccess(vo);
            } else {
                return ApiResponseUtil.getApiResponseSuccess(null);
            }
        }catch(Exception ex){
            log.error(this.getClass().getName() + " error" + ex,ex);
            return ApiResponseUtil.getApiResponseFailure();
        }
    }

    /**
     * 开启的父类默认方法
     */
    public List<DefaultMethod> enableDefaultMethods() {
        return Arrays.asList(
//                DefaultMethod.SAVE,
//                DefaultMethod.DELETE,
//                DefaultMethod.GET_LIST,
//                DefaultMethod.GET_PAGE_COUNT,
//                DefaultMethod.GET_PAGE_List
                );
    }
}
