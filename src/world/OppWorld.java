package world;

import java.util.ArrayList;
import java.util.List;

import world.ConfigurationCounter;
import world.World.Coordinate;

/* 
 * Cams Class to store the state of the opponents world
 */

public class OppWorld {
	
	private World world = new World();
	private boolean checkPossibles = false;
	
	public enum cellState { Untested, Possible, Hit, Miss }; 
	/* 
	 * NOTE although there is a cell state for Possible, this is used to simplify
	 *   the checking process. The list below is to allow quick access without 
	 *   searching through the 2D array for every guess
	 */
	public List<Coordinate> possibleTargets = new ArrayList<>();
	
	// 2d Arrays representing the opponents world
	public cellState[][] oppWorld = null; 
	public List<ConfigurationCounter> ShipCounters = new ArrayList<>();
	public ConfigurationCounter total;
	public ConfigurationCounter size5AircraftCarrier;
	public ConfigurationCounter size4Battleship;
	public ConfigurationCounter size3Submarine;
	public ConfigurationCounter size3Cruiser;
	public ConfigurationCounter size2Destroyer;
	
	public void initialiseShipCounters(){
		total = new ConfigurationCounter(numRows, numColumns, 0);
		size5AircraftCarrier = new ConfigurationCounter(numRows, numColumns, 5);
		size4Battleship = new ConfigurationCounter(numRows, numColumns, 4);
		size3Submarine = new ConfigurationCounter(numRows, numColumns, 3);
		size3Cruiser = new ConfigurationCounter(numRows, numColumns, 3);
		size2Destroyer = new ConfigurationCounter(numRows, numColumns, 2);
		ShipCounters.add(size5AircraftCarrier);
		ShipCounters.add(size4Battleship);
		ShipCounters.add(size3Submarine);
		ShipCounters.add(size3Cruiser);
		ShipCounters.add(size2Destroyer);
	}
	
	// Copy of the world boundaries
	public int numRows;
	public int numColumns;
	
	// Constructor Class
	public OppWorld( int numRow, int numColumn, boolean checkPossibles )
	{
		oppWorld = new cellState[numRow][numColumn];
		
		this.numRows = numRow;
		this.numColumns = numColumn;
		this.checkPossibles = checkPossibles;
		
		// Initialize ALL cells to untested
		for ( int j = 0; j < numRows; j++ )
		{
			for ( int i = 0; i < numColumns; i++ )
				oppWorld[j][i] = cellState.Untested;
		}
	}

	// Called from the player class, to update the state of the opponents world
	public void updateCell ( cellState state, int row, int column )
	{
		oppWorld[row][column] = state;
		
		// If a hit was detected, we need to update the surrounding cells to 
		//   check for possible ships
		if ( ( state == cellState.Hit ) && ( this.checkPossibles ) )
		{
			calculatePossibles( row, column );
		}
	}
	
	// Checks the state of the cells above, below, left and right of the target cell
	private void calculatePossibles( int row, int column )
	{
		int temp;
		// Check Up
		temp = row + 1;
		if ( temp < numRows )
			checkCellUntested( temp, column );
		
		// Check Down
		temp = row - 1;
		if ( temp >= 0 )
			checkCellUntested( temp, column );
		
		// Check Right
		temp = column + 1;
		if ( temp < numColumns )
			checkCellUntested( row, temp );
		
		// Check Left
		temp = column - 1;
		if ( temp >= 0 )
			checkCellUntested( row, temp );	
	}
	
	private void checkCellUntested( int row, int column )
	{
		// If the cell is untested, it needs to become a "possible"
		if ( oppWorld[row][column] == cellState.Untested )
		{
			oppWorld[row][column] = cellState.Possible;
			Coordinate tempCoord = world.new Coordinate();
			tempCoord.row = row;
			tempCoord.column = column;
			
			possibleTargets.add(tempCoord);
		}
		// If the cell is already marked as hit or miss or possible, then it is ignored
	}
	
	public void resetAllPossibleTargets(){
		for (int j = numRows - 1; j >= 0; j--){
			for (int i = 0; i < numColumns; i++){
				if(oppWorld[j][i] == cellState.Possible)
					oppWorld[j][i] = cellState.Untested;
			}
		}
	}
	
	public void printBoard(int[][] board) {
		for(int x = numRows - 1; x>=0; --x){
			for(int y = 0; y < 10; ++y) {
				System.out.format("%02d ", board[x][y]);
			}
			System.out.println();
		}
		System.out.println();
		
	}
	
    public void printWorld( )
    {
    	char letter;
    	
    	printLine();
    	
    	for ( int j = numRows - 1; j >= 0 ; j-- )
		{
			System.out.print( j + "|");
			
			for ( int i = 0; i < numColumns; i++ )
			{
				if ( oppWorld[j][i] == cellState.Untested )
					letter = ' ';
				else if ( oppWorld[j][i] == cellState.Hit )
					letter = 'H';
				else if ( oppWorld[j][i] == cellState.Miss )	
					letter = 'M';
				else if ( oppWorld[j][i] == cellState.Possible )
					letter = 'P';
				else
					letter = 'X';
				System.out.print( letter + "|");
			}
			System.out.print("\n");
		}
		printHeader();
    }

    public void printHeader()
    {
    	System.out.print(" ");
    	for ( int i = 0; i < numColumns; i++ )
		   	System.out.print(" " + i );
    	System.out.print("\n");
    }
    
    public void printLine()
    {
    	System.out.print("\n");
    	for ( int i = 0; i < numColumns + 1; i++ )
		   	System.out.print("--");
    	System.out.print("\n");
    }
}
