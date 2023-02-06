# TP5 : Scalix

*L'objectif de ce TP est de récupérer, manipuler et stocker des données de l'API de TMDB*  
*Nous essayerons de répondre à la question suivante : "Quels sont les réalisateurs qui ont dirigé ensemble Christian Bale et Michael Caine ?"*

**Auteur** : Pierre Lafon et Jules Carpio   
**Professeur** : Jacques Noyé  
**Destination** : Mont Oros (Métaphore du surpassement de soi)  
**Nom du cours** : Programmation multi-paradigme avec Scala  
**Date mise à jour** : 04/02/2023  

Nous avons développer des fichiers de test si vous voulez voir nos resultats.  
N'hésitez pas à supprimer les fichiers json pour voir la création de fichier.

### Indication sur le code Scalix :

Le fichier Scalix contient l'ensemble des méthodes du TP.  
**On discerne 4 parties dans ce code (ordre d'apparition)** :
- Methodes effectuant des requêtes à l'API TMDB
- Méthodes utilisant la cache secondaire (stockage dans un json)
- Méthodes utilisant la cache à deux niveaux (Memoïzation et fichier)
- Méthodes communes, notamment ```getContext```

Point à ajouter, nous avons fais attention à bien gérer tous les cas d'exception sur chacuns de nos appels d'API :
```scala
  def extractActorId(json: JValue, name: String, surname: String): Option[Int] = {
    Option(json \ "results") match {
      case Some(results) =>
        results.find { result =>
          (result \ "name").extractOpt[String].contains(s"$name $surname")
        } match {
          case Some(result) =>
            (result \ "id").extractOpt[Int]
          case _ =>
            throw new Exception("Error: actor without id")
        }
      case _ =>
        throw new Exception("Error: Actor list is empty")
    }
  }
```

### Indication sur le code Scalix2 :

L'architecture du projet est bien plus lisible. Nous discrenons plus facilement les différents types de services.
Cependant les nombres d'import à augmenté.

### Indication Memoïzation :

Nous sommes parties sur la définition que vous aviez donnée. Par conséquent nous avons :
```scala
  class Memoization [S, T] (private val f : S => T) {
    var cache = Map[S, T]()
    def memoize(s : S) :  T = {
        cache.get(s) match
          case None => val t = f(s); cache += (s, t); System.out.println("---- Cache created ----"); t;
          case Some(t) => System.out.println("---- Cache used ----"); t
    }
```
Ce qui nous donnes les val suivantes :
```scala
  val findActorIdCache = Memoization[FullName, Option[Int]](findActorId)
  val findActorMoviesDoubleCache = Memoization[Int, Set[(Int, String)]](findActorMoviesFile)
  val findMovieDirectorDoubleCache = Memoization[Int, Option[(Int, String)]](findMovieDirectorFile)
  val collaborationCache = Memoization[(FullName, FullName), Set[(String, String)]](collaboration)
```
Que nous appelons de cette manière :
```scala 
findActorIdCache.memoize(actor)
```

### Indication Quizz :

Partie non développée, il aurait fallut charger plusieurs collaboration et uriliser des listes en compréhension.   
