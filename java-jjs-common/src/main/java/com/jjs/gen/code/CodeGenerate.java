package com.jjs.gen.code;

import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
public class CodeGenerate {

    public static void generateController(GenerateParams params) throws Exception {
        String apiVersion = params.getApiVersion();
        String basePackage = params.getBasePackage();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();

        String content = """
                package #basePackage#.controller.#packageName#;
                
                import cn.dev33.satoken.annotation.SaCheckPermission;
                import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
                import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
                import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
                import com.jjs.common.BasePageQuery;
                import com.jjs.common.ConvertUtil;
                import com.jjs.common.PageListResult;
                import #basePackage#.model.#packageName#.#clazzName#Entity;
                import #basePackage#.model.#packageName#.#clazzName#VO;
                import #basePackage#.model.#packageName#.#clazzName#DTO;
                import com.jjs.response.ApiResponse;
                import com.jjs.response.ApiResponseUtil;
                import #basePackage#.service.#packageName#.#clazzName#Service;
                import lombok.extern.slf4j.Slf4j;
                import org.springframework.web.bind.annotation.*;
                import java.util.List;
                
                @RequestMapping("/#clazzNameApi#/#apiVersion#")
                @RestController
                @Slf4j
                public class #clazzName#Controller {
                
                    final
                    #clazzName#Service service;
                    public #clazzName#Controller(#clazzName#Service service) { this.service = service; }\s
                
                    /**
                      * 保存、修改
                     */
                    @SaCheckPermission("xxxxxx:save")
                    @PostMapping("/save")
                    public ApiResponse save(@RequestBody #clazzName#DTO obj) throws Exception {
                        #clazzName#Entity entity = ConvertUtil.convertTo(obj, #clazzName#Entity.class);
                        if (obj.getId() > 0) {
                            service.updateById(entity);
                        } else {
                            service.save(entity);
                        }
                        entity = service.getById(entity.getId());
                        #clazzName#VO vo = ConvertUtil.convertTo(entity, #clazzName#VO.class);
                        return ApiResponseUtil.getApiResponseSuccess("保存成功", vo);
                    }
                
                    /**
                     * 删除
                     */
                    @SaCheckPermission("xxxxxx:delete")
                    @GetMapping("/delete")
                    public ApiResponse delete(@RequestParam String id) throws Exception {
                        try {
                            service.removeById(id);
                            return ApiResponseUtil.getApiResponseSuccess("删除成功",null);
                        }catch(Exception ex){
                            log.error(this.getClass().getName() + " error" + ex,ex);
                           return ApiResponseUtil.getApiResponseFailure();
                       }
                    }
                
                    /**
                       * 查看详情
                      */
                    @SaCheckPermission("xxxxxx")
                    @GetMapping("/detail")
                    public ApiResponse getDetail(@RequestParam String id) throws Exception {
                         try {
                             #clazzName#Entity obj = service.getOne(new QueryWrapper<#clazzName#Entity>().lambda().eq(#clazzName#Entity::getId, id));
                             if (obj != null) {
                                 #clazzName#VO vo = ConvertUtil.convertTo(obj, #clazzName#VO.class);
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
                     * 获取列表
                     */
                    @SaCheckPermission("xxxxxx")
                    @GetMapping("/page-list")
                    public ApiResponse getPageList(@ModelAttribute BasePageQuery query) throws Exception {
                        try {
                            LambdaQueryWrapper<#clazzName#Entity> queryWrapper = new QueryWrapper<#clazzName#Entity>().lambda();
                            PageListResult<#clazzName#VO> result = service.getPageList(new Page<>(query.getPageNum(), query.getPageSize()), queryWrapper);
                            return ApiResponseUtil.getApiResponseSuccess(result);
                
                        } catch(Exception ex){
                            log.error(this.getClass().getName() + " error" + ex,ex);
                            return ApiResponseUtil.getApiResponseFailure();
                        }
                    }
                
                    /**
                     * 获取列表
                     */
                    @SaCheckPermission("xxxxxx")
                    @GetMapping("/list")
                    public ApiResponse getList(@ModelAttribute BasePageQuery query) throws Exception {
                        try {
                            LambdaQueryWrapper<#clazzName#Entity> queryWrapper = new QueryWrapper<#clazzName#Entity>().lambda();
                            //queryWrapper.eq(true, "name", "name47");
                            List<#clazzName#VO> result = service.getList(queryWrapper);
                            return ApiResponseUtil.getApiResponseSuccess(result);
                
                        } catch(Exception ex){
                            log.error(this.getClass().getName() + " error" + ex,ex);
                            return ApiResponseUtil.getApiResponseFailure();
                        }
                    }
                }
                
                """;

        content = content.replaceAll("#basePackage#", basePackage)
                .replaceAll("#packageName#", packageName)
                .replaceAll("#clazzName#", clazzName)
                .replaceAll("#clazzNameApi#", clazzName.toLowerCase())
                .replaceAll("#apiVersion#", apiVersion);

        writeToFile(content,
                basePackage + ".controller." + params.getPackageName(),
                params.getClazzName() + "Controller.java",
                params.getAutoDeploy());
    }

