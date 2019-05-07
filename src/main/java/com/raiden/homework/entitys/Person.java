package com.raiden.homework.entitys;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: Raiden
 * Date: 2019/4/30
 */
@Table(name = "person")
@Data
public class Person {
    @Id
    Integer id;
    @Column
    String name;
    Integer gender;
    Date birth;

}
