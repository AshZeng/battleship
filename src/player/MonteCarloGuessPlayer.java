package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import world.ConfigurationCounter;
import world.OppWorld;
import world.World;
import world.OppWorld.cellState;
import world.World.Coordinate;

/**
 * Monte Carlo guess player (task C).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class MonteCarloGuessPlayer extends Guesser implements Player{
	
	public static final int BOARD_EDGE = 0;
	public static final int CURRENT_CONFIGURATION = 1;
	public static final int INCLUSIVE_EXTRA_CELL = 1;
	
	@Override
    public void initialisePlayer(World world) {
        this.myWorld = world;
        this.opponentsWorld = new OppWorld( world.numRow, world.numColumn, true );
        this.hitsToMyFleet = new ArrayList<>();
        this.opponentsWorld.initialiseShipCounters();
        initialiseTotalCountToZero(opponentsWorld.total);
        countAllConfigurations(opponentsWorld.ShipCounters, opponentsWorld.total);
    } // end of initialisePlayer()
	
	private void initialiseTotalCountToZero(ConfigurationCounter board){
		for(int y = 0; y < board.rows; ++y){ // for each row
			for(int x = 0; x < board.columns; ++x){ // for each column
				board.ShipConfigurationCounts[y][x] = 0;
			}
		}
	}
    
    private void countAllConfigurations(List<ConfigurationCounter> shipCounters, ConfigurationCounter total) {
    	for(ConfigurationCounter board: shipCounters){ // for each counter
    		for(int y = 0; y < board.rows; ++y){ // for each row
    			for(int x = 0; x < board.columns; ++x){ // for each cell
    				int configurations = getShipConfigurationCount(x, board.shipSize, BOARD_EDGE, board.columns);
    				board.ShipConfigurationCounts[y][x] = configurations;
    				total.ShipConfigurationCounts[y][x] += configurations;
    			}
    		}
    		for(int x = 0; x < board.columns; ++x){ // for each column
    			for(int y = 0; y < board.rows; ++y){ // for each cell
    				int configurations = getShipConfigurationCount(y, board.shipSize, BOARD_EDGE, board.rows);
    				board.ShipConfigurationCounts[y][x] += configurations;
    				total.ShipConfigurationCounts[y][x] += configurations;
    			}
    		}
    	}	
	}
    
    public int getShipConfigurationCount(int coordinate, int shipSize, int rangeMin, int rangeMax) {
    	// reduce the range to be the reach of the ship
    	int newRangeMin = max(rangeMin, coordinate - shipSize + INCLUSIVE_EXTRA_CELL);
    	int newRangeMax = min(rangeMax, coordinate + shipSize);
    	int range = newRangeMax - newRangeMin;
    	return CURRENT_CONFIGURATION + range - shipSize;
    }


	@Override
    public Guess makeGuess() {
        int highestCount = 0;
        Guess g = new Guess();
        for(int y = 0; y < opponentsWorld.numRows; ++y){ //for each row
        	for(int x = 0; x < opponentsWorld.numColumns; ++x){ // for each column
        		// if it is a higher number, make it the new guess
        		if(opponentsWorld.total.ShipConfigurationCounts[y][x] > highestCount){
        			highestCount = opponentsWorld.total.ShipConfigurationCounts[y][x];
        			g.column = x;
        			g.row = y;
        		}
        	}
        }
        return g;
    } // end of makeGuess()
    
    @Override
	public void update(Guess guess, Answer answer) {
		if(answer.isHit)
			opponentsWorld.updateCell ( cellState.Hit, guess.row, guess.column );
		else
			opponentsWorld.updateCell ( cellState.Miss, guess.row, guess.column );
	}
    
    // IDEAS ON ORDER OF OPERATIONS
    // make a guess
    // update opponents board in the oppWorld array
    // --> need to add to update(), change each Count array as well
    
    // updateCount() -->
    // update totalCount first
    // set shot coordinate to -1
    // get range affected in the row
    // get range affected in the column
    // set all values in the row range to zero
    // set all values in the column range to zero
    // (excluding boundaries and the coordinate fired at) 
    //
    // for each array in shipCounters
    // change to -1 where the shot was made
    // calculate count for coordinates inside the row range
    // calculate count for coordinates inside the column range
    // 		(for each coordinate, set to zero first, and add recalculated value to the totalCount)


	private int max(int x, int y) {
    	if(x > y)
    		return x;
    	else
    		return y;
    }
    
    private int min(int x, int y) {
    	if(x < y)
    		return x;
    	else
    		return y;
    }

} // end of class MonteCarloGuessPlayer
