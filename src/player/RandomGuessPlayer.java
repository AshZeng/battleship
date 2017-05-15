package player;

import java.util.Random;

/**
 * Random guess player (task A).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class RandomGuessPlayer extends Guesser implements Player{

	@Override
    public Guess makeGuess() {
		//get a random index and pop a guess object from the list
		Random random = new Random();
		int index = random.nextInt(1000) % allAvailableGuesses.size();
		return allAvailableGuesses.remove(index);
    } // end of makeGuess()



} // end of class RandomGuessPlayer
