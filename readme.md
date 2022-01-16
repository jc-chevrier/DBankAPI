## Solution
![Logo de de DBankAPI](doc/DBankAPI.png)

API pour effectuer des échanges métier avec la `DBank`.

____
### Auteurs
CHEVRIER Jean-Christophe

____
### Technologies, librairies, frameworks principaux

- `Java 17`
- `Maven`
- `Spring Boot`
- `Keycloak`

____
### Installation et exécution

Télécharger et installer `Java 17`
(si vous ne l'avez pas déjà).

Télécharger et installer `Keycloak`  
(si vous ne l'avez pas déjà).

Lancer le serveur Keycloak.
Pour le lancer en local : `bin\standalone.bat `
ou `bin/standalone.sh` selon votre OS.

Importer sur Keycloak la configuration
présente dans `keycloak/configuration.json`
sur le répertoire GitHub du projet.

Créer des utilisateurs sur Keycloak pour
les différents rôles existants : `Admin`,
`Client`, `ATM`, `Merchant`.

Lancer l'API avec les exc exécutables du projet :
`dbank_api.bat` ou`dbank_api.sh` selon votre OS.
