import java.util.Scanner;
import java.util.HashMap;
import java.io.File;


/**
 * Classe principale du jeu. Game permet de lancer une partie.
 * 
 * @author PITIOT Pierre-Yves
 * @version 13/02/2020
 */
public class GameEngine
{
    // Attributs
    private Parser aParser;
    private Player aPlayer;
    private UserInterface aGui;
    private HashMap<String, Room> aRooms;

    // Constructeurs
    /**
     * Constructeur pour les objets de classe GameEngine.
     */
    public GameEngine()
    {
        this.aPlayer = new Player("Elsa Edington");
        this.aRooms = new HashMap<String,Room>();
        this.aParser = new Parser();
        this.createRooms();
    }
    
    

    // CREATION DES OBJETS NECESSAIRES AU FONCTIENNEMENT DU JEU
    
    /**
     * Initialise le GUI dans GameEngine.
     */
    public void setGUI( final UserInterface pUserInterface )
    {
        this.aGui = pUserInterface;
        this.printWelcome();
    }
    
    /**
     * Instantie les salles et le "réseau" du jeu
     */
    private void createRooms()
    {
        // Déclaration des lieux
        Room vDesert = new Room("Desert","Images/desert1.png");
        aRooms.put("desert_1", vDesert);
        Room vShipSouth = new Room("Ship - South","Images/crashedship.png");
        aRooms.put("ShipSouth", vShipSouth);
        Room vShipNorth = new Room("Ship - North","Images/crashedship.png");
        aRooms.put("ShipNorth", vShipNorth);
        Room vShipEast = new Room("Ship - East","Images/crashedship.png");
        aRooms.put("ShipEast", vShipEast);
        Room vShipWest = new Room("Ship - West","Images/crashedship.png");
        aRooms.put("ShipWest", vShipWest);
        Room vShipInside = new Room("inside the ship.","Images/shipinside.png");
        aRooms.put("ShipInside", vShipInside);

        // Positionnement des sorties
        vDesert.setExit("north",vShipSouth);

        vShipSouth.setExit("south", vDesert);
        vShipSouth.setExit("east", vShipEast); 
        vShipSouth.setExit("west", vShipWest);

        vShipNorth.setExit("east", vShipEast);
        vShipNorth.setExit("west", vShipWest);
        vShipNorth.setExit("up", vShipInside);

        vShipEast.setExit("north", vShipNorth);
        vShipEast.setExit("south", vShipSouth);

        vShipWest.setExit("north", vShipNorth);
        vShipWest.setExit("south", vShipSouth);

        vShipInside.setExit("down", vShipNorth);

        // Ajout des items dans les pièces
        vShipSouth.addItem(new Item("pomme", "une pomme",5));
        vShipSouth.addItem(new Item("conserves", "une boite de conserve",5));
        vDesert.addItem(new Item("débrits", "des débrits métalliques",5));

        // Initialisation du lieu courant
        this.aPlayer.setCurrentRoom(vDesert);
    }
    
    // METHODES D'AFFICHAGE
    
    /**
     * Affiche les informations sur les sorties de la Room courante.
     */
    private void printLocationInfo()
    {
        this.aGui.println(this.aPlayer.look());  
        if ( this.aPlayer.getCurrentRoom().getImageName() != null )
            this.aGui.showImage( this.aPlayer.getCurrentRoom().getImageName() );
    }//printLocationInfo()

    /**
     * Affiche le message de bienvenue au joueur.
     */
    private void printWelcome()
    {
        this.aGui.println("Welcome to the game !");
        this.aGui.println("After a space battle, you crashed on a desertic planet.");
        this.aGui.println("Type 'help' if you need help.");

        // Affichage des sorties
        this.printLocationInfo();
    }//printWelcome()

    /**
     * Affiche le message d'aide
     */
    private void printHelp()
    {
        this.aGui.println("You are lost. You are alone.");
        this.aGui.println("You wander around in the desert.\n");
        this.aGui.println("Your command words are:");
        this.aGui.println(aParser.getCommandString());
    }//printHelp()

