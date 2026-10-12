## Choix de la classe DateUtilis

La classe `org.apache.tika.utils.DateUtils`, dans `tika-core`,
permet de formater des dates et d'interpréter plusieurs formats.
Elle possède déjà des tests indirects, notamment `testGetSetDate()`
dans `tika-core/src/test/java/org/apache/tika/metadata/TestMetadata.java`.
Nous la retenons parce que sa couverture est incomplète et que
cinq mutants survivent aux tests originaux. Une forte couverture
ne garantit donc pas que les assertions détectent toutes les erreurs.

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
