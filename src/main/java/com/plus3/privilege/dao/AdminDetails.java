package com.plus3.privilege.dao;

import com.alibaba.fastjson.JSONObject;
import com.plus3.privilege.common.Action;
import com.plus3.privilege.dao.entity.Admin;
import com.plus3.privilege.dao.entity.Group;
import com.plus3.privilege.dao.entity.Permission;
import com.plus3.privilege.dao.entity.Role;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Extra format:
 * {
 *     "action": [31|16|8|4|2|1],
 *     "data": [*|group1, group2, ..., groupN|self]
 * }
 *
 * e.g.
 * {
 *     "action": "31",
 *     "data": "*"
 * }
 */
public class AdminDetails implements Serializable {
    private Admin admin;
    private Group group;
    private List<Role> roleList;
    private Collection<Permission> permissionList;
    /**
     * Key: Permission id
     * Value: Permission extra
     */
    private Map<Integer, PermissionDetails> permissionDetailsMap;

    //===================================================================================
    public AdminDetails(Admin admin,
                        Group group,
                        List<Role> roleList,
                        Collection<Permission> permissionList,
                        Map<Integer, PermissionDetails> permissionDetailsMap) {
        this.admin = admin;
        this.group = group;
        this.roleList = roleList;
        this.permissionList = permissionList;
        this.permissionDetailsMap = permissionDetailsMap;
    }

    public Admin getAdmin() {
        return admin;
    }

    public Group getGroup() {
        return group;
    }

    public List<Role> gerRoles() {
        return roleList;
    }

    public Collection<Permission> getPermissions() {
        return permissionList;
    }

    public boolean hasRole(int roleId) {
        for (Role role : roleList) {
            if (role.getId() == roleId)
                return true;
        }
        return false;
    }

    public boolean isPermitted(int permissionId) {
        if (admin.getId() == 0)
            return true;

        for (Permission permission : permissionList) {
            if (permission.getId() == permissionId)
                return true;
        }
        return false;
    }

    public boolean isPermitted(int permissionId, Action action) {
        if (permissionDetailsMap == null
                || permissionDetailsMap.size() == 0)
            return true;

        if (admin.getId() == 0)
            return true;

        if (isPermitted(permissionId) == false)
            return false;

        // all actions granted as default
        PermissionDetails permissionDetails = getPermissionDetails(permissionId);
        if (permissionDetails == null)
            return true;

        return permissionDetails.isGranted(action);
    }

    public PermissionDetails getPermissionDetails(int permissionId) {
        return permissionDetailsMap.get(permissionId);
    }
}
