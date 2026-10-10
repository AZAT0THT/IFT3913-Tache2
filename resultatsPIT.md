commande pour l'analyse : 

.\mvnw.cmd -pl :tika-core test-compile org.pitest:pitest-maven:mutationCoverage "-DtargetClasses=org.apache.tika.io.EndianUtils"


Résultats avec les tests originaux :
- 38 mutants détectés, 14 survivants et 154 sans couverture.
- Score de mutation : 18,45 %.
- Lignes couvertes : 31 sur 121.

  Après ajout des 9 tests générés corrigés :
- 39 mutants détectés, 13 survivants et 154 sans couverture.
- Score de mutation : 18,93 %.
- Lignes couvertes : 32 sur 121.

  Après ajout du premier test manuel :
- 40 mutants détectés, 12 survivants et 154 sans couverture.
- Score de mutation : 19,42 %.
- Lignes couvertes : 32 sur 121.

  Après ajout des quatre autres tests manuels :
- 76 mutants détectés, 24 survivants et 106 sans couverture.
- Score de mutation : 36,89 %.
- Lignes couvertes : 54 sur 121.
