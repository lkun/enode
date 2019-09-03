package com.enodeframework.messaging;

import com.enodeframework.common.io.AsyncTaskResult;
import com.enodeframework.infrastructure.IObjectProxy;
import com.enodeframework.infrastructure.MethodInvocation;
import com.enodeframework.messaging.IMessage;

import java.util.concurrent.CompletableFuture;

public interface IMessageHandlerProxy1 extends IObjectProxy, MethodInvocation {
    CompletableFuture<AsyncTaskResult> handleAsync(IMessage message);
}