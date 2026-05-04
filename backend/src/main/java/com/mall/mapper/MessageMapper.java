package com.mall.mapper;

import com.mall.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Select("SELECT m.*, fu.username as from_username, tu.username as to_username FROM messages m JOIN users fu ON m.from_user_id = fu.id JOIN users tu ON m.to_user_id = tu.id WHERE m.from_user_id = #{userId} OR m.to_user_id = #{userId} ORDER BY m.created_at DESC")
    List<Message> findByUserId(Long userId);

    @Select("SELECT m.*, fu.username as from_username, tu.username as to_username FROM messages m JOIN users fu ON m.from_user_id = fu.id JOIN users tu ON m.to_user_id = tu.id WHERE (m.from_user_id = #{fromUserId} AND m.to_user_id = #{toUserId}) OR (m.from_user_id = #{toUserId} AND m.to_user_id = #{fromUserId}) ORDER BY m.created_at ASC")
    List<Message> findConversation(@Param("fromUserId") Long fromUserId, @Param("toUserId") Long toUserId);

    @Insert("INSERT INTO messages(from_user_id, to_user_id, content) VALUES (#{fromUserId}, #{toUserId}, #{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Message message);

    @Update("UPDATE messages SET is_read = TRUE WHERE to_user_id = #{userId} AND is_read = FALSE")
    int markAsRead(Long userId);

    @Select("SELECT COUNT(*) FROM messages WHERE to_user_id = #{userId} AND is_read = FALSE")
    int countUnread(Long userId);
}