    public static void generateMapper(GenerateParams params) throws Exception {
        String basePackage = params.getBasePackage();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();

        String content = """
                package #basePackage#.mapper.#packageName#;
                import com.baomidou.mybatisplus.core.mapper.BaseMapper;
                import #basePackage#.model.#packageName#.#clazzName#Entity;
                
                public interface #clazzName#Mapper extends BaseMapper<#clazzName#Entity> {
                
                }
                """;

        content = content.replaceAll("#basePackage#", basePackage)
                .replaceAll("#packageName#", packageName)
                .replaceAll("#clazzName#", clazzName);

        writeToFile(content,
                basePackage+".mapper." +params.getPackageName(),
                params.getClazzName()+"Mapper.java",
                params.getAutoDeploy());
    }

    public static void generateService(GenerateParams params) throws Exception {
        String basePackage = params.getBasePackage();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();

        String content = """
                package #basePackage#.service.#packageName#;
                
                import com.baomidou.mybatisplus.core.conditions.Wrapper;
                import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
                import com.baomidou.mybatisplus.extension.service.IService;
                import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
                import com.baomidou.mybatisplus.core.mapper.BaseMapper;
                import com.jjs.common.PageListResult;
                import #basePackage#.model.#packageName#.Convertor_#clazzName#;
                import #basePackage#.mapper.#packageName#.#clazzName#Mapper;
                import #basePackage#.model.#packageName#.#clazzName#Entity;
                import #basePackage#.model.#packageName#.#clazzName#VO;
                import lombok.extern.slf4j.Slf4j;
                import org.springframework.stereotype.Service;
                
                import java.util.List;
                
                @Service
                @Slf4j
                public class #clazzName#Service extends ServiceImpl<BaseMapper<#clazzName#Entity>, #clazzName#Entity> implements IService<#clazzName#Entity> {
                
                    final #clazzName#Mapper mapper;
                    public #clazzName#Service(#clazzName#Mapper mapper) { this.mapper = mapper; }\s
                
                    public PageListResult<#clazzName#VO> getPageList(Page<#clazzName#Entity> page, Wrapper<#clazzName#Entity> queryWrapper) throws Exception {
                        Page<#clazzName#Entity> resultPage = mapper.selectPage(page, queryWrapper);
                        List<#clazzName#Entity> list = resultPage.getRecords();
                        List<#clazzName#VO> voList = Convertor_#clazzName#.INSTANCE.convertToVoList(list);
                        PageListResult<#clazzName#VO> pageResult = new PageListResult<>();
                        pageResult.setTotalCount(resultPage.getTotal());
                        pageResult.setPageNum(page.getCurrent());
                        pageResult.setPageSize(page.getSize());
                        pageResult.setList(voList);
                        return pageResult;
                    }
                    public List<#clazzName#VO> getList(Wrapper<#clazzName#Entity> queryWrapper) throws Exception {
                        List<#clazzName#Entity> list = mapper.selectList(queryWrapper);
                        return Convertor_#clazzName#.INSTANCE.convertToVoList(list);
                    }
                }
                
                """;

        content = content.replaceAll("#basePackage#", basePackage)
                .replaceAll("#packageName#", packageName)
                .replaceAll("#clazzName#", clazzName);

        writeToFile(content,
                basePackage + ".service." + params.getPackageName(),
                params.getClazzName() + "Service.java",
                params.getAutoDeploy());
    }

