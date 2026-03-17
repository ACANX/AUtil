package com.acanx.util.annotation;

import java.util.List;

/**
 * Person
 *
 * @author ACANX
 * @since 20260317
 */
public class Person {

    private String name;
    private Integer age;
    private Address address;
    private List<String> hobbies;
    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 将当前对象拷贝到目标对象
     */
    public void copyTo(Person target) {
        if (target == null) return;
        if (this.name != null) target.setName(this.name);
        if (this.age != null) target.setAge(this.age);
        if (this.address != null) {
            if (target.getAddress() == null) {
                target.setAddress(new Address());
            }
            target.getAddress().setStreet(this.address.getStreet());
            target.getAddress().setCity(this.address.getCity());
            target.getAddress().setZipCode(this.address.getZipCode());
        }
        if (this.hobbies != null) {
            target.setHobbies(this.hobbies);
        }
        if (this.email != null) target.setEmail(this.email);
    }
}
