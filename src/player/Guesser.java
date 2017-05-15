package player;

import java.util.ArrayList;
import java.util.List;

import world.OtherWorld;
import world.World;
import world.World.Coordinate;
import world.World.ShipLocation;

public abstract class Guesser implements Player{
	
	private static final int NUMBER_OF_VULNERABLE_COORDINATES = 17;
    public World myWorld;
    public OtherWorld opponentsWorld;
    public List<Guess> hitsToMyFleet;
    public List<Guess> allAvailableGuesses; 

    @Override
    public void initialisePlayer(World world) {
        this.myWorld = world;
        this.opponentsWorld = new OtherWorld();
        this.hitsToMyFleet = new ArrayList<>();
        this.allAvailableGuesses = new ArrayList<>();
        enumerateGuesses(allAvailableGuesses);
    } // end of initialisePlayer()

    private void enumerateGuesses(List<Guess> list) {
		for(int row = 0; row < myWorld.numRow; ++row){
			for(int col = 0; col < myWorld.numColumn; ++col){
				Guess g = new Guess();
				g.row = row;
				g.column = col;
				list.add(g);
			}
		}
	}

	@Override
    public Answer getAnswer(Guess guess) {
        Answer answer = new Answer();
        //check if the guess hits the fleet
        if(guessIsAccurate(guess, answer)){
        	answer.isHit = true;
            hitsToMyFleet.add(guess);
        }
        else
        	answer.isHit = false;
        return answer;
    } // end of getAnswer()

    private boolean guessIsAccurate(Guess guess, Answer answer) {
    	// Iterate through ships
    	for(ShipLocation ship : myWorld.shipLocations) {
    		// Iterate over the coordinates of each ship
    		for(Coordinate c: ship.coordinates) {
    			if(sameAs(c, guess)){ //guess is a hit
					// if ship is sunk altogether
					if(updateShipSunkStatus(ship))
						answer.shipSunk = ship.ship;
					return true;
				}	
    		}	
    	}
		return false;
	}

	private boolean updateShipSunkStatus(ShipLocation ship) {
		boolean shipSunk = true;
		// Iterate over the coordinates of the ship
		for(Coordinate c: ship.coordinates) {
			// if at least one part of ship hasn't been hit
			if(notContainedInGuessList(c, hitsToMyFleet))
				shipSunk = false;
		}
		return shipSunk;
	}
	
    @Override
    public void update(Guess guess, Answer answer) {
        //updating my opponents world 
        //after I have received a response from my shot fired
    	if(answer.isHit)
    		opponentsWorld.hits.add(createCoordinate(guess));
    	else
    		opponentsWorld.misses.add(createCoordinate(guess));
    	if(answer.shipSunk != null)
    		opponentsWorld.shipsSunk.add(answer.shipSunk);
    } // end of update()

    @Override
    public boolean noRemainingShips() {
        return hitsToMyFleet.size() >= NUMBER_OF_VULNERABLE_COORDINATES;
    } // end of noRemainingShips()

    // Start helper methods --->
    public Guess createGuess(Coordinate c){
        Guess g = new Guess();
        g.row = c.row;
        g.column = c.column;
        return g;
    }
    
    public Coordinate createCoordinate(Guess g){
    	Coordinate c = myWorld.new Coordinate();
    	c.row = g.row;
    	c.column = g.column;
    	return c;
    }

    public boolean notContainedInCoordinateList(Coordinate coordinate, List<Coordinate> array){
        for(Coordinate c: array){
            if(coordinate.row == c.row && coordinate.column == c.column)
                return false;
        }
        return true;
    }
    
    public boolean notContainedInGuessList(Coordinate coordinate, List<Guess> array){
    	Guess coordinateGuess = createGuess(coordinate);
        for(Guess g: array){
            if(coordinateGuess.row == g.row && coordinateGuess.column == g.column)
                return false;
        }
        return true;
    }

    public boolean sameAs(Coordinate c, Guess guess){
        return c.row == guess.row && c.column == guess.column;
    }

}
