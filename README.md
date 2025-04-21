# ✈️ SendToServer
![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Platform](https://img.shields.io/badge/Platform-Paper--Velocity-orange)
![License](https://img.shields.io/badge/License-MIT-brightgreen)
![Author](https://img.shields.io/badge/Made%20by-AlessioGTA-blueviolet)

> Un plugin minimalista e veloce per **trasferire i giocatori tra server Velocity** tramite comando o NPC.  
> Realizzato con ❤️ da **AlessioGTA** per il network **[MCLEGACY](https://www.mclegacy.it)**

## 📌 Caratteristiche
- ✅ Comando `/sendtoserver <nome_server>`
- ✅ Compatibile con **Velocity** (tramite canale BungeeCord supportato)
- ✅ Configurazione personalizzabile (`config.yml`)
- ✅ Pensato per l'uso con NPC (es. **Citizens**, **CommandNPC**)
- ✅ Colori console ANSI per un log pulito e professionale
- ✅ Supporto per **permessi** e comando `/sendtoserver reload`

## 📦 Installazione
1. Compila o scarica il file `.jar`
2. Inseriscilo nella cartella `plugins/` del server Paper **collegato a Velocity**
3. Riavvia il server o esegui `/reload`
4. Configura a piacimento il file `config.yml`

## 🧪 Comandi
| Comando                       | Descrizione                                  | Permesso                       |
|------------------------------|----------------------------------------------|--------------------------------|
| `/sendtoserver <server>`     | Teletrasporta un giocatore al server dato    | `sendtoserver.use`             |
| `/sendtoserver reload`       | Ricarica il file di configurazione           | `sendtoserver.reload`          |

## ⚙️ Configurazione (`config.yml`)
```yaml
# SendToServer - Plugin creato da AlessioGTA per MCLEGACY
# Website: https://www.mclegacy.it
# Questo plugin permette il trasferimento dei giocatori tra server Velocity
# tramite comando o NPC con messaggi personalizzabili

messages:
  server-not-found: "&cIl server specificato non esiste o non è raggiungibile."
  sending: "&aSei stato trasferito al server: &e%server%"
  no-permission: "&cNon hai il permesso per eseguire questo comando."
  config-reloaded: "&aConfigurazione ricaricata con successo!"
  usage: "&cUtilizzo corretto: /sendtoserver <server>"
```

## 🤖 Integrazione con NPC
Compatibile con:
- [Citizens](https://www.spigotmc.org/resources/citizens.13811/)
- [CommandNPC](https://www.spigotmc.org/resources/command-npc.302/)

Esempio comando da legare a un NPC:
```bash
/sendtoserver hub
```

## 🧑‍💻 Autore
**AlessioGTA**  
Founder del network **MCLEGACY**  
📍 [mclegacy.it](https://www.mclegacy.it)

## 📜 Licenza
Questo progetto è distribuito sotto licenza **MIT**. Sei libero di usarlo, modificarlo, e condividerlo citando l'autore.
