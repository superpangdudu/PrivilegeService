package com.plus3.privilege.service;

import com.plus3.privilege.common.ObjectCacheMap;
import com.plus3.privilege.dao.entity.Permission;
import com.plus3.privilege.dao.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class PermissionService {
    //===================================================================================
    public interface PermissionServiceListener {
        void onPermissionCreated(Permission permission);
        void onPermissionUpdated(Permission permission);
        void onPermissionDeleted(Permission permission);
        void onPermissionReset(List<Permission> permissionList);
    }

    private List<PermissionServiceListener> listenerList = new ArrayList<>();

    public void addListener(PermissionServiceListener listener) {
        listenerList.add(listener);
    }

    private void notifyPermissionCreated(Permission permission) {
        for (PermissionServiceListener listener : listenerList)
            listener.onPermissionCreated(permission);
    }

    private void notifyPermissionUpdated(Permission permission) {
        for (PermissionServiceListener listener : listenerList)
            listener.onPermissionUpdated(permission);
    }

    private void notifyPermissionDeleted(Permission permission) {
        for (PermissionServiceListener listener : listenerList)
            listener.onPermissionDeleted(permission);
    }

    private void notifyPermissionReset(List<Permission> permissionList) {
        for (PermissionServiceListener listener : listenerList)
            listener.onPermissionReset(permissionList);
    }

    //===================================================================================
    @Autowired private PermissionMapper permissionMapper;

    private ObjectCacheMap<Integer, Permission> permissionMap = new ObjectCacheMap<>();

    //===================================================================================
    public synchronized void reset() {
        permissionMap.clear();

        List<Permission> permissionList = permissionMapper.selectAll();
        if (permissionList == null)
            return;

        for (Permission permission : permissionList)
            permissionMap.put(permission.getId(), permission);

        notifyPermissionReset(permissionList);
    }

    public Permission createPermission(int parentId, String name, String url, String type, String description) {
        Permission permission = new Permission();
        permission.setParentId(parentId);
        permission.setName(name);
        permission.setUrl(url);
        permission.setType(type);
        permission.setDescription(description);

        int ret = permissionMapper.insertSelective(permission);
        if (ret <= 0)
            return null;

        permissionMap.put(permission.getId(), permission);

        //
        notifyPermissionCreated(permission);
        return permission;
    }

    public int updatePermission(Permission permission) {
        int ret = permissionMapper.updateByPrimaryKey(permission);
        if (ret <= 0)
            return -1;

        permissionMap.put(permission.getId(), permission);

        //
        notifyPermissionUpdated(permission);
        return 0;
    }

    public int deletePermission(int id) {
        Permission permission = permissionMap.get(id);
        if (permission == null)
            permission = permissionMapper.selectByPrimaryKey(id);

        if (permission == null)
            return -1;

        int ret = permissionMapper.deleteByPrimaryKey(id);
        if (ret <= 0)
            return -1;

        permissionMap.remove(id);

        //
        notifyPermissionDeleted(permission);
        return 0;
    }

    public Permission getById(int id) {
        Permission permission = permissionMap.get(id);
        if (permission != null)
            return permission;

        permission = permissionMapper.selectByPrimaryKey(id);
        if (permission != null)
            permissionMap.put(id, permission);

        return permission;
    }

    public List<Permission> getAllPermission() {
        List<Permission> permissionList = new ArrayList<>();
        permissionList.addAll(permissionMap.values());

        return permissionList;
    }

    public List<Permission> getParents(int permissionId) {
        List<Permission> permissionList = new ArrayList<>();

        Permission permission = permissionMap.get(permissionId);
        if (permission == null)
            return permissionList;

        while (permission.getParentId() != -1) {
            permission = permissionMap.get(permission.getParentId());
            permissionList.add(permission);
        }

        return permissionList;
    }

    public List<Permission> getChildren(int permissionId) {
        List<Permission> permissionList = new ArrayList<>();

        Collection<Permission> permissionCollection = permissionMap.values();
        for (Permission permission : permissionCollection) {
            if (permission.getParentId() == permissionId)
                permissionList.add(permission);
        }

        return permissionList;
    }
}
