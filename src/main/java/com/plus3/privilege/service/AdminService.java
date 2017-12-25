package com.plus3.privilege.service;

import com.plus3.privilege.common.ObjectCacheMap;
import com.plus3.privilege.dao.entity.Admin;
import com.plus3.privilege.dao.entity.Group;
import com.plus3.privilege.dao.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class AdminService implements GroupService.GroupServiceListener {
    //===================================================================================
    public interface AdminServiceListener {
        void onAdminCreated(Admin admin);
        void onAdminDeleted(Admin admin);
        void onAdminUpdated(Admin admin);
        void onAdminReset(List<Admin> adminList);
    }

    private List<AdminServiceListener> adminServiceListenerList = new ArrayList<>();

    public void addListener(AdminServiceListener listener) {
        adminServiceListenerList.add(listener);
    }

    private void notifyAdminCreated(Admin admin) {
        for (AdminServiceListener listener : adminServiceListenerList)
            listener.onAdminCreated(admin);
    }

    private void notifyAdminDeleted(Admin admin) {
        for (AdminServiceListener listener : adminServiceListenerList)
            listener.onAdminDeleted(admin);
    }

    private void notifyAdminUpdated(Admin admin) {
        for (AdminServiceListener listener : adminServiceListenerList)
            listener.onAdminUpdated(admin);
    }

    private void notifyAdminReset(List<Admin> adminList) {
        for (AdminServiceListener listener : adminServiceListenerList)
            listener.onAdminReset(adminList);
    }

    //===================================================================================
    @Override
    public void onGroupCreated(Group group) {
        // TODO create Group administrator automatically
    }

    @Override
    public void onGroupUpdated(Group group) {
        // nothing to do
    }

    @Override
    public void onGroupDeleted(Group group) {
        // 清除对应组织下的管理员
        Collection<Admin> adminList = adminMap.values();
        for (Admin admin : adminList) {
            if (admin.getGroupId() != group.getId())
                continue;

            deleteAdmin(admin.getId());
        }
    }

    @Override
    public void onGroupReset(List<Group> groupList) {
        // nothing to do
    }

    //===================================================================================
    @Autowired private AdminMapper adminMapper;

    private ObjectCacheMap<Integer, Admin> adminMap = new ObjectCacheMap<>();

    //===================================================================================
    public synchronized void reset() {
        adminMap.clear();

        List<Admin> adminList = adminMapper.selectAll();
        if (adminList == null)
            return;

        for (Admin admin : adminList)
            adminMap.put(admin.getId(), admin);

        //
        notifyAdminReset(adminList);
    }

    public Admin createAdmin(String name, String password, String salt, String nickName,
                             int groupId, String groupToken, int creatorId,
                             String description) {
        Admin admin = new Admin();
        admin.setName(name);
        admin.setPassword(password);
        admin.setPasswordSalt(salt);
        admin.setNickName(nickName);
        admin.setGroupId(groupId);
        admin.setGroupToken(groupToken);
        admin.setDescription(description);
        admin.setCreatorId(creatorId);

        int ret = adminMapper.insertSelective(admin);
        if (ret < 1)
            return null;

        adminMap.put(admin.getId(), admin);

        //
        notifyAdminCreated(admin);

        return admin;
    }

    public List<Admin> getAll() {
        List<Admin> adminList = new ArrayList<>();
        adminList.addAll(adminMap.values());

        return adminList;
    }

    public Admin getById(int id) {
        Admin admin = adminMap.get(id);
        if (admin != null)
            return admin;

        admin = adminMapper.selectByPrimaryKey(id);
        if (admin != null)
            adminMap.put(id, admin);

        return admin;
    }

    public Admin getByName(String name) {
        Collection<Admin> adminCollection = adminMap.values();
        for (Admin admin : adminCollection) {
            if (admin.getName().equals(name))
                return admin;
        }

        //
        Admin admin = adminMapper.selectByName(name);
        if (admin != null)
            adminMap.put(admin.getId(), admin);

        return admin;
    }

    public int deleteAdmin(int id) {
        Admin admin = adminMap.remove(id);
        if (admin == null)
            return -1;

        int ret = adminMapper.deleteByPrimaryKey(id);
        if (ret < 1)
            return -1;

        //
        notifyAdminDeleted(admin);

        return 0;
    }

    public int updateAdmin(Admin admin) {
        adminMapper.updateByPrimaryKeySelective(admin);

        //
        notifyAdminUpdated(admin);

        return 0;
    }

    public int updateAdmin(int id, String name, String password, String salt, String nickName, String description) {
        Admin admin = adminMap.get(id);
        if (admin == null)
            return -1;

        admin.setName(name);
        admin.setPassword(password);
        admin.setPasswordSalt(salt);
        admin.setNickName(nickName);
        admin.setDescription(description);

        adminMapper.updateByPrimaryKeySelective(admin);

        //
        notifyAdminUpdated(admin);

        return 0;
    }

    public boolean isCorrectPassword(String name, String password) {
        Admin admin = getByName(name);
        if (admin == null)
            return false;

        if (isCorrectPassword(password, admin.getPasswordSalt(), admin.getPassword()) == false)
            return false;

        return true;
    }

    public boolean isCorrectPassword(Admin admin, String password) {
        if (isCorrectPassword(password, admin.getPasswordSalt(), admin.getPassword()) == false)
            return false;

        return true;
    }

    public Admin login(String name, String password) {
        Admin admin = getByName(name);
        if (admin == null)
            return null;

        if (isCorrectPassword(password, admin.getPasswordSalt(), admin.getPassword()) == false)
            return null;

        return admin;
    }

    public int updatePassword(int id, String password) {
        Admin admin = getById(id);
        if (admin == null)
            return -1;

        String saltedPassword = getSaltedPassword(password, admin.getPasswordSalt());
        admin.setPassword(saltedPassword);

        return updateAdmin(admin);
    }

    //===================================================================================
    // TODO
    private String getSaltedPassword(String password, String salt) {
        return password;
    }

    private boolean isCorrectPassword(String password, String salt, String saltedPassword) {
        return saltedPassword.equals(getSaltedPassword(password, salt));
    }
}
