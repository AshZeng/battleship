package player;


/**
 * Greedy guess player (task B).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class GreedyGuessPlayer extends Guesser implements Player{

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


} // end of class GreedyGuessPlayer
