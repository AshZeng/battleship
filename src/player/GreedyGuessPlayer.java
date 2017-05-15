package player;

import java.util.ArrayList;
import java.util.List;

import world.OtherWorld;
import world.World;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class GreedyGuessPlayer extends Guesser implements Player{
	
	public List<Guess> checkerBoardGuesses;
	
	@Override
    public void initialisePlayer(World world) {
        this.myWorld = world;
        this.opponentsWorld = new OtherWorld();
        this.hitsToMyFleet = new ArrayList<>();
        this.checkerBoardGuesses = new ArrayList<>();
        enumerateGuesses(checkerBoardGuesses);
    } // end of initialisePlayer()
	
	private void enumerateGuesses(List<Guess> list) {
		for(int row = 0; row < myWorld.numRow; ++row){
			for(int col = row%2; col < myWorld.numColumn; col+=2){
				Guess g = new Guess();
				g.row = row;
				g.column = col;
				list.add(g);
			}
		}
	}

    @Override
    public Guess makeGuess() {
        // ** Hunting mode **
        // Allowed guesses are any that are on the checker board pattern
        // 
        // ** Targeting greedy mode **
        // Check one square in each direction
        // if notYetFiredUpon(), then fire
        // Recursively call that direction when a hit is found
        // On return from the Recursive calls, if(shipNotYetSunk)
        // then guessInOppositeDirection()
        

        // To be implemented.

        // dummy return
        return null;
    } // end of makeGuess()


} // end of class GreedyGuessPlayer
