package com.mall.mapper;

import com.mall.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {
    @Select("SELECT * FROM notifications WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT 50")
    List<Notification> findByUserId(Long userId);

    @Insert("INSERT INTO notifications(user_id, type, content, related_id) VALUES (#{userId}, #{type}, #{content}, #{relatedId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Notification notification);

    @Update("UPDATE notifications SET is_read = TRUE WHERE user_id = #{userId}")
    int markAllRead(Long userId);

    @Select("SELECT COUNT(*) FROM notifications WHERE user_id = #{userId} AND is_read = FALSE")
    int countUnread(Long userId);
}
