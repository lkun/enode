package org.enodeframework.rocketmq.message;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.enodeframework.common.serializing.JsonTool;
import org.enodeframework.queue.QueueMessage;
import org.enodeframework.queue.applicationmessage.AbstractApplicationMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author anruence@gmail.com
 */
public class RocketMQApplicationMessageListener extends AbstractApplicationMessageListener implements MessageListenerConcurrently {
    private final Logger logger = LoggerFactory.getLogger(RocketMQApplicationMessageListener.class);

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            QueueMessage queueMessage = RocketMQTool.covertToQueueMessage(msgs);
            handle(queueMessage, message -> {
                latch.countDown();
            });
            latch.await();
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            logger.error("Ops, consume ApplicationMessage failed, msgs:{}", JsonTool.serialize(msgs), e);
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
}