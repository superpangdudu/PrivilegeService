package com.plus3.privilege.dao;

import com.alibaba.fastjson.JSONObject;
import com.plus3.privilege.common.Action;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 所属权限对应的操作和数据范围设置
 * 如果对应权限没有配置此参数（为Null或为空），默认获取全部
 *
 * 保存格式：JSON
 * {
 *     action: 31|16|8|4|2|1 // 具体参数对应操作{@Link Action}
 *     data: *|self|groupId1, groupId2, ..., groupIdN // * -- 组及下级全部数据，self -- 个人创建的数据，groups -- 具体组织的数据
 * }
 */
public class PermissionDetails implements Serializable {
    private int roleId;
    private int permissionId;
    private Set<Action> actions;
    private Set<String> grantedGroups; // TODO 目前只精确到组织级别，是否扩展看后期需求

    //===================================================================================
    public PermissionDetails(int roleId, int permissionId, String extra) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        combineExtra(extra);
    }

    public boolean belongsTo(int roleId, int permissionId) {
        if (this.roleId != roleId)
            return false;
        if (this.permissionId != permissionId)
            return false;
        return true;
    }

    public void combineExtra(String extra) {
        if (StringUtils.isEmpty(extra))
            return;

        try {
            JSONObject jsonObject = JSONObject.parseObject(extra);
            String actionValue = jsonObject.getString("action");
            String dataValue = jsonObject.getString("data");

            // get Actions
            if (actions == null)
                actions = new HashSet<>();

            if (StringUtils.isEmpty(actionValue)) {
                actions.add(Action.Write);
                actions.add(Action.Read);
                actions.add(Action.Update);
                actions.add(Action.Delete);
                actions.add(Action.Run);
            } else {
                Integer value = Integer.valueOf(actionValue);
                for (Action action : Action.values()) {
                    if ((value & action.getCode()) > 0)
                        actions.add(action);
                }
            }

            // get data region
            if (grantedGroups == null) {
                grantedGroups = new HashSet<>();

                if (StringUtils.isEmpty(dataValue) != false) {
                    String[] groups = dataValue.split(",");
                    for (String groupId : groups)
                        grantedGroups.add(groupId.trim());
                }

                return;
            }

            // Null and "*" get the top priority
            if (grantedGroups.size() == 0)
                return;
            if (grantedGroups.size() == 1
                    && grantedGroups.toArray()[0].equals("*"))
                return;

            //
            Set<String> extraGroup = new HashSet<>();
            if (StringUtils.isEmpty(dataValue)) {
                grantedGroups = extraGroup;
                return;
            }

            String[] groups = dataValue.split(",");

            //
            if (groups.length == 1
                    && groups[0].equals("self"))
                return;
            if (groups.length == 1
                    && groups[0].equals("*")) {
                extraGroup.add("*");
                grantedGroups = extraGroup;
                return;
            }

            grantedGroups.remove("self");

            //
            for (String groupId : groups)
                grantedGroups.add(groupId);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //===================================================================================
    public boolean isGranted(Action action) {
        if (actions == null
                || actions.size() == 0)
            return true;

        return actions.contains(action);
    }

    public Collection<Action> getActions() {
        return actions;
    }

    public Collection<String> getGrantedGroups() {
        return grantedGroups;
    }

    //===================================================================================
    public static PermissionDetails createPermissionDetails(int roleId, int permissionId, String extra) {
        return new PermissionDetails(roleId, permissionId, extra);
    }
}
