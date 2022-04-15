package com.nowcoder.community;


import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailSendTest {

    @Autowired
    private MailClient mailClient;  //自己封装的一个发送邮件的类，通过sendMail(String to, String subject, String content)方法发送

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendMail() {
        mailClient.sendMail("1255574204@qq.com", "Test", "welcome use spring mail");
    }

    @Test
    public void testHTMLMail() {  //发送一个HTML动态网页，给指定邮箱，使用thymeleaf模板引擎渲染
        Context context = new Context();
        context.setVariable("username", "sunday");

        String content = templateEngine.process("/mail/demo", context);
        System.out.println(content);

        mailClient.sendMail("1255574204@qq.com", "HTML", content);
    }
}
