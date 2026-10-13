# DateUtils
## Choix de la classe DateUtilis

La classe `org.apache.tika.utils.DateUtils`, dans `tika-core`,
permet de formater des dates et d'interpréter plusieurs formats.
Elle possède déjà des tests indirects, notamment `testGetSetDate()`
dans `tika-core/src/test/java/org/apache/tika/metadata/TestMetadata.java`.
Nous la retenons parce que sa couverture est incomplète et que
cinq mutants survivent aux tests originaux. Une forte couverture
ne garantit donc pas que les assertions détectent toutes les erreurs.
Il est pertinent d'analyser une classe possédant déjà une forte couverture
(mais toujours inférieure à 100%).

Mesures JaCoCo avant ajout de tests :
Classe : 98 % des instructions et 85 % des branches.
Méthode `tryToParse(String)` : 96 % des instructions et 83 % des branches.


## PIT avec les tests originaux (DateUtils)
Commande utilisé pour l'analyse :
```bash

./mvnw -pl tika-core test-compile org.pitest:pitest-maven:mutationCoverage -DtargetClasses=org.apache.tika.utils.DateUtils
```
Résultats :
| Mesure | Résultat initial |
| --- | --- |
| Couverture des lignes selon PIT | 37/38 (97 %) |
| Mutants générés | 23 |
| Mutants détectés (`KILLED`) | 18 |
| Mutants survivants (`SURVIVED`) | 5 |
| Mutants non couverts (`NO_COVERAGE`) | 0 |
| Score de mutation | 18/23 = 78,26 % |

Mutants survivants dans `DateUtils.java` :

| Ligne | Mutation |
| --- | --- |
| 63 | Inversion de la condition sur le fuseau horaire |
| 64 | Suppression de l'appel à `setTimeZone` |
| 66 | Retour de `null` dans `createDateFormat` |
| 138 | Retour d'une liste vide dans `loadDateFormats` |
| 151 | Remplacement de la condition de longueur `< 6` par `<= 6` |

## Génération et correction du test DateUtils

La génération a été exécutée avec ChatUniTest 2.1.1 et le modèle
`codeqwen:v1.5-chat`, exécuté localement avec Ollama.
Paramètres : `testNumber=1`, `maxRounds=2`, `thread=false`.

Commande depuis la racine du dépôt :

```bash
./mvnw -f tika-core/pom.xml \
  io.github.zju-aces-ise:chatunitest-maven-plugin:2.1.1:method \
  '-DselectMethod=org.apache.tika.utils.DateUtils#tryToParse'
```

### Résultat sans correction manuelle

L'exécution a duré 9 min 7 s. Aucun test n'a été automatiquement validé :

- Première ronde : échec de compilation à cause d'imports Mockito
  ajoutés par ChatUniTest, absents de la réponse initiale du modèle.
- Deuxième ronde : le modèle a proposé davantage de Mockito et une
  réponse séparée en plusieurs blocs. ChatUniTest n'a pas pu extraire le code.

### Corrections et intégration

Le premier test généré a été repris dans
`tika-core/src/test/java/org/apache/tika/utils/DateUtilsChatUniTestTest.java`.

Trois corrections ont été appliquées manuellement :

1. Nettoyage des imports inutiles, notamment Mockito.
2. Construction de la date attendue avec `Date.from(Instant.parse(...))`.
   Cela évite l'exception non gérée de `SimpleDateFormat.parse` et fixe
   explicitement l'instant attendu en UTC.
3. Remplacement de la donnée prétendument invalide par `"not-a-date"`.
   La chaîne initialement choisie, `"2022-01-01T12:00:00"`, est acceptée
   par Tika.

### Critique des oracles

Le test généré propose trois scénarios : une date valide, une chaîne
présentée comme invalide et une valeur nulle. Cependant, son oracle
initial dépend du fuseau local et la chaîne présentée comme invalide
est en réalité acceptée par Tika. Après correction, `assertEquals` 
vérifie un instant UTC précis.
Les deux assertions `assertNull` vérifient le rejet de `"not-a-date"`
et de `null`. Ces assertions vérifient des résultats observables,
mais la justesse des données et des valeurs attendues a nécessité
une intervention humaine.

Le test original `TestMetadata.testGetSetDate()` possède déjà des
oracles précis : il compare notamment des dates à des valeurs
connues en millisecondes et vérifie plusieurs formats et décalages
horaires. Le test généré corrigé couvre moins de variantes, mais
exerce directement une nouvelle instance de `DateUtils`.

Le test ajouté manuellement, `acceptsSixCharacterDate()`, cible une
limite absente du test généré : une entrée reconnue de six caractères.
Son oracle `assertNotNull` est moins précis qu'une égalité sur 
la date obtenue, mais il suffit pour distinguer l'acceptation de l'entrée
de son rejet par le mutant `< 6` remplacé par `<= 6`.

Les deux nouveaux tests sont donc complémentaires : le premier
vérifie des résultats pour trois scénarios, tandis que le second
cible précisément le mutant restant.


### Validation locale

```bash
./mvnw -pl tika-core -Dtest=DateUtilsChatUniTestTest test
```

