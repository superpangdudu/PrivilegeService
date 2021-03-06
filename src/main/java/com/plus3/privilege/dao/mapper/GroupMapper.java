package com.plus3.privilege.dao.mapper;

import com.plus3.privilege.dao.entity.Group;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GroupMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int insert(Group record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int insertSelective(Group record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    Group selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int updateByPrimaryKeySelective(Group record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table group
     *
     * @mbggenerated Mon Dec 18 14:27:29 CST 2017
     */
    int updateByPrimaryKey(Group record);

    List<Group> selectAll();
    Group selectByName(@Param("name") String name, @Param("token") String token);
}