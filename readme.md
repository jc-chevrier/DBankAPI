## Solution
![Logo de de DBankAPI](doc/DBankAPI.png)

API pour effectuer des échanges métier avec la `DBank`.

____
### Auteurs
CHEVRIER Jean-Christophe

____
### Description globale

Cette API permet la communication avec la `DBank` ou `Digital Bank`,
qui est une banque numérique fictive inventée pour le projet.

Cetet banque numérique `DBank` peut être comparée à des applications telles que `Revolut.com`.

Elle permet l'échange avec 4 types de rôle bien déterminés : 

- `Admin` : rôle des personnes ou des logiciels internes de la `DBank` exerçant des
  actions sur les données de la banque, sans aucunes limitations.

- `Client` : rôle des clients ou des applications mobiles de la `DBank`, via lesquelles un client
effectue des actions sur ses données : virement, changement de numéro de téléphone, demande de nouveau compte ou 
de carte etc.

- `ATM` : GAB en français : Guichet Automatique Bancaire, ce rôle correspond aux accès des distributeurs automatiques,
qui interrogent l'API pour différentes actions, telles que vérifier le code d'une carte ou retirer de l'argent
sur un compte, voir les dernières opérations, etc

- `Merhant` : marchand en français, ce rôle correspond aux accès des sites de e-commerce, qui communiquent avec l'API 
pour vérifier les informations d'une carte, ou encore réaliser des opérations bancaires / transactions.

<br>
Comme vous avez pu le lire un rôle peut correspondre à uhe personne humaine ou un logiciel client de l'API.

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

- Télécharger et installer `Keycloak`
(si vous ne l'avez pas déjà).

- Lancer le serveur Keycloak.
Pour le lancer en local, aller dans le répertoire de `Keycloak`, et lancer `bin\standalone.bat`
ou `bin/standalone.sh` selon votre OS.

- Importer sur Keycloak la configuration `keycloak/configuration.json`,
présente sur le répertoire `GitHub` du projet.

- Créer des utilisateurs sur Keycloak, pour
les différents rôles existants : `Admin`,
`Client`, `ATM`, `Merchant`.

- Lancer l'API avec les exécutables du projet :
`dbank_api.bat` ou `dbank_api.sh` selon votre OS.
