# WkNetic-Bridge

[中文](README.md) | [English](README.en.md)

WkNetic-Bridge is a Minecraft server plugin that bridges Minecraft servers with backend community services, enabling player account synchronization and authentication.

## Features

- **Multi-Authentication Mode Support**:
  - Online-mode servers: Automatic synchronization of premium players
  - Offline-mode servers: Support for AuthMe and FastLogin plugins
  - Fallback custom authentication: Provides custom login verification when the above plugins are unavailable

- **Player Behavior Control**:
  - Unauthenticated players are prohibited from moving, chatting, and executing commands
  - Normal permissions are restored after successful login

- **Real-time Synchronization**:
  - Automatic account synchronization to backend community services when players join
  - Support for distinguishing between premium and offline account types

- **Network Communication**:
  - High-performance network connection based on Netty
  - JSON format data transmission
  - Automatic reconnection mechanism

## System Requirements

- Minecraft Server: Supports Bukkit/Spigot API 1.21+
- Java Version: 21+
- Optional Dependencies:
  - AuthMe (Offline player authentication)
  - FastLogin (Premium player fast login)

## Installation Steps

1. **Download Plugin**:
   ```bash
   # Build the project
   mvn clean package
   ```

2. **Place Files**:
   - Place `target/WkNetic-Bridge-1.0-SNAPSHOT.jar` in the server's `plugins/` directory
   - If using FastLogin, place FastLogin.jar in the `plugins/WkNetic-Bridge/lib/` directory

3. **Start Server**:
   - The plugin will automatically generate configuration files

## Configuration

After the plugin runs for the first time, it will generate a default configuration in `plugins/WkNetic-Bridge/config.yml`:

```yaml
# Backend service configuration
backend:
  ip: "127.0.0.1"      # Backend server IP
  port: 8081           # Backend server port
  token: "your-token"  # Authentication token, please change to actual value
```

### Configuration Items

- `backend.ip`: IP address of the backend Spring Boot service
- `backend.port`: Port number of the backend service
- `backend.token`: Token for authentication, ensure it matches the backend configuration

## Usage

### 1. Server Administrator

- Modify backend configuration in `config.yml`
- Restart server for configuration to take effect
- Plugin will automatically connect to backend service

### 2. Player Experience

- **Premium Players**: Automatic synchronization after joining, no additional operations needed
- **Offline Players (AuthMe)**: Enter password after joining, synchronize after successful login
- **Offline Players (Custom)**: Enter password as prompted after joining (feature to be implemented)

### 3. Monitor Logs

The plugin will output connection status and synchronization information in the console:
```
[WkNetic-Bridge] Connecting to backend: 127.0.0.1:8081
[WkNetic-Bridge] ✅ Successfully connected to WkNetic backend!
[WkNetic-Bridge] Selected login hook: AuthMe
[WkNetic-Bridge] Community account synchronized successfully!
```

## Authentication Flow

### Online-mode Server
```
Player joins → Detect premium → Sync PREMIUM → Allow gameplay
```

### Offline-mode Server
```
Player joins → Detect premium → No → Add to unauthenticated list → Wait for authentication
                      ↓
               Authentication successful → Remove from list → Sync CRACKED → Allow gameplay
```

## Development Information

### Project Structure
```
src/main/java/cn/wekyjay/wknetic/
├── auth/                    # Authentication related
│   ├── hook/               # Plugin hooks
│   ├── listener/           # Event listeners
│   ├── AuthManager.java    # Authentication manager (split)
│   ├── LoginAuthManager.java    # Login authentication manager
│   └── PremiumAuthManager.java  # Premium authentication manager
├── bridge/                 # Bridge core
│   ├── BridgeClientHandler.java # Netty client handler
│   ├── NetworkManager.java      # Network manager
│   └── WkNeticBridge.java       # Main plugin class
└── resources/             # Resource files
    ├── config.yml         # Default configuration
    └── plugin.yml         # Plugin description
```

### Technology Stack
- **Network Communication**: Netty 4.1+
- **JSON Processing**: Gson
- **Version Compatibility**: XSeries (Multi-version support)
- **Build Tool**: Maven

### Extension Development

To add new authentication methods:

1. Create a new hook class under `auth/hook/`, implementing the `ILoginHook` interface
2. Add detection logic in the corresponding `AuthManager`
3. Add corresponding event handling in `AuthListener`

## Frequently Asked Questions

### Q: What to do if backend connection fails?
A: Check if the IP, port, and token in `config.yml` are correct, and if the backend service is running.

### Q: Why can't players move/chat?
A: This is normal behavior. Unauthenticated players have restricted actions. Normal permissions are restored after login.

### Q: How to add new authentication plugins?
A: Refer to existing `AuthmeHook` and `FastLoginHook` implementations to create new hook classes.

### Q: How to configure FastLogin dependency?
A: Place FastLogin.jar in the `lib/` directory, Maven will load it automatically.

## License

This project uses the MIT License.

## Contributing

Issues and Pull Requests are welcome to improve the project.

## Contact

For questions, please contact the developer through GitHub Issues.</content>
<parameter name="filePath">/Users/macbook/文件/Studio/Java/WkNetic/WkNetic-Bridge/README.en.md