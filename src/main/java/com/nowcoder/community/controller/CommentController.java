package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.*;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @Resource
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }

    @RequestMapping(path = "/myReply/{userId}", method = RequestMethod.GET)
    public String getMyReply(@PathVariable("userId") int userId, Page page, Model model) {
        page.setPath("/comment/myReply/" + userId);
        page.setLimit(5);
        int myReplyCount = commentService.findCommentCountByUserId(userId);
        page.setRows(myReplyCount);

        model.addAttribute("myReplyCount", myReplyCount);

        List<Comment> list = commentService.findCommentsByUserId(userId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> myReplyList = new ArrayList<>();
        if (list != null) {
            for (Comment comment : list) {
                Map<String, Object> map = new HashMap<>();
                map.put("comment", comment);

                DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
                map.put("post", post);

                myReplyList.add(map);
            }
        }
        model.addAttribute("myReplyList", myReplyList);

        return "/site/my-reply";
    }
}
