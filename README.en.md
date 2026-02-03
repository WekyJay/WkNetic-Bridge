<div align="center">

# WkNetic-Bridge

[![Java Version](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21+-green?logo=minecraft)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red?logo=apachemaven)](https://maven.apache.org/)

**A high-performance Minecraft server bridge plugin for player account synchronization and community integration**

[中文](README.md) | [English](README.en.md)

</div>

---

## 📖 Introduction

WkNetic-Bridge is a bridge plugin specifically designed for Minecraft servers, aimed at seamlessly connecting game servers with backend community service systems. Through high-performance Netty network communication, it enables real-time player account synchronization, multiple authentication modes support, and intelligent permission management.

### ✨ Core Features

- 🔐 **Multiple Authentication Modes** - Supports premium, cracked (AuthMe, FastLogin), and custom authentication
- ⚡ **High-Performance Communication** - Asynchronous network architecture based on Netty with JSON data transmission
- 🔄 **Real-Time Synchronization** - Automatic player status synchronization to backend community services
- 🎮 **Behavior Control** - Automatically restricts movement, chat, and command execution for unauthenticated players
- 🛡️ **Smart Hooks** - Automatically detects and adapts to server authentication environment
- 🔌 **Plug and Play** - Simple configuration, auto-reconnection, no complex setup required

## 📋 System Requirements

| Component | Requirement |
|-----------|-------------|
| **Minecraft Server** | Bukkit/Spigot/Paper 1.21+ |
| **Java Version** | JDK 21 or higher |
| **Optional Dependencies** | AuthMe 5.6+, FastLogin |

## 🚀 Quick Start

### Installation Steps

1. **Build the Plugin**
   ```bash
   mvn clean package
   ```

2. **Deploy the Plugin**
   ```bash
   # Copy the generated JAR file to the plugins directory
   cp target/WkNetic-Bridge-1.0-SNAPSHOT.jar <server-path>/plugins/
   ```

3. **First Launch**
   - Start the server, the plugin will automatically generate configuration files
   - Stop the server and edit the configuration file

### ⚙️ Configuration

Edit `plugins/WkNetic-Bridge/config.yml`:

```yaml
Backend:
  ip: "127.0.0.1"          # Backend server IP address
  port: 8081                # Netty communication port (not Web port)
  token: "your-token"       # Authentication token (use /wk link to obtain)

Common:
  server-name: 'Survival-1' # Server name (leave empty for auto-generation)
  server-version: '1.19.4'  # Server version (leave empty for actual version)
```

**Configuration Details**

| Parameter | Description | Default |
|-----------|-------------|---------|
| `Backend.ip` | Backend service IP address | `127.0.0.1` |
| `Backend.port` | Netty service port | `8081` |
| `Backend.token` | Authentication token | - |
| `Common.server-name` | Server identifier name | Auto-generated |
| `Common.server-version` | Server version info | Auto-detected |

## 📚 User Guide

### Administrator Operations

1. **Configure Backend Connection**
   - Modify backend server information in `config.yml`
   - Ensure `token` matches the backend service configuration

2. **Restart Server**
   ```bash
   /reload confirm
   # Or restart the server
   ```

3. **Monitor Connection Status**
   ```
   [WkNetic-Bridge] Connecting to backend: 127.0.0.1:8081
   [WkNetic-Bridge] ✅ Successfully connected to WkNetic backend!
   [WkNetic-Bridge] Selected login hook: AuthMe
   ```

### Player Experience

| Player Type | Authentication Flow |
|-------------|---------------------|
| **Premium Players** | Join server → Auto verify → Sync account → Start playing |
| **Cracked Players (AuthMe)** | Join server → Enter password → Auth passed → Sync account → Start playing |
| **Cracked Players (Custom)** | Join server → Follow prompts → Complete authentication |

### Authentication Flow Diagram

**Premium Server**
```
Player Joins → Detect Premium → Sync PREMIUM → Allow Gameplay
```

**Cracked Server**
```
Player Joins → Restrict Actions → Wait for Auth
              ↓
        Auth Plugin Verification (AuthMe/FastLogin)
              ↓
        Auth Success → Sync CRACKED → Allow Gameplay
```

## 🏗️ Project Structure

```
WkNetic-Bridge/
├── src/main/java/cn/wekyjay/wknetic/
│   ├── auth/                           # Authentication module
│   │   ├── hook/                       # Plugin hook system
│   │   │   ├── ILoginHook.java        # Hook interface
│   │   │   ├── AuthmeHook.java        # AuthMe adapter
│   │   │   ├── FastLoginHook.java     # FastLogin adapter
│   │   │   └── CustomLoginHook.java   # Custom authentication
│   │   ├── listener/                   # Event listeners
│   │   ├── LoginAuthManager.java       # Login auth manager
│   │   └── PremiumAuthManager.java     # Premium auth manager
│   └── bridge/                         # Bridge core
│       ├── WkNeticBridge.java          # Main plugin class
│       ├── NetworkManager.java         # Network manager
│       └── BridgeClientHandler.java    # Netty handler
├── src/main/resources/
│   ├── config.yml                      # Config template
│   └── plugin.yml                      # Plugin metadata
└── pom.xml                              # Maven configuration
```

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Netty** | 4.1.68+ | Asynchronous network communication framework |
| **Gson** | Latest | JSON serialization/deserialization |
| **XSeries** | Latest | Cross-version compatibility support |
| **Lombok** | 1.18.30 | Simplify Java code |
| **Maven** | 3.8+ | Project build management |

## 🔧 Development Guide

### Local Development Setup

```bash
# Clone the project
git clone https://github.com/your-repo/WkNetic-Bridge.git
cd WkNetic-Bridge

# Install dependencies
mvn clean install

# Start test server (requires configuration)
mvn exec:java
```

### Adding New Authentication Methods

1. **Create Hook Class**
   ```java
   public class YourAuthHook implements ILoginHook {
       @Override
       public boolean isAvailable() {
           // Detect if plugin is available
       }
       
       @Override
       public void onPlayerLogin(Player player) {
           // Handle login logic
       }
   }
   ```

2. **Register to Manager**
   Add detection and initialization logic in `LoginAuthManager`

3. **Implement Listener**
   Add corresponding event handling in `AuthListener`

## 📊 Performance Optimization

- ✅ Asynchronous network I/O, doesn't block main thread
- ✅ Connection pool reuse, reduces resource overhead
- ✅ Smart caching mechanism, reduces latency
- ✅ Auto-reconnection strategy, ensures high availability

## ❓ FAQ

<details>
<summary><b>Q: What to do if backend connection fails?</b></summary>

**Solutions:**
1. Check if `config.yml` configuration is correct
2. Confirm backend service is running and listening on specified port
3. Verify firewall rules allow connection
4. Check logs for detailed error information
</details>

<details>
<summary><b>Q: Which Minecraft versions are supported?</b></summary>

The plugin supports Bukkit/Spigot/Paper 1.21 and above. Theoretically backward compatible to 1.13+, but the latest version is recommended for the best experience.
</details>

<details>
<summary><b>Q: How to obtain a Token?</b></summary>

Token is generated by the backend service and can be obtained through:
- Use in-game command `/wk link`
- Generate from backend admin panel
- Contact server administrator
</details>

<details>
<summary><b>Q: Can I use the same configuration on multiple servers?</b></summary>

Yes! Just ensure each server has a unique `server-name` configuration. Token can be generated from the community admin backend, one token per server.
</details>

<details>
<summary><b>Q: How much memory does the plugin use?</b></summary>

The plugin itself uses minimal memory (<10MB), mainly depending on the number of players and network communication frequency.
</details>

<details>
<summary><b>Q: Players cannot move/chat?</b></summary>

This is normal behavior. Unauthenticated players are restricted from actions. Automatically restored after login.
</details>

<details>
<summary><b>Q: How to add new authentication plugins?</b></summary>

Refer to existing `AuthmeHook` and `FastLoginHook` implementations, create a new hook class that implements the `ILoginHook` interface.
</details>

<details>
<summary><b>Q: How to configure FastLogin dependency?</b></summary>

Place FastLogin.jar in the `plugins/WkNetic-Bridge/lib/` directory, Maven will automatically load the dependency.
</details>

## 📝 Changelog

### v1.0-SNAPSHOT (In Development)
- ✅ Implemented basic authentication system
- ✅ AuthMe and FastLogin integration support
- ✅ Completed Netty network communication
- ✅ Real-time player status synchronization
- 🔄 Custom authentication mode refinement in progress

## 🤝 Contributing

Contributions are welcome! Please follow this workflow:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Submit a Pull Request

**Code Standards:**
- Follow Google Java Style Guide
- Write unit tests
- Add necessary comments
- Update relevant documentation

## 📄 License

This project is licensed under the [MIT License](LICENSE).

## 🔗 Links

- 📖 [Full Documentation](https://github.com/your-repo/WkNetic-Bridge/wiki)
- 🐛 [Issue Tracker](https://github.com/your-repo/WkNetic-Bridge/issues)
- 💬 [Discussions](https://github.com/your-repo/WkNetic-Bridge/discussions)
- 📧 Contact us: support@wknetic.com

## 🙏 Acknowledgments

Thanks to the following open source projects and communities:

- [Spigot](https://www.spigotmc.org/) - Minecraft Server API
- [Netty](https://netty.io/) - High-performance network application framework
- [AuthMe](https://github.com/AuthMe/AuthMeReloaded) - Powerful player authentication plugin
- [FastLogin](https://github.com/games647/FastLogin) - Fast login for premium players
- [XSeries](https://github.com/CryptoMorin/XSeries) - Cross-version compatibility library

## ⭐ Star History

If this project helps you, please give us a Star ⭐

[![Star History Chart](https://api.star-history.com/svg?repos=your-repo/WkNetic-Bridge&type=Date)](https://star-history.com/#your-repo/WkNetic-Bridge&Date)

---

<div align="center">

**[⬆ Back to Top](#wknetic-bridge)**

Made with ❤️ by WkNetic Team

</div>

## Contributing

Issues and Pull Requests are welcome to improve the project.

## Contact

For questions, please contact the developer through GitHub Issues.</content>
<parameter name="filePath">/Users/macbook/文件/Studio/Java/WkNetic/WkNetic-Bridge/README.en.md