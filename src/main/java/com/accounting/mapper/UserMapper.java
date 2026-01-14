package com.accounting.mapper;

import com.accounting.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(@Param("id") Long id);

    @Insert("INSERT INTO user (username, password, real_name, phone, email, status) " +
            "VALUES (#{username}, #{password}, #{realName}, #{phone}, #{email}, #{status})")
    void insert(User user);
}
