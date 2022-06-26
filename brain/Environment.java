package brain;

import java.security.SecureRandom;
import java.util.*;
import java.io.*;

/**
 * Maintain the environment for a 2D cellular automaton.
 * 
 * @author David J. Barnes
 * @version  2016.02.29
 */
public class Environment
{
    // Default size for the environment.
    private static final int DEFAULT_ROWS = 88;
    private static final int DEFAULT_COLS = 88;
    
    // The grid of cells.
    private Cell[][] cells;
    // Visualization of the environment.
    private final EnvironmentView view;
    
    private int[] values;
    private String fileName = "./brain/Rimsky Korsakov - Flight of the bumblebee (arr. Rachmaninoff).csv";//"./brain/Rimsky Korsakov - Flight of the bumblebee (arr. Rachmaninoff).csv";
    private int indexForFile = 0;
    
    /**
     * Create an environment with the default size.
     */
    public Environment()
    {
        this(DEFAULT_ROWS, DEFAULT_COLS);
        
        setTypeMethod();
        readFile();
    }

    /**
     * Create an environment with the given size.
     * @param numRows The number of rows.
     * @param numCols The number of cols;
     */
    public Environment(int numRows, int numCols)
    {
        setup(numRows, numCols);
        randomize();
        view = new EnvironmentView(this, numRows, numCols);
        view.showCells();
    }
    
    /**
     * Run the automaton for one step.
     */
    public void step()
    {
        int numRows = cells.length;
        int numCols = cells[0].length;
        
        
        //note: this is highly inefficient
        //before updating last row, move all the rows above one place upwards(0 <> 1 ... 48 <> 49)
        for(int row = 0; row < numRows - 1; row++) {
            for(int col = 0; col < numCols; col++) {
                setCellState(row, col, cells[row+1][col].getState());
            }
        }
        
        //test array
        //int[] shortendValues = Arrays.copyOfRange(values, 11279,  values.length);
        
        //temp array 
        int [] rowOfStates = new int[numCols];
        
        
        if (indexForFile + 88 > values.length) {
            for (int i = 0; i <  88; i++) {
                rowOfStates[i] = 0;  
            }               
        }else {
            for (int i = indexForFile; i < indexForFile + 88; i++) {
                rowOfStates[i%88] = values[i];  
            }
        }
        
        //printear rowOfStates para ver si esta funcionando
        //for (int i = 0; i < rowOfStates.length; i++) {
           //System.out.print(rowOfStates[i] + " ");
        //}
        //System.out.println(" ");
        
        increaseIndex();
        
                
        //create array with RANDOM states, i ∈ (0, 256) para intensidad 
        //int [] rowOfStates = new int[numCols];
        //for (int i = 0; i < numCols; i++) {
            //rowOfStates[i] = (int) Math.floor(Math.random()*(256));
        //}
       
        // Update the cells' states.
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                setCellState(DEFAULT_ROWS - 1, col, rowOfStates[col]);
            }
        }
    }
    
    
    /**
     * Reset the state of the automaton to all DEAD.
     */
    public void reset()
    {
        int numRows = cells.length;
        int numCols = cells[0].length;
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                setCellState(row, col, Cell.DEAD);
            }
        }
    }
    
    /**
     * Generate a random setup.
     */
    public void randomize()
    {
        int numRows = cells.length;
        int numCols = cells[0].length;
        SecureRandom rand = new SecureRandom();
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                setCellState(row, col, rand.nextInt(Cell.NUM_STATES));
            }
        }
    }
    
    public void setCellType(int row, int col, int type){
        cells[row][col].setTypeOfNote(type);
    }
    /**
     * Set the state of one cell.
     * @param row The cell's row.
     * @param col The cell's col.
     * @param state The cell's state.
     */
    public void setCellState(int row, int col, int state)
    {
        cells[row][col].setState(state);
    }
    
    /**
     * Return the grid of cells.
     * @return The grid of cells.
     */
    public Cell[][] getCells()
    {
        return cells;
    }
    
    /**
     * Setup a new environment of the given size.
     * @param numRows The number of rows.
     * @param numCols The number of cols;
     */
    private void setup(int numRows, int numCols)
    {
        cells = new Cell[numRows][numCols];
        for(int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                cells[row][col] = new Cell();
            }
        }
        setupNeighbors();
    }
    
    /**
     * Give to a cell a list of its neighbors.
     */
    private void setupNeighbors()
    {
        int numRows = cells.length;
        int numCols = cells[0].length;
        // Allow for 8 neighbors plus the cell.
        ArrayList<Cell> neighbors = new ArrayList<>(9);
        for(int row = 0; row < numRows; row++) {
            for(int col = 0; col < numCols; col++) {
                Cell cell = cells[row][col];
                // This process will also include the cell.
                for(int dr = -1; dr <= 1; dr++) {
                    for(int dc = -1; dc <= 1; dc++) {
                        int nr = (numRows + row + dr) % numRows;
                        int nc = (numCols + col + dc) % numCols;
                        neighbors.add(cells[nr][nc]);
                    }
                }
                // The neighbours should not include the cell at
                // (row,col) so remove it.
                neighbors.remove(cell);
                cell.setNeighbors(neighbors);
                neighbors.clear();
            }
        }
    }
    public void setTypeMethod() {
        //TODO: fix to match correct black notes
        //set type of notes (white or black)
        for(int col = 0; col < DEFAULT_ROWS; col++) {
            for(int row = 0; row < DEFAULT_COLS; row++) {
                setCellType(row, col, col % 3 == 0 ? 1 : 0);//usar mod n para proporcion de n:1 entre colores
            }
        }
    }
    
    public void readFile() {
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            if (line != null) {
                String [] stringValues = line.split(",");
                values = new int[stringValues.length];
                for (int i = 0; i < values.length; i++) {
                    if (stringValues[i].isEmpty()) {
                        values[i] = 0;
                    } else {
                        values[i] = Integer.parseInt(stringValues[i]);
                    }
                }
            }
        }catch(IOException e) {
            e.printStackTrace();
            System.err.println("Unable to open file");    
        }
    }
    public void increaseIndex() {
        this.indexForFile = indexForFile + 88;    
    }
}