    public static void generatePojo(GenerateParams params, List<TableFieldInfo> fields) throws Exception{

        String basePackage = params.getBasePackage();
        String apiVersion = params.getApiVersion();

        final String tableName = params.getTableName();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();

        String content = "package " + basePackage + ".pojo."+packageName+";\n" +
                "\n" +
                "import com.baomidou.mybatisplus.annotation.*; \n" +
                "import lombok.Getter;\n" +
                "import lombok.Setter;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@Getter\n" +
                "@Setter\n" +
                "@TableName(\""+ tableName +"\") \n" +
                "public class "+clazzName+" implements Serializable {\n";

        for (TableFieldInfo field : fields) {
            String dataType = field.getDataType();
            String columnType = field.getColumnType();
            String name = field.getColumnName();
            String comment = field.getColumnComment();

            content += "    /**\n" +
                    "     * "+ comment +"\n" +
                    "     */\n";
            if (name.equals("id")) {
                content +=
                        "    @TableId(type = IdType.AUTO) \n" +
                                "    private " + dataType(dataType, columnType) + " " + camelCase(name) + ";\n\n";
            } else if (name.equals("create_time")) {
                content +=
                        "    @TableField(updateStrategy = FieldStrategy.NEVER) \n" +
                                "    private long createTime = System.currentTimeMillis() / 1000;\n\n";
            } else if (name.equals("update_time")) {
                content +=
                        "    private long updateTime = System.currentTimeMillis() / 1000;\n\n";
            } else {
                content +=
                        "    private " + dataType(dataType, columnType) + " " + camelCase(name) + ";\n\n";
            }

        }

        content +=
                "}\n";

        writeToFile(content,
                basePackage+".pojo." +params.getPackageName(),
                clazzName+".java",
                params.getAutoDeploy());
    }