    // COMMANDES
    
    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    public void interpretCommand( final String pCommandLine ) 
    {
        this.aGui.println( "> " + pCommandLine );
        Command vCommand = this.aParser.getCommand( pCommandLine );

        if ( vCommand.isUnknown() ) {
            this.aGui.println( "I don't know what you mean..." );
            return;
        }

        String vCommandWord = vCommand.getCommandWord();
        if ( vCommandWord.equals( "help" ) )
            this.printHelp();
        else if ( vCommandWord.equals( "go" ) )
            this.goRoom(vCommand);
        else if ( vCommandWord.equals( "eat" ) )
            this.eat();
        else if ( vCommandWord.equals( "look" ) )
            this.look();
        else if ( vCommandWord.equals( "quit" ) ) 
            this.quit(vCommand);
        else if ( vCommandWord.equals( "back" ) ) 
            this.back(vCommand);
        else if (vCommandWord.equals("test"))
            this.test(vCommand);
        else if (vCommandWord.equals("take"))
            this.take(vCommand);
        else if (vCommandWord.equals("drop"))
            this.drop(vCommand);
    }

    private void take(final Command pCommand)
    {
        this.aGui.println(this.aPlayer.take(pCommand.getSecondWord()));
    }
    
    private void drop(final Command pCommand)
    {
        this.aGui.println(this.aPlayer.drop(pCommand.getSecondWord()));
    }
    
    /**
     * Gère le changement de lieu
     * 
     * @params La commande entrée par le joueur
     */
    private void goRoom(final Command pCommand)
    {
        // Si un seul mot, on retourne "go where ?"
        if ( ! pCommand.hasSecondWord() ) {
            // if there is no second word, we don't know where to go...
            this.aGui.println( "Go where?" );
            return;
        }

        // On cherche la prochaine pièce
        String vDirection = pCommand.getSecondWord();

        Room vNextRoom = this.aPlayer.getCurrentRoomExit(vDirection);

        // On effectue ou pas le changement de lieu
        if ( vNextRoom == null )
            this.aGui.println( "There is no door!" );
        else {
            this.goTo(vNextRoom);
        }

    }//goRoom()
    
    /**
     * Effectue les étapes necessaires pour changer de pièces.
     * 
     * @params la nouvelle Room.
     */
    private void goTo(final Room pRoom)
    {
        this.aPlayer.goTo(pRoom);
        this.printLocationInfo();
    }

    /**
     * Commande "look" : Affiche la description de la pièce.
     */
    private void look()
    {
        this.aGui.println(this.aPlayer.look());
    }//look()

    /**
     * Commande "eat" : Permet de manger.
     */
    private void eat()
    {
        this.aGui.println("You have eaten now, and you are not hungry anymore");
    }//eat()

    /**
     * Commande "back" : Permet de revenir à la pièce précédente.
     */
    private void back(final Command pCommand)
    {
        if (pCommand.hasSecondWord())
            this.aGui.println("'back' is not supposed to have a second word.");
        else
        { 
            if (this.aPlayer.previousRoomIsEmpty())
                this.aGui.println("You can't go back now.");
            else
            {
                this.aPlayer.goBack();
                this.printLocationInfo();
            }
        }
    }//back()

    /**
     * Commande "test" : permet de lire un fichier pour executer des commandes.
     */
    private void test(final Command pCommand)
    {
        String vFileName;
        if (pCommand.hasSecondWord()) 
            vFileName = pCommand.getSecondWord();
        else 
            vFileName = "test";

        try
        {
            Scanner vSc = new Scanner(new File(vFileName+".txt"));
            while (vSc.hasNext()){
                this.interpretCommand(vSc.nextLine());
            }
        }
        catch(final java.io.FileNotFoundException pE)
        {
            this.aGui.println("Le fichier demandé n'a pas été trouvé.");
        }
    }

    /**
     * Détecte si le joueur veut quitter le jeu
     */
    private boolean quit(final Command pCommand)
    {
        if (pCommand.hasSecondWord()) {
            this.aGui.println("Quit what ?");
            return false;
        }
        this.endGame();
        return true;
    }//quit()

    // OTHER
    
    /**
     * Active la fin de jeu.
     */
    private void endGame()
    {
        this.aGui.println( "Thank you for playing.  Good bye." );
        this.aGui.enable( false );
    }
} // Game
