package com.example.aihou.AiService;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aihou.DTO.command.ConsultationSessionCreateDTO;
import com.example.aihou.DTO.response.ConsultationMessageResponseDTO;
import com.example.aihou.entity.ConsultationMessage;
import com.example.aihou.entity.ConsultationSession;
import com.example.aihou.mapper.ConsultationSessionMapper;
import com.example.aihou.service.ConsultationMessageService;
import com.example.aihou.service.ConsultationSessionService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.ai.chat.messages.Message;

import java.util.ArrayList;
import java.util.List;

@Service
public class PsychologicalSupportService {
    @Autowired
    @Qualifier("open-ai")
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private ConsultationSessionMapper consultationSessionMapper;
    @Autowired
    private ConsultationSessionService consultationSessionService;
    @Autowired
    private ConsultationMessageService consultationMessageService;

    public StructOutPut.StreamChatSession startSession(Long userId, ConsultationSessionCreateDTO createDTO) {
        ConsultationSession consultationSession = consultationSessionService.createSession(userId, createDTO);
        consultationMessageService.saveUserMessage(consultationSession.getId(),createDTO.getInitialMessage(),null);
String sessionId="session_"+consultationSession.getId();
        return new StructOutPut.StreamChatSession(
                sessionId,
                userId,
                createDTO.getInitialMessage(),
                System.currentTimeMillis(),
                System.currentTimeMillis() + 86400000L, // 24小时
                1,
                "ACTIVE"
        );
    }
    public Flux<String> streamPsychologicalChat(String sessionId, String userMessage) {

        // 创建响应流
        return Flux.create(sink -> {
            Long dbSessionId = extractSessionId(sessionId);
            if (dbSessionId == null) {
                sink.error(new RuntimeException("会话ID格式错误"));
                return;
            }
            // 判断是否为初始消息：初始消息已在 startSession 里保存过，这里避免重复保存
            Integer messageCount = consultationMessageService.getMessageCountBySessionId(dbSessionId);
            boolean isInitialMessage = (messageCount != null && messageCount == 1);
            if (!isInitialMessage) {
                consultationMessageService.saveUserMessage(dbSessionId, userMessage, null);
            }
            // 进行流式对话
// 构建系统提示词
            String conversationId = "conversation_"+sessionId;
            List<Message> userMessages = new ArrayList<>();
            userMessages.add(new UserMessage(userMessage));
            chatMemory.add(conversationId, new UserMessage(userMessage));
            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(PromptManage.PSYCHOLOGICAL_SUPPORT_SYSTEM_PROMPT)
            ));
            StringBuilder fullResponse = new StringBuilder();
// 使用chatClient发送消息到Open AI
            chatClient.prompt(prompt)
                    .user(userMessage)
                    .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)) // 补全了缺失的 sessionId
                    .stream()      // 补全：因为是流式对话，必须调用 stream()
                    .content()
                    .doOnNext(Fragment->{
                        fullResponse.append(Fragment);
                        sink.next(Fragment);
                    })
                    .doOnComplete(()->{
                        String completeRes=fullResponse.toString();
                        consultationMessageService.saveAiMessage(dbSessionId, completeRes, "openai");
                        List<Message> aiMessages = new ArrayList<>();
                        aiMessages.add(new AssistantMessage(completeRes));
                        chatMemory.add(conversationId,aiMessages);
                        sink.complete();
                    })
                    .doOnError(error->{sink.error(error);})
                    .subscribe();


        });
    }
    // 获取参数中的sessionId
    public Long extractSessionId(String sessionId) {
        if (sessionId != null && sessionId.startsWith("session_")) {
            String idStr = sessionId.substring("session_".length());
            return Long.parseLong(idStr);
        }
        return null;
    }


}
