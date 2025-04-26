# 🚀 SendToServer v.1.5

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Velocity-blueviolet)
![Version](https://img.shields.io/badge/Version-1.5-success)
![License](https://img.shields.io/github/license/AlessioGTA/SendToServer)
![Author](https://img.shields.io/badge/Author-AlessioGTA-orange)
![Website](https://img.shields.io/badge/mclegacy.it-Visit-blue?logo=github)

> Minecraft Velocity + Paper plugin for transferring players between servers
> ✨ Created by **AlessioGTAII** for the [MCLEGACY](https://www.mclegacy.it) network

---

## 🆕 What's new in version 1.5

- ✅ **Introduction of the player statistics system** saved on MySQL
- ✅ **New tables**: `sts_player_status`, `sts_player_stats`, `sts_graphs_stats`
- ✅ **Support for collecting statistics** for balance, kills, deaths, game time, distance traveled, interactions and over 75 different events
- ✅ **Advanced configuration**: `stats_config.yml`, `stats_to_follow.yml`, `graphs_config.yml` files
- ✅ **Dynamic graphs** on the balance trend (and in the future also of the other stats!)
- ✅ **Practical example** of statistics display:
👉 [MCLEGACY Leaderboard](https://www.mclegacy.it/leaderboard/index.php)

SendToServer is no longer just a simple system for moving between servers: it becomes an **advanced player monitoring and management platform!**

---

## 📦 Description

**SendToServer** is a simple and advanced plugin for Paper servers with Velocity proxy that allows:

- Transfer of players between servers via command
- An interactive GUI with NPCs and clickable menus
- Automatic synchronization of servers from Velocity proxy
- Complete customization via config
- Dynamic `UUID → Server` system updated live
- Collection of **extended statistics** of players!

---

## 🎮 Main Features

- ✅ `/sendtoserver <server>` for auto-transfer
- ✅ `/sendtoserver gui` to open GUI with player and server
- ✅ GUI with player heads and lore info (server + coordinates)
- ✅ `GetServer` and `GetServers` support via Plugin Messaging
- ✅ Dynamic tab-complete with real servers
- ✅ Config `server-icons:` automatically generated
- ✅ Compatible with NPCs (e.g. Citizens + CommandNPC)
- ✅ 100% configurable (`material`, `displayName`, `slot`, lore...)
- ✅ **New**: automatic player stats collection
- ✅ **New**: database dedicated to stats graphs

---

## 📂 Commands

| Command | Description |
|----------------------------|--------------------------------------------|
| `/sendtoserver <server>` | Send yourself to a server |
| `/sendtoserver gui` | Opens the interactive GUI |
| `/sendtoserver reload` | Reloads config.yml and stats configurations |

---

## 🔐 Permissions

| Permission | Description |
|--------------------------|-----------------------------------|
| `sendtoserver.use` | Access to basic commands |
| `sendtoserver.reload` | Reloads config |

---

## ⚙️ Configuration

The `config.yml` file allows you to customize:

- Server list (automatically synchronized)
- Messages and GUI texts
- Interactive lore with placeholders
- Customizable icons for each server:

```yaml
server-icons:
hub:
material: NETHER_STAR
name: "&bBack to HUB"
slot: 10
