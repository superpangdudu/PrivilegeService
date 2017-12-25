package com.plus3.privilege.service;

import com.plus3.privilege.common.ObjectCacheMap;
import com.plus3.privilege.dao.entity.Group;
import com.plus3.privilege.dao.mapper.GroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class GroupService {
    //===================================================================================
    public interface GroupServiceListener {
        void onGroupCreated(Group group);
        void onGroupUpdated(Group group);
        void onGroupDeleted(Group group);
        void onGroupReset(List<Group> groupList);
    }

    private List<GroupServiceListener> groupServiceListenerList = new ArrayList<>();

    public void addListener(GroupServiceListener listener) {
        groupServiceListenerList.add(listener);
    }

    private void notifyGroupCreated(Group group) {
        for (GroupServiceListener listener : groupServiceListenerList)
            listener.onGroupCreated(group);
    }

    private void notifyGroupUpdated(Group group) {
        for (GroupServiceListener listener : groupServiceListenerList)
            listener.onGroupUpdated(group);
    }

    private void notifyGroupDeleted(Group group) {
        for (GroupServiceListener listener : groupServiceListenerList)
            listener.onGroupDeleted(group);
    }

    private void notifyGroupReset(List<Group> groupList) {
        for (GroupServiceListener listener : groupServiceListenerList)
            listener.onGroupReset(groupList);
    }

    //===================================================================================
    @Autowired private GroupMapper groupMapper;

    private ObjectCacheMap<Integer, Group> groupMap = new ObjectCacheMap<>();

    //===================================================================================
    public synchronized void reset() {
        groupMap.clear();

        List<Group> groupList = groupMapper.selectAll();
        if (groupList == null)
            return;

        for (Group group : groupList)
            groupMap.put(group.getId(), group);

        notifyGroupReset(groupList);
    }

    public Group createGroup(int parentId, String parentToken, int creatorId,
                             String name, String description, String domain) {
        Group group = new Group();
        group.setParentId(parentId);
        group.setName(name);
        group.setDescription(description);
        group.setDomain(domain);

        int ret = groupMapper.insertSelective(group);
        if (ret <= 0)
            return null;

        groupMap.put(group.getId(), group);

        group.setToken(parentToken + "_" + group.getId());
        groupMapper.updateByPrimaryKey(group);

        //
        notifyGroupCreated(group);
        return group;
    }

    public Group getById(int id) {
        Group group = groupMap.get(id);
        if (group != null)
            return group;

        group = groupMapper.selectByPrimaryKey(id);
        if (group == null)
            return null;

        groupMap.put(id, group);
        return group;
    }

    public Group getByName(String name, String token) {
        Collection<Group> groupCollection = groupMap.values();
        for (Group group : groupCollection) {
            if (group.getName().equals(name)
                    && group.getToken().equals(token))
                return group;
        }

        Group group = groupMapper.selectByName(name, token);
        if (group == null)
            return null;

        groupMap.put(group.getId(), group);
        return group;
    }

    public List<Group> getWithChildren(String groupToken) {
        List<Group> groupList = new ArrayList<>();

        Collection<Group> groupCollection = groupMap.values();
        for (Group group : groupCollection) {
            if (group.getToken().startsWith(groupToken))
                groupList.add(group);
        }

        return groupList;
    }

    public int updateGroup(Group group) {
        int ret = groupMapper.updateByPrimaryKey(group);
        if (ret <= 0)
            return -1;

        groupMap.put(group.getId(), group);

        //
        notifyGroupUpdated(group);
        return 0;
    }

    public int deleteGroup(int id) {
        int ret = groupMapper.deleteByPrimaryKey(id);
        if (ret <= 0)
            return -1;

        Group group = groupMap.remove(id);

        //
        notifyGroupDeleted(group);
        return 0;
    }
}
