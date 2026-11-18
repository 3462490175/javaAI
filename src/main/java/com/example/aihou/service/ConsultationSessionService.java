package com.example.aihou.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.example.aihou.DTO.command.ConsultationSessionCreateDTO;
import com.example.aihou.entity.ConsultationSession;
import com.example.aihou.entity.User;
import com.example.aihou.mapper.ConsultationSessionMapper;
import com.example.aihou.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ConsultationSessionService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;

    public ConsultationSession createSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        // 验证用户是否存在
        User user = userMapper.selectById(userId);
        if (user != null) {
            // 创建会话记录
            ConsultationSession session = ConsultationSession.builder()
                    .userId(userId)
                    .sessionTitle(createDTO.getSessionTitle())
                    .startedAt(LocalDateTime.now())
                    .build(); // 补全了分号并赋值给变量

            // 如果未提供标题
            if (StrUtil.isBlank(createDTO.getSessionTitle())) {
                // 修复了原图 String.format 的语法错误，直接拼接字符串
                session.setSessionTitle("宁心AI助手 - " + DateUtil.format(LocalDateTime.now(), "MM-dd HH:mm"));
            }consultationSessionMapper.insert(session);
            return session;
        }


        return null;
    }
}
