package com.acanx.util.test.model;

/**
 * 基础实体类（父类）
 */
public class BaseEntity {

    private Long id;
    private String createTime;

    public BaseEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
