package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //根据用户id查询用户发了哪些帖子(用在客户首页)
    //offset表示每一页起始行行号，limit表示每一页最多查询多少数据
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);

    //@Param注解用于给参数起别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);
}
