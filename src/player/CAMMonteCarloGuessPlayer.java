package player;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.sun.javafx.scene.traversal.Direction;

import ship.Ship;
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
public class CAMMonteCarloGuessPlayer extends Guesser implements Player{

	public static final int BOARD_EDGE = 0;
	public static final int CURRENT_CONFIGURATION = 1;
	public static final int INCLUSIVE_EXTRA_CELL = 1;
	public List<Ship> ships;
	public int opponentHitCount;
	
	private int directions [] = { -1,0, 1,0, 0,1, 0,-1 };
	private Guess currentDirection = new Guess(); 
	private boolean targetingMode = false;
	private Guess nextTarget = new Guess();

		
	// Used to store the position of the orignal hit, when targeting ships
	private Guess originalHit = new Guess();
	
	@Override
	public void initialisePlayer(World world) {
		this.myWorld = world;
		this.opponentsWorld = new OppWorld( world.numRow, world.numColumn, true );
		this.hitsToMyFleet = new ArrayList<>();
		this.ships = new ArrayList<>();
		this.opponentsWorld.initialiseShipCounters();
		this.opponentHitCount = 0;
		
		resetTargetVariables();
		
		initialiseTotalCountToZero(opponentsWorld.total);
		setupAllConfigurations(opponentsWorld.ShipCounters, opponentsWorld.total);
	} // end of initialisePlayer()

	private void resetTargetVariables() {
		originalHit.row = -2;
		originalHit.column = -2;
		currentDirection.row = -2;
		currentDirection.column = -2;
		nextTarget.row = -2;
		nextTarget.column = -2;
	}
	
	
	
	public void initialiseTotalCountToZero(ConfigurationCounter board){
		for(int y = 0; y < board.rows; ++y){ // for each row
			for(int x = 0; x < board.columns; ++x){ // for each column
				board.ShipConfigurationCounts[y][x] = 0;
			}
		}
	}

	public void setupAllConfigurations(List<ConfigurationCounter> shipCounters, ConfigurationCounter total) {
		for(ConfigurationCounter board: shipCounters){ // for each counter
			for(int y = 0; y < board.rows; ++y){ // for each row
				for(int x = 0; x < board.columns; ++x){ // for each cell
					//Count the configurations
					int configurations = getShipConfigurationCountForOneCell(y, x, board.shipSize);
					//Store the result in the board
					board.ShipConfigurationCounts[y][x] = configurations;
					//Update the total Count for that cell
					total.ShipConfigurationCounts[y][x] += configurations;
				}
			}
		}	
	}// end of countAllConfigurations()

	public int getShipConfigurationCountForOneCell(int column, int row, int shipSize) {
		int rowMax = getUpperBoundForRow(column, row, shipSize);
		int rowMin = getLowerBoundForRow(column, row, shipSize);
		int colMax = getUpperBoundForColumn(column, row, shipSize);
		int colMin = getLowerBoundForColumn(column, row, shipSize);
		// calculate the total number of cells available in the row and col
		int rowRange = rowMax - rowMin - INCLUSIVE_EXTRA_CELL;
		int colRange = colMax - colMin - INCLUSIVE_EXTRA_CELL;
		// calculate how many ways a ship can be placed in that space
		int rowConfigurations = max(0, CURRENT_CONFIGURATION + rowRange - shipSize);
		int colConfigurations = max(0, CURRENT_CONFIGURATION + colRange - shipSize);
		return rowConfigurations + colConfigurations;
	}

	public int getLowerBoundForColumn(int column, int row, int shipSize) {
		for(int y = 1; y < shipSize; ++y){ //cells below in the column
			if(isOutOfBounds(column, row - y) || isObstacle(column, row - y))
				return row - y; 
		}
		return row - shipSize; // Bound is excluded from the range
	}

	public int getUpperBoundForColumn(int column, int row, int shipSize) {
		for(int y = 1; y < shipSize; ++y){ //cells above in the column
			if(isOutOfBounds(column, row + y) || isObstacle(column, row + y))
				return row + y; 
		}
		return row + shipSize; // Bound is excluded from the range
	}

	public int getLowerBoundForRow(int column, int row, int shipSize) {
		for(int x = 1; x < shipSize; ++x){ //cells below in the row
			if(isOutOfBounds(column - x, row) || isObstacle(column - x, row))
				return column - x; 
		}
		return column - shipSize; // Bound is excluded from the range
	}

