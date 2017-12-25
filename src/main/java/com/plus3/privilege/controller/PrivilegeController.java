package com.plus3.privilege.controller;

import com.alibaba.fastjson.JSONObject;
import com.plus3.privilege.common.AbstractTree;
import com.plus3.privilege.dao.AdminDetails;
import com.plus3.privilege.dao.PermissionDetails;
import com.plus3.privilege.dao.entity.*;
import com.plus3.privilege.manager.PrivilegeManager;
import com.plus3.privilege.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static com.plus3.privilege.common.Result.*;

@RestController
@RequestMapping("/privilege")
public class PrivilegeController {
    @Autowired private PrivilegeManager privilegeManager;

    @Autowired private AdminService adminService;
    @Autowired private GroupService groupService;
    @Autowired private RoleService roleService;
    @Autowired private PermissionOfRoleService permissionOfRoleService;
    @Autowired private PermissionService permissionService;

    //===================================================================================
    @RequestMapping("/getAdminDetailsById")
    public JSONObject getAdminDetails(Integer id) {
        AdminDetails adminDetails = privilegeManager.getAdminDetails(id);
        if (adminDetails == null)
            return Failed;

        return RESULT(adminDetails);
    }

    @RequestMapping("/getAdminDetailsByName")
    public JSONObject getAdminDetails(String name, String password) {
        AdminDetails adminDetails = privilegeManager.getAdminDetails(name, password);
        if (adminDetails == null)
            return Failed;

        return RESULT(adminDetails);
    }

    //===================================================================================
    @RequestMapping("/getAdminById")
    public JSONObject getAdmin(Integer id) {
        Admin admin = adminService.getById(id);
        if (admin == null)
            return Failed;

        return RESULT(admin);
    }

    @RequestMapping("/getAdminByName")
    public JSONObject getAdmin(String name, String password) {
        Admin admin = adminService.login(name, password);
        if (admin == null)
            return Failed;
        return RESULT(admin);
    }

    @RequestMapping("/createAdmin")
    public JSONObject createAdmin(String name, String password, String salt, String nickName, String description,
                                  int groupId, String groupToken, int creatorId) {
        Admin admin = adminService.createAdmin(name, password, salt, nickName,
                groupId, groupToken, creatorId, description);
        if (admin == null)
            return Failed;

        return RESULT(admin);
    }

    @RequestMapping("/deleteAdmin")
    public JSONObject deleteAdmin(int id) {
        int ret = adminService.deleteAdmin(id);
        if (ret == 0)
            return Ok;
        return Failed;
    }

    @RequestMapping("/updateAdminPassword")
    public JSONObject updateAdminPassword(Integer id, String oldPassword, String newPassword) {
        Admin admin = adminService.getById(id);
        if (admin == null)
            return Failed;

        if (adminService.isCorrectPassword(admin, oldPassword) == false)
            return IncorrectPassword;

        int ret = adminService.updatePassword(admin.getId(), newPassword);
        if (ret != 0)
            return Failed;

        return Ok;
    }

    @RequestMapping("/setAdminNickName")
    public JSONObject setAdminNickName(Integer id, String nickName) {
        Admin admin = adminService.getById(id);
        if (admin == null)
            return Failed;

        admin.setNickName(nickName);
        adminService.updateAdmin(admin);

        return Ok;
    }

    @RequestMapping("/setAdminDescription")
    public JSONObject setAdminDescription(Integer id, String description) {
        Admin admin = adminService.getById(id);
        if (admin == null)
            return Failed;

        admin.setDescription(description);
        adminService.updateAdmin(admin);

        return Ok;
    }

    //===================================================================================
    @RequestMapping("/createGroup")
    public JSONObject createGroup(String name, String domain, String description,
                                  Integer parentId, String parentToken, Integer creatorId) {
        Group group = groupService.createGroup(parentId, parentToken, creatorId,
                name, description, domain);
        if (group == null)
            return Failed;

        return Ok;
    }

    @RequestMapping("/deleteGroup")
    public JSONObject deleteGroup(Integer id) {
        int ret = groupService.deleteGroup(id);
        if (ret == 0)
            return Ok;
        return Failed;
    }

    @RequestMapping("/updateGroup")
    public JSONObject updateGroup(Integer id, String name, String description) {
        Group group = groupService.getById(id);
        if (group == null)
            return Failed;

        group.setName(name);
        group.setDescription(description);

        int ret = groupService.updateGroup(group);
        if (ret == 0)
            return Ok;
        return Failed;
    }

    @RequestMapping("/getChildrenGroups")
    public JSONObject getChildrenGroups(Integer id) {
        Group group = groupService.getById(id);
        if (group == null)
            return Failed;

        List<Group> groupList = groupService.getWithChildren(group.getToken());

        return RESULT(groupList);
    }

    //===================================================================================
    @RequestMapping("/createRole")
    public JSONObject createRole(String name, String description,
                                 Integer groupId, String groupToken) {
        Role role = roleService.createRole(name, groupId, groupToken, description);
        if (role == null)
            return Failed;
        return RESULT(role);
    }

    @RequestMapping("/deleteRole")
    public JSONObject deleteRole(Integer id) {
        int ret = roleService.deleteRole(id);
        if (ret == 0)
            return Ok;
        return Failed;
    }

    @RequestMapping("/createPermission")
    public JSONObject createPermission(Integer parentId, String name, String url, String type, String description) {
        Permission permission = permissionService.createPermission(parentId, name, url, type, description);
        if (permission == null)
            return Failed;
        return Ok;
    }

