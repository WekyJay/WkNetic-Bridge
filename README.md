# WkNetic-Bridge

[中文](README.md) | [English](README.en.md)

WkNetic-Bridge 是一个 Minecraft 服务器插件，用于桥接 Minecraft 服务器与后端社区服务，实现玩家账号同步和认证。

## 功能特性

- **多认证模式支持**：
  - 正版模式服务器：自动同步正版玩家
  - 离线模式服务器：支持 AuthMe 和 FastLogin 插件
  - 兜底自定义认证：当上述插件均不可用时提供自定义登录验证

- **玩家行为控制**：
  - 未登录玩家禁止移动、聊天和执行命令
  - 登录完成后恢复正常权限

- **实时同步**：
  - 玩家加入时自动同步账号信息到后端社区服务
  - 支持正版和离线账号类型区分

- **网络通信**：
  - 基于 Netty 的高性能网络连接
  - JSON 格式数据传输
  - 自动重连机制

## 系统要求

- Minecraft 服务器：支持 Bukkit/Spigot API 1.21+
- Java 版本：21+
- 依赖插件（可选）：
  - AuthMe（离线玩家认证）
  - FastLogin（正版玩家快速登录）

## 安装步骤

1. **下载插件**：
   ```bash
   # 编译项目
   mvn clean package
   ```

2. **放置文件**：
   - 将 `target/WkNetic-Bridge-1.0-SNAPSHOT.jar` 放入服务器的 `plugins/` 目录
   - 如果使用 FastLogin，将 FastLogin.jar 放入 `plugins/WkNetic-Bridge/lib/` 目录

3. **启动服务器**：
   - 插件会自动生成配置文件

## 配置说明

插件首次运行后会在 `plugins/WkNetic-Bridge/config.yml` 生成默认配置：

```yaml
# 后端服务配置
backend:
  ip: "127.0.0.1"      # 后端服务器IP
  port: 8081           # 后端服务器端口
  token: "your-token"  # 认证令牌，请修改为实际值
```

### 配置项说明

- `backend.ip`：后端 Spring Boot 服务的 IP 地址
- `backend.port`：后端服务的端口号
- `backend.token`：用于身份验证的令牌，确保与后端配置一致

## 使用方法

### 1. 服务器管理员

- 修改 `config.yml` 中的后端配置
- 重启服务器使配置生效
- 插件会自动连接到后端服务

### 2. 玩家体验

- **正版玩家**：加入服务器后自动同步，无需额外操作
- **离线玩家（AuthMe）**：加入后输入密码登录，登录成功后同步
- **离线玩家（自定义）**：加入后按提示输入密码（功能待实现）

### 3. 监控日志

插件会在控制台输出连接状态和同步信息：
```
[WkNetic-Bridge] 正在连接后端: 127.0.0.1:8081
[WkNetic-Bridge] ✅ 成功连接到 WkNetic 后端!
[WkNetic-Bridge] Selected login hook: AuthMe
[WkNetic-Bridge] 社区账号已成功同步！
```

## 认证流程

### 正版模式服务器
```
玩家加入 → 检测正版 → 同步 PREMIUM → 允许游戏
```

### 离线模式服务器
```
玩家加入 → 检测正版 → 否 → 添加到未登录列表 → 等待认证
                      ↓
               认证成功 → 从列表移除 → 同步 CRACKED → 允许游戏
```

## 开发信息

### 项目结构
```
src/main/java/cn/wekyjay/wknetic/
├── auth/                    # 认证相关
│   ├── hook/               # 插件钩子
│   ├── listener/           # 事件监听器
│   ├── AuthManager.java    # 认证管理器（已拆分）
│   ├── LoginAuthManager.java    # 登录认证管理器
│   └── PremiumAuthManager.java  # 正版认证管理器
├── bridge/                 # 桥接核心
│   ├── BridgeClientHandler.java # Netty 客户端处理器
│   ├── NetworkManager.java      # 网络管理器
│   └── WkNeticBridge.java       # 主插件类
└── resources/             # 资源文件
    ├── config.yml         # 默认配置
    └── plugin.yml         # 插件描述
```

### 技术栈
- **网络通信**：Netty 4.1+
- **JSON 处理**：Gson
- **版本兼容**：XSeries (多版本支持)
- **构建工具**：Maven

### 扩展开发

如需添加新的认证方式：

1. 在 `auth/hook/` 下创建新的钩子类，实现 `ILoginHook` 接口
2. 在相应的 `AuthManager` 中添加检测逻辑
3. 在 `AuthListener` 中添加相应的事件处理

## 常见问题

### Q: 连接后端失败怎么办？
A: 检查 `config.yml` 中的 IP、端口和令牌配置是否正确，后端服务是否启动。

### Q: 玩家无法移动/聊天？
A: 这是正常行为，未登录玩家会被限制操作。登录后自动恢复。

### Q: 如何添加新的认证插件？
A: 参考现有的 `AuthmeHook` 和 `FastLoginHook` 实现，创建新的钩子类。

### Q: FastLogin 依赖怎么配置？
A: 将 FastLogin.jar 放入 `lib/` 目录，Maven 会自动加载。

## 许可证

本项目采用 MIT 许可证。

## 贡献

欢迎提交 Issue 和 Pull Request 来改进项目。

## 联系方式

如有问题，请通过 GitHub Issues 联系开发者。</content>
<parameter name="filePath">/Users/macbook/文件/Studio/Java/WkNetic/WkNetic-Bridge/README.md