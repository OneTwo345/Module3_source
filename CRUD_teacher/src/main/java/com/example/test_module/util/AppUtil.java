package com.example.test_module.util;

import com.example.test_module.util.AppConstant;
import com.example.test_module.util.RunnableCustom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUtil {
    public static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper();
    }
    public static Object getObject(HttpServletRequest request, Class clazz) {
        // tạo object bằng contructor không tham số.
        Object object;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }

        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();

            if(AppConstant.ACTION.equals(paramName)){
                continue;
            }

            System.out.println(request.getParameter(paramName));
            // Use reflection to set the parameter value to the corresponding field in the User class
            try {
                String paramValue = mapper.writeValueAsString(request.getParameter(paramName));



                // lấy value ra

                Field field = clazz.getDeclaredField(paramName);
                var fieldType = field.getType();
                field.setAccessible(true); // Set accessible, as the fields may be private


                var value = mapper.readValue(paramValue,fieldType);
                field.set(object, value);
                //set cho tung field
                // Add more type conversions as needed for other field types (e.g., boolean, double, etc.)
            } catch (NoSuchFieldException | IllegalAccessException | NumberFormatException e) {
                // Handle exceptions as needed
                System.out.println(e.getMessage());
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
            }

        }
        return object;
    }
    public static Object getObjectWithValidation(HttpServletRequest request, Class clazz, Map<String, RunnableCustom> validators){
        // tạo object bằng contructor không tham số.
        Object object;
        try {
            object = clazz.newInstance();
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }

        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if(AppConstant.ACTION.equals(paramName)){
                continue;
            }

            System.out.println(request.getParameter(paramName));
            // Use reflection to set the parameter value to the corresponding field in the User class
            try {
                // lấy value ra
                String paramValue = mapper.writeValueAsString(request.getParameter(paramName));
                if(paramName.contains("_")){
                    //handle cho việc object lồng object.
                    String[] fields = paramName.split("_");
                    Field field = clazz.getDeclaredField(fields[0]);
                    field.setAccessible(true); // chuyen public
                    var fieldType = field.getType(); // class Name
                    var objectChild = fieldType.newInstance(); // tạo 1 object
                    field.set(object, objectChild);
                    Field fieldChild = fieldType.getDeclaredField(fields[1]); // field object con
                    fieldChild.setAccessible(true);
                    var value = mapper.readValue(paramValue,fieldChild.getType());
                    fieldChild.set(objectChild, value);
                    continue;
                }
                RunnableCustom validator = validators.get(paramName);
                if(validator != null){
                    validator.setValue(request.getParameter(paramName));
                    validator.run();
                }
                Field field = clazz.getDeclaredField(paramName);
                field.setAccessible(true); // Set accessible, as the fields may be private
                Class<?> fieldType = field.getType();

                var value = mapper.readValue(paramValue,fieldType);
                field.set(object, value);
                //set cho tung field
                // Add more type conversions as needed for other field types (e.g., boolean, double, etc.)
            } catch (NoSuchFieldException | IllegalAccessException | NumberFormatException | InstantiationException e) {
                // Handle exceptions as needed
                System.out.println(e.getMessage());
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
            }

        }
        return object;
    }

    public static Object getParameterWithDefaultValue(HttpServletRequest request, String name, Object valueDefault){
        String value = request.getParameter(name);
        if(value == null)return valueDefault;
        return value;
    }

    public static String buildInsertSql(String tableName, Object object) {
        List<Object> arrayValue = new ArrayList<>();
        StringBuilder sql = new StringBuilder("INSERT INTO " + tableName + " (");
        try{
            Object value = null;
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                field.setAccessible(true);
                value = field.get(object);

                if (fieldName.equals("serialVersionUID") || fieldName.equals("id") || value == null) {
                    continue;
                }

                var sqlAppendField = new StringBuilder(camelCaseToSnakeCase(fieldName));

                if(!field.getType().isEnum() && field.getType().getName().contains("model")){
                    sqlAppendField = new StringBuilder(camelCaseToSnakeCase(fieldName) + "_id");
                    var objectChild = field.get(object);
                    var fieldId =objectChild.getClass().getDeclaredField("id");
                    fieldId.setAccessible(true);
                    value = fieldId.get(objectChild);
                }
                arrayValue.add(value);
                sql.append(sqlAppendField);
                if (i < fields.length - 1) {
                    sql.append(",");
                }
            }
            sql.append(") VALUES (");
            for (int i = 0; i < arrayValue.size(); i++) {
                sql.append("'").append(arrayValue.get(i)).append("'");
                if (i < arrayValue.size() - 1) {
                    sql.append(",");
                }
            }
            sql.append(")");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return sql.toString();
    }
    public static String buildUpdateSql(String tableName, Object object) {
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        Object id = 0L;
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                String fieldName = field.getName();
                Object value = field.get(object);
                if(fieldName.equals("id")){
                    id = value;
                }
                if (fieldName.equals("serialVersionUID")
                        || fieldName.equals("id")
                        || value == null) {
                    continue;
                }
                if(!field.getType().isEnum() && field.getType().getName().contains("model")){

                    Field fld = object.getClass().getDeclaredField(fieldName);
                    fld.setAccessible(true);
                    var objectChild = fld.get(object);
                    var fieldIdChild = objectChild.getClass().getDeclaredField("id");
                    fieldIdChild.setAccessible(true);
                    var idChild = fieldIdChild.get(objectChild);
                    sql.append(camelCaseToSnakeCase(fieldName + "_id")).append("='").append(idChild).append("'");
                    sql.append(",");
                    continue;
                }
                sql.append(camelCaseToSnakeCase(fieldName)).append("='").append(value).append("'");

                sql.append(",");
            }
            sql.deleteCharAt(sql.length() -1);
            sql.append(" WHERE (id = '").append(id).append("')");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return sql.toString();
    }
    private static boolean isExcluded(String fieldName, String[] excludedFields) {
        for (String excludedField : excludedFields) {
            if (excludedField.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
    public static String camelCaseToSnakeCase(String camelCase) {
        return camelCase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
    }
    public static <T> T getObjectFromResultSet(ResultSet rs, Class<T> clazz) {
        T object;
        try {
            object = clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                String fieldName = field.getName();
                if (fieldName.equals("serialVersionUID")) {
                    continue;
                }

                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                if(!fieldType.isEnum() && fieldType.getPackage().getName().contains("model")){
                    var objectChild = fieldType.getDeclaredConstructor().newInstance();
                    for (var fieldChild: fieldType.getDeclaredFields()) {
                        String fieldChildName = fieldChild.getName();
                        fieldChild.setAccessible(true);
                        String paramValue = mapper
                                .writeValueAsString(rs.getObject
                                        (camelCaseToSnakeCase(fieldName)+ "." +camelCaseToSnakeCase(fieldChildName)));
                        Object value = mapper.readValue(paramValue, fieldChild.getType());
                        fieldChild.set(objectChild, value);
                    }
                    field.set(object, objectChild);
                    continue;
                }

                String paramValue = mapper.writeValueAsString(rs.getObject(camelCaseToSnakeCase(fieldName)));
                Object value = mapper.readValue(paramValue, fieldType);
                field.set(object, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return object;
    }

}