    @RequestMapping("/getRolesOfGroup")
    public JSONObject getRolesOfGroup(Integer groupId, boolean withChildren) {
        Group group = groupService.getById(groupId);
        if (group == null)
            return Failed;

        List<Role> roleList = roleService.getGroupRoles(group.getToken(), withChildren);
        return RESULT(roleList);
    }

    @RequestMapping("/addPermissionToRole")
    public JSONObject addPermissionToRole(Integer roleId, Integer permissionId, String extra) {
        PermissionOfRole permissionOfRole = permissionOfRoleService.createPermissionOfRole(roleId, permissionId, extra);
        if (permissionOfRole == null)
            return Failed;
        return RESULT(permissionOfRole);
    }

    @RequestMapping("/deletePermissionOfRole")
    public JSONObject deletePermissionOfRole(Integer id) {
        permissionOfRoleService.deletePermissionOfRole(id);
        return Ok;
    }

    @RequestMapping("/updatePermissionOfRoleExtra")
    public JSONObject updatePermissionOfRoleExtra(Integer id, String extra) {
        int ret = permissionOfRoleService.updatePermissionOfRole(id, extra);
        if (ret == 0)
            return Ok;
        return Failed;
    }

    @RequestMapping("/getPermissionOfRoleExtra")
    public JSONObject getPermissionOfRoleExtra(Integer id) {
        PermissionOfRole permissionOfRole = permissionOfRoleService.getPermissionOfRole(id);
        if (permissionOfRole == null)
            return Failed;
        return RESULT(permissionOfRole.getExtra());
    }

    @RequestMapping("/getAllPermissions")
    public JSONObject getAllPermissions() {
        List<Permission> permissionList = permissionService.getAllPermission();
        List<AbstractTree.TreeNode<Permission>> nodeList = getPermissionTree(permissionList);

        return RESULT(nodeList);
    }

    @RequestMapping("/getPermissionsOfRole")
    public JSONObject getPermissionsOfRole(Integer roleId) {
        List<PermissionOfRole> permissionOfRoleList = permissionOfRoleService.getRolePermissions(roleId);
        List<Permission> permissionList = new ArrayList<>();
        Map<Integer, PermissionDetails> permissionDetailsMap = new HashMap<>();

        for (PermissionOfRole permissionOfRole : permissionOfRoleList) {
            Permission permission = permissionService.getById(permissionOfRole.getPermissionId());
            permissionList.add(permission);
        }

        List<AbstractTree.TreeNode<Permission>> nodeList = getPermissionTree(permissionList);

        //
        for (PermissionOfRole permissionOfRole : permissionOfRoleList) {
            PermissionDetails permissionDetails =
                    new PermissionDetails(roleId, permissionOfRole.getPermissionId(),permissionOfRole.getExtra());
            permissionDetailsMap.put(permissionOfRole.getPermissionId(), permissionDetails);
        }

        return RESULT(nodeList)
                .addData("details", permissionDetailsMap);
    }

    @RequestMapping("/getPermissionsOfGroup")
    public JSONObject getPermissionsOfGroup(Integer groupId) {
        Group group = groupService.getById(groupId);

        List<Role> roleList = roleService.getGroupRoles(group.getToken(), false);
        Set<Permission> permissionSet = new HashSet<>();
        Map<Integer, PermissionDetails> permissionDetailsMap = new HashMap<>();

        for (Role role : roleList) {
            Collection<Permission> permissionList = getPermissionOfRole(role.getId());
            for (Permission permission : permissionList)
                permissionSet.add(permission);

            //
            List<PermissionOfRole> permissionOfRoleList = permissionOfRoleService.getRolePermissions(role.getId());
            if (permissionOfRoleList == null)
                continue;

            for (PermissionOfRole permissionOfRole : permissionOfRoleList) {
                PermissionDetails permissionDetails = permissionDetailsMap.get(permissionOfRole.getPermissionId());
                if (permissionDetails == null)
                    permissionDetailsMap.put(permissionOfRole.getPermissionId(),
                            new PermissionDetails(permissionOfRole.getRoleId(),
                                    permissionOfRole.getPermissionId(),
                                    permissionOfRole.getExtra()));
                else
                    permissionDetails.combineExtra(permissionOfRole.getExtra());
            }
        }

        List<AbstractTree.TreeNode<Permission>> nodeList = getPermissionTree(permissionSet);

        return RESULT(nodeList)
                .addData("details", permissionDetailsMap);
    }

    //===================================================================================
    private List<AbstractTree.TreeNode<Permission>> getPermissionTree(Collection<Permission> permissionList) {
        AbstractTree<Permission> permissionTree = new AbstractTree<>();
        for (Permission permission : permissionList)
            permissionTree.addNode(permission, permission.getId(), permission.getParentId());
        List<AbstractTree.TreeNode<Permission>> nodeList = permissionTree.buildTree();

        return nodeList;
    }

    private Collection<Permission> getPermissionOfRole(int roleId) {
        List<PermissionOfRole> permissionOfRoleList = permissionOfRoleService.getRolePermissions(roleId);
        List<Permission> permissionList = new ArrayList<>();

        for (PermissionOfRole permissionOfRole : permissionOfRoleList) {
            Permission permission = permissionService.getById(permissionOfRole.getPermissionId());
            permissionList.add(permission);
        }

        return permissionList;
    }
}