    public static void generateEntity(GenerateParams params, List<TableFieldInfo> fields) throws Exception{

        String basePackage = params.getBasePackage();
        String apiVersion = params.getApiVersion();

        final String tableName = params.getTableName();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();

        String content = "package " + basePackage + ".model."+packageName+";\n" +
                "\n" +
                "import com.baomidou.mybatisplus.annotation.*; \n" +
                "import lombok.Getter;\n" +
                "import lombok.Setter;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@Getter\n" +
                "@Setter\n" +
                "@TableName(\""+ tableName +"\") \n" +
                "public class "+clazzName+"Entity implements Serializable {\n";

        for (TableFieldInfo field : fields) {
            String dataType = field.getDataType();
            String columnType = field.getColumnType();
            String name = field.getColumnName();
            String comment = field.getColumnComment();

            content += "    /**\n" +
                    "     * "+ comment +"\n" +
                    "     */\n";
            if (name.equals("id")) {
                content +=
                        "    @TableId(type = IdType.AUTO) \n" +
                                "    private " + dataType(dataType, columnType) + " " + camelCase(name) + ";\n\n";
            } else if (name.equals("create_time")) {
                content +=
                        "    @TableField(updateStrategy = FieldStrategy.NEVER) \n" +
                                "    private long createTime = System.currentTimeMillis() / 1000;\n\n";
            } else if (name.equals("update_time")) {
                content +=
                        "    private long updateTime = System.currentTimeMillis() / 1000;\n\n";
            } else {
                content +=
                        "    private " + dataType(dataType, columnType) + " " + camelCase(name) + ";\n\n";
            }

        }

        content +=
                "}\n";

        writeToFile(content,
                basePackage+".model." +params.getPackageName(),
                clazzName+"Entity.java",
                params.getAutoDeploy());
    }
    public static void generateDTO(GenerateParams params, List<TableFieldInfo> fields) throws Exception {

        String basePackage = params.getBasePackage();
        String apiVersion = params.getApiVersion();

        final String tableName = params.getTableName();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();

        String content = "package " + basePackage + ".model." + packageName + ";\n" +
                "\n" +
                "import com.baomidou.mybatisplus.annotation.*; \n" +
                "import lombok.Getter;\n" +
                "import lombok.Setter;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@Getter\n" +
                "@Setter\n" +
                "public class " + clazzName + "DTO implements Serializable {\n";

        for (TableFieldInfo field : fields) {
            String dataType = field.getDataType();
            String columnType = field.getColumnType();
            String name = field.getColumnName();
            String comment = field.getColumnComment();

            if (name.equals("deleted") || name.equals("create_time") || name.equals("update_time")) {
                continue;
            }
            content += "    /**\n" +
                    "     * " + comment + "\n" +
                    "     */\n";
            content +=
                    "    private " + dataType(dataType, columnType) + " " + camelCase(name) + ";\n\n";

        }

        content +=
                "}\n";

        writeToFile(content,
                basePackage + ".model." + params.getPackageName(),
                clazzName + "DTO.java",
                params.getAutoDeploy());
    }
    public static void generateVO(GenerateParams params, List<TableFieldInfo> fields) throws Exception {

        final String tableName = params.getTableName();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();
        String basePackage = params.getBasePackage();
        String apiVersion = params.getApiVersion();

        String content = "package " + basePackage + ".model." + packageName + ";\n" +
                "\n" +
                "import com.baomidou.mybatisplus.annotation.*; \n" +
                "import lombok.Getter;\n" +
                "import lombok.Setter;\n" +
                "\n" +
                "import java.io.Serializable;\n" +
                "import java.util.Date;\n" +
                "\n" +
                "@Getter\n" +
                "@Setter\n" +
                "public class " + clazzName + "VO implements Serializable {\n";

        for (TableFieldInfo field : fields) {
            String dataType = field.getDataType();
            String columnType = field.getColumnType();
            String name = field.getColumnName();
            String comment = field.getColumnComment();

            content += "    /**\n" +
                    "     * " + comment + "\n" +
                    "     */\n";
            content +=
                    "    private " + dataType(dataType, columnType) + " " + camelCase(name) + ";\n\n";

        }

        content +=
                "}\n";

        writeToFile(content,
                basePackage + ".model." + params.getPackageName(),
                clazzName + "VO.java",
                params.getAutoDeploy());
    }
    public static void generateConvertor(GenerateParams params) throws Exception {

        final String tableName = params.getTableName();
        final String packageName = params.getPackageName();
        final String clazzName = params.getClazzName();
        String basePackage = params.getBasePackage();

        String content = """
                package #basePackage#.model.#packageName#;
                
                import #basePackage#.utils.TypeConverter;
                import com.jjs.common.PageListResult;
                import org.mapstruct.Mapper;
                import org.mapstruct.factory.Mappers;
                
                import java.util.Collection;
                import java.util.List;
                
                @Mapper(uses = {TypeConverter.class})
                public interface Convertor_#clazzName# {
                    Convertor_#clazzName# INSTANCE = Mappers.getMapper(Convertor_#clazzName#.class);
                     //    @Mapping(
                     //            target = "createTimeV2",
                     //            source = "createTimeV2",
                     //            qualifiedByName = "dateToString"
                     //    )
                     #clazzName#VO convertToVo(#clazzName#Entity entity);
                     #clazzName#Entity convertToEntity(#clazzName#DTO dto);
                     List<#clazzName#VO> convertToVoList(Collection<#clazzName#Entity> sourceList);
                     PageListResult<#clazzName#VO> convertToPageResult(PageListResult<#clazzName#Entity> sourcePageResult);
                }
              
                """;

        content = content.replaceAll("#basePackage#", basePackage)
                .replaceAll("#packageName#", packageName)
                .replaceAll("#clazzName#", clazzName);

        writeToFile(content,
                basePackage + ".model." + params.getPackageName(),
                "Convertor_"+clazzName + ".java",
                params.getAutoDeploy());
    }

