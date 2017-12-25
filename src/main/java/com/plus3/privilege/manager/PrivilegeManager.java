package com.plus3.privilege.manager;

import com.plus3.privilege.dao.AdminDetails;
import com.plus3.privilege.dao.PermissionDetails;
import com.plus3.privilege.dao.entity.*;
import com.plus3.privilege.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 *
 */
@Service
public class PrivilegeManager {
    @Autowired private AdminService adminService;
    @Autowired private GroupService groupService;
    @Autowired private RoleService roleService;
    @Autowired private PermissionService permissionService;
    @Autowired private PermissionOfRoleService permissionOfRoleService;

    //===================================================================================
    @PostConstruct
    public void init() {
        groupService.addListener(adminService);
        groupService.addListener(roleService);

        roleService.addListener(permissionOfRoleService);
        permissionService.addListener(permissionOfRoleService);

        //
        groupService.reset();
        adminService.reset();
        roleService.reset();
        permissionService.reset();
        permissionOfRoleService.reset();
    }

    //===================================================================================
    public AdminDetails getAdminDetails(int adminId) {
        Admin admin = adminService.getById(adminId);
        if (admin == null)
            return null;

        Group group = groupService.getById(admin.getGroupId());

        List<Role> roleList = roleService.getGroupRoles(group.getToken(), false);
        if (roleList == null
                || roleList.size() == 0)
            return new AdminDetails(admin, group, new ArrayList<>(), new ArrayList<>(), new HashMap<>());

        //
        Set<Permission> permissionSet = new HashSet<>();
        Map<Integer, PermissionDetails> permissionDetailsMap = new HashMap<>();

        for (Role role : roleList) {
            List<PermissionOfRole> permissionOfRoleList = permissionOfRoleService.getRolePermissions(role.getId());
            if (permissionOfRoleList == null
                    || permissionOfRoleList.size() == 0)
                continue;

            for (PermissionOfRole permissionOfRole : permissionOfRoleList) {
                int permissionId = permissionOfRole.getPermissionId();

                Permission permission = permissionService.getById(permissionId);
                permissionSet.add(permission);

                //
                String permissionExtra = permissionOfRole.getExtra();
                if (StringUtils.isEmpty(permissionExtra))
                    continue;

                PermissionDetails permissionDetails = permissionDetailsMap.get(permissionId);
                if (permissionDetails == null) {
                    permissionDetails = PermissionDetails.createPermissionDetails(permissionOfRole.getRoleId(),
                            permissionOfRole.getPermissionId(), permissionExtra);

                    if (permissionDetails != null)
                        permissionDetailsMap.put(permissionId, permissionDetails);
                }
                else
                    permissionDetails.combineExtra(permissionExtra);
            }
        }

        //
        return new AdminDetails(admin, group,
                roleList, permissionSet, permissionDetailsMap);
    }

    public AdminDetails getAdminDetails(String name, String password) {
        Admin admin = adminService.login(name, password);
        if (admin == null)
            return null;

        return getAdminDetails(admin.getId());
    }

    public Collection<Permission> getGroupPermissions(int groupId) {
        Group group = groupService.getById(groupId);

        List<Role> roleList = roleService.getGroupRoles(group.getToken(), false);
        if (roleList == null)
            return null;

        Set<Permission> permissionSet = new HashSet<>();
        for (Role role : roleList) {
            List<PermissionOfRole> permissionOfRoleList = permissionOfRoleService.getRolePermissions(role.getId());
            if (permissionOfRoleList == null)
                continue;

            for (PermissionOfRole permissionOfRole : permissionOfRoleList) {
                Permission permission = permissionService.getById(permissionOfRole.getPermissionId());
                permissionSet.add(permission);
            }
        }

        return permissionSet;
    }
}
