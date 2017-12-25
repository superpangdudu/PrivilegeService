package com.plus3.privilege.service;

import com.plus3.privilege.common.ObjectCacheMap;
import com.plus3.privilege.dao.entity.Group;
import com.plus3.privilege.dao.entity.Role;
import com.plus3.privilege.dao.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class RoleService implements GroupService.GroupServiceListener {
    //===================================================================================
    public interface RoleServiceListener {
        void onRoleCreated(Role role);
        void onRoleUpdated(Role role);
        void onRoleDeleted(Role role);
        void onRoleReset(List<Role> roleList);
    }

    private List<RoleServiceListener> roleServiceListeners = new ArrayList<>();

    public void addListener(RoleServiceListener listener) {
        roleServiceListeners.add(listener);
    }

    private void notifyRoleCreated(Role role) {
        for (RoleServiceListener listener : roleServiceListeners)
            listener.onRoleCreated(role);
    }

    private void notifyRoleUpdated(Role role) {
        for (RoleServiceListener listener : roleServiceListeners)
            listener.onRoleUpdated(role);
    }

    private void notifyRoleDeleted(Role role) {
        for (RoleServiceListener listener : roleServiceListeners)
            listener.onRoleDeleted(role);
    }

    private void notifyRoleReset(List<Role> roleList) {
        for (RoleServiceListener listener : roleServiceListeners)
            listener.onRoleReset(roleList);
    }

    //===================================================================================
    @Autowired private RoleMapper roleMapper;

    private ObjectCacheMap<Integer, Role> roleMap = new ObjectCacheMap<>();

    //===================================================================================
    public synchronized void reset() {
        roleMap.clear();

        List<Role> roleList = roleMapper.selectAll();
        if (roleList == null)
            return;

        for (Role role : roleList)
            roleMap.put(role.getId(), role);

        //
        notifyRoleReset(roleList);
    }

    public Role createRole(String name, int groupId, String groupToken, String description) {
        Role role = new Role();
        role.setName(name);
        role.setGroupId(groupId);
        role.setGroupToken(groupToken);
        role.setDescription(description);

        int ret = roleMapper.insertSelective(role);
        if (ret <= 0)
            return null;

        roleMap.put(role.getId(), role);

        //
        notifyRoleCreated(role);
        return role;
    }

    public List<Role> getGroupRoles(String groupToken, boolean withChildren) {
        if (withChildren)
            return getGroupRolesWithChildren(groupToken);
        return getGroupRoles(groupToken);
    }

    public int deleteRole(int id) {
        Role role = roleMap.get(id);
        if (role == null)
            role = roleMapper.selectByPrimaryKey(id);

        if (role == null)
            return -1;

        int ret = roleMapper.deleteByPrimaryKey(id);
        if (ret <= 0)
            return -1;

        roleMap.remove(id);

        //
        notifyRoleDeleted(role);
        return 0;
    }

    public int updateRole(Role role) {
        int ret = roleMapper.updateByPrimaryKey(role);
        if (ret <= 0)
            return -1;

        roleMap.put(role.getId(), role);

        //
        notifyRoleUpdated(role);
        return 0;
    }

    //===================================================================================
    private List<Role> getGroupRoles(String groupToken) {
        List<Role> roleList = new ArrayList<>();

        Collection<Role> roleCollection = roleMap.values();
        if (roleCollection == null)
            return roleList;

        for (Role role : roleCollection) {
            if (role.getGroupToken().equals(groupToken))
                roleList.add(role);
        }

        return roleList;
    }

    private List<Role> getGroupRolesWithChildren(String groupToken) {
        List<Role> roleList = new ArrayList<>();

        Collection<Role> roleCollection = roleMap.values();
        if (roleCollection == null)
            return roleList;

        for (Role role : roleCollection) {
            if (role.getGroupToken().startsWith(groupToken))
                roleList.add(role);
        }

        return roleList;
    }

    //===================================================================================
    // GroupService.GroupServiceListener implementation
    @Override
    public void onGroupCreated(Group group) {
        // nothing to do
    }

    @Override
    public void onGroupUpdated(Group group) {
        // nothing to do
    }

    @Override
    public void onGroupDeleted(Group group) {
        // delete roles against the group
        Collection<Role> roleCollection = roleMap.values();
        for (Role role : roleCollection) {
            if (role.getGroupId() != group.getId())
                continue;

            deleteRole(role.getId());
        }
    }

    @Override
    public void onGroupReset(List<Group> groupList) {
        // nothing to do
    }
}