	public int getUpperBoundForRow(int column, int row, int shipSize) {
		for(int x = 1; x < shipSize; ++x){ //cells above in the row
			if(isOutOfBounds(column + x, row) || isObstacle(column + x, row))
				return column + x; 
		}
		return column + shipSize; // Bound is excluded from the range
	}

	@Override
	public Guess makeGuess() {
		//Print the board for Debug
//		opponentsWorld.printBoard(opponentsWorld.total.ShipConfigurationCounts);
		//if there are hits and not all surrounding cells have been fired at
		// **Targeting Mode
		if ( targetingMode ) {
			return nextTarget;
		}
		
		// **Hunting Mode**
		if(getTotalSunkShipSize() == opponentHitCount)
			opponentsWorld.possibleTargets.clear();
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
	
	public Guess getPossibleTargetWithHighestCount(){
		int highestCount = 0;
		//initialise the coordinate to the first element in the list to guarantee return value
		Coordinate largest = opponentsWorld.possibleTargets.get(0);
		//find the coordinate with the largest count
		for(Coordinate c: opponentsWorld.possibleTargets){
			if(opponentsWorld.total.ShipConfigurationCounts[c.row][c.column] > highestCount){
				largest = c;
				highestCount = opponentsWorld.total.ShipConfigurationCounts[c.row][c.column];
			}
		}
		// remove it from the list
		opponentsWorld.possibleTargets.remove(largest);
		// return it as a guess object
		return createGuess(largest);
	}
	
	public int getTotalSunkShipSize(){
		int totalSize = 0;
		for (Ship s: ships)
			totalSize += s.len();
		return totalSize;
	}

	@Override
	public void update(Guess guess, Answer answer) {
		if(answer.isHit){
			opponentsWorld.updateCell ( cellState.Hit, guess.row, guess.column );
			opponentHitCount++;
			
			// If a ship has been sunk
			if(answer.shipSunk != null){
				this.ships.add(answer.shipSunk);
				targetingMode = false;
				// Clear the originalHit variable
				resetTargetVariables();
			}
			else 
				targetingComputer( guess );
		}
		else {
			opponentsWorld.updateCell ( cellState.Miss, guess.row, guess.column );
			// If we are still in targetingMode and we have missed, then we need to change directions
			if ( targetingMode ) {
				currentDirection.row *= -1;
				currentDirection.column *= -1;
				nextTarget.row = originalHit.row + currentDirection.row;
				nextTarget.column = originalHit.column + currentDirection.column;
				// If this cell has already been shot at
				if ( opponentsWorld.total.ShipConfigurationCounts[nextTarget.column][nextTarget.row] < 1 ) {
					currentDirection.row = -2;
					targetingComputer( guess );
				}
			}
		}
		
		updateConfigurationCount(guess);
		recalculateTotalCount();
	}

	
	/** 
	 * Called when a guess has successfully scored a hit, this function will
	 * 1 - Store the position of the hit (to allow further targeting)
	 * 2 - Choose a surrounding cell with the highest possibility and store its direction 
	 * 			(relative to the initial hit)
	 * 3 - Shoot at the above cell
	 * 4 - If this is also a hit, then we can reasonably assume that until the opponent 
	 * 			returns shipSunk or we encounter a miss, we can continue shooting successfully
	 * 			in this direction
	 * @author Cameron Watt
	 */
	private void targetingComputer( Guess guess ) {
		// 1 - Store the position of the original hit for future calculations
		if ( originalHit.row == -2 ) {
			originalHit.row = guess.row;
			originalHit.column = guess.column;
			targetingMode = true;
		}
		// 2 - Choose a surrounding Cell AND DIRECTION IF not already done
		if ( currentDirection.row == -2 )
			nextTarget = chooseSurroundingCell( guess );
		// 4 - Else if we already have a direction, continue in that direction
		else
		{
			nextTarget.row += currentDirection.row;
			nextTarget.column += currentDirection.column;
		}
		// 3 - Shoot at the above cell 
		// This will be done in the next makeGuess()
		return;
	}
	
	/* 
	 * Used to support the above function by selecting the highest possible cell
	 * surrounding the target guess
	 */
	private Guess chooseSurroundingCell( Guess guess ) {
		int x, y;
		int highestCount = 0;
		Guess g = new Guess();
		for ( int i = 0; i < directions.length; i = (i + 2) )
		{
			y = directions[i] + guess.row;
			x = directions[i+1] + guess.column;
			if(opponentsWorld.total.ShipConfigurationCounts[y][x] > highestCount){
				currentDirection.row = directions[i];
				currentDirection.column = directions[i+1];
				highestCount = opponentsWorld.total.ShipConfigurationCounts[y][x];
				g.row = y;
				g.column = x;
			}
		}
		return g;
	}
	
	
	
	
	
	public void recalculateTotalCount() {
		//reset all counts to zero
		initialiseTotalCountToZero(opponentsWorld.total);
		//iterate through every cell in shipCounters and accumulate a total
		for(ConfigurationCounter shipCounter: opponentsWorld.ShipCounters){ // for each ship
			for(int y = 0; y < myWorld.numRow; ++y){ // for each row
				for(int x = 0; x < myWorld.numColumn; ++x){ // for each column
					opponentsWorld.total.ShipConfigurationCounts[y][x] +=
							shipCounter.ShipConfigurationCounts[y][x];
				}
			}
		}
	}

	public void updateConfigurationCount(Guess guess) {
		//for each shipCounter
		for(ConfigurationCounter shipCounter: opponentsWorld.ShipCounters){
			//set shot to zero
			shipCounter.ShipConfigurationCounts[guess.row][guess.column] = 0;
			// travel each direction to update counts
			updateRowUpper(guess, shipCounter);
			updateRowLower(guess, shipCounter);
			updateColumnUpper(guess, shipCounter);
			updateColumnLower(guess, shipCounter);
		}
	}

	public void updateRowUpper(Guess guess, ConfigurationCounter shipCounter) {
		// visit each cell above the shot in the row and recalculate the count
		for(int x = 1; x < shipCounter.shipSize; ++x){ // start at 1 to avoid the shot cell
			// if there is an obstacle, stop
			if(isOutOfBounds(guess.row, guess.column + x)
					|| isObstacle(guess.row, guess.column + x))
				break;
			else // recalculate the configurations
				shipCounter.ShipConfigurationCounts[guess.row][guess.column + x] =
				getShipConfigurationCountForOneCell(guess.row, guess.column + x, shipCounter.shipSize);
		}
	}

	public void updateRowLower(Guess guess, ConfigurationCounter shipCounter) {
		// visit each cell below the shot in the row and recalculate the count
		for(int x = 1; x < shipCounter.shipSize; ++x){ 
			// if there is an obstacle, stop
			if(isOutOfBounds(guess.row, guess.column - x)
					|| isObstacle(guess.row, guess.column - x))
				break;
			else // recalculate the configurations
				shipCounter.ShipConfigurationCounts[guess.row][guess.column - x] =
				getShipConfigurationCountForOneCell(guess.row, guess.column - x, shipCounter.shipSize);
		}
	}

	public void updateColumnUpper(Guess guess, ConfigurationCounter shipCounter){
		//visit each cell above the shot in the column and recalculate the count
		for(int y = 1; y < shipCounter.shipSize; ++y){
			// if there is an obstacle, stop
			if(isOutOfBounds(guess.row + y, guess.column) 
					|| isObstacle(guess.row + y, guess.column))
				break;
			else // recalculate the configurations
				shipCounter.ShipConfigurationCounts[guess.row + y][guess.column] =
				getShipConfigurationCountForOneCell(guess.row + y, guess.column, shipCounter.shipSize);
		}
	}
	
	public void updateColumnLower(Guess guess, ConfigurationCounter shipCounter){
		//visit each cell below the shot in the column and recalculate the count
		for(int y = 1; y < shipCounter.shipSize; ++y){
			// if there is an obstacle, stop
			if(isOutOfBounds(guess.row - y, guess.column)
					|| isObstacle(guess.row - y, guess.column))
				break;
			else // recalculate the configurations
				shipCounter.ShipConfigurationCounts[guess.row - y][guess.column] =
				getShipConfigurationCountForOneCell(guess.row - y, guess.column, shipCounter.shipSize);
		}
	}

	public boolean isObstacle(int y, int x) {
		return (opponentsWorld.oppWorld[y][x] == cellState.Miss 
				|| opponentsWorld.oppWorld[y][x] == cellState.Hit);
	}
	
	public boolean isOutOfBounds(int y, int x) {
		return (y < BOARD_EDGE
				|| y >= myWorld.numRow
				|| x < BOARD_EDGE
				|| x >= myWorld.numColumn);
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
