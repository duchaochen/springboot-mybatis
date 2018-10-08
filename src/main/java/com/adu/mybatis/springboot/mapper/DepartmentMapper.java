package com.adu.mybatis.springboot.mapper;

import com.adu.mybatis.springboot.pojo.DepartmentPojo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * 必须扫描本包，比如@Mapper或者@MapperScan
 */
public interface DepartmentMapper {

    @Select("select * from department where id=#{id}")
    DepartmentPojo getById01(Integer id);

    /**
     * 使用驼峰命名法,keyProperty返回主键
     * @param dept
     */
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into department(name) values(#{name})")
    void insertDept01(DepartmentPojo dept);

    DepartmentPojo getById02(Integer id);

    void insertDept02(DepartmentPojo dept);

}
