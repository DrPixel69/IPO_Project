package pkg_characters;


import java.util.HashMap;
import java.util.Stack;

import pkg_rooms.Room;
import pkg_rooms.Door;

import pkg_items.ItemList;
import pkg_items.Item;
import pkg_items.Beamer;
import pkg_items.UsableItem;
import pkg_items.EatableItem;



/**
 * Classe Player : décrit le fonctionnement d'un joueur.
 *
 * @author PITIOT Pierre-Yves
 * @version 21/04/2020
 */
public class Player extends AbstractCharacter
{
    private Stack<Room> aPreviousRooms;
    private int aTurnsLeft;
    private int aMaxWeight;
    private int aCurrentWeight;
    private ItemList aInventory;

    /**
     * Constructeur d'objets de classe Player
     */
    public Player(final String pName)
    {
        // initialisation des variables d'instance
        super(pName);
        
        this.aPreviousRooms = new Stack<Room>();
        this.aInventory = new ItemList();
        this.aMaxWeight = 10;
        this.aCurrentWeight = 0;
        this.aTurnsLeft = 100;
    }

    // COMMANDES

    public String load(final String pItemName)
    {
        if (!this.aInventory.hasItem(pItemName)) return "Cet objet n'est pas dans votre inventaire.";
        Item vItem = this.aInventory.getItem(pItemName);
        if (!(vItem instanceof UsableItem)) return "Cet objet ne peut pas être chargé.";
        return ((UsableItem)vItem).load(this);
    }

    public String use(final String pItemName)
    {
        if (!this.aInventory.hasItem(pItemName)) return "Cet objet n'est pas dans votre inventaire.";
        Item vItem = this.aInventory.getItem(pItemName);
        if (!(vItem instanceof UsableItem)) return "Cet objet ne peut pas être utilisé.";
        return ((UsableItem)vItem).use(this);
    }

    /**
     * Permet de déplacer le joueur vers la pièce précédente.
     */
    public String eat(final String pName)
    {
        if (!this.aInventory.hasItem(pName)){
            return "Cet objet n'est pas dans votre inventaire.";
        }
        if (!(this.aInventory.getItem(pName) instanceof EatableItem)){
            return "Cet objet n'est pas commestible";
        }
        String vStr = ((EatableItem)this.aInventory.getItem(pName)).eat(this);
        this.removeItem(pName);
        return vStr;
    }

    /**
     * Permet de déplacer le joueur vers une nouvelle Room.
     */
    public String goTo(final Room pRoom)
    {
        this.aPreviousRooms.push(this.aCurrentRoom);

        if (!pRoom.isExit(this.aCurrentRoom))
        {
            this.clearPreviousRooms();
        }

        if (pRoom instanceof Door)
        {
            if (((Door)pRoom).isOpen() || this.hasKey(pRoom))
            {
                this.setCurrentRoom(((Door)pRoom).getNextRoom(this.aCurrentRoom));
                ((Door)pRoom).setOpen();

                return "La porte est ouverte. Vous vous êtes déplacé.\n";
            }

            return "La porte est fermée. Vous ne pouvez pas passer.\n";
        }
        this.setCurrentRoom(pRoom);
        return "Vous vous êtes déplacé.\n";
    }

    /**
     * Permet de déplacer le joueur vers la pièce précédente.
     */
    public void goBack()
    {
        this.setCurrentRoom(this.aPreviousRooms.pop());
    }

    /**
     * Retourne la longDesription de la CurrentRoom
     */
    public String lookRoom()
    {
        return aCurrentRoom.getLongDescription();
    }

    /**
     * Retourne la description de l'item demandé.
     */
    public String lookItem(final String pName)
    {
        if (this.aInventory.hasItem(pName)) return this.aInventory.getItem(pName).getDescription();
        if (this.aCurrentRoom.getItems().hasItem(pName)) return this.aCurrentRoom.getItems().getItem(pName).getDescription();
        return "Cet objet n'est pas reconnu.";
    }

