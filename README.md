# Atelier Développement Mobile Natif - TP 2
**Consommer l'API REST depuis Android – Retrofit et architecture MVVM**

Ce projet implémente l'application Android communiquant avec l'API Node.js/Express (TP 1) en suivant l'architecture **MVVM** (Model-View-ViewModel) recommandée par Google.

---

## 🚀 Fonctionnalités réalisées
- **Connexion Réseau :** Utilisation de **Retrofit** et `kotlinx.serialization` pour consommer l'API REST.
- **Architecture MVVM & UDF :** Séparation des couches (UI, ViewModel, Repository, Source de données) avec un flux unidirectionnel via `StateFlow` et `SharedFlow`.
- **Opérations CRUD :**
  - Affichage de la liste des patients avec gestion des états (Chargement, Succès, Erreur).
  - Ajout d'un patient via une boîte de dialogue et un appel `POST`.
  - Modification d'un patient existant via un appel `PUT`.
  - Suppression d'un patient via un appel `DELETE` (code HTTP 204).
  - Filtrage des patients par statut (Tous, Actifs, Inactifs) à l'aide de `FilterChip`.
- **Gestion des Erreurs :** Traduction des erreurs réseau (`IOException`, `HttpException`) et affichage de messages dynamiques via une `Snackbar`.

---

## 📁 Structure du projet & Livrables
- **Code source Android :** Situé dans le dossier `app/src/main/java/com/example/patients/`
- **Captures d'écran :** Disponibles dans le dossier `Screenshots/`
- **Réponses aux questions :** Retrouvez l'intégralité des réponses de compréhension dans le fichier `reponses aux questions.txt`  à la racine du dépôt.
