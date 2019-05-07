package com.raiden.homework;

import com.raiden.homework.dao.PersonDao;
import com.raiden.homework.entitys.Person;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/29
 */
public class test {

    public static void main(String[] args) {
        PersonDao dao = new PersonDao();
        List<Person> list = dao.select();
        System.out.println(list.size());
        for (Person p : list) {
            System.out.println(p.toString());
        }
    }


}
