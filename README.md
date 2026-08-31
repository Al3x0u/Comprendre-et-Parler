# Comprendre et Parler

Application web de gestion d'horaires pour interprètes, développée par l'équipe **Silent Code**.

## Description

Comprendre et Parler permet de gérer les missions d'interprétariat : planification des rendez-vous, affectation des interprètes, gestion des demandes en attente et détection automatique des conflits d'horaire.

## Fonctionnalités

- **Calendrier interactif** (FullCalendar) pour visualiser et gérer les missions
- **Détection de conflits d'horaire** en temps réel avec retour visuel (toasts)
- **Gestion des demandes** (`/demandes`) avec filtrage et pagination
- **Gestion des comptes** : activation / désactivation des utilisateurs
- **Sécurité** : stockage des identifiants en session, politique de mots de passe

## Stack technique

- **Backend** : Java, Spring Boot
- **Frontend** : Thymeleaf, FullCalendar, JavaScript
- **Base de données** : Oracle SQL
- **Build** : Maven

## Prérequis

- JDK 17+
- Maven 3.8+
- Une instance Oracle SQL (locale ou distante)

## Installation

1. Cloner le dépôt :
```bash
   git clone https://github.com/Al3x0u/Comprendre-et-Parler.git
   cd Comprendre-et-Parler
```

2. Configurer la connexion à la base de données dans `src/main/resources/application.properties` :
```properties
   spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/xe
   spring.datasource.username=<votre_utilisateur>
   spring.datasource.password=<votre_mot_de_passe>
   spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
```

3. Compiler le projet :
```bash
   mvn clean install
```

## Lancement

```bash
mvn spring-boot:run
```

L'application est ensuite accessible sur [http://localhost:8080](http://localhost:8080).

## Équipe

Projet réalisé par l'équipe **Silent Code**.

## Licence

Projet académique — usage éducatif.
