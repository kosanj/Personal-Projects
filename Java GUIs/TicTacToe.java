/* Due Date: N/A											 		  *
 * Project Title: Tic Tac Toe											*/

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Random;

@SuppressWarnings("serial")
public class TicTacToe extends JFrame {

	//Dimensions of the tiles
	int x = 3;
	int y = 3;
	
	//Value to switch
	char value = 'X';
	
	//Array of the tiles
	JButton[] tiles = new JButton[x*y];
	
	//Array of enabled tiles
	int[] tilesEnabled = new int[x*y];
	int rNum,highestNum,randNum;
	
	JFrame frame = null; //For the JOptionPane to be in the middle of the frame

	//Text of Buttons
	String one,two,three,four,five,six,seven,eight,nine;
	
	//See if there was a match
	boolean match = false;
	//See if game is done
	boolean done = false;
	//Count how many tiles are enabled
	int countEnabledTiles=0;
	
	//Constructor for TicTacToe class
	public TicTacToe() {				
		//Initialize the components
		initComponents();
	}
	
	//Method for setting properties of elements added to the GridBagLayout (width, position, fill)
	private void setGridBagLayoutProperties (int fill, int width, int horizontalPosition, int verticalPosition, GridBagConstraints c, JPanel panel, JComponent component) {
		c.fill=fill;
		c.gridwidth = width;
		c.gridx = horizontalPosition;
		c.gridy = verticalPosition;
		panel.add(component, c);
	}
	
	private void valueSwitch () {
		if (value == 'X')
			value = 'O';
		else
			value = 'X';
	}
	
	private void getTileValues () {
		//Get values on tiles to check if there is a row of matches
		one = tiles[0].getText();
		two = tiles[1].getText();
		three = tiles[2].getText();
		four = tiles[3].getText();
		five = tiles[4].getText();
		six = tiles[5].getText();
		seven = tiles[6].getText();
		eight = tiles[7].getText();
		nine = tiles[8].getText();
	}
	
	private void checkIfMatch(JRadioButton option5) {
		
		//Get values on tiles to compare
		getTileValues();
		
		//If there is a match
		if((one.equals(two) && two.equals(three) && one!="" && two!="" && three!="") 
				|| (four.equals(five) && five.equals(six) && four!="" && five!="" && six!="")
				|| (seven.equals(eight) && eight.equals(nine) && seven!="" && eight!="" && nine!="")
				|| (one.equals(five) && five.equals(nine) && one!="" && five!="" && nine!="")
				|| (three.equals(five) && five.equals(seven) && three!="" && five!="" && seven!="")
				|| (one.equals(four) && four.equals(seven) && one!="" && four!="" && seven!="")
				|| (two.equals(five) && five.equals(eight) && two!="" && five!="" && eight!="")
				|| (three.equals(six) && six.equals(nine) && three!="" && six!="" && nine!="")) {
			
			//Disable all tiles
			for(int i=0;i<x*y;i++) {
				tiles[i].setEnabled(false);
			}
			
			//Add a pop-up which shows the winner message and add a label to center the message on it.
            JLabel labelDone = new JLabel();  //Pop up showing winner
            if(!option5.isSelected())
            	valueSwitch();
            labelDone.setText("Player "+value+" wins. Click reset to try again.");
            labelDone.setHorizontalAlignment(SwingConstants.CENTER);
            JOptionPane.showMessageDialog(frame, labelDone,"",JOptionPane.PLAIN_MESSAGE);
            
            match = true;
            done = true;
		}
		
		//If there is no match once the game is complete
		if(one!="" && two!="" && three!="" && four!="" && five!="" && six!="" && seven!="" && eight!="" && nine!="" && !match) {
			//Add a pop-up which shows the failure message and add a label to center the message on it.
            JLabel labelWrong = new JLabel();  //Pop up showing failure message
            labelWrong.setText("Nobody Wins. Click reset to play again.");
            labelWrong.setHorizontalAlignment(SwingConstants.CENTER);
            JOptionPane.showMessageDialog(frame, labelWrong,"",JOptionPane.PLAIN_MESSAGE);
            done=true;
		}
	}
	
	private void checkIfDone () {
		//The game is also over if all tiles have been disabled so the computer player should not be able to choose a tile after
		for(int i=0;i<tiles.length;i++) {
			//If any of the tiles are still enabled the game is over
			if(tiles[i].isEnabled()) {
				countEnabledTiles++;
			} 
		}
		if(countEnabledTiles==0) //If no tiles are enabled then game is over
			done=true;
	}
	