Résultats obtenus : une méthode de test contenant trois assertions,
zéro échec, zéro erreur et `BUILD SUCCESS`.

## PIT après ajout du test généré corrigé

L'analyse utilise la même commande et la même configuration PIT
que l'analyse initiale, avec le test généré corrigé ajouté aux tests originaux.

| Mesure | Tests originaux | Avec le test généré corrigé |
|---|---:|---:|
| Mutants générés | 23 | 23 |
| Détectés | 18 | 22 |
| Survivants | 5 | 1 |
| Sans couverture | 0 | 0 |
| Score de mutation | 78,26 % | 95,65 % |
| Lignes couvertes selon PIT | 37/38 | 38/38 |

Le gain est de quatre mutants détectés, soit un gain de 17,39 point de pourcntage.

### Mutants détectés par le test ajouté

Le rapport XML attribue huit détections à
`DateUtilsChatUniTestTest.testTryToParse()`.
Quatre correspondent à des mutants auparavant survivants :

| Ligne | Mutation | Pourquoi le test la détecte |
|---|---|---|
| 63 | Inversion de la condition sur le fuseau | Le format avec `Z` conserve le fuseau local au lieu d'UTC. Dans un environnement non UTC, l'instant obtenu diffère de l'instant attendu. |
| 64 | Suppression de `setTimeZone` | L'interprétation de la date utilise le fuseau local. L'égalité avec l'instant UTC attendu échoue dans un environnement non UTC. |
| 66 | Retour de `null` dans `createDateFormat` | L'utilisation d'un formateur nul provoque une exception pendant l'analyse de la date valide. |
| 138 | Retour d'une liste vide dans `loadDateFormats` | Aucun format n'est essayé : la méthode retourne `null` pour la date valide, ce qui fait échouer l'assertion. |

Les quatre autres mutants attribués au nouveau test étaient déjà détectés
par les tests originaux :

- Ligne 151, deux inversions de conditions : la date valide est rejetée
  prématurément et produit `null`.
- Ligne 157, remplacement de `n - 3` par `n + 3` :
  l'accès à la chaîne dépasse ses limites et provoque une exception.
- Ligne 164, remplacement du résultat par `null` :
  l'assertion sur la date valide échoue.

La détection des mutations portant sur le fuseau dépend de l'environnement
d'exécution ; le score précédent est celui mesuré localement.

### Mutant restant

À la ligne 151, PIT remplace `dateString.length() < 6`
par `dateString.length() <= 6`.

Le test ajouté ne fournit aucune chaîne de longueur exactement six.
Il ne distingue donc pas ces deux conditions.
Ce survivant montre que couvrir toutes les lignes ne garantit pas
de vérifier toutes les valeurs limites.

## Test manuel et résultat final pour DateUtils

Fichier contenant les tests manuels :
`tika-core/src/test/java/org/apache/tika/utils/DateUtilsManualTest.java`

| Élément | Documentation |
|---|---|
| Nom | `acceptsSixCharacterDate()` |
| Intention | Vérifier qu'une date reconnue par le parseur et longue d'exactement six caractères est acceptée. |
| Donnée choisie | `"99-1-1"` comporte exactement six caractères. Le parseur actuel accepte ces champs numériques abrégés. Cette donnée cible précisément la frontière modifiée par PIT. |
| Oracle | `assertNotNull` vérifie que l'analyse produit une date. Il vérifie ici l'aceptation de l'entrée, sans controler la valeur exacte de la date obtenue. |
| Mutant ciblé | Ligne 151 : remplacement de `dateString.length() < 6` par `dateString.length() <= 6`. |
| Pourquoi il est détecté | Le code original poursuit l'analyse et retourne une date. Le mutant rejette immédiatement cette entrée et retourne `null`, faisant échouer l'assertion. |

Le test ciblé passe sans erreur. Le rapport PIT confirme que ce test
détecte le dernier mutant survivant.

### Comparaison finale

| Suite cumulative | Détectés | Survivants | Sans couverture | Score |
|---|---:|---:|---:|---:|
| Tests originaux | 18 | 5 | 0 | 78,26 % |
| + test généré corrigé | 22 | 1 | 0 | 95,65 % |
| + test manuel | 23 | 0 | 0 | 100 % |

La dernière analyse couvre 38/38 lignes selon PIT et termine par
`BUILD SUCCESS`. Ces résultats concernent les 23 mutants produits
avec la configuration utilisée mais ils ne prouvent pas l'absence de bogues.

# Tests pour la classe EndianUtils
#### Couverture initiale JaCoCo : **23 % des instructions**, **35 % des branches**, **31/121 lignes**, soit environ **26 % des lignes**.

## Génération de tests : passage du modèle 3B au 7B

Premier essai : On a utilisé le modèle `qwen2.5-coder:3b`. La génération initiale et la réparation automatique ont échoué à la compilation : mauvais import d'exception, Mockito indisponible et problèmes de simulation du flux.

Configuration : URL locale `http://localhost:11434/v1/chat/completions`, clé de configuration `local`, `testNumber=1`, `maxRounds=2`, méthode ciblée `EndianUtils#readUIntBE`.

