package player;

import java.util.List;

import world.OppWorld;
//import world.OtherWorld;
import world.World;
import world.OppWorld.cellState;
import world.World.Coordinate;
import world.World.ShipLocation;

public abstract class Guesser implements Player{
	
	private static final int NUMBER_OF_VULNERABLE_COORDINATES = 17;
    public World myWorld;
    public OppWorld opponentsWorld;
    public List<Guess> hitsToMyFleet;

	@Override
    public Answer getAnswer(Guess guess) {
        Answer answer = new Answer();
        //check if the guess hits the fleet
        if(guessIsAccurate(guess, answer)){
        	answer.isHit = true;
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
    				hitsToMyFleet.add(guess);
					// if ship is sunk altogether
					if(updateShipSunkStatus(ship))
						answer.shipSunk = ship.ship;
					return true;
				}	
    		}	
    	}
		return false;
	}

	public boolean updateShipSunkStatus(ShipLocation ship) {
		boolean shipSunk = true;
		// Iterate over the coordinates of the ship
		for(Coordinate c: ship.coordinates) {
			// if at least one part of ship hasn't been hit
			if(notContainedInGuessList(c, hitsToMyFleet)){
				shipSunk = false;
			}
		}
		return shipSunk;
	}
	
	@Override
	public void update(Guess guess, Answer answer) {
		if(answer.isHit)
			opponentsWorld.updateCell ( cellState.Hit, guess.row, guess.column );
		else
			opponentsWorld.updateCell ( cellState.Miss, guess.row, guess.column );
	}

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
