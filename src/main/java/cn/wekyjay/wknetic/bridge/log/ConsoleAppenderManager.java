package cn.wekyjay.wknetic.bridge.log;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;
/**
 * 控制台日志 Appender 管理器，负责注册和注销自定义的控制台日志 Appender
 */
public class ConsoleAppenderManager {

    private ConsoleAppender appender;
    private WkNeticBridge plugin;

    private ConsoleAppenderManager() {}

    private static class InstanceHolder {
        private static final ConsoleAppenderManager INSTANCE = new ConsoleAppenderManager();
    }

    public static ConsoleAppenderManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void setPlugin(WkNeticBridge plugin) {
        this.plugin = plugin;
    }

    private void updateLoggers(Configuration config, ConsoleAppender appender) {
        for (LoggerConfig loggerConfig : config.getLoggers().values()) {
            loggerConfig.addAppender(appender, null, null);
        }
        config.getRootLogger().addAppender(appender, null, null);
    }

    public boolean register() {
        try {
            final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
            final Configuration config = ctx.getConfiguration();
            this.appender = new ConsoleAppender("WkNeticConsole", null);
            this.appender.start();
            config.addAppender(this.appender);
            this.updateLoggers(config, this.appender);
            if (this.plugin != null) {
                this.plugin.getLogger().info("控制台转发已成功注入！");
            }
        } catch (Exception e) {
            if (this.plugin != null) {
                this.plugin.getLogger().info("控制台注入失败");
            }
            return false;
        }
        return true;
    }

    public boolean unregister() {
        if (this.appender != null) {
            try {
                this.appender.stop();
                final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
                final Configuration config = ctx.getConfiguration();
                config.getRootLogger().removeAppender(this.appender.getName());
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}