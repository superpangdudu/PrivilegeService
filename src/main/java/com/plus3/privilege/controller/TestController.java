package com.plus3.privilege.controller;

import com.plus3.privilege.dao.entity.Admin;
import com.plus3.privilege.dao.mapper.AdminMapper;
import com.plus3.privilege.manager.SimpleDataReader;
import com.plus3.privilege.test.ITest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by admin on 2017/12/15.
 */
@Controller
@RequestMapping("/test")
public class TestController {
    @Autowired private AdminMapper adminMapper;
    @Autowired private ITest iTest;

    @Autowired private SimpleDataReader simpleDataReader;

    @RequestMapping("/test")
    public void test() {
        Admin admin = adminMapper.selectByPrimaryKey(0);
        System.out.println(admin.getName());

        iTest.doSomething();
    }

    @RequestMapping("/testRead")
    public void testRead() throws Exception {
        List<Admin> adminList = simpleDataReader.simpleQuery("admin", Admin.class, null, null);
        for (Admin admin : adminList)
            System.out.println(admin.getName());
    }
}
