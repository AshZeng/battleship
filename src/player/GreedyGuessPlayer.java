package player;

import java.util.Scanner;
import world.World;

/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class GreedyGuessPlayer  implements Player{

    @Override
    public void initialisePlayer(World world) {
        // To be implemented.
    } // end of initialisePlayer()

    @Override
    public Answer getAnswer(Guess guess) {
        // To be implemented.

        // dummy return
        return null;
    } // end of getAnswer()


    @Override
    public Guess makeGuess() {
        // ** Hunting mode **
        // Build an array of allowed guess
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


    @Override
    public void update(Guess guess, Answer answer) {
        // To be implemented.
    } // end of update()


    @Override
    public boolean noRemainingShips() {
        // To be implemented.

        // dummy return
        return true;
    } // end of noRemainingShips()

} // end of class GreedyGuessPlayer
