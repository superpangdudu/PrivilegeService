package com.plus3.privilege.dao.mapper;

import com.plus3.privilege.dao.entity.PermissionOfRole;

import java.util.List;

public interface PermissionOfRoleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table permission_of_role
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table permission_of_role
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int insert(PermissionOfRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table permission_of_role
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int insertSelective(PermissionOfRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table permission_of_role
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    PermissionOfRole selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table permission_of_role
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int updateByPrimaryKeySelective(PermissionOfRole record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table permission_of_role
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int updateByPrimaryKey(PermissionOfRole record);

    List<PermissionOfRole> selectAll();
}