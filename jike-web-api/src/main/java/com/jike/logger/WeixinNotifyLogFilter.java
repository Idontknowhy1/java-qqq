package com.jike.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class WeixinNotifyLogFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event.getMessage().startsWith("wxNotify")) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }
}
