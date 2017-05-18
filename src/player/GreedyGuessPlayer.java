package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import world.OppWorld;
//import world.OtherWorld;
import world.World;
import world.World.Coordinate;

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
        this.opponentsWorld = new OppWorld( world.numRow, world.numColumn, true );
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
    	// ** Targeting greedy mode **
    	if ( opponentsWorld.possibleTargets.size() > 0 )
    	{    		
			Guess g = new Guess();
			Coordinate tempCoord = myWorld.new Coordinate();
			tempCoord = opponentsWorld.possibleTargets.remove(0);

			g.row = tempCoord.row;
			g.column = tempCoord.column;
			
    		/*
    		 *  Check if this possible target is in the checkBoardGuesses list
    		 *  This is to avoid duplicate guesses
    		 */
			Guess tempGuess = new Guess();
			for ( int i = 0; i < checkerBoardGuesses.size(); i++ ) 
			{
				tempGuess = checkerBoardGuesses.get(i);
				if ( ( tempGuess.row == g.row ) && ( tempGuess.column == g.column ) )
				{
					checkerBoardGuesses.remove(i);
					i = checkerBoardGuesses.size();	// TO EXIT LOOP
				}
			}
			return g;
    	}
    	
        // ** Hunting mode **
    	Random random = new Random();
		int index = random.nextInt(1000) % checkerBoardGuesses.size();
		return checkerBoardGuesses.remove(index);
    } // end of makeGuess()
} // end of class GreedyGuessPlayer
