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

import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;

public class NetworkManager {

    private final WkNeticBridge plugin;
    private EventLoopGroup group;
    private Channel channel;
    private boolean isConnecting = false;

    public NetworkManager(WkNeticBridge plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        if (isConnecting || (channel != null && channel.isActive())) return;
        isConnecting = true;


        // 读取配置
        String host = plugin.getConfig().getString("backend.ip", "127.0.0.1");
        int port = plugin.getConfig().getInt("backend.port", 8081);

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
                    plugin.getLogger().info("✅ 成功连接到 WkNetic 后端!");

                    // 发送身份验证
                    sendLogin();
                } else {
                    plugin.getLogger().warning("❌ 连接失败，10秒后重试...");
                    // ⚠️ 必须回到 Bukkit 主线程去调度重试，保证线程安全
                    Bukkit.getScheduler().runTaskLater(plugin, () -> connect(), 200L);
                }
            }
        });
    }

    public void disconnect() {
        if (group != null) {
            group.shutdownGracefully();
        }
    }

    /**
     * 发送 JSON 字符串
     */
    public void send(String msg) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(msg);
        }
    }

    private void sendLogin() {
        String token = plugin.getConfig().getString("backend.token");

        JsonObject json = new JsonObject();
        json.addProperty("type", 1);  // 类型 1 = 登录
        json.addProperty("token", token); // 认证令牌

        send(json.toString());
    }
}