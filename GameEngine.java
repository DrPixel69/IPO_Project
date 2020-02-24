import java.util.HashMap;





/**
 * Classe principale du jeu. Game permet de lancer une partie.
 * 
 * @author PITIOT Pierre-Yves
 * @version 13/02/2020
 */
public class GameEngine
{
    // Attributs
    private Room aCurrentRoom;
    private Parser aParser;
    private UserInterface aGui;
    private HashMap<String, Room> aRooms;
    
    // Constructeurs
    /**
     * Constructeur pour les objets de classe GameEngine.
     */
    public GameEngine()
    {
        aRooms = new HashMap<String,Room>();
        this.aParser = new Parser();
        this.createRooms();
        
    }
    
    public void setGUI( final UserInterface pUserInterface )
    {
        this.aGui = pUserInterface;
        this.printWelcome();
    }

    // Methodes
    /**
     * Instantie les salles et le "réseau" du jeu
     */
    private void createRooms()
    {
        // Déclaration des lieux
        Room vDesert = new Room("Desert","castle.gif");
        aRooms.put("desert_1", vDesert);
        Room vShipSouth = new Room("Ship - South","castle.gif");
        aRooms.put("ShipSouth", vShipSouth);
        Room vShipNorth = new Room("Ship - North","castle.gif");
        aRooms.put("ShipNorth", vShipNorth);
        Room vShipEast = new Room("Ship - East","castle.gif");
        aRooms.put("ShipEast", vShipEast);
        Room vShipWest = new Room("Ship - West","castle.gif");
        aRooms.put("ShipWest", vShipWest);
        Room vShipInside = new Room("inside the ship.","castle.gif");
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

        // Initialisation du lieu courant
        this.aCurrentRoom = vDesert;
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

        Room vNextRoom = this.aCurrentRoom.getExit(vDirection);
        /////{System.out.println("Unknown direction !"); return;}

        // On effectue ou pas le changement de lieu
        if ( vNextRoom == null )
            this.aGui.println( "There is no door!" );
        else {
            this.aCurrentRoom = vNextRoom;
            this.printLocationInfo();;
            if ( this.aCurrentRoom.getImageName() != null )
                this.aGui.showImage( this.aCurrentRoom.getImageName() );
        }
        
        
    }//goRoom()
    
    /**
     * Affiche les informations sur les sorties de la Room courante.
     */
    private void printLocationInfo()
    {
        this.aGui.println(this.aCurrentRoom.getLongDescription());      
    }//printLocationInfo()
    
    /**
     * Affiche le message de bienvenue au joueur.
     */
    private void printWelcome()
    {
        this.aGui.println("Welcome to the game !");
        this.aGui.println("After a space battle, you crashed on a desertic planet.");
        this.aGui.println("Type 'help' if you need help.");

        if ( this.aCurrentRoom.getImageName() != null )
            this.aGui.showImage( this.aCurrentRoom.getImageName() );

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

    /**
     * Détecte si le joueur veut quitter le jeu
     */
    private boolean quit(final Command pCommand)
    {
        if (pCommand.hasSecondWord()) {
            this.aGui.println("Quit what ?");
            return false;
        }
        return true;
    }//quit()

    /**
     * Appelle la bonne méthode en fonction de la commande passée en paramètre.
     */
    private boolean processCommand(final Command pCommand)
    {
        if (pCommand.isUnknown()){
            this.aGui.println("I don't know what you mean...");
            return false;
        }
        
        if (pCommand.getCommandWord().equals("quit")) return this.quit(pCommand);
        else if (pCommand.getCommandWord().equals("go")) this.goRoom(pCommand);
        else if (pCommand.getCommandWord().equals("help")) this.printHelp();
        else if (pCommand.getCommandWord().equals("look")) this.look();
        else if (pCommand.getCommandWord().equals("eat")) this.eat();
        
        return false;
    }//processCommand()
    
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
            this.goRoom( vCommand );
        else if ( vCommandWord.equals( "eat" ) )
            this.eat();
        else if ( vCommandWord.equals( "look" ) )
            this.look();
        else if ( vCommandWord.equals( "quit" ) ) {
            if ( vCommand.hasSecondWord() )
                this.aGui.println( "Quit what?" );
            else
                this.endGame();
        }
    }
    
    private void look()
    {
        this.aGui.println(this.aCurrentRoom.getLongDescription());
    }//look()
    
    private void eat()
    {
        this.aGui.println("You have eaten now, and you are not hungry anymore");
    }//eat()
    
    private void endGame()
    {
        this.aGui.println( "Thank you for playing.  Good bye." );
        this.aGui.enable( false );
    }
} // Game