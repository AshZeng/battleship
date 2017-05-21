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
    				//Count the configurations
    				int configurations = getShipConfigurationCountForOneCell(x, board.shipSize, BOARD_EDGE, board.columns);
    				//Store the result in the board
    				board.ShipConfigurationCounts[y][x] = configurations;
    				//Update the total Count for that cell
    				total.ShipConfigurationCounts[y][x] += configurations;
    			}
    		}
    		//Same again for the column
    		for(int x = 0; x < board.columns; ++x){ // for each column
    			for(int y = 0; y < board.rows; ++y){ // for each cell
    				int configurations = getShipConfigurationCountForOneCell(y, board.shipSize, BOARD_EDGE, board.rows);
    				board.ShipConfigurationCounts[y][x] += configurations;
    				total.ShipConfigurationCounts[y][x] += configurations;
    			}
    		}
    	}	
	}// end of countAllConfigurations()

	public int getShipConfigurationCountForOneCell(int coordinate, int shipSize, int rangeMin, int rangeMax) {
    	// reduce the range to be the reach of the ship
    	int newRangeMin = max(rangeMin, coordinate - shipSize + INCLUSIVE_EXTRA_CELL);
    	int newRangeMax = min(rangeMax, coordinate + shipSize);
    	int range = newRangeMax - newRangeMin;
    	return CURRENT_CONFIGURATION + range - shipSize;
    }

	@Override
    public Guess makeGuess() {
		//if there are hits and not all surrounding cells have been fired at
		// **Targeting Mode
		if (opponentsWorld.possibleTargets.size() > 0)
			return this.createGuess(opponentsWorld.possibleTargets.remove(0));
		// **Hunting Mode**
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
		
		//Set the state of the cell that was fired at to -1
		opponentsWorld.total.ShipConfigurationCounts[guess.row][guess.column] = -1;
		//Find range between shot cell and obstacles
		int rangeMinRow = getRangeMinRow(guess);
		int rangeMaxRow = getRangeMaxRow(guess);
		int rangeMinColumn = getRangeMinColumn(guess);
		int rangeMaxColumn = getRangeMaxColumn(guess);
		UpdateConfigurations(rangeMinRow, rangeMaxRow, rangeMinColumn, rangeMaxColumn, guess);
	}
    
    private void UpdateConfigurations(
    		int rangeMinRow,
    		int rangeMaxRow,
    		int rangeMinColumn,
    		int rangeMaxColumn,
    		Guess guess) {
    	//loop through total counter to reset to 0
    	resetAffectedCellsToZero(opponentsWorld.total, rangeMinRow, rangeMaxRow, rangeMinColumn, rangeMaxColumn, guess);
    	for(ConfigurationCounter board: opponentsWorld.ShipCounters){ // for each shipCounter
    		// recalculate the count for each cell affected by the shot
    		for(int x = rangeMinRow; x < guess.column; ++x){ // for each cell in the row below the shot 
    			int configurations = getShipConfigurationCountForOneCell(x, board.shipSize, rangeMinRow, guess.column);
    			board.ShipConfigurationCounts[guess.row][x] = configurations;
    			opponentsWorld.total.ShipConfigurationCounts[guess.row][x] += configurations;
    		}
    		for(int x = guess.column + 1; x < rangeMaxRow; ++x){ // for each cell in the row above the shot
    			int configurations = getShipConfigurationCountForOneCell(x, board.shipSize, guess.column + 1, rangeMaxRow);
    			board.ShipConfigurationCounts[guess.row][x] = configurations;
    			opponentsWorld.total.ShipConfigurationCounts[guess.row][x] += configurations;
    		}
    		for(int y = rangeMinColumn; y < guess.row; ++y){ // for each cell in the column below the shot
    			int configurations = getShipConfigurationCountForOneCell(y, board.shipSize, rangeMinColumn, guess.row);
    			board.ShipConfigurationCounts[y][guess.column] += configurations;
    			opponentsWorld.total.ShipConfigurationCounts[y][guess.column] += configurations;
    		}
    		for(int y = rangeMinColumn; y < rangeMaxColumn; ++y){ // for each cell in the column above the shot
    			int configurations = getShipConfigurationCountForOneCell(y, board.shipSize, guess.row + 1, rangeMaxColumn);
    			board.ShipConfigurationCounts[y][guess.column] += configurations;
    			opponentsWorld.total.ShipConfigurationCounts[y][guess.column] += configurations;
    		}
    	}	
    }// end of UpdateConfigurations()
    
    private void resetAffectedCellsToZero(
			ConfigurationCounter total, 
			int rangeMinRow, 
			int rangeMaxRow,
			int rangeMinColumn, 
			int rangeMaxColumn, 
			Guess guess) {
    	// reset all counters in the affected cells to zero so new totals can be summed
		for(int x = rangeMinRow; x < rangeMaxRow; ++x){
			total.ShipConfigurationCounts[guess.row][x] = 0;
		}
		for(int y = rangeMinColumn; y < rangeMaxColumn; ++y){
			total.ShipConfigurationCounts[y][guess.column] = 0;
		}
		//The state of the cell that was fired at will have been overwritten with a zero
		//Set it to -1 again here
		opponentsWorld.total.ShipConfigurationCounts[guess.row][guess.column] = -1;
	}// end of resetAffectedCellsToZero()

	private int getRangeMaxColumn(Guess guess) {
		for(int y = guess.row; y < opponentsWorld.numRows; ++y){
			if(opponentsWorld.total.ShipConfigurationCounts[guess.row][y] == -1)
				return opponentsWorld.total.ShipConfigurationCounts[guess.row][y];
		}
		return 0;
	}

	private int getRangeMinColumn(Guess guess) {
		for(int y = guess.row; y >= BOARD_EDGE; --y){
			if(opponentsWorld.total.ShipConfigurationCounts[y][guess.column] == -1)
				return opponentsWorld.total.ShipConfigurationCounts[y][guess.column];
		}
		return 0;
	}

	private int getRangeMaxRow(Guess guess) {
		for(int x = guess.column; x < opponentsWorld.numColumns; ++x){
			if(opponentsWorld.total.ShipConfigurationCounts[guess.row][x] == -1)
				return opponentsWorld.total.ShipConfigurationCounts[guess.row][x];
		}
		return opponentsWorld.numColumns;
	}

	private int getRangeMinRow(Guess guess) {
		for(int x = guess.column; x >= BOARD_EDGE; --x){
			if(opponentsWorld.total.ShipConfigurationCounts[guess.row][x] == -1)
				return opponentsWorld.total.ShipConfigurationCounts[guess.row][x];
		}
		return 0;
	}

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
