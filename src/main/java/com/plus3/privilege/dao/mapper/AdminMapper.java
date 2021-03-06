package com.plus3.privilege.dao.mapper;

import com.plus3.privilege.dao.entity.Admin;

import java.util.List;

public interface AdminMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int insert(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int insertSelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    Admin selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int updateByPrimaryKeySelective(Admin record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table admin
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int updateByPrimaryKey(Admin record);

    List<Admin> selectAll();
    Admin selectByName(String name);
}