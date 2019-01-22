# Hackenbush
Le jeu Hackenbush se joue à deux joueurs, Bleu et Rouge, sur un dessin composé d’un certains nombre de sommets auquels sont attachées des arêtes coloriées en bleu, rouge ou vert. Une partie de ces sommets sont disposés sur une ligne noire symbolisant le sol. Les sommets attachés aux extrémités d’une même arête sont dit adjacents. Les deux extrémités d’une arête peuvent être attachées au même sommet, ie. un sommet peut être adjacent à lui-même. Un chemin est une suite de sommets dans laquelle tout élément sauf le dernier est adjacent à son successeur. Le premier élément d’une telle suite est dit connecté au dernier. Tout sommet connecté à un sommet situé sur la ligne noire du dessin est dit connecté au sol. Noter que, nécessairement, deux sommets adjacents sont ou bien tous deux connectés au sol, ou bien tous deux non connectés au sol.

# Déroulement du jeu
Les joueurs jouent à tour de rôle, en commançant par Rouge. A son tour, chaque joueur choisit une arête : soit une arête de sa couleur, soit une arête verte. Le dessin est alors modifié de la manière suivante :

L’arête choisie est supprimée du dessin.
Les sommets qui ne sont plus connectés au sol sont supprimés du dessin, ainsi que toutes les arêtes attachées à ces sommets. Noter qu’au terme de cette transformation, les sommets restants sont tous connectés au sol, et les arêtes restantes ont toutes leurs deux extrémités attachées.

# Fin du jeu
Le jeu se termine au moment où le joueur dont c’est le tour n’a plus de coup jouable. Dans le jeu normal, son adversaire est alors déclaré gagnant. Dans le jeu de type misère en revanche, son adversaire est déclaré perdant. Le choix du type de jeu doit évidemment être convenu en début de partie.

Le jeu de Nim est un cas spécial de Hackenbush. Dans le jeu de Nim, le dessin est formé de suites filiforme d’arêtes (ou tiges), tout vertes, Il existe pour le jeu de Nim un algorithme permettant de déterminer le joueur gagnant et, à chaque tour de ce même joueur, quel est le coup gagnant.

# Indices pour jouer
Classe principale :
	Main.java

Historique pages :
    * crlt_f -> permet d'aller vers l'arrière
    * crlt_b -> permet d'aller vers l'avant

Rotation :
   * Left and Right

Scale :
  * Up and Down
  
  ![](https://github.com/zzyviolette/hackenbush/raw/master/Screenshots/page1.png)
  
  ![](https://github.com/zzyviolette/hackenbush/raw/master/Screenshots/page2.png)
  
  ![](https://github.com/zzyviolette/hackenbush/raw/master/Screenshots/page3.png)
  
 
