package org.enodeframework.kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.enodeframework.commanding.CommandResult;
import org.enodeframework.commanding.CommandReturnType;
import org.enodeframework.commanding.ICommand;
import org.enodeframework.common.io.AsyncTaskResult;
import org.enodeframework.common.io.AsyncTaskStatus;
import org.enodeframework.common.utilities.Ensure;
import org.enodeframework.queue.QueueMessage;
import org.enodeframework.queue.command.AbstractCommandService;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

/**
 * @author anruence@gmail.com
 */
public class KafkaCommandService extends AbstractCommandService {
    private KafkaTemplate<String, String> producer;

    public KafkaTemplate<String, String> getProducer() {
        return producer;
    }

    public void setProducer(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    @Override
    public CompletableFuture<AsyncTaskResult> sendAsync(ICommand command) {
        return SendMessageService.sendMessageAsync(producer, buildKafkaMessage(command, false));
    }

    @Override
    public CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command) {
        return executeAsync(command, CommandReturnType.CommandExecuted);
    }

    @Override
    public CompletableFuture<AsyncTaskResult<CommandResult>> executeAsync(ICommand command, CommandReturnType commandReturnType) {
        CompletableFuture<AsyncTaskResult<CommandResult>> taskCompletionSource = new CompletableFuture<>();
        try {
            Ensure.notNull(commandResultProcessor, "commandResultProcessor");
            commandResultProcessor.registerProcessingCommand(command, commandReturnType, taskCompletionSource);
            CompletableFuture<AsyncTaskResult> sendMessageAsync = SendMessageService.sendMessageAsync(producer, buildKafkaMessage(command, true));
            sendMessageAsync.thenAccept(sendResult -> {
                if (sendResult.getStatus().equals(AsyncTaskStatus.Success)) {
                    //commandResultProcessor中会继续等命令或事件处理完成的状态
                } else {
                    taskCompletionSource.complete(new AsyncTaskResult<>(sendResult.getStatus(), sendResult.getErrorMessage()));
                    commandResultProcessor.processFailedSendingCommand(command);
                }
            }).exceptionally(ex -> {
                taskCompletionSource.complete(new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage()));
                return null;
            });
        } catch (Exception ex) {
            taskCompletionSource.complete(new AsyncTaskResult<>(AsyncTaskStatus.Failed, ex.getMessage()));
        }
        return taskCompletionSource;
    }

    protected ProducerRecord<String, String> buildKafkaMessage(ICommand command, boolean needReply) {
        QueueMessage queueMessage = buildCommandMessage(command, needReply);
        return KafkaTool.covertToProducerRecord(queueMessage);
    }
}
