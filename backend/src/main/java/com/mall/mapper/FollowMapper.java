package com.mall.mapper;

import com.mall.entity.Follow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {
    @Select("SELECT COUNT(*) FROM follows WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    int exists(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    @Insert("INSERT INTO follows(follower_id, followee_id) VALUES (#{followerId}, #{followeeId})")
    int insert(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    @Delete("DELETE FROM follows WHERE follower_id = #{followerId} AND followee_id = #{followeeId}")
    int delete(@Param("followerId") Long followerId, @Param("followeeId") Long followeeId);

    @Select("SELECT f.*, u.username as followee_name FROM follows f JOIN users u ON f.followee_id = u.id WHERE f.follower_id = #{userId}")
    List<Follow> findFollowees(Long userId);

    @Select("SELECT COUNT(*) FROM follows WHERE followee_id = #{userId}")
    int countFollowers(Long userId);

    @Select("SELECT COUNT(*) FROM follows WHERE follower_id = #{userId}")
    int countFollowees(Long userId);
}
