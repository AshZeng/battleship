package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import world.OtherWorld;
import world.World;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class RandomGuessPlayer extends Guesser implements Player{
	
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
    public Guess makeGuess() {
		//get a random index and pop a guess object from the list
		Random random = new Random();
		int index = random.nextInt(1000) % allAvailableGuesses.size();
		return allAvailableGuesses.remove(index);
    } // end of makeGuess()



} // end of class RandomGuessPlayer