Deuxième essai : `qwen2.5-coder:7b`. On a essayé un modèle plus grand pour générer des tests plus efficaces.

Avec la même méthode et les mêmes paramètres, le 7B a proposé **9 méthodes de test**, avec de vrais flux `ByteArrayInputStream`. Les deux rondes ont encore échoué : deux constantes étaient trop grandes pour un `long` Java.

## Réparation des tests générés

Fichier ajouté : **`EndianUtilsChatUniTest7BTest.java`**. Les neuf cas du 7B ont été conservés et réparés.

**Cinq corrections**, en comptant le nettoyage des imports comme une correction :

1. Importer `EndianUtils.BufferUnderrunException` au bon emplacement.
2. Supprimer les imports inutiles, notamment ceux de Mockito.
3. Remplacer le résultat attendu 1 par **0** pour les huit octets commençant par quatre zéros.
4. Remplacer la constante du cas « long négatif » par **4294967295L**.
5. Remplacer celle du cas « long maximal » par **4294967295L** également.

La cause des trois erreurs dans les résultats attendus vient du fait que `readUIntBE` ne lit que quatre octets, même si le flux en contient huit. Les deux dernières corrections permettent également de résoudre les problèmes liés aux constantes impossibles à compiler. Enfin, des adaptations supplémentaires ont été apportées, notamment le renommage de la classe, l'ajout de la licence Apache et des ajustements de mise en forme.

## Tests manuels

Fichier : **`EndianUtilsManualTest.java`**.

| Test | Intention, données et résultat attendu |
|---|---|
| `rejectsThreeByteBigEndianInput` | `12 34 56` : un octet manque pour `readUIntBE`, donc `BufferUnderrunException`. |
| `readsBigEndianLongValues` | `01 02 03 84 85 06 07 08` : attendre `0x0102038485060708L`. Huit zéros doivent donner 0. |
| `readsLittleEndianLongValues` | Mêmes octets, ordre inversé : attendre `0x0807068584030201L`. Vérifier également huit zéros. |
| `rejectsSevenByteBigEndianLongInput` | Sept octets au lieu de huit pour `readLongBE` : attendre `BufferUnderrunException`. |
| `rejectsSevenByteLittleEndianLongInput` | Même vérification pour `readLongLE`. |

Les octets distincts révèlent les erreurs d'ordre ou de calcul; `84` et `85` exercent le bit de poids fort; les zéros vérifient une limite valide. Les flux tronqués sont exactement un octet trop courts.

## Comparaison des assertions

Les tests générés utilisent `assertEquals` pour vérifier la valeur obtenue et `assertThrows` pour vérifier qu'un flux trop court provoque l'exception attendue. Ils vérifient donc des résultats précis. Cependant, certains tests donnent huit octets à une méthode qui n'en lit que quatre : après correction, ils répètent des vérifications déjà présentes. Les tests originaux vérifiaient aussi déjà de grandes valeurs non signées.

Les tests manuels utilisent les mêmes assertions, mais ajoutent des cas manquants : un flux de trois octets pour `readUIntBE`, puis des lectures sur huit octets en big-endian et little-endian. Les valeurs attendues vérifient l'ordre des octets, tandis que les flux de sept octets vérifient le rejet des données incomplètes. Ils élargissent donc les comportements testés au-delà de la méthode ciblée par ChatUniTest.

## Résultats PIT

| Suite cumulative | Détectés | Survivants | Sans couverture | Score | Lignes couvertes |
|---|---:|---:|---:|---:|---:|
| Originaux | 38 | 14 | 154 | 18,45 % | 31/121 |
| + 9 tests générés corrigés | 39 | 13 | 154 | 18,93 % | 32/121 |
| + premier test manuel | 40 | 12 | 154 | 19,42 % | 32/121 |
| + quatre autres tests manuels | **76** | **24** | **106** | **36,89 %** | **54/121** |

Toujours **206 mutants**. Score = détectés / 206. PIT affiche **37 %** au dernier lancement; la couverture des lignes atteint **44,63 %**, affichée **45 %**.

### Mutant supplémentaire détecté par les tests générés

Le passage de 38 à 39 mutants détectés vient du test `testReadUIntBEWithZeroValues()`. Il fournit quatre octets nuls à `readUIntBE` et vérifie que le résultat est 0. À la ligne 111, le mutant remplace la condition `< 0` par `<= 0`. Il rejette alors ces octets pourtant valides et lève une exception : le test échoue et détecte donc le mutant.

### Limites des tests

À la fin, 24 mutants restent vivants : les tests exécutent le code concerné, mais ne détectent pas ces modifications. Les 106 mutants sans couverture correspondent à du code que les tests n'exécutent pas. La génération ciblait seulement `readUIntBE`, et les tests manuels ont ajouté des cas pour `readLongBE` et `readLongLE`; plusieurs autres méthodes restent donc insuffisamment testées.

Le nombre de survivants augmente parce que 12 mutants auparavant sans couverture sont maintenant exécutés, mais restent non détectés. Les tests améliorent la détection sans couvrir toute la classe. Nous n'avons pas démontré que les mutants restants sont équivalents au code original.