	//Count how many tiles are enabled
	private int tilesEnabled () {
		int tilesEnabled=0;
		
		for(int i = 0; i<9; i++) {
			if(tiles[i].isEnabled())
				tilesEnabled++;
		}
		return tilesEnabled;
	}
	
	private void randomChoiceComputerPlayer () {
		//Computer Player does it's turn
		if(!done) {
			do {
				//Computer selects randomly for a tile that has not been selected yet
				Random rand = new Random();
				rNum=rand.nextInt(9);
			}
			while (!tiles[rNum].isEnabled());
			
			//Set text on randomly chosen tile
			tiles[rNum].setText(String.valueOf(value));
			tiles[rNum].setEnabled(false);
			value='X';	
		}
	}
		
	//Without random number it is unbeatable
	private void smartComputerPlayer(JRadioButton option3, JRadioButton option4) {
		//First 9 values are to compare horizontally; next 9 values are to compare vertically; next 6 values are diagonal comparisons on gameboard (for each row/column/diagonal check left then right then in between the 2 tiles being compared)
		int[] firstTile = {0,1,0,3,4,3,6,7,6,0,3,0,1,4,1,2,5,2,0,4,0,2,4,2};
		int[] secondTile = {1,2,2,4,5,5,7,8,8,3,6,6,4,7,7,5,8,8,4,8,8,4,6,6};
		int[] blockTile = {2,0,1,5,3,4,8,6,7,6,0,3,7,1,4,8,2,5,8,0,4,6,2,4};   //The tile that could potentially clicked by computer
		boolean computerPlayerFinished = false;
		
		//To give hard computer a chance to make a mistake so it can be beat
		if(option3.isSelected()) { //HARD ONLY
			//Computer selects randomly between 0 and 9 so that there is a 1 in 10 chance of the computer making a "mistake"
			Random r = new Random();
			randNum=r.nextInt(10);
			if(randNum==0)
				System.out.println("MISTAKE");
		} else {
			randNum=5; //So it'll use the regular logic
		}
		
		if(randNum>0) {
			//Searches for a offensive spot to put a tile (16 is the number of searches as shown with the number of elements in each array (Try to win first)
			for(int i=0;i<24;i++) {
				
				//If the computer has found a spot then exit the loop
				if(computerPlayerFinished) 
					break;
				
				//If the the tiles being compared are disabled and BOTH have the COMPUTERS value on them, computer must select the one beside to win. Make sure the tile used to win is enabled (hasn't been clicked yet)
				if(!tiles[firstTile[i]].isEnabled() && !tiles[secondTile[i]].isEnabled() && tiles[blockTile[i]].isEnabled() && (tiles[firstTile[i]].getText()).equals(tiles[secondTile[i]].getText()) && (tiles[firstTile[i]].getText()).equals(String.valueOf(value))) {
					tiles[blockTile[i]].setText(String.valueOf(value)); //Computers sets text on selected tile
					tiles[blockTile[i]].setEnabled(false);				//Computer disabled tile it selected so it cannot be clicked again
					computerPlayerFinished = true;						//So it can break out of loop
				}							
				
			}
					
			//Searches for a defensive spot to put a tile (16 is the number of searches as shown with the number of elements in each array) (Try to Block second)
			for(int i=0;i<24;i++) {
			
				//If the computer has found a spot then exit the loop (also accounts for if it made a move in previous loop)
				if(computerPlayerFinished)
					break;
				
				//If the the tiles being compared are disabled and BOTH have the OPPONENTS value on them, computer must block. Make sure the tile used to block is enabled (hasn't been clicked yet)
				if(!tiles[firstTile[i]].isEnabled() && !tiles[secondTile[i]].isEnabled() && tiles[blockTile[i]].isEnabled() && (tiles[firstTile[i]].getText()).equals(tiles[secondTile[i]].getText()) && (tiles[firstTile[i]].getText())!=(String.valueOf(value))) {
					tiles[blockTile[i]].setText(String.valueOf(value)); //Computers sets text on selected tile
					tiles[blockTile[i]].setEnabled(false);				//Computer disabled tile it selected so it cannot be clicked again
					computerPlayerFinished = true;						//So it can break out of loop
				}							
				
			}
			
			//If there is no place to block or match (offensive or defensive move not needed) then pick randomly
			if(!computerPlayerFinished) {
				
				//GET RID OF DOUBLE TRAP IF PLAYER SELECTS MIDDLE TILE (ONLY IN HARD/IMPOSSIBLE MODE HENCE if option3 radiobutton is selected) 
				if(!tiles[4].isEnabled() && tilesEnabled() == 8 && (option3.isSelected() || option4.isSelected())) {
					tiles[0].setText(String.valueOf(value));
					tiles[0].setEnabled(false);
					computerPlayerFinished=true;
				} else if(!tiles[8].isEnabled() && tilesEnabled() == 6 && (option3.isSelected() || option4.isSelected())) {  //Second step of double trap
					tiles[2].setText(String.valueOf(value));
					tiles[2].setEnabled(false);
					computerPlayerFinished=true;
				}
				
				//Another double trap where if player gets 3 tiles together in a corner they win so must block(Only do this in hard mode --option 3) 
				if(tilesEnabled()==6 && !tiles[1].isEnabled() && !tiles[3].isEnabled() && tiles[1].getText().equals(tiles[3].getText()) && tiles[0].isEnabled() && (option3.isSelected() || option4.isSelected())) {
					tiles[0].setText(String.valueOf(value));
					tiles[0].setEnabled(false);
					computerPlayerFinished=true;
				} else if(tilesEnabled()==6 && !tiles[1].isEnabled() && !tiles[5].isEnabled() && tiles[1].getText().equals(tiles[5].getText()) && tiles[2].isEnabled() && (option3.isSelected() || option4.isSelected())) {
					tiles[2].setText(String.valueOf(value));
					tiles[2].setEnabled(false);
					computerPlayerFinished=true;
				} else if(tilesEnabled()==6 && !tiles[3].isEnabled() && !tiles[7].isEnabled() && tiles[3].getText().equals(tiles[7].getText()) && tiles[6].isEnabled() && (option3.isSelected() || option4.isSelected())) {
					tiles[6].setText(String.valueOf(value));
					tiles[6].setEnabled(false);
					computerPlayerFinished=true;
				} else if(tilesEnabled()==6 && !tiles[5].isEnabled() && !tiles[7].isEnabled() && tiles[5].getText().equals(tiles[7].getText()) && tiles[8].isEnabled() && (option3.isSelected() || option4.isSelected())) {
					tiles[8].setText(String.valueOf(value));
					tiles[8].setEnabled(false);
					computerPlayerFinished=true;
				}
				
				//Second double trap to look at
				if(tiles[4].isEnabled() && (option3.isSelected() || option4.isSelected())) {  //Computer player should get rid of middle tile ASAP if player hasn't selected it first because all double traps involve middle tile (if the player selects middle tile first that possible double trap is dealt with above)
					tiles[4].setText(String.valueOf(value));		//ONLY IN HARD MODE (hence if option3.isSelected()
					tiles[4].setEnabled(false);
				} else if (!computerPlayerFinished){				//If no other option available choose randomly
					randomChoiceComputerPlayer();
				}
				computerPlayerFinished = true;
			}
			
			value='X'; //Back to player 1
			
		} else { //1/10 chance the computer randomly chooses rather than the impossible logic above
			randomChoiceComputerPlayer();
		}
			
	}
	
