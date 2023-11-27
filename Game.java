package Zuul;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *  This class is the main class of the "World of Zuul" application. 
 *  "World of Zuul" is a very simple, text based adventure game.  Users 
 *  can walk around some scenery. That's all. It should really be extended 
 *  to make it more interesting!
 * 
 *  To play this game, create an instance of this class and call the "play"
 *  method.
 * 
 *  This main class creates and initialises all the others: it creates all
 *  rooms, creates the parser and starts the game.  It also evaluates and
 *  executes the commands that the parser returns.
 * 
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0 (February 2002)
 * 
 * Last Edited: 12/9/22 by Nikaansh S.
 */

class Game 
{
    Room reception, dungeon, deepdark, kitchen, cellar, courtyard, bedroom, potteryroom, arena, restarea, pedestalroom, ritualroom, throneroom, hallway, secretexit;

    private Parser parser;
    private Room currentRoom;
    ArrayList<Item> inventory = new ArrayList<Item>();
    
    /**
     * Create the game and initialise its internal map.
     */
    public Game() 
    {
        createRooms();
        parser = new Parser();
    }

    public static void main(String[] args) {
        Game newZuul = new Game();
        newZuul.play();
    }

    /**
     * Create all the rooms and link their exits together.
     */
    private void createRooms()
    {      
        // create the rooms
        reception = new Room("an omnimous waiting room for guests with a big chained door on the wall");
        dungeon = new Room("a dark desolate place where people who disobeyed the lord were kept. it is littered with bones and the cells are rusted. you spot a red glint in one of the corners");
        deepdark = new Room("A deeper and darker part of the dungeon. It is where the most dangerous criminals were kept and where the dark monsters hide");
        kitchen = new Room("a place where pigs were slain and dishes were made");
        cellar = new Room("This is where cooking supplies and materials were kept. a strong scent of alcohol is in the air");
        courtyard = new Room("a big and beautiful garden which has slows but surly grown wild without the care of the gardeners");
        bedroom = new Room("a luxurios bedroom where the lord and lady of the mansion must have slept. there is a small bump in the bed");
        potteryroom = new Room("some of the most talented craftsmen worked here making wounderful creations that benefitted all");
        arena = new Room("a place where the bravest of warriors fought for a chance of the lord's favor. the sides of the arena are littered with bones, armor, and weapons");
        restarea = new Room("This is where the gladiators rested after and before fighting");
        pedestalroom = new Room("a empty room with nothing but a pedestal which holds a gaping mouth seemingly looking for something to quench its thrist");
        ritualroom = new Room("the the mouth's thirst quenched a door opened on the side. decending down the stairs you find a pentagrams pulsing slowly a dark red color. in the middle is a strange red gem");
        throneroom = new Room("a luxurious room with a grand throne at the end. there seems to be a hole near the throne");
        hallway = new Room("a big, long, and desolate hallway. the stone is wearing down and the statues on the side have all but crumbled. there is one statue that is slighly different with two holes where its eyes should have been");
        secretexit = new Room("finally, the way out and a new insight on your great-grandfathers long lost treasure");

        
        // initialise room exits
        reception.setExit("south", dungeon);
        reception.setExit("west", kitchen);

        dungeon.setExit("north", reception);
        dungeon.setExit("south", deepdark);
        dungeon.setItem(new Item("RedGem"));

        deepdark.setExit("north", dungeon);

        kitchen.setExit("east", reception);
        kitchen.setExit("west", courtyard);
        kitchen.setExit("north", cellar);

        cellar.setExit("south", kitchen);

        courtyard.setExit("north", bedroom);
        courtyard.setExit("east", kitchen);
        courtyard.setExit("south", arena);
        courtyard.setItem(new Item("GreenLiquid"));

        bedroom.setExit("south", courtyard);
        bedroom.setExit("west", potteryroom);
        bedroom.setItem(new Item("ThornyKey"));

        potteryroom.setExit("east", bedroom);
        potteryroom.setItem(new Item("GlassBottle"));

        arena.setExit("north", courtyard);
        arena.setExit("south", restarea);
        arena.setExit("west", pedestalroom);
        arena.setItem(new Item("RustySword"));

        restarea.setExit("north", arena);

        pedestalroom.setExit("east", arena);
        pedestalroom.setExit("south", ritualroom);
        pedestalroom.setExit("west", throneroom);

        ritualroom.setExit("north", pedestalroom);
        ritualroom.setItem(new Item("RedGem"));

        throneroom.setExit("east", pedestalroom);
        throneroom.setExit("west", hallway);

        hallway.setExit("east", throneroom);
        hallway.setExit("north", secretexit);
        
        currentRoom = reception;  // start game at the reception

        inventory.add(new Item("Great-grandpa's Letter"));
    }