    public static void generateMapperXml(GenerateParams params, List<TableFieldInfo> fields) throws Exception{

        String basePackage = params.getBasePackage();
        String apiVersion = params.getApiVersion();

        boolean generateInsert = false;
        boolean generateUpdate = false;
        boolean generateDelete = false;
        boolean generateGetModel = false;
        boolean generateSelectList = false;
        boolean generateSelectPageList = false;
        boolean generateSelectPageCount = false;

        String sqlColumns = "";
        for (int idx = 0; idx < fields.size(); idx++) {
            TableFieldInfo field = fields.get(idx);
            sqlColumns += "            " + field.getColumnName();
            if (idx == fields.size()-1) {
                sqlColumns += "\n";
            } else {
                sqlColumns += ",\n";
            }
        }

        String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE mapper PUBLIC \"-//mybatis.org//DTD Mapper 3.0//EN\" \"http://mybatis.org/dtd/mybatis-3-mapper.dtd\" >\n" +
                "\n" +
                "<mapper namespace=\"" + basePackage + ".mapper."+params.getPackageName()+"."+params.getClazzName()+"Mapper\">\n" +
                "\n" +

                "    <!--<sql id=\"selectComps\">\n" +
                "        select\n" + sqlColumns + "        from "+params.getTableName()+"\n" +
                "    </sql>\n -->" +
                "\n";

        // 添加 insert方法
        if (generateInsert) {
            content += "    <insert id=\"insert\" parameterType=\"" + basePackage + ".model." + params.getPackageName() + "Entity." + params.getClazzName() + "\" useGeneratedKeys=\"true\" keyProperty=\"id\">\n" +
                    "        INSERT INTO " + params.getTableName() + "(\n";

            for (int idx = 0; idx < fields.size(); idx++) {
                TableFieldInfo field = fields.get(idx);
                if (field.getColumnName().equals("id")) {
                    continue;
                }
                content += "            " + field.getColumnName();
                if (idx == fields.size() - 1) {
                    content += "\n";
                } else {
                    content += ",\n";
                }
            }

            content += "        ) values (\n";

            for (int idx = 0; idx < fields.size(); idx++) {
                TableFieldInfo field = fields.get(idx);
                if (field.getColumnName().equals("id")) {
                    continue;
                }
                content += "            #{" + camelCase(field.getColumnName()) + "}";
                if (idx == fields.size() - 1) {
                    content += "\n";
                } else {
                    content += ",\n";
                }
            }

            content += "        )\n" +
                    "    </insert>\n";
        }

        if (generateUpdate) {
            content += "\n" +
                    "    <update id=\"update\" parameterType=\"" + basePackage + ".model." + params.getPackageName() + "Entity." + params.getClazzName() + "\">\n" +
                    "        update " + params.getTableName() + " set\n";

            for (int idx = 0; idx < fields.size(); idx++) {
                TableFieldInfo field = fields.get(idx);
                if (field.getColumnName().equals("id")) {
                    continue;
                }
                content += "            " + field.getColumnName() + " = #{" + camelCase(field.getColumnName()) + "}";
                if (idx == fields.size() - 1) {
                    content += "\n";
                } else {
                    content += ",\n";
                }
            }

            content += "        where id = #{id}\n" +
                    "    </update>\n" +
                    "\n";
        }

        if (generateDelete) {
            content += "    <!-- 删除数据 -->\n" +
                    "    <delete id=\"delete\" parameterType=\"int\">\n" +
                    "        delete from " + params.getTableName() + " where id = #{id}\n" +
                    "    </delete>\n";
        }

        if (generateGetModel) {
            content += "\n" +
                    "    <select id=\"getModel\" parameterType=\"HashMap\" resultType=\"" + basePackage + ".model." + params.getPackageName() + "." + params.getClazzName() + "Entity\">\n" +
                    "        <include refid=\"selectComps\"/>\n" +
                    "        where id = #{id} \n" +
                    "        limit 1\n" +
                    "    </select>\n";
        }

        if (generateSelectList) {
            content += "\n" +
                    "    <select id=\"getList\" parameterType=\"HashMap\" resultType=\"" + basePackage + ".model." + params.getPackageName() + "." + params.getClazzName() + "Entity\">\n" +
                    "        <include refid=\"selectComps\"/>\n" +
                    "        where 1 = 1\n" +
                    "        <if test=\"id != null\">\n" +
                    "            and id = #{id}\n" +
                    "        </if>\n" +
                    "    </select>\n" +
                    "\n";
        }

        if (generateSelectPageList) {
            content += "    <select id=\"getPageList\" parameterType=\"HashMap\" resultType=\"" + basePackage + ".model." + params.getPackageName() + "." + params.getClazzName() + "Entity\">\n" +
                    "        <include refid=\"selectComps\"/>\n" +
                    "        where 1 = 1\n" +
                    "        <if test=\"id != null\">\n" +
                    "            and id = #{id}\n" +
                    "        </if>\n" +
                    "        limit #{startIndex},#{pageSize}\n" +
                    "    </select>\n" +
                    "\n";
        }

        if (generateSelectPageCount) {
            content += "    <!-- 总记录数 -->\n" +
                    "    <select id=\"getListCount\" resultType=\"Integer\">\n" +
                    "        select count(0) from " + params.getTableName() + "\n" +
                    "    </select>\n" +
                    "\n";
        }

        content +=  "</mapper>";

        writeToFile(content,
                basePackage+".mapper." +params.getPackageName(),
                params.getClazzName()+"Mapper.xml",
                params.getAutoDeploy());
    }

