# CyPath
Répertoire du projet logiciel CyPath

Lien Trello (utilisé uniquement au début du projet pour dégrossir les étapes du projet) : https://trello.com/b/Rxa3Cuod/cypath\

# Pour compiler le projet :
- Assurez vous d'avoir installé JavaFX (Version 17.0.7 ou plus récent).
- Ouvrir IntelliJ.
- Selectionner "Open from VCS"
- Coller le lien de ce répertoire et sélectionner le dossier dans lequel vous allez cloner le projet
- Aller dans File -> Project Structure... -> Project et sélectionnez un SDK.
- Aller dans File -> Project Structure... -> Libraries et importez le dossier lib de JavaFX.

- Si vous n'arrivez toujours pas à complier :
  - Aller dans Edit Configurations à côté du bouton run.
  - Selectionner modify options -> Add VM Argument.
  - Ecrire : --module-path "path\lib" --add-modules javafx.controls,javafx.fxml
  - Remplacer ce qu'il y a entre guillements par l'endroit où vous avez installé JavaFX.


# Commandes :
- Choisir le nombre de joueurs avec le slider puis cliquer sur valider.
- Déplacer un pion en faisant un clic gauche sur un des cercles transparents, ce sont les mouvements possibles. Si vous cliquez sur une case sur laquelle il n'est pas possible de se déplacer, une animation apparaît.
- Placer une barrière en cliquant sur le bouton "Select Barrier", puis, cliquer gauche sur une des intersections du plateau (carrés gris). Une barrière fantôme va s'afficher à l'intersection que la souris survole. Pour changer l'orientation de la barrière, faire un clic droit avant de la placer. Si vous cliquez sur une intersection sur laquelle il n'est pas possible de placer une barrière, une animation apparaît. Il faut bien cliquer sur les carrés gris pour être sûr de placer la barrière au bon endroit .
- Quand la quantité des barrières restantes atteint 0, il n'est plus possible de placer une barrière. Un label placé en haut à gauche indique le nombre de barrières restantes et s'actualise tout seul.
- Pour sauvegarder la partie cliquer sur "save game".
- Pour charger une partie sauvegardée cliquer sur "load game". A noter qu'il n'est plus possible de "load game" après avoir sauvegardé une partie. Pour reprendre la partie sauvegardée, il faut fermer et relancer l'application.
- La sauvegarde est unique et est écrasée à chaque fois, par conséquent, il n'est pas nécessaire de demander à l'utilisateur quel fichier choisir au chargement.
- Lorsque la partie est gagnée, la case du gagnant apparaît en vert et un label indique le gagnant et la fin de la partie.

# Comment générer la javadoc :
- Ouvrir le projet sur intelliJ.
- Aller dans Tools ->Generate JavaDoc...
- Selectioner Output directory et saisir un dossier dans lequel sauvegarder.
- Cliquer sur OK
- Dans le dossier séléctionné pour sauvegarder la javadoc:
    - Ouvrir le fichier index.html
