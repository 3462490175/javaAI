package com.example.aihou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aihou.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
