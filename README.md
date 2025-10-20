# Coin Deco - Backend E-Commerce

Une API REST pour une boutique de décoration en ligne, développée avec Spring Boot.

## À propos

Coin Deco est le backend d'une plateforme e-commerce permettant de gérer :
- Un catalogue de produits avec images
- Des commandes clients
- Des paiements sécurisés via Stripe
- Un espace d'administration protégé

## Technologies utilisées

- **Java 17** - Langage de programmation
- **Spring Boot 3.5** - Framework backend
- **Spring Security** - Sécurité et authentification
- **JWT** - Gestion des tokens d'authentification
- **MySQL** - Base de données
- **Stripe** - Traitement des paiements
- **Maven** - Gestion des dépendances

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :
- Java 17 ou supérieur
- Maven 3.6+
- MySQL 8.0+
- Un compte Stripe (pour les paiements)

## Installation

1. **Cloner le projet**
   ```bash
   git clone <url-du-repo>
   cd coin-deco-back
   ```

2. **Configurer la base de données**

   Créez une base de données MySQL :
   ```sql
   CREATE DATABASE coin_deco;
   ```

3. **Configurer l'application**

   Modifiez le fichier `src/main/resources/application.properties` avec vos informations :
   ```properties
   # Base de données
   spring.datasource.url=bdd
   spring.datasource.username=votre_username
   spring.datasource.password=votre_password

   # Stripe
   stripe.api.key=votre_clé_stripe
   ```

4. **Installer les dépendances**
   ```bash
   ./mvnw clean install
   ```

5. **Lancer l'application**
   ```bash
   ./mvnw spring-boot:run
   ```

L'API sera accessible sur `http://localhost:8080`

## Fonctionnalités principales

### Produits
- Consulter la liste des produits
- Voir les détails d'un produit
- Gérer les images des produits

### Commandes
- Créer une commande
- Suivre l'état d'une commande
- Historique des commandes
- Confirmer une commande

### Paiements
- Intégration Stripe pour les paiements sécurisés
- Gestion des transactions

### Administration
- Connexion administrateur avec JWT
- Gestion du catalogue produits
- Suivi des commandes
- Protection CSRF

## Structure du projet

```
src/main/java/com/example/coindecoback/
├── config/           # Configuration (sécurité, CORS, Stripe)
├── controller/       # Points d'entrée API (REST)
├── dto/             # Objets de transfert de données
├── entity/          # Entités de base de données
├── jwt/             # Utilitaires JWT
├── mapper/          # Conversion entre entités et DTO
├── repository/      # Accès aux données
└── service/         # Logique métier
```

## API Endpoints

### Produits
- `GET /api/products` - Liste tous les produits
- `GET /api/products/{id}` - Détails d'un produit

### Commandes
- `POST /api/orders` - Créer une commande
- `GET /api/orders/{id}` - Détails d'une commande
- `POST /api/orders/{id}/confirm` - Confirmer une commande

### Paiements
- `POST /api/payment/create-payment-intent` - Créer une intention de paiement

### Administration
- `POST /api/admin/login` - Connexion administrateur
- `POST /api/admin/products` - Ajouter un produit
- `PUT /api/admin/products/{id}` - Modifier un produit
- `DELETE /api/admin/products/{id}` - Supprimer un produit

## Sécurité

L'application utilise :
- **JWT** pour l'authentification des administrateurs
- **Spring Security** pour la protection des endpoints
- **Protection CSRF** pour les formulaires
- **CORS** configuré pour le frontend

## Développement

### Compiler le projet
```bash
./mvnw compile
```

### Lancer les tests
```bash
./mvnw test
```

### Créer un package
```bash
./mvnw package
```

Le fichier JAR sera généré dans `target/`

## Contributions

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir une issue ou une pull request.

## Licence

Ce projet est sous licence libre.

## Support

Pour toute question ou problème, ouvrez une issue sur le repository.
