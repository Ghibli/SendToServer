# 🚀 SendToServer v.1.5

![Java](https://img.shields.io/badge/Java-17-blue?logo=java)
![Platform](https://img.shields.io/badge/Platform-Paper%20%7C%20Velocity-blueviolet)
![Version](https://img.shields.io/badge/Version-1.5-success)
![License](https://img.shields.io/github/license/AlessioGTA/SendToServer)
![Author](https://img.shields.io/badge/Author-AlessioGTA-orange)
![Website](https://img.shields.io/badge/mclegacy.it-Visit-blue?logo=github)

> Plugin Minecraft Velocity + Paper per il trasferimento di giocatori tra server  
> ✨ Creato da **AlessioGTAII** per il network [MCLEGACY](https://www.mclegacy.it)

---

## 🆕 Novità della versione 1.5

- ✅ **Introduzione del sistema di statistiche dei giocatori** salvato su MySQL
- ✅ **Nuove tabelle**: `sts_player_status`, `sts_player_stats`, `sts_graphs_stats`
- ✅ **Supporto alla raccolta delle statistiche** di bilancio, uccisioni, morti, tempo di gioco, distanza percorsa, interazioni e oltre 75 eventi diversi
- ✅ **Configurazione avanzata**: file `stats_config.yml`, `stats_to_follow.yml`, `graphs_config.yml`
- ✅ **Grafici dinamici** sull'andamento del bilancio (e in futuro anche delle altre stats!)
- ✅ **Esempio pratico** di visualizzazione statistiche:  
  👉 [Leaderboard MCLEGACY](https://www.mclegacy.it/leaderboard/index.php)

SendToServer non è più solo un semplice sistema di spostamento tra server: diventa una **piattaforma avanzata di monitoraggio e gestione dei giocatori!**

---

## 📦 Descrizione

**SendToServer** è un plugin semplice e avanzato per server Paper con proxy Velocity che permette:

- Il trasferimento di giocatori tra server via comando
- Una GUI interattiva con NPC e menu cliccabili
- Sincronizzazione automatica dei server dal proxy Velocity
- Personalizzazione completa via config
- Sistema dinamico `UUID → Server` aggiornato live
- Raccolta **statistiche estese** dei giocatori!

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
- ✅ **Nuovo**: raccolta automatica di statistiche giocatore
- ✅ **Nuovo**: database dedicato ai grafici delle statistiche

---

## 📂 Comandi

| Comando                   | Descrizione                                 |
|----------------------------|---------------------------------------------|
| `/sendtoserver <server>`   | Invia te stesso a un server                  |
| `/sendtoserver gui`        | Apre la GUI interattiva                      |
| `/sendtoserver reload`     | Ricarica il config.yml e le configurazioni stats |

---

## 🔐 Permessi

| Permesso                 | Descrizione                       |
|---------------------------|-----------------------------------|
| `sendtoserver.use`        | Accesso ai comandi base           |
| `sendtoserver.reload`     | Ricarica il config                |

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
