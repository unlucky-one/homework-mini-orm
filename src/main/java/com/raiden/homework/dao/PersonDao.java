package com.raiden.homework.dao;

import com.raiden.homework.entitys.Person;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Resource;
import javax.persistence.Column;
import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/5/6
 */
public class PersonDao {
    Properties properties = null;

    public List<Person> select() {
        List<Person> personList = new ArrayList<Person>();
        try {
            properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/application.properties"));

            Class.forName(properties.getProperty("datasource.driver"));
            String username = properties.getProperty("datasource.username");
            String password = properties.getProperty("datasource.password");
            String url = properties.getProperty("datasource.url");
            Connection con = DriverManager.getConnection(url, username, password);

            String sql = "select * from person";
            PreparedStatement statement = con.prepareStatement(sql);
            ResultSet result = statement.executeQuery();

            setMap(Person.class);
            while (result.next()) {
                Person p;
                p = convert(result, Person.class);
                if (p != null)
                    personList.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return personList;
    }

    Map<String, String> nickNameMap = new HashMap<String, String>();

    void setMap(Class clazz) {
        for (Field field : clazz.getFields()) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                nickNameMap.put(column.name(), field.getName());
            }
        }

    }

    <T> T convert(ResultSet result, Class<T> tClass) {
        T t = null;
        try {
            t = tClass.newInstance();
            int paramSize = result.getMetaData().getColumnCount();
            Constructor<?>[] constructors = tClass.getDeclaredConstructors();
            //构造方法赋值
            for (Constructor c : constructors) {
                if (c.getParameterTypes().length > 0 && c.getParameterTypes().length == paramSize) {
                    Object[] params = new Object[c.getParameterTypes().length];
                    for (int i = 0; i < paramSize; i++) {
                        params[i] = result.getObject(i + 1);
                    }
                    try {
                        return (T) c.newInstance(params);
                    } catch (Exception e) {
                        break;
                    }
                }
            }

            for (int i = 0; i < paramSize; i++) {
                Object value = result.getObject(i + 1);
                String columnName = result.getMetaData().getColumnName(i + 1);
                String name = columnName;
                if (nickNameMap.containsKey(name))
                    name = nickNameMap.get(name);
                Field field = tClass.getDeclaredField(name);
                field.setAccessible(true);
                if (value != null && value.getClass() == field.getType())
                    field.set(t, value);
                else if (field.getType() == Date.class) {
                    field.set(t, str2Date(value.toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    Date str2Date(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
