# trade-route-optimizer
Your AI best friend for buying MTG cards

# 🃏 Your AI best friend for buying MTG cards

Une solution moderne et intelligente pour gérer votre collection Magic: The Gathering, suivre l'évolution du marché et optimiser vos achats via l'intelligence artificielle.

## 🚀 Vision du projet

L'achat de cartes à l'unité peut s'avérer complexe lorsque l'on cherche à minimiser les frais de port et à trouver le meilleur prix global. Ce projet transforme cette corvée en une expérience conversationnelle. Grâce au **Function Calling**, l'assistant n'est pas seulement un chatbot : il interagit directement avec votre inventaire et les données en temps réel de Cardmarket pour exécuter vos stratégies d'achat.

## 🛠 Stack Technique

- **Backend :** Java 17 avec **Spring Boot 3.x**
- **Architecture :** Microservices modulaires (Gateway, Collection, Market, AI Orchestrator)
- **Base de données :** **PostgreSQL** avec support **pgvector** pour la recherche sémantique
- **IA :** **Spring AI** (Support de modèles locaux via Ollama ou Cloud)
- **Gestion de l'infra :** Docker & Docker Compose
- **Interface :** Web sobre et réactive (Thymeleaf + HTMX)

## 🏗 Architecture du système

Le projet est découpé en services spécialisés pour garantir une maintenance aisée et une scalabilité optimale :

- **`mtg-gateway`** : Point d'entrée unique gérant le routage des requêtes.
- **`mtg-collection-service`** : Gestion persistante de votre inventaire personnel et de vos listes de souhaits.
- **`mtg-market-service`** : Client haute performance pour l'API Cardmarket, responsable du tracking des prix et de l'algorithme de consolidation des paniers.
- **`mtg-ai-orchestrator`** : Le cerveau du projet. Il utilise le **Tool Calling** pour transformer vos requêtes en langage naturel (ex: *"Trouve-moi 4 Sol Ring au meilleur prix total"*) en appels API concrets.

## 🤖 Capacités IA & Automatisation

L'assistant est conçu pour dépasser la simple recherche textuelle :
- **Optimisation Logistique :** Analyse croisée des stocks vendeurs pour réduire drastiquement le nombre de colis.
- **Analyse de Tendance :** Détection des opportunités d'achat basées sur l'historique des prix stocké en base de données.
- **Interface Conversationnelle :** Posez vos questions comme à un ami expert, l'IA se charge du reste.

## 🚦 Démarrage Rapide

### Prérequis
- Java 17+
- Docker & Docker Compose
- Une instance Ollama (pour l'IA locale) ou une clé API Cloud

### Installation
1. Cloner le dépôt :
   ```bash
   git clone [https://github.com/yann5963/trade-route-optimizer.git](https://github.com/yann5963/trade-route-optimizer.git)