    /**
     * Ramasse un objet
     */
    public String take(final String pNom)
    {
        if (this.aCurrentRoom.hasItem(pNom)){
            int vItemWeight = this.aCurrentRoom.getItem(pNom).getWeight();
            if (this.aCurrentWeight + vItemWeight <= this.aMaxWeight)
            {
                this.aInventory.addItem(this.aCurrentRoom.getItem(pNom));
                this.aCurrentWeight += vItemWeight;
                this.aCurrentRoom.removeItem(pNom);

                return pNom + " a été ramassé.";
            }
            else return "Votre inventaire est plein";
        }
        else return "Il n'y a pas cet objet à cet endroit.";
    }

    /**
     * Ramasse un objet
     */
    public String drop(final String pNom)
    {
        if (this.aInventory.hasItem(pNom)){
            this.aCurrentRoom.addItem(this.aInventory.getItem(pNom));
            this.removeItem(pNom);

            return pNom + " a été jeté.";
        }
        else return "Il n'y a pas cet objet dans votre inventaire";
    }

    // GETTERS

    /**
     * Retourne la pièce dans laquelle se trouve le joueur.
     */
    public Room getCurrentRoom()
    {
        return this.aCurrentRoom;
    }

    /**
     * Retourne le poids total de l'inventaire.
     */
    public int getCurrentWeight()
    {
        return this.aCurrentWeight;
    }

    /**
     * Retourne le poids max de l'inventaire.
     */
    public int getMaxWeight()
    {
        return this.aMaxWeight;
    }

    /**
     * Retourne la sortie de la CurrentRoom vers la direction donnée.
     */

    public Room getCurrentRoomExit(final String pDirection)
    {
        return this.aCurrentRoom.getExit(pDirection);
    }

    /**
     * Retourne l'inventaire du joueur
     */
    public ItemList getInventory()
    {
        return this.aInventory;
    }
    
    /**
     * Retire un objet de l'inventaire du joueur
     */
    public void removeItemFromInventory(final String pName)
    {
        this.aInventory.removeItem(pName);
    }
    // SETTERS

    

    // OTHER

    /**
     * Teste si il y a une Room précédente.
     */
    public boolean previousRoomIsEmpty()
    {
        return this.aPreviousRooms.empty();
    }

    /**
     * Vide le PreviousRooms
     */
    public void clearPreviousRooms()
    {
        this.aPreviousRooms.clear();
    }

    /**
     * Ajoute une nouvelle Room à aPreviousRoom.
     */
    private void pushRoom(final Room pRoom)
    {
        this.aPreviousRooms.push(pRoom);
    }

    /**
     * Augmente le poids max de l'inventaire du joueur
     */
    public void increaseMaxWeight(final int pWeight)
    {
        this.aMaxWeight += pWeight;
    }

    /**
     * Diminue le nombre de tours restants.
     */
    public void decreaseTurnsLeft()
    {
        this.aTurnsLeft -= 1;
    }

    public boolean TestTurnsLeft()
    {
        if (aTurnsLeft <= 0){  // On teste si il reste des tours
            return true;
        }
        else return false;
    }


    /**
     * Retire un item de l'inventaire
     */
    public void removeItem(final String pName)
    {
        this.aCurrentWeight -= this.aInventory.getItem(pName).getWeight();
        this.aInventory.removeItem(pName);
    }

    /**
     * Teste si le joueur possède la clé de la porte entrée.
     */
    public boolean hasKey(final Room pRoom)
    {
        if (((Door)pRoom).getKey() == null) return false;
        return this.hasItem((((Door)pRoom).getKey().getNom()));
    }
    
    /**
     * Teste si le joueur possède l'item demandé
     */
    public boolean hasItem(final String pName)
    {
        return this.aInventory.hasItem(pName);
    }
}
