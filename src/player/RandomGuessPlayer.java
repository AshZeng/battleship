package player;

import world.OtherWorld;
import world.World;
import world.World.Coordinate;
import world.World.ShipLocation;

import java.util.Random;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class RandomGuessPlayer implements Player{

    private static final int NUMBER_OF_VULNERABLE_COORDINATES = 17;
    private World myWorld;
    private OtherWorld opponentsWorld;
    

    @Override
    public void initialisePlayer(World world) {
        this.myWorld = world;
        this.opponentsWorld = new OtherWorld();
    } // end of initialisePlayer()
    

    @Override
    public Answer getAnswer(Guess guess) {
        Answer answer = new Answer();
        //check if the guess hits the fleet
        if(guessIsAccurate(guess, answer)){
        	answer.isHit = true;
            myWorld.hitsToMyFleet.add(guess);
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
				if(c.sameAs(guess)){ //guess is a hit
					c.hit = true;
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
		ship.sunk = true;
		// Iterate over the coordinates of the ship
		for(Coordinate c: ship.coordinates) {
			// if at least one part of ship hasn't been hit
			if(c.hit == false)
				ship.sunk = false;
		}
		return ship.sunk;
	}


	@Override
    public Guess makeGuess() {
        // declare a random object for generating numbers;
    	Random random = new Random();
        // create coordinate object for comparing with previous guesses
        Coordinate coordinate = myWorld.new Coordinate();
        // create boolean to keep looping while looking for new coordinate
        boolean stillLooking = true;
        while(stillLooking){ // generate x and y values
        	coordinate.column = random.nextInt(100) % myWorld.numColumn;
        	coordinate.row = random.nextInt(100) % myWorld.numRow;
            // if coordinate not in the list of previous guesses
            if(coordinate.isNotContainedIn(opponentsWorld.shots))
            	stillLooking = false; // then stop looking
        }
        // build up a map of my opponent's world
        opponentsWorld.shots.add(coordinate);
        return coordinate.createGuess();
    } // end of makeGuess()


    @Override
    public void update(Guess guess, Answer answer) {
        //updating my opponents world 
        //after I have received a response from my shot fired
    	if(answer.isHit)
    		opponentsWorld.hits.add(guess.createCoordinate());
    	else
    		opponentsWorld.misses.add(guess.createCoordinate());
    	if(answer.shipSunk != null)
    		opponentsWorld.shipsSunk.add(answer.shipSunk);
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        return myWorld.hitsToMyFleet.size() >= NUMBER_OF_VULNERABLE_COORDINATES;
    } // end of noRemainingShips()

} // end of class RandomGuessPlayer
