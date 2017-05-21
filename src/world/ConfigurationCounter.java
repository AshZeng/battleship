package world;

/**
 * Class to hold information about the number of possible ship configurations 
 * that pass through a single coordinate on the board.
 * Numbers used for edge cases:
 * -1 Represents a shot has been fired on that coordinate
 * ????? Represents no possible ship combinations for that particular coordinate 
 * @author patstockwell
 *
 */
public class ConfigurationCounter {
	
	public int[][] ShipConfigurationCounts;
	public int shipSize;
	public int rows;
	public int columns;
	
	public ConfigurationCounter(int rows, int columns, int shipSize){
		this.ShipConfigurationCounts = new int[rows][columns];
		this.shipSize = shipSize;
		this.columns = columns;
		this.rows = rows;
		initializeArrayToZero();
	}

	private void initializeArrayToZero(){
		for(int y = 0; y < rows; ++y){ // for each row
			for(int x = 0; x < columns; ++x){ // for each column
				ShipConfigurationCounts[y][x] = 0;
			}
		}
	}
	
	// Reset all possibilities (except shots made) to 0
	public void resetPossibilities( ) {
		int value;
		for(int y = 0; y < rows; ++y){ // for each row
			for(int x = 0; x < columns; ++x){ // for each column
				value = ShipConfigurationCounts[y][x];
				// Will Ignore all shots marked as well as areas that 
				// have previously been declared not possible (ie 0)
				if ( value > 0  )
					ShipConfigurationCounts[y][x] = 0;
			}
		}
	}
	
	// Print the current configuration counter to the console
    public void printConfigCounter( )
    { 	
    	int currentValue;
    	printLine();
    	for ( int j = rows - 1; j >= 0 ; j-- )
		{
			System.out.print( j + "|");
			
			for ( int i = 0; i < columns; i++ )
			{
				currentValue = ShipConfigurationCounts[j][i];
				
				if ( (currentValue > 9) || (currentValue < 0) )
					System.out.print( currentValue + "|");
				else 
					System.out.print( " " + currentValue + "|");
			}
			System.out.print("\n");
		}
		printHeader();
    }

    public void printHeader()
    {
    	System.out.print(" ");
    	for ( int i = 0; i < columns; i++ )
		   	System.out.print("  " + i );
    	System.out.print("\n");
    }
    
    public void printLine()
    {
    	System.out.print("\n");
    	for ( int i = 0; i < columns + 1; i++ )
		   	System.out.print("---");
    	System.out.print("\n");
    }
}