    /**
     *  Main play routine.  Loops until end of play.
     */
    public void play() 
    {            
        printWelcome();

        // Enter the main command loop.  Here we repeatedly read commands and
        // execute them until the game is over.
                
        boolean finished = false;
        while (! finished) {
            Command command = parser.getCommand();
            finished = processCommand(command);
        }
        System.out.println("Thank you for playing.  Good bye.");
    }

    /**
     * Print out the opening message for the player.
     */
    private void printWelcome()
    {
        System.out.println();
        System.out.println("Welcome to Adventure!");
        System.out.println("Adventure is a new, incredibly boring adventure game.");
        System.out.println("Type 'help' if you need help.");
        System.out.println();
        System.out.println(currentRoom.getLongDescription());
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    private boolean processCommand(Command command) 
    {
        boolean wantToQuit = false;

        if(command.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help")) {
            printHelp();
        }
        else if (commandWord.equals("go")) {
            wantToQuit = goRoom(command);
        }
        else if (commandWord.equals("quit")) {
            wantToQuit = quit(command);
        }
        else if (commandWord.equals("inventory")) {
            printInventory();
        }
        else if (commandWord.equals("get")) {
            getItem(command);
        }
        else if (commandWord.equals("drop")) {
            dropItem(command);
        }
        return wantToQuit;
    }

    //drops item from inventory
    private void dropItem(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to drop...
            System.out.println("Drop what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to leave current room.
        Item newItem = null;
        int index = 0;
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getDescription().equals(item)) {
                newItem = inventory.get(i);
                index = i;
            }
        }

        if (newItem == null)
            System.out.println("That item is not in you inventory");
        else {
            inventory.remove(index); 
            currentRoom.setItem(new Item(item));
            System.out.println("Dropped: " + item);
        }
    }

    //puts item in inventory
    private void getItem(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know what to pickup...
            System.out.println("Get what?");
            return;
        }

        String item = command.getSecondWord();

        // Try to leave current room.
        Item newItem = currentRoom.getItem(item);

        if (newItem == null)
            System.out.println("That item is not here");
        else {
            inventory.add(newItem); 
            currentRoom.removeItem(item);
            System.out.println("Picked up: " + item);
        }
    }

    //prints inventory
    private void printInventory() {
        String output = "";
        for (int i = 0; i < inventory.size(); i++) {
            output += inventory.get(i).getDescription() + " ";
        }
        System.out.println("You are carrying: ");
        System.out.println(output);
    }

    // implementations of user commands:

    /**
     * Print out some help information.
     * Here we print some stupid, cryptic message and a list of the 
     * command words.
     */
    private void printHelp() 
    {
        System.out.println("You are lost. You are alone. You wander");
        System.out.println("around at the university.");
        System.out.println();
        System.out.println("Your command words are:");
        parser.showCommands();
    }

    /** 
     * Try to go to one direction. If there is an exit, enter the new
     * room, otherwise print an error message.
     */
    private boolean goRoom(Command command) 
    {
        if(!command.hasSecondWord()) {
            // if there is no second word, we don't know where to go...
            System.out.println("Go where?");
            return false;
        }

        String direction = command.getSecondWord();

        // Try to leave current room.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null)
            System.out.println("There is no door!");
        else {
            currentRoom = nextRoom;
            System.out.println(currentRoom.getLongDescription());
            if (currentRoom == secretexit) {
                return true;
            }

        }
        return false;
    }

    /** 
     * "Quit" was entered. Check the rest of the command to see
     * whether we really quit the game. Return true, if this command
     * quits the game, false otherwise.
     */
    private boolean quit(Command command) 
    {
        if(command.hasSecondWord()) {
            System.out.println("Quit what?");
            return false;
        }
        else
            return true;  // signal that we want to quit
    }

}
