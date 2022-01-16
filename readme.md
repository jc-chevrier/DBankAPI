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

- Télécharger et installer `Java 17`
(si vous ne l'avez pas déjà).
<br>
<br>
- Télécharger et installer `Keycloak`
(si vous ne l'avez pas déjà).
  <br>
  <br>
- Lancer le serveur Keycloak.
Pour le lancer en local, dans le répertoire de Keycloak, lancer, `bin\standalone.bat`
ou `bin/standalone.sh` selon votre OS.
  <br>
  <br>
- Importer sur Keycloak la configuration `keycloak/configuration.json`,
présente sur le répertoire GitHub du projet.
  <br>
  <br>
- Créer des utilisateurs sur Keycloak,pour
les différents rôles existants : `Admin`,
`Client`, `ATM`, `Merchant`.
  <br>
  <br>
- Lancer l'API avec les exécutables du projet :
`dbank_api.bat` ou`dbank_api.sh` selon votre OS.
