package world;

/**
 * Class to hold information about the number of possible ship configurations 
 * that pass through a single coordinate on the board.
 * Numbers used for edge cases:
 * -1 Represents a shot has been fired on that coordinate
 *  0 Represents no possible ship combinations for that particular coordinate 
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
	}

}
