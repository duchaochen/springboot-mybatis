package com.adu.mybatis.springboot.controller;

import com.adu.mybatis.springboot.mapper.DepartmentMapper;
import com.adu.mybatis.springboot.pojo.DepartmentPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
public class DepartmentController {

    @Autowired
    private DepartmentMapper departmentMapper;

    @GetMapping("/dept/{id}")
    public DepartmentPojo getById01(@PathVariable("id") Integer id) {
        DepartmentPojo byId01 = departmentMapper.getById01(id);
        return byId01;
    }

    @PostMapping("/dept")
    public DepartmentPojo insertDept01(DepartmentPojo departmentPojo) {
        departmentMapper.insertDept01(departmentPojo);
        return departmentPojo;
    }

    @GetMapping("/dept2/{id}")
    public DepartmentPojo getById02(@PathVariable("id") Integer id) {
        DepartmentPojo byId01 = departmentMapper.getById02(id);
        return byId01;
    }

    @PostMapping("/dept2")
    public DepartmentPojo insertDept02(DepartmentPojo departmentPojo) {
        departmentMapper.insertDept02(departmentPojo);
        return departmentPojo;
    }



}