    private static void writeToFile(String content, String packageName, String filename, boolean deploy) throws Exception{
        String path = System.getProperty("user.dir");

        String codeDir = path + "/code/";
        if (deploy) {
            // 自动部署到项目中
            packageName = packageName.replace(".","/");
            codeDir = path + "/src/main/java/" + packageName;
        }
        filename = codeDir + "/" + filename;
        // 创建code目录
        Path rootLocation = Paths.get(codeDir);
        if(Files.notExists(rootLocation)){
            Files.createDirectories(rootLocation);
        }

        FileOutputStream fos = new FileOutputStream(filename, false); //true表示在文件末尾追加
        fos.write(content.getBytes());
        fos.close();
    }

    private static String dataType(String dataType, String columnType) {

        List<String> stringList = Arrays.asList("CHAR","VARCHAR","TINYBLOB","TINYTEXT","BLOB","TEXT","MEDIUMBLOB","MEDIUMTEXT","LONGBLOB","LONGTEXT");
        List<String> dateList = Arrays.asList("DATE","TIME","YEAR","DATETIME","TIMESTAMP");
        List<String> longList = Arrays.asList("MEDIUMINT","INTEGER","BIGINT");
        List<String> intList = Arrays.asList("SMALLINT","INT");

        String type = dataType.toUpperCase();
        columnType = columnType.toUpperCase();

        if ("TINYINT(1)".equals(columnType)) {
            return "boolean";
        } else if ("FLOAT".equals(type)) {
            return "float";
        }else if ("DOUBLE".equals(type)) {
            return "double";
        }else if ("DECIMAL".equals(type)) {
            return "double";
        } else if (stringList.contains(type)) {
            return "String";
        } else if (dateList.contains(type)) {
            return "Date";
        } else if (intList.contains(type)) {
            return "int";
        } else if (longList.contains(type)) {
            return "long";
        }
        return "String";
    }

    private static String camelCase(String input) {
        String[] words = input.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (result.length() == 0) {
                result.append(word.toLowerCase());
            } else {
                result.append(word.substring(0, 1).toUpperCase());
                result.append(word.substring(1).toLowerCase());
            }
        }
        String camelCase = result.toString();
        return camelCase;
    }
}
