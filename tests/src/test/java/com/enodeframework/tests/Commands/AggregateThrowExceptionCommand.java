package com.enodeframework.tests.Commands;

import com.enodeframework.commanding.Command;

public class AggregateThrowExceptionCommand extends Command {
    public boolean PublishableException;

    public boolean isPublishableException() {
        return PublishableException;
    }

    public void setPublishableException(boolean publishableException) {
        PublishableException = publishableException;
    }
}
