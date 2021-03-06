package pkg_rooms;

import java.util.HashMap; 
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Random;

import pkg_items.ItemList;
import pkg_items.Item;
import pkg_characters.Character;

/**
 * Classe décrivant les pièces du jeu.
 * 
 * @author PITIOT Pierre-Yves
 * @version 09/05/2020
 */
public class Room
{
    // Attributs
    
    private String aDescription;
    private HashMap<String, Room> aExits;
    private String aImageName;
    private ItemList aItems;
    private HashMap<String, Character> aPNJ;
    
    // Constructeurs
    
    /**
     * Constructeur de Room
     * @param pDescription : Description du lieu.
     */
    public Room(final String pDescription, final String pImage){
        this.aDescription = pDescription;
        this.aExits = new HashMap<String,Room>();
        this.aImageName = pImage;
        this.aItems = new ItemList();
        this.aPNJ = new HashMap<String,Character>();
    }//Room()
    
    // Getters 
    
    /**
     * Retourne la description de la pièce
     * @return la description de la pièce
     * 
     */
    public String getDescription()
    {
        return this.aDescription;
    }//getDescription()
    
    /**
     * Retourne les objets présents dans la pièce
     * @return les objets présents dans la pièce
     * 
     */
    public ItemList getItems()
    {
        return this.aItems;
    }//getDescription()
    
    /**
     * Retourne la sortie en fonction de la direction entrée. 
     * @return la pièce se trouvant dans la direction donnée.
     */
    public Room getExit(String pDir)
    {
        return aExits.get(pDir);
    }//getExit()
    
    /**
     * Retourne une sortie aleatoire. 
     * @return Retourne une sortie aleatoire. 
     */
    public Room getAleaExit()
    {
        ArrayList<Room> vExits = new ArrayList<Room>(this.aExits.values());
        return vExits.get((new Random()).nextInt(vExits.size()));
    }//getExit()
    
    /**
     * Retourne les directions où se trouvent une sortie.
     * @return les directions où se trouvent une sortie.
     */
    public String getExitString()
    {
        StringBuilder vSB = new StringBuilder("Sorties :");
        Set<String> keys = aExits.keySet();
        for (String exit : keys)
        {
            vSB.append(" ").append(exit);
        }
        
        return vSB.toString();
    }//getExitString()
    
    /**
     * Retourne une longe description de cette pièce, de la forme :
     *  Vous êtes dans la cuisine.
     *  Exits : north west
     * @return Une description de la pièce et ses sorties.
     */
    public String getLongDescription()
    {
        return "Vous êtes " + this.aDescription + ".\n"+ this.getItemDescription() + "\n" +getPNJDescription()+ this.getExitString();
    }// getLongDescription()
    
    /**
     * Retourne la liste des Items présents dans cette pièce
     */
    public String getItemDescription()
    {
        if (!this.aItems.isEmpty()) {
            StringBuilder vSB = new StringBuilder("A cet endroit, il y a les objets suivants : ");
            Collection<Item> items = this.aItems.values();
            for (Item item : items)
            {
                vSB.append(" ").append(item);
            }
            return vSB.toString();
        }
        else return "Il n'y a pas d'item à cet endroit.";
    }
    
    /**
     * Retourne la liste des Items présents dans cette pièce
     */
    public String getPNJDescription()
    {
        if (!this.aPNJ.isEmpty()) {
            StringBuilder vSB = new StringBuilder("A cet endroit, il y a les personnes suivantes : ");
            Collection<Character> vPNJs = this.aPNJ.values();
            for (Character vPNJ : vPNJs)
            {
                vSB.append(" ").append(vPNJ.getName());
            }
            vSB.append("\n");
            return vSB.toString();
        }
        else return "";
    }
    
    /**
     * Return a string describing the room's image name
     */
    public String getImageName()
    {
         return this.aImageName;
    }
    
    /**
     * Return a string describing the room's image name
     */
    public Character getPNJ(final String pName)
    {
         return this.aPNJ.get(pName);
    }
    
    /**
     * Renvoie l'Item demandé.
     */
    public Item getItem(final String pNom)
    {
        return this.aItems.getItem(pNom);
    }
    
    // Setters
    
    /**
     * Modifie les sorties
     */
    public void setExit(final String pDirection,final Room pNeighbor)
    {
        this.aExits.put(pDirection, pNeighbor);
    }
    
    // Other
    
    /**
     * Ajoute un item à la pièce.
     */
    public void addItem(final Item pItem)
    {
        this.aItems.addItem(pItem);
    }
    
    /**
     * Ajoute un PNJ à la pièce.
     */
    public void addPNJ(final Character pPNJ)
    {
        this.aPNJ.put(pPNJ.getName(), pPNJ);
        pPNJ.setCurrentRoom(this);
    }
    
    /**
     * Teste si la pièce contient l'item demandé.
     */
    public boolean hasItem(final String pNom)
    {
        return this.aItems.hasItem(pNom);
    }
    
    /**
     * Teste si la pièce contient le PNJ demandé.
     */
    public boolean hasPNJ(final String pNom)
    {
        return this.aPNJ.containsKey(pNom);
    }
    
    /**
     * Retire un PNJ.
     * @param Le nom du PNJ.
     */
    public void removePNJ(final String pName)
    {
        this.aPNJ.remove(pName);
    }
    
    /**
     * Retire un item à la liste.
     */
    public void removeItem(final String pNom)
    {
        this.aItems.removeItem(pNom);
    }
    
    /**
     * Teste si la Room entrée est une sortie de cette Room
     */
    public boolean isExit(final Room pRoom)
    {
        return this.aExits.containsValue(pRoom);
    }
    
    /**
     * Teste si la Current Room entrée est une TransporterRoom
     */
    public boolean isTransporterRoom()
    {
        return (this instanceof TransporterRoom);
    }
    
} // Room
