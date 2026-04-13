package com.jjs.common;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

// 添加(propagation = Propagation.REQUIRED)事务具有传播性，多个Service之间合作时也可以确保数据完整性
@Transactional(propagation = Propagation.REQUIRED)
public class AppBaseServiceV2<T, VOT, DTOT> extends ServiceImpl<AppBaseMapperV2<T>, T> implements IService<T> {

    public AppBaseMapperV2<T> getMapper(){
        return null;
    }
    public Class<VOT> getVoClass() { return null; }
    public Class<T> getEntityClass() { return null; }

    public PageListResult<VOT> getPageList(Page<T> page, Wrapper<T> queryWrapper) throws Exception {

        // 2. 执行分页查询。第二个参数是查询条件包装器，null 表示无附加条件
        Page<T> resultPage = getMapper().selectPage(page, queryWrapper);

        List<T> list = resultPage.getRecords(); // 当前页的数据列表

        List<VOT> voList = ConvertUtil.convertToList(list, getVoClass());

        PageListResult<VOT> pageResult = new PageListResult<VOT>();
        pageResult.setTotalCount(resultPage.getTotal());
        pageResult.setPageNum(page.getCurrent());
        pageResult.setPageSize(page.getSize());
        pageResult.setList(voList);

        return pageResult;
    }

    public List<VOT> getList(Wrapper<T> queryWrapper) throws Exception {
        List<T> list = this.getMapper().selectList(queryWrapper);
        return ConvertUtil.convertToList(list, getVoClass());
    }

    /**
     * 事务回滚
     */
    public void rollbackTranscation() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }

}
