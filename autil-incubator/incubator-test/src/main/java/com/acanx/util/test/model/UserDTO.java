package com.acanx.util.test.model;

import java.util.List;

/**
 * 用户数据传输对象（源对象）
 */
public class UserDTO {

    private String name;
    private int age;
    private boolean active;
    private String email;
    private String password;
    private Long id;
    private List<String> roles;

    public UserDTO() {
    }

    public UserDTO(String name, int age, boolean active, String email, String password, Long id, List<String> roles) {
        this.name = name;
        this.age = age;
        this.active = active;
        this.email = email;
        this.password = password;
        this.id = id;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", id=" + id +
                ", roles=" + roles +
                '}';
    }
}
