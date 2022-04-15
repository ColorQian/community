package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_follow})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息内容为空");
            return;
        }

        // 处理消息
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式不正确");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USERID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        //封装Message对象的content属性
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());
        // 将Event对象的data属性，也封装到content中
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        // 触发事件之后, 生产者生产消息, 消费者监听到有消息之后 ->
        // 将消息队列中的事件Event(或者可以简单的看作消息)取出 ->
        // 按需要将其封装为Message对象,并且持久化到数据库中保存。
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
