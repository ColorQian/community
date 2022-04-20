package com.nowcoder.community;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class TestMain {

    @Test
    public void testMap() {
        User user1 = new User();
        user1.setUsername("zhangsan");

        HashMap<String, User> map = new HashMap<>();

        map.put("user1", user1);

        user1 = new User();
        user1.setUsername("lisi");
        map.put("user2", user1);

        for (Map.Entry<String, User> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    @Test
    public void getPassword() {
        String s = CommunityUtil.md5(123 + "167f9");
        System.out.println(s);
    }

}
