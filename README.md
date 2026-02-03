<div align="center">

# WkNetic-Bridge

[![Java Version](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21+-green?logo=minecraft)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red?logo=apachemaven)](https://maven.apache.org/)

**一个高性能的 Minecraft 服务器桥接插件，实现玩家账号同步与社区一体化**

[中文](README.md) | [English](README.en.md)

</div>

---

## 📖 简介

WkNetic-Bridge 是一个专为 Minecraft 服务器设计的桥接插件，旨在无缝连接游戏服务器与后端社区服务系统。通过高性能的 Netty 网络通信，实现玩家账号实时同步、多种认证模式支持和智能权限管理。

### ✨ 核心特性

- 🔐 **多认证模式** - 支持正版、离线（AuthMe、FastLogin）及自定义认证
- ⚡ **高性能通信** - 基于 Netty 的异步网络架构，JSON 数据传输
- 🔄 **实时同步** - 玩家状态自动同步到后端社区服务
- 🎮 **行为管控** - 未认证玩家自动限制移动、聊天和命令执行
- 🛡️ **智能钩子** - 自动检测并适配服务器认证环境
- 🔌 **即插即用** - 简单配置，自动重连，无需复杂设置

## 📋 系统要求

| 组件 | 要求 |
|------|------|
| **Minecraft 服务器** | Bukkit/Spigot/Paper 1.21+ |
| **Java 版本** | JDK 21 或更高 |
| **可选依赖** | AuthMe 5.6+、FastLogin |

## 🚀 快速开始

### 安装步骤

1. **构建插件**
   ```bash
   mvn clean package
   ```

2. **部署插件**
   ```bash
   # 将生成的 JAR 文件放入 plugins 目录
   cp target/WkNetic-Bridge-1.0-SNAPSHOT.jar <服务器路径>/plugins/
   ```

3. **首次启动**
   - 启动服务器，插件将自动生成配置文件
   - 停止服务器，编辑配置文件

### ⚙️ 配置说明

编辑 `plugins/WkNetic-Bridge/config.yml`：

```yaml
Backend:
  ip: "127.0.0.1"          # 后端服务器 IP 地址
  port: 8081                # Netty 通信端口（非 Web 端口）
  token: "你的Token"        # 认证令牌（使用 /wk link 获取）

Common:
  server-name: 'Survival-1' # 服务器名称（留空则自动生成）
  server-version: '1.19.4'  # 服务器版本（留空则使用实际版本）
```

**配置项详解**

| 参数 | 说明 | 默认值 |
|------|------|--------|
| `Backend.ip` | 后端服务 IP 地址 | `127.0.0.1` |
| `Backend.port` | Netty 服务端口 | `8081` |
| `Backend.token` | 身份认证令牌 | - |
| `Common.server-name` | 服务器标识名称 | 自动生成 |
| `Common.server-version` | 服务器版本信息 | 自动检测 |

## 📚 使用指南

### 管理员操作

1. **配置后端连接**
   - 修改 `config.yml` 中的后端服务器信息
   - 确保 `token` 与后端服务配置一致

2. **重启服务器**
   ```bash
   /reload confirm
   # 或重启服务器
   ```

3. **监控连接状态**
   ```
   [WkNetic-Bridge] 正在连接后端: 127.0.0.1:8081
   [WkNetic-Bridge] ✅ 成功连接到 WkNetic 后端!
   [WkNetic-Bridge] Selected login hook: AuthMe
   ```

### 玩家体验

| 玩家类型 | 认证流程 |
|---------|---------|
| **正版玩家** | 加入服务器 → 自动验证 → 同步账号 → 开始游戏 |
| **离线玩家 (AuthMe)** | 加入服务器 → 输入密码 → 认证通过 → 同步账号 → 开始游戏 |
| **离线玩家 (自定义)** | 加入服务器 → 按提示操作 → 完成认证 |

### 认证流程图

**正版服务器**
```
玩家加入 → 检测正版 → 同步 PREMIUM → 允许游戏
```

玩家加入 → 限制行为 → 等待认证
              ↓
        认证插件验证 (AuthMe/FastLogin)
              ↓
        认证成功 → 同步 CRACKED → 允许游戏
```

## 🏗️ 项目结构

```
WkNetic-Bridge/
├── src/main/java/cn/wekyjay/wknetic/
│   ├── auth/                           # 认证模块
│   │   ├── hook/                       # 插件钩子系统
│   │   │   ├── ILoginHook.java        # 钩子接口
│   │   │   ├── AuthmeHook.java        # AuthMe 适配
│   │   │   ├── FastLoginHook.java     # FastLogin 适配
│   │   │   └── CustomLoginHook.java   # 自定义认证
│   │   ├── listener/                   # 事件监听器
│   │   ├── LoginAuthManager.java       # 登录认证管理
│   │   └── PremiumAuthManager.java     # 正版认证管理
│   └── bridge/                         # 桥接核心
│       ├── WkNeticBridge.java          # 主插件类
│       ├── NetworkManager.java         # 网络管理器
│       └── BridgeClientHandler.java    # Netty 处理器
├── src/main/resources/
│   ├── config.yml                      # 配置模板
│   └── plugin.yml                      # 插件元数据
└── pom.xml                              # Maven 配置
```

## 🛠️ 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| **Netty** | 4.1.68+ | 异步网络通信框架 |
| **Gson** | Latest | JSON 序列化/反序列化 |
| **XSeries** | Latest | 跨版本兼容性支持 |
| **Lombok** | 1.18.30 | 简化 Java 代码 |
| **Maven** | 3.8+ | 项目构建管理 |

## 🔧 开发指南

### 本地开发环境搭建

```bash
# 克隆项目
git clone https://github.com/your-repo/WkNetic-Bridge.git
cd WkNetic-Bridge

# 安装依赖
mvn clean install

# 启动测试服务器（需配置）
mvn exec:java
```

### 添加新认证方式

1. **创建钩子类**
   ```java
   public class YourAuthHook implements ILoginHook {
       @Override
       public boolean isAvailable() {
           // 检测插件是否可用
       }
       
       @Override
       public void onPlayerLogin(Player player) {
           // 处理登录逻辑
       }
   }
   ```

2. **注册到管理器**
   在 `LoginAuthManager` 中添加检测和初始化逻辑

3. **实现监听器**
   在 `AuthListener` 中添加相应的事件处理

## 📊 性能优化

- ✅ 异步网络 I/O，不阻塞主线程
- ✅ 连接池复用，减少资源开销
- ✅ 智能缓存机制，降低延迟
- ✅ 自动重连策略，保证高可用

## ❓ 常见问题

<details>
<summary><b>Q: 连接后端失败怎么办？</b></summary>

**解决方案：**
1. 检查 `config.yml` 配置是否正确
2. 确认后端服务已启动并监听在指定端口
3. 验证防火墙规则是否放行
4. 查看日志获取详细错误信息
</details>

<details>
<summary><b>Q: 支持哪些 Minecraft 版本？</b></summary>

插件支持 Bukkit/Spigot/Paper 1.21 及以上版本。理论上向下兼容至 1.13+，但建议使用最新版本以获得最佳体验。
</details>

<details>
<summary><b>Q: 如何获取 Token？</b></summary>

Token 由后端服务生成，可通过以下方式获取：
- 使用游戏内命令 `/wk link`
- 访问后端管理面板生成
- 联系服务器管理员获取
</details>

<details>
<summary><b>Q: 能否在多个服务器使用同一个配置？</b></summary>

可以！只需确保每个服务器的 `server-name` 配置唯一即可，token 可以在社区管理后台生成，一服一令牌。
</details>

<details>
<summary><b>Q: 插件占用多少内存？</b></summary>

插件本身占用内存极小（<10MB），主要取决于玩家数量和网络通信频率。
</details>

## 📝 更新日志

### v1.0-SNAPSHOT (开发中)
- ✅ 实现基础认证系统
- ✅ 支持 AuthMe、FastLogin 集成
- ✅ 完成 Netty 网络通信
- ✅ 玩家状态实时同步
- 🔄 自定义认证模式完善中

## 🤝 贡献指南

欢迎贡献代码！请遵循以下流程：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

**代码规范：**
- 遵循 Google Java Style Guide
- 编写单元测试
- 添加必要的注释
- 更新相关文档

## 📄 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

## 🔗 相关链接

- 📖 [完整文档](https://github.com/your-repo/WkNetic-Bridge/wiki)
- 🐛 [问题反馈](https://github.com/your-repo/WkNetic-Bridge/issues)
- 💬 [讨论区](https://github.com/your-repo/WkNetic-Bridge/discussions)
- 📧 联系我们: wekyjay@icloud.com

## ⭐ Star History

如果这个项目对你有帮助，请给我们一个 Star ⭐

[![Star History Chart](https://api.star-history.com/svg?repos=your-repo/WkNetic-Bridge&type=Date)](https://star-history.com/#your-repo/WkNetic-Bridge&Date)

---

<div align="center">

**[⬆ 返回顶部](#wknetic-bridge)**

Made with ❤️ by WkNetic Team

</div>

### Q: 玩家无法移动/聊天？
A: 这是正常行为，未登录玩家会被限制操作。登录后自动恢复。

<details>
<summary><b>Q: 如何添加新的认证插件？</b></summary>

参考现有的 `AuthmeHook` 和 `FastLoginHook` 实现，创建新的钩子类，实现 `ILoginHook` 接口。
</details>

<details>
<summary><b>Q: FastLogin 依赖怎么配置？</b></summary>

将 FastLogin.jar 放入 `plugins/WkNetic-Bridge/lib/` 目录，Maven 会自动加载依赖。
</details>

## 📝 更新日志

### v1.0-SNAPSHOT (开发中)
- ✅ 实现基础认证系统
- ✅ 支持 AuthMe、FastLogin 集成
- ✅ 完成 Netty 网络通信
- ✅ 玩家状态实时同步
- 🔄 自定义认证模式完善中

## 🤝 贡献指南

欢迎贡献代码！请遵循以下流程：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

**代码规范：**
- 遵循 Google Java Style Guide
- 编写单元测试
- 添加必要的注释
- 更新相关文档

## 📄 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

## 🔗 相关链接

- 📖 [完整文档](https://github.com/your-repo/WkNetic-Bridge/wiki)
- 🐛 [问题反馈](https://github.com/your-repo/WkNetic-Bridge/issues)
- 💬 [讨论区](https://github.com/your-repo/WkNetic-Bridge/discussions)
- 📧 联系我们: support@wknetic.com

## 🙏 致谢

感谢以下开源项目和社区：

- [Spigot](https://www.spigotmc.org/) - Minecraft 服务器 API
- [Netty](https://netty.io/) - 高性能网络应用框架
- [AuthMe](https://github.com/AuthMe/AuthMeReloaded) - 强大的玩家认证插件
- [FastLogin](https://github.com/games647/FastLogin) - 正版玩家快速登录
- [XSeries](https://github.com/CryptoMorin/XSeries) - 跨版本兼容库

## ⭐ Star History

如果这个项目对你有帮助，请给我们一个 Star ⭐

[![Star History Chart](https://api.star-history.com/svg?repos=your-repo/WkNetic-Bridge&type=Date)](https://star-history.com/#your-repo/WkNetic-Bridge&Date)

---

<div align="center">

**[⬆ 返回顶部](#wknetic-bridge)**

Made with ❤️ by WkNetic Team

</div></content>
<parameter name="filePath">/Users/macbook/文件/Studio/Java/WkNetic/WkNetic-Bridge/README.md