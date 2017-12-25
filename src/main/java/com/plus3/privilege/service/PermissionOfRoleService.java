package com.plus3.privilege.service;

import com.plus3.privilege.common.ObjectCacheMap;
import com.plus3.privilege.dao.entity.Permission;
import com.plus3.privilege.dao.entity.PermissionOfRole;
import com.plus3.privilege.dao.entity.Role;
import com.plus3.privilege.dao.mapper.PermissionOfRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class PermissionOfRoleService implements RoleService.RoleServiceListener,
        PermissionService.PermissionServiceListener {
    //===================================================================================
    public interface PermissionOfRoleServiceListener {
        void onPermissionOfRoleCreated(PermissionOfRole permissionOfRole);
        void onPermissionOfRoleUpdated(PermissionOfRole permissionOfRole);
        void onPermissionOfRoleDeleted(PermissionOfRole permissionOfRole);
        void onPermissionOfRoleReset(List<PermissionOfRole> permissionOfRoleList);
    }

    private List<PermissionOfRoleServiceListener> permissionOfRoleServiceListenerList = new ArrayList<>();

    public void addListener(PermissionOfRoleServiceListener listener) {
        permissionOfRoleServiceListenerList.add(listener);
    }

    private void notifyPermissionOfRoleCreated(PermissionOfRole permissionOfRole) {
        for (PermissionOfRoleServiceListener listener : permissionOfRoleServiceListenerList)
            listener.onPermissionOfRoleCreated(permissionOfRole);
    }

    private void notifyPermissionOfRoleUpdated(PermissionOfRole permissionOfRole) {
        for (PermissionOfRoleServiceListener listener : permissionOfRoleServiceListenerList)
            listener.onPermissionOfRoleUpdated(permissionOfRole);
    }

    private void notifyPermissionOfRoleDeleted(PermissionOfRole permissionOfRole) {
        for (PermissionOfRoleServiceListener listener : permissionOfRoleServiceListenerList)
            listener.onPermissionOfRoleDeleted(permissionOfRole);
    }

    private void notifyPermissionOfRoleReset(List<PermissionOfRole> permissionOfRoleList) {
        for (PermissionOfRoleServiceListener listener : permissionOfRoleServiceListenerList)
            listener.onPermissionOfRoleReset(permissionOfRoleList);
    }

    //===================================================================================
    @Autowired private PermissionOfRoleMapper permissionOfRoleMapper;

    /**
     * Key: permission id
     * Value: Permission
     */
    private ObjectCacheMap<Integer, PermissionOfRole> permissionOfRoleMap = new ObjectCacheMap<>();

    /**
     * Key： role id
     * Value: PermissionOfRole list
     */
    private ObjectCacheMap<Integer, List<PermissionOfRole>> rolePermissionsMap = new ObjectCacheMap<>();

    //===================================================================================
    public void reset() {
        permissionOfRoleMap.clear();

        List<PermissionOfRole> permissionOfRoleList = permissionOfRoleMapper.selectAll();
        if (permissionOfRoleList == null)
            return;

        for (PermissionOfRole permissionOfRole : permissionOfRoleList) {
            permissionOfRoleMap.put(permissionOfRole.getId(), permissionOfRole);
            addToRolePermissionList(permissionOfRole);
        }

        //
        notifyPermissionOfRoleReset(permissionOfRoleList);
    }

    public PermissionOfRole createPermissionOfRole(int roleId, int permissionId, String extra) {
        PermissionOfRole permissionOfRole = new PermissionOfRole();
        permissionOfRole.setRoleId(roleId);
        permissionOfRole.setPermissionId(permissionId);
        permissionOfRole.setExtra(extra);

        int ret = permissionOfRoleMapper.insertSelective(permissionOfRole);
        if (ret <= 0)
            return null;

        permissionOfRoleMap.put(permissionOfRole.getId(), permissionOfRole);
        addToRolePermissionList(permissionOfRole);

        //
        notifyPermissionOfRoleCreated(permissionOfRole);
        return permissionOfRole;
    }

    public List<PermissionOfRole> getRolePermissions(int roleId) {
        return rolePermissionsMap.get(roleId);
    }

    public PermissionOfRole getPermissionOfRole(int id) {
        return permissionOfRoleMap.get(id);
    }

    public int updatePermissionOfRole(int id, String extra) {
        PermissionOfRole permissionOfRole = permissionOfRoleMap.get(id);
        if (permissionOfRole == null) {
            permissionOfRole = permissionOfRoleMapper.selectByPrimaryKey(id);
            if (permissionOfRole == null)
                return -1;

            addToRolePermissionList(permissionOfRole);
        }

        permissionOfRole.setExtra(extra);
        int ret = permissionOfRoleMapper.updateByPrimaryKeySelective(permissionOfRole);
        if (ret <= 0)
            return -1;

        permissionOfRoleMap.put(permissionOfRole.getId(), permissionOfRole);

        //
        notifyPermissionOfRoleUpdated(permissionOfRole);
        return 0;
    }

    public PermissionOfRole deletePermissionOfRole(int id) {
        PermissionOfRole permissionOfRole = permissionOfRoleMap.get(id);
        if (permissionOfRole == null)
            permissionOfRole = permissionOfRoleMapper.selectByPrimaryKey(id);

        if (permissionOfRole == null)
            return null;

        int ret = permissionOfRoleMapper.deleteByPrimaryKey(id);
        if (ret <= 0)
            return null;

        permissionOfRoleMap.remove(id);
        rolePermissionsMap.get(permissionOfRole.getRoleId()).remove(permissionOfRole);

        //
        notifyPermissionOfRoleDeleted(permissionOfRole);
        return permissionOfRole;
    }

    //===================================================================================
    private void addToRolePermissionList(PermissionOfRole permissionOfRole) {
        int roleId = permissionOfRole.getRoleId();

        List<PermissionOfRole> permissionOfRoleList = rolePermissionsMap.get(roleId);
        if (permissionOfRoleList == null) {
            permissionOfRoleList = new ArrayList<>();
            rolePermissionsMap.put(roleId, permissionOfRoleList);
        }

        permissionOfRoleList.add(permissionOfRole);
    }

    //===================================================================================
    // RoleService.RoleServiceListener implementation
    @Override
    public void onRoleCreated(Role role) {
        // nothing to do
    }

    @Override
    public void onRoleUpdated(Role role) {
        // nothing to do
    }

    @Override
    public void onRoleDeleted(Role role) {
        // delete PermissionOfRole against the Role
        Collection<PermissionOfRole> permissionOfRoleCollection = permissionOfRoleMap.values();
        for (PermissionOfRole permissionOfRole : permissionOfRoleCollection) {
            if (permissionOfRole.getRoleId() != role.getId())
                continue;

            deletePermissionOfRole(permissionOfRole.getId());
        }
    }

    @Override
    public void onRoleReset(List<Role> roleList) {
        // nothing to do
    }

    //===================================================================================
    // PermissionService.PermissionServiceListener implementation
    @Override
    public void onPermissionCreated(Permission permission) {
        // nothing to do
    }

    @Override
    public void onPermissionUpdated(Permission permission) {
        // nothing to do
    }

    @Override
    public void onPermissionDeleted(Permission permission) {
        // delete PermissionOfRole against the Permission
        Collection<PermissionOfRole> permissionOfRoleCollection = permissionOfRoleMap.values();
        for (PermissionOfRole permissionOfRole : permissionOfRoleCollection) {
            if (permissionOfRole.getRoleId() != permission.getId())
                continue;

            deletePermissionOfRole(permissionOfRole.getId());
        }
    }

    @Override
    public void onPermissionReset(List<Permission> permissionList) {
        // nothing to do
    }
}
