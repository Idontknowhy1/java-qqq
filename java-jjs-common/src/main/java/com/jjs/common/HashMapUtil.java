package com.jjs.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.util.*;

public class HashMapUtil {

    /**
     * 将对象转换为HashMap对象
     * @param obj
     * @return
     */
    public static HashMap<String, Object> objectToMap(Object obj) {

        if (obj == null) {
            return null;
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if(value != null) {
                    map.put(field.getName(), field.get(obj));
                }
            }
        } catch (Exception e) {

        }

        return map;
    }

    /**
     * 排序并拼接为queryString
     * @param map
     * @return
     */
    public static String toQueryString(Map map){

        Set<String> keySet = map.keySet();
        //定义一个list将keyset放入list中，因为Collections中排序的方法是sort，支持的类型是list，所以这里要用到list
        List<String> testlist = new ArrayList<String>(keySet);
        //使用Collections的方法sort进行排序
        Collections.sort(testlist, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        //获取list集合的迭代器，String为迭代元素的类型
        Iterator<String> iterator = testlist.iterator();
        String queryString = "";
        while(iterator.hasNext()){
            //获取到ley
            String key = iterator.next();
            //获取value
            String values = (String) map.get(key);
            if(values !=null && !values.isEmpty()){
                queryString += "&"+key+"="+values;
            }
        }
        return queryString.substring(1);

    }

    /**
     * 将HashMap转换为XML字符串
     * @param hashMap
     * @param root
     * @return
     */
    public static String toXML(HashMap hashMap,String root){
        String xml = "<"+">\n";
        Set<String> keySet = hashMap.keySet();
        for(String key : keySet){
            String value = (String) hashMap.get(key);
            xml += "    <"+key+">"+ value +"<\\"+key+">\n";
        }
        xml += "</"+root+">";
        return xml;
    }

    /**
     * JSON字符串转换为HashMap
     * @param json
     * @return
     */
    public static HashMap<String, Object> hashMapFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HashMap<String, Object> map = objectMapper.readValue(json, new TypeReference<HashMap<String, Object>>() {});
            return map;
        } catch (Exception e) {
            return null;
        }
    }
}