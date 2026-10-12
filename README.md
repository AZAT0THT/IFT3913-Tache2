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


# Tests pour la classe EndianUtils
#### Couverture initiale JaCoCo : **23 % des instructions**, **35 % des branches**, **31/121 lignes**, soit environ **26 % des lignes**.


## Génération de tests : passage du modèle 3B au 7B

Premier essai : On utililse le modèle `qwen2.5-coder:3b`. La génération initiale et la réparation automatique ont échoué à la compilation : mauvais import d’exception, Mockito indisponible et problèmes de simulation du flux.

Configuration : URL locale `http://localhost:11434/v1/chat/completions`, clé de configuration `local`, `testNumber=1`, `maxRounds=2`, méthode ciblée `EndianUtils#readUIntBE`.

Deuxième essai : `qwen2.5-coder:7b`. On a essayé un modèle plus grand pour générer des tests plus efficaces.

Avec les mêmes méthodes et mêmes paramètres, le 7B a proposé **9 méthodes de test**, avec de vrais flux `ByteArrayInputStream`. Les deux rondes ont encore échoué : deux constantes étaient trop grandes pour un `long` Java.

## Réparation des tests générés

Fichier ajouté : **`EndianUtilsChatUniTest7BTest.java`**. Les neuf cas du 7B ont été conservés et réparés.

**Quatre corrections fonctionnelles**, en comptant le nettoyage des imports comme une correction :

1. Importer `EndianUtils.BufferUnderrunException` au bon emplacement.
2. Remplacer le résultat attendu 1 par **0** pour les huit octets commençant par quatre zéros.
3. Remplacer la constante du cas « long négatif » par **4294967295L**.
4. Remplacer celle du cas « long maximal » par **4294967295L** également.

La cause des trois erreurs d’oracle vient du fait que readUIntBE ne lit que quatre octets, même si le flux en contient huit. Les deux dernières corrections permettent également de résoudre les problèmes liés aux constantes impossibles à compiler. Enfin, des adaptations supplémentaires ont été apportées, notamment le renommage de la classe, l’ajout de la licence Apache et des ajustements de mise en forme.

## Tests manuels

Fichier : **`EndianUtilsManualTest.java`**.

| Test | Intention, données et résultat attendu |
|---|---|
| `rejectsThreeByteBigEndianInput` | `12 34 56` : un octet manque pour `readUIntBE`, donc `BufferUnderrunException`. |
| `readsBigEndianLongValues` | `01 02 03 84 85 06 07 08` : attendre `0x0102038485060708L`. Huit zéros doivent donner 0. |
| `readsLittleEndianLongValues` | Mêmes octets, ordre inversé : attendre `0x0807068584030201L`. Vérifier également huit zéros. |
| `rejectsSevenByteBigEndianLongInput` | Sept octets au lieu de huit pour `readLongBE` : attendre `BufferUnderrunException`. |
| `rejectsSevenByteLittleEndianLongInput` | Même vérification pour `readLongLE`. |

Les octets distincts révèlent les erreurs d’ordre ou de calcul; `84` et `85` exercent le bit de poids fort; les zéros vérifient une limite valide. Les flux tronqués sont exactement un octet trop courts.

## Résultats PIT

| Suite cumulative | Détectés | Survivants | Sans couverture | Score | Lignes couvertes |
|---|---:|---:|---:|---:|---:|
| Originaux | 38 | 14 | 154 | 18,45 % | 31/121 |
| + 9 tests générés corrigés | 39 | 13 | 154 | 18,93 % | 32/121 |
| + premier test manuel | 40 | 12 | 154 | 19,42 % | 32/121 |
| + quatre autres tests manuels | **76** | **24** | **106** | **36,89 %** | **54/121** |

Toujours **206 mutants**. Score = détectés / 206. PIT affiche **37 %** au dernier lancement; la couverture des lignes atteint **44,63 %**, affichée **45 %**.
