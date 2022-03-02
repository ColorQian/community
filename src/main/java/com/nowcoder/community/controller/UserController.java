package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage() {
        return "/site/setting";
    }

    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader (MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片!");
            return "/site/setting";
        }

        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")); //获取图片的格式
        if (suffix == null) {
            model.addAttribute("error", "文件格式不正确!");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败:" + e.getMessage());
            throw new RuntimeException("上传文件失败, 服务器发生异常", e);
        }

        //在服务端更新用户头像的url,并且更新到数据库
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);

        //更新成功,重定向到首页
        return "redirect:/index";
    }

    //网站的头部,需要更新用户的头像,根据请求路径访问
    //String headerUrl = domain + contextPath + "/user/header/" + fileName; 根据此路径请求头像
    @RequestMapping(path = "/header/{fileName}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {

        //服务器将头像存放到了本地
        fileName = uploadPath + "/" + fileName;
        //获取文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1); //拿到图片的类型:png,jpg等

        //服务端向客户端响应图片
        response.setContentType("image/" + suffix);
        try (
                //放在try中，final会自动关闭
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败: " + e.getMessage());
        }
    }

    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword,
                                 HttpServletRequest request, Model model) {
        User user = hostHolder.getUser();
        Map<String, Object> map = userService.selectPasswordById(user.getId(), oldPassword);

        if (StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword) || StringUtils.isBlank(confirmPassword)) {
            model.addAttribute("setPasswordMsg", "密码不能为空");
            return "/site/setting";
        }

        if (!newPassword.equals(confirmPassword)) {  //新密码和确认密码不一致
            model.addAttribute("setPasswordMsg", "新密码和确认密码不一致");
            return "/site/setting";
        }

        if (map.isEmpty() || map == null) {  //说明原密码没有问题,那么就修改密码

            if (oldPassword.equals(newPassword)) {  //新旧密码如果一致
                model.addAttribute("setPasswordMsg", "新旧密码不能一致");
                return "/site/setting";
            }

            userService.updatePassword(user.getId(), newPassword);

            //密码修改成功,首先将ticket凭证设置为失效,跳转到登陆页面
            String ticket = CookieUtil.getValue(request, "ticket");
            userService.logout(ticket);
            return "redirect:/login";   //重定向默认get请求

        } else { //说明原密码有问题
                model.addAttribute("setPasswordMsg", map.get("setPasswordMsg"));
                return "/site/setting";
        }
    }

}