	//Initialize the UI Components
	private void initComponents() {
		frame = this;	//For the JOptionPane to be in the middle of the frame 
		
		//Set up GridBagLayout (to evenly space the buttons)
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setBorder(new EmptyBorder(10,10,10,10));
		GridBagConstraints c = new GridBagConstraints();
		
		//Print out the Game Title "Matching Game" on a JLabel
        JLabel gameTitle = new JLabel();
        gameTitle.setFont(new java.awt.Font("Myanmar MN", 1, 36));
        gameTitle.setText("Tic Tac Toe");
        setGridBagLayoutProperties (GridBagConstraints.HORIZONTAL, 4,0,0,c,panel,gameTitle);  

        //SELECTION BETWEEN MODES OF GAME; SECTION INTO PANELS FOR DESIGN
        //New panel in new column
        JPanel radioPanel1 = new JPanel(new GridLayout(2,1));
        setGridBagLayoutProperties (GridBagConstraints.NONE, 1,0,1,c, panel, radioPanel1);
        JRadioButton option1 = new JRadioButton("1P Easy"); //Add Radio Button 
        option1.setSelected(true);						
        radioPanel1.add(option1);
        JRadioButton option2 = new JRadioButton("1P Medium"); //Add Radio Button
        radioPanel1.add(option2);
        //New panel in new column
        JPanel radioPanel2 = new JPanel(new GridLayout(2,1));
        setGridBagLayoutProperties (GridBagConstraints.NONE, 1,1,1,c, panel, radioPanel2);
        JRadioButton option3 = new JRadioButton("1P Hard");  //Add Radio Button 
        radioPanel2.add(option3);
        JRadioButton option4 = new JRadioButton("Impossible");
        radioPanel2.add(option4);
        //New panel in new column
        JPanel radioPanel3 = new JPanel(new GridLayout(2,1));
        setGridBagLayoutProperties (GridBagConstraints.NONE, 1,2,1,c, panel, radioPanel3);
        JRadioButton option5 = new JRadioButton("2 Player");  //Add Radio Button 
        radioPanel3.add(option5);
        //Group the radio buttons so you can only click one at a time
        ButtonGroup bgroup = new ButtonGroup();    
        bgroup.add(option1);
        bgroup.add(option2);
        bgroup.add(option3);
        bgroup.add(option4);
        bgroup.add(option5);
        
        
        
		int p = 0;	//To create each new tile
		//Create tiles in a loop
		for(int i = 0; i < x; i++) { //vertical increment for creating tiles (buttons)
			
			for(int j = 0; j < y; j++) { //horizontal increment for creating tiles (buttons)
				//Tile creation and formatting (increments to create a grid)
				JButton tile1 = new JButton();
				tile1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 40));
		        tile1.setText("");
		        tile1.setPreferredSize(new Dimension(100, 100));
		        tile1.setForeground(Color.DARK_GRAY);
				setGridBagLayoutProperties (GridBagConstraints.HORIZONTAL, 1,j,i+2,c, panel, tile1);
				tiles[p++] = tile1;
				
				// Action performed when user clicks a tile it shows the value assigned to it
				tile1.addActionListener(new ActionListener()
				{ 
				  public void actionPerformed(ActionEvent e)
				  {
					  	//Disable radio buttons so you can't switch while playing (must reset)
					  	option1.setEnabled(false);
		            	option2.setEnabled(false);
		            	option3.setEnabled(false);
		            	option4.setEnabled(false);
		            	option5.setEnabled(false);

		            	
		            	//Search for which tiles were clicked and store 
		            	for(int k = 0; k < tiles.length; k++) {
		            		//When the tile clicked is matched with where it is in the array show the value on that block
		            		if(tiles[k] == tile1) {
								
		            			tile1.setText(String.valueOf(value)); //Show the value assigned to the tile
		            			tile1.setEnabled(false);			  //So user can't click same tile twice
								if(!option5.isSelected()) {
									value='O';						  		//To change to the opposite persons turn (X v.s. O)
								}
									
		            		}
							
		            	}
						
						//Check if the game is done 
						checkIfMatch(option5);     //Check if there is a match before next player(will return done false so that the computer player can do it's turn)
						checkIfDone();      //If all tiles have been disabled it means game is done
						
						if (!done) { //If the game isn't done yet computer players turn
							if (option2.isSelected() || option3.isSelected() || option4.isSelected()) {  //Hard or Medium or Impossible
								smartComputerPlayer(option3,option4);
							} else if (option1.isSelected()) {  //Easy
								randomChoiceComputerPlayer();
							} else if(option5.isSelected()) {
								valueSwitch();
							}
							
						    checkIfMatch(option5); //After Computer Players turn check if they won (so player cannot go again)
						    checkIfDone();
						}
	
				  }
				});
			}
		}
		
		//Show a resetButton to start a new game (restart timer, reshuffle values)
        JButton resetButton = new JButton();
        resetButton.setFont(new java.awt.Font("Lao Sangam MN", 0, 20));
        resetButton.setText("RESET");        
		setGridBagLayoutProperties (GridBagConstraints.NONE, 1,2,0,c, panel, resetButton);
		
		//Action performed when user clicks a tile it shows the value assigned to it
		resetButton.addActionListener(new ActionListener() { 
		  public void actionPerformed(ActionEvent e) {
				
				for(int i = 0; i < tiles.length; i++) {
					tiles[i].setText("");
					tiles[i].setEnabled(true);
				}
				
				match=false;
				done=false;
				
                value='X';
                
                //Enable Radio Buttons
                option1.setEnabled(true);
            	option2.setEnabled(true);
            	option3.setEnabled(true);
            	option4.setEnabled(true);
            	option5.setEnabled(true);
		  }
		});
		
		//Defaults
	    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	    setResizable(false);
		add(panel);
	    pack();	
	}

	//Start the program
	public static void main(String[] args) {
		//Java look and feel "Nimbus"
		try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    //Override Nimbus L&F: Change Default Orange to Blue by changing the Nimbus defaults in the UIManager (So progress bar is not orange)
            		UIDefaults defaults = UIManager.getLookAndFeelDefaults();  
            	    defaults.put("nimbusOrange",defaults.get("nimbusFocus"));
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TicTacToe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TicTacToe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TicTacToe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TicTacToe.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
		
		//Start
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TicTacToe().setVisible(true);
            }
        });
        
	}
}