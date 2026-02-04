package cn.wekyjay.wknetic.bridge;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.bukkit.Bukkit;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cn.wekyjay.wknetic.api.enums.PacketType;

import java.nio.charset.StandardCharsets;
import java.lang.reflect.Method;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.logging.Logger;

public class NetworkManager {

    private final WkNeticBridge plugin;
    private EventLoopGroup group;
    private Channel channel;
    private boolean isConnecting = false;
    private Logger log;

    // 新属性
    private final String host; // 后端地址
    private final int port; // 后端端口
    private final String token; // 认证令牌
    private final String serverName; // 服务器名称
    private final String serverVersion;  // 服务器版本



    public NetworkManager(WkNeticBridge plugin) {
        this.plugin = plugin;
        this.log = plugin.getLogger();
        // 从配置文件读取参数
        this.host = plugin.getConfig().getString("Backend.ip", "127.0.0.1");
        this.port = plugin.getConfig().getInt("Backend.port", 8081);
        this.token = plugin.getConfig().getString("Backend.token", "");
        this.serverName = plugin.getConfig().getString("Common.server-name", Bukkit.getServer().getName() + "-" + Bukkit.getServer().getPort());
        this.serverVersion = plugin.getConfig().getString("Common.server-version", Bukkit.getVersion());
    }

    public void connect() {
        if (isConnecting || (channel != null && channel.isActive())) return;
        isConnecting = true;


        // 使用 NIO 线程组 (不要在 1.8 里使用 Epoll，兼容性最好是 Nio)
        group = new NioEventLoopGroup();

        // 组装客户端启动器
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)       // 通用 SocketChannel
                .option(ChannelOption.TCP_NODELAY, true)      // 禁用 Nagle 算法，降低延迟
                .handler(new ChannelInitializer<SocketChannel>() {  // 通道初始化器
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();          // 获取管道

                        // === 协议层 (必须和后端保持一致) ===
                        // 最大长度 Integer.MAX_VALUE, 长度字段偏移0, 长度字段占4字节
                        p.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        p.addLast(new LengthFieldPrepender(4));

                        // === 编解码层 ===
                        p.addLast(new StringDecoder(StandardCharsets.UTF_8));
                        p.addLast(new StringEncoder(StandardCharsets.UTF_8));

                        // === 业务层 ===
                        p.addLast(new BridgeClientHandler(plugin));
                    }
                });

        plugin.getLogger().info("正在连接后端: " + host + ":" + port);


        // 发起连接
        b.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                isConnecting = false;
                if (future.isSuccess()) {
                    channel = future.channel();
                    log.info("✅ 成功连接到 WkNetic 后端!");

                    // 发送身份验证
                    sendLogin();
                } else {
                    log.warning("❌ 连接失败，10秒后重试...");
                    // ⚠️ 必须回到 Bukkit 主线程去调度重试，保证线程安全
                    Bukkit.getScheduler().runTaskLater(plugin, () -> connect(), 200L);
                }
            }
        });
    }


    /**
     * 发送 JSON 字符串
     */
    public void send(String msg) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(msg);
        }
    }


    /**
     * 发送登录认证数据
     */
    private void sendLogin() {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.SERVER_LOGIN.toString());  // 类型 210 = 游戏服务器登录
        json.addProperty("token", token);
        json.addProperty("serverName", serverName);
        json.addProperty("serverVersion", serverVersion);

        send(json.toString());
    }

    /**
     * 发送心跳包
     */
    public void sendHeartbeat() {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("type", PacketType.HEARTBEAT.toString());
            json.addProperty("token", token);

            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(json.toString());
            }
        } catch (Exception e) {
            log.severe("发送心跳失败: " + e.getMessage());
        }
    }

    /**
     * 发送服务器信息包
     */
    public void sendServerInfo() {
    try {
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.SERVER_INFO.toString());
        json.addProperty("token", token);
        json.addProperty("serverName", serverName);
        json.addProperty("serverVersion", serverVersion);
        json.addProperty("motd", Bukkit.getMotd());
        json.addProperty("onlinePlayers", Bukkit.getOnlinePlayers().size());
        json.addProperty("maxPlayers", Bukkit.getMaxPlayers());
        json.addProperty("tps", getTps()); // 你自己的方法
        json.addProperty("ramUsage", getUsedRam());
        json.addProperty("maxRam", getMaxRam());
        json.add("playerList", buildPlayerListJson());
        json.add("pluginList", buildPluginListJson());

        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(json.toString());
        }
    } catch (Exception e) {
        log.severe("发送SERVER_INFO失败: " + e.getMessage());
    }
}

    /**
     * 发送自定义消息
     */
    public void sendMessage(String message) {
        try {
            if (channel != null && channel.isActive()) {
                channel.writeAndFlush(message);
            }
        } catch (Exception e) {
            log.severe("❌ 发送消息失败: " + e.getMessage());
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        if (channel != null) {
            channel.close();
        }
        if (group != null) {
            group.shutdownGracefully();
        }
        log.info("✅ 已断开连接");
    }

    public boolean isConnected() {
        return channel != null && channel.isActive();
    }

    /**
     * 获取 TPS（平均 1 分钟）
     */
    private double getTps() {
        try {
            Object server = Bukkit.getServer();
            Method getTpsMethod = server.getClass().getMethod("getTPS");
            double[] tps = (double[]) getTpsMethod.invoke(server);
            if (tps != null && tps.length > 0) {
                return Math.round(tps[0] * 100.0) / 100.0;
            }
        } catch (Exception ignored) {
            // 忽略不支持的服务端实现
        }
        return -1.0;
    }

    /**
     * 获取已使用内存（MB）
     */
    private long getUsedRam() {
        Runtime runtime = Runtime.getRuntime();
        long usedBytes = runtime.totalMemory() - runtime.freeMemory();
        return usedBytes / (1024 * 1024);
    }

    /**
     * 获取最大内存（MB）
     */
    private long getMaxRam() {
        Runtime runtime = Runtime.getRuntime();
        long maxBytes = runtime.maxMemory();
        return maxBytes / (1024 * 1024);
    }

    /**
     * 构建在线玩家列表 JSON
     */
    private JsonArray buildPlayerListJson() {
        JsonArray array = new JsonArray();
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        for (Player player : players) {
            if (player == null) {
                continue;
            }
            JsonObject obj = new JsonObject();
            obj.addProperty("name", player.getName());
            obj.addProperty("uuid", player.getUniqueId().toString());
            obj.addProperty("world", player.getWorld().getName());
            try { obj.addProperty("gameMode", player.getGameMode().name()); } catch (Exception ignored) {};
            try { obj.addProperty("ping", player.getPing()); } catch (Exception ignored) {};
            array.add(obj);
        }
        return array;
    }

    /**
     * 构建插件列表 JSON
     */
    private JsonArray buildPluginListJson() {
        JsonArray array = new JsonArray();
        for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", p.getName());
            obj.addProperty("version", p.getDescription().getVersion());
            obj.addProperty("author", String.join(", ", p.getDescription().getAuthors()));
            obj.addProperty("description", p.getDescription().getDescription());
            obj.addProperty("enabled", p.isEnabled());
            array.add(obj);
        }
        return array;
    }
}