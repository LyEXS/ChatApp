# 🚀 Déploiement ChatApp Server sur DigitalOcean

## Prérequis
- Un compte DigitalOcean (https://cloud.digitalocean.com)
- Git installé localement
- Votre code poussé sur un dépôt Git (GitHub, GitLab, etc.)

---

## Étape 1 — Créer un Droplet

1. Allez sur https://cloud.digitalocean.com/droplets/new
2. Choisissez :
   - **OS** : Ubuntu 24.04 LTS
   - **Plan** : Basic → Regular (CPU) → **$6/mois** (1 vCPU, 1 GB RAM) suffit pour commencer
   - **Région** : La plus proche de vos utilisateurs (ex: Frankfurt pour l'Europe)
   - **Authentification** : **SSH Key** (recommandé) ou Password
3. Cliquez **Create Droplet**
4. Notez l'**adresse IP** du Droplet (ex: `164.90.xxx.xxx`)

---

## Étape 2 — Se connecter au Droplet

```bash
ssh root@VOTRE_IP_DROPLET
```

---

## Étape 3 — Installer Docker et Docker Compose

```bash
# Mettre à jour le système
apt update && apt upgrade -y

# Installer Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Vérifier l'installation
docker --version
docker compose version
```

---

## Étape 4 — Cloner le projet sur le serveur

```bash
# Installer git si nécessaire
apt install git -y

# Cloner votre dépôt
cd /opt
git clone https://github.com/VOTRE_USER/VOTRE_REPO.git chatapp
cd chatapp/Server
```

> **Alternative sans Git** : Vous pouvez aussi transférer les fichiers avec `scp` :
> ```bash
> # Depuis votre machine locale
> scp -r /chemin/vers/Server root@VOTRE_IP_DROPLET:/opt/chatapp/Server
> ```

---

## Étape 5 — Configurer les variables d'environnement

```bash
# Éditer le fichier .env avec des mots de passe SÉCURISÉS
nano /opt/chatapp/Server/.env
```

Changez les valeurs par défaut :
```env
MYSQL_ROOT_PASSWORD=VotreMotDePasseRootSecurise123!
DB_NAME=chat_db
DB_USER=chatuser
DB_PASSWORD=VotreMotDePasseSecurise456!
```

---

## Étape 6 — Lancer l'application

```bash
cd /opt/chatapp/Server

# Construire et démarrer (première fois, le build prend ~2-3 min)
docker compose up -d --build

# Vérifier que tout tourne
docker compose ps

# Voir les logs
docker compose logs -f
# (Ctrl+C pour quitter les logs)
```

Vous devriez voir :
```
chat-mysql    | ... ready for connections ...
chat-server   | Serveur de Chat lancé sur le port 2026...
```

---

## Étape 7 — Configurer le Firewall

```bash
# Activer le firewall
ufw allow OpenSSH
ufw allow 2026/tcp    # Port du serveur Chat
ufw enable

# Vérifier
ufw status
```

---

## Étape 8 — Connecter votre client

Dans votre application client, changez l'adresse du serveur :
```
Host: VOTRE_IP_DROPLET
Port: 2026
```

---

## 🔧 Commandes utiles

| Commande | Description |
|----------|-------------|
| `docker compose up -d --build` | Rebuild et redémarrer |
| `docker compose down` | Arrêter les conteneurs |
| `docker compose down -v` | Arrêter + supprimer la BDD |
| `docker compose logs -f` | Voir les logs en temps réel |
| `docker compose logs -f server` | Logs du serveur uniquement |
| `docker compose logs -f mysql` | Logs de MySQL uniquement |
| `docker compose restart server` | Redémarrer le serveur |
| `docker compose ps` | Voir l'état des conteneurs |
| `docker exec -it chat-mysql mysql -uchatuser -p chat_db` | Accéder à MySQL |

---

## 🔄 Mettre à jour le serveur

```bash
cd /opt/chatapp/Server

# Récupérer les dernières modifications
git pull origin main

# Reconstruire et redémarrer
docker compose up -d --build
```

---

## 📊 Surveiller les ressources

```bash
# Utilisation CPU/RAM des conteneurs
docker stats

# Espace disque
df -h
```

---

## ⚠️ Notes importantes

1. **Sécurité** : Changez TOUJOURS les mots de passe par défaut dans `.env`
2. **Backup BDD** : Faites des backups réguliers :
   ```bash
   docker exec chat-mysql mysqldump -uchatuser -pVOTRE_MDP chat_db > backup_$(date +%Y%m%d).sql
   ```
3. **Restaurer un backup** :
   ```bash
   docker exec -i chat-mysql mysql -uchatuser -pVOTRE_MDP chat_db < backup.sql
   ```
4. **Le port MySQL (3306) n'est PAS exposé** sur Internet, seul le serveur Java peut y accéder via le réseau Docker interne.
5. **Données persistantes** : Les données MySQL sont stockées dans un volume Docker (`mysql_data`). Elles survivent aux redémarrages. Seul `docker compose down -v` les supprime.

