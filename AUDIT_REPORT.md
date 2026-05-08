# Rapport d'Audit Automatisé : Synchronisation Backend-Frontend

## 1. Audit des Endpoints (HTMX Integrity)

### Analyse des fichiers HTML
- `mtg-collection-service/src/main/resources/templates/views/settings.html`:
  - `hx-get="?lang=fr"` -> Mappé (paramètre géré globalement par Spring)
  - `hx-get="?lang=en"` -> Mappé
  - `hx-get="?lang=de"` -> Mappé
  - `hx-get="?lang=es"` -> Mappé
- `mtg-collection-service/src/main/resources/templates/views/collection.html`:
  - `hx-get="/ui/collection/add"` -> Mappé (`UIController.getAddCardForm()`)
  - `hx-get="/ui/collection/filter"` -> Mappé (`UIController.filterCollection()`)
  - `hx-get="/ui/deals/fragment"` -> Mappé (`UIController.getDealsFragment()`)
- `mtg-collection-service/src/main/resources/templates/views/market-search.html`:
  - `hx-post="/ui/market/simulate"` -> **NON MAPPÉ**
- `mtg-collection-service/src/main/resources/templates/base.html`:
  - `hx-get="/ui/deals/count"` -> Mappé (`UIController.getDealsCount()`)
  - `hx-post="/api/ai/ask"` -> Mappé (`ChatController.askAi()`)
- `mtg-collection-service/src/main/resources/templates/fragments/add-card-modal.html`:
  - `hx-post="/ui/collection/add"` -> Mappé (`UIController.addCard()`)
- `mtg-collection-service/src/main/resources/templates/fragments/deals-section.html`:
  - `hx-get="/ui/deals/fragment"` -> Mappé (`UIController.getDealsFragment()`)
  - `hx-post="/ui/deals/{id}/ignore"` -> Mappé (`UIController.ignoreDeal()`)
  - `hx-post="/api/market/cart/sync"` -> Mappé (`OptimizerController.syncCart()`)

### Résumé des anomalies détectées
- **Statut :** ERREUR
- **Fichier impacté :** `mtg-collection-service/src/main/resources/templates/views/market-search.html`
- **Description :** L'endpoint `/ui/market/simulate` est appelé en POST par le bouton "Simuler l'achat (Ajout BD)" (`hx-post="/ui/market/simulate"`), mais le contrôleur `UIController` (ni aucun autre) ne possède de méthode correspondante.
- **Suggestion de correction :** Ajouter une méthode `@PostMapping("/market/simulate")` dans `UIController`.

## 4. Nettoyage et Optimisation

### Code Mort Identifié
- **Méthode :** `UIController.handleAiChat()`
- **Endpoint associé :** `@PostMapping("/ai/chat")` (Ancien endpoint : `/ui/ai/chat`)
- **Description :** Cette méthode n'est plus appelée nulle part dans le frontend. L'interface utilise désormais `hx-post="/api/ai/ask"` dans `base.html` qui est géré par `ChatController.askAi()`.
- **Suggestion :** Supprimer la méthode `handleAiChat()` dans `UIController.java`.
