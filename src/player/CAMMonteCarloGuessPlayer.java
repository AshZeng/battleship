package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import world.ConfigurationCounter;
import world.OppWorld;
import world.OppWorld.cellState;
import world.World;
import world.World.Coordinate;

/**
 * Monte Carlo guess player (task C).
 * Please implement this class.
 *
 * @author Youhan, Jeffrey
 */
public class CAMMonteCarloGuessPlayer extends Guesser implements Player{
	
	public static final int BOARD_EDGE = 0;
	public static final int CURRENT_CONFIGURATION = 1;
	public static final int INCLUSIVE_EXTRA_CELL = 1;
	
	@Override
    public void initialisePlayer(World world) {
        this.myWorld = world;
        this.opponentsWorld = new OppWorld( world.numRow, world.numColumn, true );
        this.hitsToMyFleet = new ArrayList<>();
        this.opponentsWorld.initialiseShipCounters();
        countAllConfigurations(opponentsWorld.ShipCounters, opponentsWorld.total);
        opponentsWorld.total.printConfigCounter();
    } // end of initialisePlayer()
	
	private void countAllConfigurations(List<ConfigurationCounter> shipCounters, ConfigurationCounter total) {
    	// Reset all possibilities that are marked higher than 0
//		total.resetPossibilities();
		for(int y = 0; y < total.rows; ++y){ // for each row
			for(int x = 0; x < total.columns; ++x){ // for each cell
				// If the ship hasn't been previous marked (shot or not possible)
				if ( total.ShipConfigurationCounts[y][x] >= 0 )
				{
					total.ShipConfigurationCounts[y][x] = 0;
					for(ConfigurationCounter board: shipCounters){ // for each counter

	    				board.ShipConfigurationCounts[y][x] = 0;
	    				// Count Rows
	    				int configurations = countLine( board, x, y, board.shipSize, false );
	    				// Count Columns
	    				configurations += countLine( board, x, y, board.shipSize, true );
	    				// Reset the current boards value to the newly calculated value 
	    				board.ShipConfigurationCounts[y][x] = configurations;
	    				// Increment the total count
	    				total.ShipConfigurationCounts[y][x] += configurations;
    				}
    			}
    		}
		}
	}

	// Recieves a single x or y for each of the start and finish coordinates depending on the direction
	private int countLine( ConfigurationCounter board, int x, int y, int shipSize, boolean vertical ){

		int coord;
		int start;
		int finish = 0;
		int totalConfigs = 0;
		
		// Check Left
		if ( vertical ) {
			start = checkBelow( x, y-1, BOARD_EDGE );
			coord = y;
//			start = checkDirection( x, y, coord, BOARD_EDGE, 0, -1 );
		}
		else {
			start = checkLeft( x-1, y, BOARD_EDGE );
			coord = x;
//			start = checkDirection( x, y, coord, BOARD_EDGE, -1, 0 );
		}

		while ( finish < board.columns )
		{
			if ( vertical ) {
				finish = checkAbove( x, coord+1, board.rows );
//				finish = checkDirection( x, y, coord, board.rows, 0, 1 );
				totalConfigs = getShipConfigurationCount( coord, shipSize, start, finish); 
			}
			else {
				finish = checkRight( coord+1, y, board.columns );
//				finish = checkDirection( x, y, coord, board.columns, 1, 0 );
				totalConfigs = getShipConfigurationCount( coord, shipSize, start, finish); 
			}
			coord = finish;
			start = finish; // + 1; ########################################################################
		}
		return totalConfigs;	
	}
	
	int checkLeft( int x, int y, int finishObs ) {
		while ( x > finishObs ) {
			cellState cell = opponentsWorld.oppWorld[y][x];
			if ( cell == cellState.Miss )
				return x;
			x--;
		}
		return finishObs;
	}
	
	int checkRight( int x, int y, int finishObs ) {
		while ( x < finishObs ) {
			cellState cell = opponentsWorld.oppWorld[y][x];
			if ( cell == cellState.Miss )
				return x;
			x++;	// Move right
		}
		return finishObs;
	}
	
	int checkBelow( int x, int y, int finishObs ) {
		while ( y > finishObs ) {
			cellState cell = opponentsWorld.oppWorld[y][x];
			if ( cell == cellState.Miss )
				return y;
			y--;
		}
		return finishObs;
	}
	
	int checkAbove( int x, int y, int finishObs ) {
		while ( y < finishObs ) {
			cellState cell = opponentsWorld.oppWorld[y][x];
			if ( cell == cellState.Miss )
				return y;
			y++;	// Move to the cell above
		}
		return finishObs;
	}
	
// COULDN'T GET THIS ONE TO WORK CORRECTLY
	// xMove and yMove can be 1, 0 or -1
//	int checkDirection( int x, int y, int startObs, int finishObs, int xMove, int yMove ) {
//		int pos = startObs;
//			System.out.println( "pos = " + pos + " finishObs = " + finishObs );
//		while ( pos < finishObs )
////		for ( int pos = startObs; pos < finishObs; pos += xMove + yMove )
//		{
//			cellState cell = opponentsWorld.oppWorld[y + (pos * yMove)][x + (pos * xMove)];
//			// if ( ( cell == cellState.Miss ) || ( cell == cellState.Sunk ) )
//			if ( cell == cellState.Miss )
//				return pos;
//			pos += xMove + yMove;
//		}
//		return finishObs;
//	}
	
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
		
		// If a ship is sunk then remove the configuration board from ShipCounters
		
		updateCount( guess );
		
		opponentsWorld.total.printConfigCounter();
		System.out.println();
	}
    
    void updateCount( Guess guess ) {
    	// Set the shot Coordinate on the totals board to be -1
    	opponentsWorld.total.ShipConfigurationCounts[guess.row][guess.column] = -1;
    	// Set the shot Coordinate on each of the boards to be -1
    	for(ConfigurationCounter board: opponentsWorld.ShipCounters){ // for each counter
    		board.ShipConfigurationCounts[guess.row][guess.column] = -1;
    		
    	}
    	
    	countAllConfigurations(opponentsWorld.ShipCounters, opponentsWorld.total);
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
