package cn.wekyjay.wknetic.bridge.log;

import java.util.logging.Logger;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;

import cn.wekyjay.wknetic.bridge.WkNeticBridge;


/**
 * 自定义控制台日志 Appender，将日志数据发送到指定的处理逻辑
 */
public class ConsoleAppender extends AbstractAppender {
    private Logger log = WkNeticBridge.getInstance().getLogger();

    protected ConsoleAppender(String name, Filter filter) {
        super(name, filter, null, true, null);
    }

    @Override
    public void append(LogEvent event) {
        // 获取格式化后的消息内容
        String message = event.getMessage().getFormattedMessage();
        String level = event.getLevel().toString();
        long timestamp = event.getTimeMillis();

        // 构造你的数据包 (可以是 JSON 或自定义对象)
        // 建议通过你已有的 Netty EventLoop 异步发送，避免拖慢服务器
        // 做测试
        // log.info("ConsoleAppender received log: " + message + " | Level: " + level + " | Timestamp: " + timestamp);
        
    }
}
