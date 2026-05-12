# Rapport d'Audit Automatisé : Synchronisation Backend-Frontend

## 1. Audit des Endpoints (HTMX Integrity)
- **Statut :** OK (avec quelques avertissements)
- **Fichier impacté :** Multiples
- **Description :** La majorité des endpoints HTMX (`hx-get`, `hx-post`, etc.) dans les templates Thymeleaf trouvent une correspondance dans les contrôleurs Spring.

## 2. Analyse de Signature des Méthodes
- **Statut :** ERREUR
- **Fichier impacté :** `mtg-collection-service/src/main/resources/templates/fragments/deals-section.html`
- **Description :** L'endpoint `/api/market/cart/sync` est appelé en POST par le bouton "Ajouter au Panier Cardmarket". Le contrôleur attend un payload JSON (`@RequestBody`), et le bouton utilise `hx-vals` pour envoyer un JSON, mais l'attribut `hx-ext="json-enc"` est manquant sur l'élément. Cela provoquera une erreur HTTP 415 Unsupported Media Type.
- **Suggestion de correction :** Ajouter l'attribut `hx-ext="json-enc"` sur le bouton correspondant.

- **Statut :** AVERTISSEMENT (Architecture HTMX)
- **Fichier impacté :** `mtg-collection-service/src/main/java/com/example/mtg/collection/controller/UIController.java`
- **Description :** Certaines méthodes qui renvoient des pages entières (comme `/ui/wishlist`) pourraient être appelées en mode fragment selon l'architecture stricte mentionnée dans la mémoire de l'application ("Controllers must check for the HX-Request header to return only the specific fragment"). Cependant, le code de `UIController` gère globalement cela via une annotation au niveau de la classe ou un fallback au niveau d'un intercepteur (à vérifier).

## 3. Rapport d'Audit
- Le rapport est généré et présenté en dessous.

## 4. Nettoyage et Optimisation
- **Statut :** INFO (Code Mort)
- **Fichier impacté :** `mtg-market-service/src/main/java/com/example/mtg/market/controller/OptimizerController.java`
- **Description :** L'endpoint `/api/market/optimize` est présent mais n'est pas appelé par l'interface utilisateur.
- **Suggestion de correction :** Vérifier s'il s'agit d'une API publique. Si ce n'est pas le cas, la supprimer.

- **Statut :** INFO (Code Mort)
- **Fichier impacté :** `mtg-collection-service/src/main/java/com/example/mtg/collection/controller/ReferenceController.java`
- **Description :** L'endpoint `/api/collection/reference/sets` est défini mais n'est jamais appelé dans les templates frontend.
- **Suggestion de correction :** Supprimer cette méthode.
