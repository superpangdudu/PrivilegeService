package com.plus3.privilege.dao.entity;

import java.io.Serializable;

public class Permission implements Serializable {
    private Integer id;
    private Integer parentId;
    private String name;
    private String url;
    private String type;
    private String description;
    private String extra;


    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getExtra() {
        return extra;
    }
    public void setExtra(String extra) {
        this.extra = extra;
    }

    //===================================================================================
    @Override
    public boolean equals(Object object) {
        if (object instanceof Permission == false)
            return false;

        Permission permission = (Permission) object;
        if (permission == this)
            return true;
        if (permission.getId() == getId())
            return true;
        return false;
    }
}