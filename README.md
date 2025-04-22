# 🚀 SendToServer v.1.4

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Velocity-blueviolet)
![Version](https://img.shields.io/badge/Version-1.4-success)
![License](https://img.shields.io/github/license/AlessioGTA/SendToServer)
![Author](https://img.shields.io/badge/Author-AlessioGTA-orange)
![Website](https://img.shields.io/badge/mclegacy.it-Visit-blue?logo=github)


> Plugin Minecraft Velocity + Paper per il trasferimento di giocatori tra server  
> ✨ Creato da **AlessioGTA** per il network [MCLEGACY](https://www.mclegacy.it)

---

## 📦 Descrizione

**SendToServer** è un plugin semplice e avanzato per server Paper con proxy Velocity che permette:

- Il trasferimento di giocatori tra server via comando
- Una GUI interattiva con NPC e menu cliccabili
- Sincronizzazione automatica dei server dal proxy Velocity
- Personalizzazione completa via config
- Sistema dinamico `UUID → Server` aggiornato live

---

## 🎮 Funzionalità principali

- ✅ `/sendtoserver <server>` per auto-trasferimento
- ✅ `/sendtoserver gui` per aprire la GUI con player e server
- ✅ GUI con teste dei player e lore informativo (server + coordinate)
- ✅ Supporto `GetServer` e `GetServers` via Plugin Messaging
- ✅ Tab-complete dinamico con i server reali
- ✅ Config `server-icons:` generato automaticamente
- ✅ Compatibile con NPC (es. Citizens + CommandNPC)
- ✅ Configurabile al 100% (`material`, `displayName`, `slot`, lore...)

---

## 📂 Comandi

| Comando                   | Descrizione                                 |
|--------------------------|---------------------------------------------|
| `/sendtoserver <server>` | Invia te stesso a un server                  |
| `/sendtoserver gui`      | Apre la GUI interattiva                      |
| `/sendtoserver reload`   | Ricarica il config.yml                      |

---

## 🔐 Permessi

| Permesso                 | Descrizione                       |
|--------------------------|-----------------------------------|
| `sendtoserver.use`       | Accesso ai comandi base           |
| `sendtoserver.reload`    | Ricarica il config                |

---

## ⚙️ Configurazione

Il file `config.yml` permette di personalizzare:

- Elenco server (sincronizzato automaticamente)
- Messaggi e testi GUI
- Lore interattiva con placeholders
- Icone personalizzabili per ogni server:

```yaml
server-icons:
  hub:
    material: NETHER_STAR
    name: "&bTorna all'HUB"
    slot: 10
