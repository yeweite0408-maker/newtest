package com.mall.service;

import com.mall.entity.Message;
import com.mall.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageMapper messageMapper;

    public MessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public List<Message> findByUserId(Long userId) {
        return messageMapper.findByUserId(userId);
    }

    public List<Message> findConversation(Long fromUserId, Long toUserId) {
        return messageMapper.findConversation(fromUserId, toUserId);
    }

    public int send(Message message) {
        return messageMapper.insert(message);
    }

    public int markAsRead(Long userId) {
        return messageMapper.markAsRead(userId);
    }

    public int countUnread(Long userId) {
        return messageMapper.countUnread(userId);
    }
}
