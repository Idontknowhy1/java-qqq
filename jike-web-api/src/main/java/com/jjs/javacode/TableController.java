package com.jjs.javacode;

import com.jjs.gen.code.CodeGenerate;
import com.jjs.gen.code.GenerateParams;
import com.jjs.gen.code.TableFieldInfo;
import com.jjs.response.ApiResponse;
import com.jjs.response.ApiResponseUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.List;

@RestController
public class TableController {
    @Autowired
    TableFieldInfoMapper mapper;

    @Value("${app.generateFuntionAllowed}")
    Boolean generateAllowed;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private String basePackage = "com.jike";
    private String apiVersion = "v1";

    @GetMapping("/javacode/generate")
    public ApiResponse generate(@RequestParam String tableName,
                                @RequestParam String clazzName,
                                @RequestParam String packageName,
                                @RequestParam(defaultValue = "false") Boolean autoDeploy) throws Exception{

        if (generateAllowed == false) {
            return ApiResponseUtil.getApiResponseMethodNotAllowed();
        }

        String databaseName = "";
        // 1. 获取 SqlSession
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            // 2. 从 SqlSession 中获取 Connection 并执行你的操作
            try (Connection conn = sqlSession.getConfiguration().getEnvironment().getDataSource().getConnection()) {
                DatabaseMetaData metaData = conn.getMetaData();
                databaseName = conn.getCatalog(); // 对于 MySQL，获取当前数据库名
                // String databaseName = conn.getSchema(); // 对于某些数据库（如 PostgreSQL、Oracle），使用 getSchema()
                System.out.println("当前数据库名称: " + databaseName);
            } catch (Exception e) {
                e.printStackTrace();
                // 这里你返回了 ApiResponseUtil.getApiResponseAuthFailure(e.getMessage());
                // 在实际应用中，可能需要根据异常类型进行更细致的处理或回滚事务
                // sqlSession.rollback();
                return ApiResponseUtil.getApiResponseFailure(e.getMessage());
            }
            // 3. 如果所有操作成功，提交事务（因为 openSession() 默认不会自动提交）
            sqlSession.commit();
        } catch (Exception e) { // 这里捕获 try-with-resources 中 SqlSession 关闭或其他可能发生的异常
            e.printStackTrace();
            return ApiResponseUtil.getApiResponseFailure(e.getMessage());
        }

        GenerateParams params = new GenerateParams();
        params.setClazzName(clazzName);
        params.setPackageName(packageName);
        params.setTableName(tableName);
        params.setAutoDeploy(autoDeploy);
        params.setBasePackage(basePackage);
        params.setApiVersion(apiVersion);

        String finalDatabaseName = databaseName;
        List<TableFieldInfo> fields = mapper.getFieldList(new HashMap() {{
            put("tableName", params.getTableName());
            put("tableSchema", finalDatabaseName);
        }});

        // 生成Controller
        CodeGenerate.generateController(params);
        // 生成Pojo
        //        CodeGenerate.generatePojo(params,fields);
        CodeGenerate.generateEntity(params,fields);
        CodeGenerate.generateDTO(params,fields);
        CodeGenerate.generateVO(params,fields);
        // 生成Mapper.xml
        CodeGenerate.generateMapperXml(params,fields);
        // 生成Mapper
        CodeGenerate.generateMapper(params);
        // 生成Service
        CodeGenerate.generateService(params);
        CodeGenerate.generateConvertor(params);

        return ApiResponseUtil.getApiResponseSuccess("");
    }
}
