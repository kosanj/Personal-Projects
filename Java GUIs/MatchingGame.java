/* Completion Date: June 10, 2021                                 				*
 * Project Title: Matching Game                                         */

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.swing.Timer;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;

@SuppressWarnings("serial")
public class MatchingGame extends JFrame {

    //Dimensions of the tiles
    int x = 4;
    int y = 4;

    //Font
    Font testFont = new Font("Arial Rounded MT Bold", 0, 40);

    //Array of the tiles
    JButton[] tiles = new JButton[x*y];
    JFrame frame = null; //For the JOptionPane to be in the middle of the frame

    //Array of values to display if clicked which will be randomly assigned to each tile
    String[] valuesIcon = {"♥","☺","☻", "☏", "✄", "♛", "♞", "☠","♥","☺","☻", "☏", "✄", "♛", "♞", "☠"};
    String[] valuesLetters = {"A","B","C","D","E","F","G","H","A","B","C","D","E","F","G","H"};
    String[] valuesNumbers = {"1","2","3","4","5","6","7","8","1","2","3","4","5","6","7","8"};          //Because windows doesn't support the symbols in the valuesIcons array
    String[] values = null;

    //Store which 2 buttons were selected
    int previousClick = -1;
    int currentClick = -1;
    //Progress of game & Timer
    int progress = 0;
    long timerClock = -1;

    int counter=0; //Counts how many times there is no match

    //Constructor for MatchingGame class
    public MatchingGame() {
        //Shuffles the values assigned to each tile (so each game will be unique)
        values = valuesIcon;  //By Default the symbols will be chosen as values
        //Initialize the components
        initComponents();
        shuffleValues();
    }

    //Method to shuffle values assigned to each tile (so each game will be unique)
    private void shuffleValues() {
        List<String> valuesList = Arrays.asList(values);    //Convert unshuffled array to list
        Collections.shuffle(valuesList);               //Shuffle method in Collections which can shuffle a list
        valuesList.toArray(values);                         //Convert shuffled list back to array
    }

    //Method for setting properties of elements added to the GridBagLayout (width, position, fill)
    private void setGridBagLayoutProperties (int fill, int width, int horizontalPosition, int verticalPosition, GridBagConstraints c, JPanel panel, JComponent component) {
        c.fill=fill;
        c.gridwidth = width;
        c.gridx = horizontalPosition;
        c.gridy = verticalPosition;
        panel.add(component, c);
    }

    //Initialize the UI Components
    private void initComponents() {
        frame = this;  //For the JOptionPane to be in the middle of the frame

        //Set up GridBagLayout (to evenly space the buttons)
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10,10,10,10));
        GridBagConstraints c = new GridBagConstraints();

        //Print out the Game Title "Matching Game" on a JLabel
        JLabel gameTitle = new JLabel();
        gameTitle.setFont(new java.awt.Font("Myanmar MN", 1, 36));
        gameTitle.setText("Matching Game");
        setGridBagLayoutProperties (GridBagConstraints.HORIZONTAL, 4,0,0,c,panel,gameTitle);

        //Progress Bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(8);              //Max is 8 because 16 tiles (8 matches to complete)
        progressBar.setStringPainted(true);
        setGridBagLayoutProperties (GridBagConstraints.HORIZONTAL, 4,0,1,c,panel, progressBar);

        //Label for the timer
        JLabel timer = new JLabel();
        timer.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 12));
        timer.setBorder(new EmptyBorder(5,20,5,5));
        timer.setText("0 seconds");
        setGridBagLayoutProperties (GridBagConstraints.HORIZONTAL, 1,5,1,c,panel, timer);

        //Create a timer showing how many seconds have passed
        new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                //If timerClock isn't -1(It is -1 before the 1st tile is clicked) and if game is incomplete timer continues
                if(timerClock != -1 && progress != x*y/2)
                    timer.setText(""+((new Date().getTime() - timerClock)/1000)+ " seconds"); //Subtract current time from previous time then convert from milliseconds to seconds
            }
        }).start();

        //Selection between icons to be assigned with radio buttons
        JPanel radioPanel = new JPanel(new GridLayout(2,1)); //Set up GridLayout panel to place Radio Buttons with 2 rows and 1 column. This panel is on top of the gridBagLayout panel
        setGridBagLayoutProperties (GridBagConstraints.NONE, 1,5,4,c, panel, radioPanel);
        JRadioButton option1 = new JRadioButton(); //Add Radio Button to select icons option
        if(testFont.canDisplay(valuesIcon[0].charAt(0))) {	//If the device can display symbols, the symbols option will be displayed as option 1, else Numbers option will show
            option1.setText("Symbols");
            values = valuesIcon;
        } else {
            option1.setText("Numbers");
            values = valuesNumbers;
        }
        option1.setSelected(true);						
        radioPanel.add(option1);
        JRadioButton option2 = new JRadioButton();  //Add Radio Button to select icons option
        option2.setText("Letters");
        radioPanel.add(option2);
        ButtonGroup bgroup = new ButtonGroup();    //Group the radio buttons so you can only click one at a time
        bgroup.add(option1);
        bgroup.add(option2);

        //Action Performed when radio buttons Symbols is selected
        option1.addActionListener(new ActionListener() {
            // Action performed when user clicks a tile it shows the value assigned to it
            public void actionPerformed(ActionEvent e) {
            	//Sets option 1 values for buttons to symbols or numbers depending on what is available on that device
                if(testFont.canDisplay(valuesIcon[0].charAt(0))) {
                    values = valuesIcon;
                } else {
                    values = valuesNumbers;
                }
                shuffleValues();
            }
        });

        //Action Performed when radio buttons Symbols is selected
        option2.addActionListener(new ActionListener() {
            // Action performed to switch icons assigned to tiles
            public void actionPerformed(ActionEvent e) {
                values=valuesLetters;
                shuffleValues();
            }
        });

        //JLabel with # of mistakes left
        JPanel mistakePanel = new JPanel(new GridLayout(2,1)); //Set up GridLayout panel to place labels for mistakes left with 2 rows and 1 column. This panel is on top of the gridBagLayout panel
        setGridBagLayoutProperties (GridBagConstraints.NONE, 1,5,2,c, panel, mistakePanel);
        JLabel mistakesLeft = new JLabel("Mistakes left:");                   //The words "Mistakes Left:" on a label added to the panel with grid layout
        mistakesLeft.setFont(new java.awt.Font("Arial", 1, 12));
        mistakePanel.add(mistakesLeft);
        JLabel numMistakesLeft = new JLabel("15",JLabel.RIGHT);               //The number of mistakes left displayed underneath the "Mistakes Left:" JLabel in GridLayout panel
        numMistakesLeft.setFont(new java.awt.Font("Arial", 1, 12));
        mistakePanel.add(numMistakesLeft);

        //Error message if symbols aren't supported on windows
        JPanel errorMessagePanel = new JPanel(new GridLayout(2,1));
        setGridBagLayoutProperties (GridBagConstraints.HORIZONTAL, 1,5,3,c,panel, errorMessagePanel);
        JLabel characterIssueLn1 = new JLabel ();
        characterIssueLn1.setFont(new java.awt.Font("Arial",1,10));
        errorMessagePanel.add(characterIssueLn1);
        JLabel characterIssueLn2 = new JLabel ();
        characterIssueLn2.setHorizontalAlignment(0);
        characterIssueLn2.setFont(new java.awt.Font("Arial",1,10));
        errorMessagePanel.add(characterIssueLn2);

        //Show message to user that symbols have been changed to numbers
        if(!testFont.canDisplay(valuesIcon[0].charAt(0))) {
            characterIssueLn1.setText("Symbols changed");
            characterIssueLn2.setText("to numbers");
        }

        int p = 0; //To create each new tile
        //Create tiles in a loop
        for(int i = 0; i < x; i++) { //vertical increment for creating tiles (buttons)

            for(int j = 0; j < y; j++) { //horizontal increment for creating tiles (buttons)
                //Tile creation and formatting (increments to create a grid)
                JButton tile1 = new JButton();
                //tile1.setFont(new java.awt.Font("Wingdings", 0, 24)); // NOI18N
                tile1.setFont(new java.awt.Font("Arial Rounded MT Bold", 0, 40));
                tile1.setText("");
                tile1.setPreferredSize(new Dimension(80, 80));
                tile1.setForeground(Color.DARK_GRAY);
                setGridBagLayoutProperties (GridBagConstraints.HORIZONTAL, 1,j,i+2,c, panel, tile1);
                tiles[p++] = tile1;

                // Action performed when user clicks a tile it shows the value assigned to it
                tile1.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {

                        //If you click a tile, game starts and you cannot switch values(radio buttons)
                        option1.setEnabled(false);
                        option2.setEnabled(false);

                        //Search for which tiles were clicked and store
                        for(int k = 0; k < tiles.length; k++) {
                            //When the tile clicked is matched with where it is in the array show the value on that block
                            if(tiles[k] == tile1) {

                                tile1.setText(values[k]); //Show the value assigned to the tile

                                //Start clock after clicking 1st button
                                if(timerClock == -1) {
                                    timerClock = new Date().getTime();
                                }

                                //Can't click same tile twice in a row (this makes sure that it doesn't count as the next click)
                                if(currentClick == k)
                                    break;

                                previousClick = currentClick; //Whatever stored in current click stores in previousClick
                                currentClick = k;          //Whatever currently clicked stored in currentClick

                                //If not = to -1 for both current and previously clicked tile that means 2 tiles clicked
                                if(previousClick != -1 && currentClick != -1) {

                                    /*If current and previous clicked have equal values assigned then match occurred and they stay disabled
                                     * If they don't have equal values then the tiles get enabled again and become blank again.
                                     * Also update the progress bar and the progress every time there is a match.*/
                                    if(values[previousClick].equals(values[currentClick])) {
                                        tiles[previousClick].setEnabled(false);
                                        tiles[currentClick].setEnabled(false);
                                        previousClick = currentClick = -1;
                                        progress++;
                                        progressBar.setValue(progress);

                                        if(progress == x*y/2) { //If game complete display a Dialog pop-up
                                            JLabel label = new JLabel();
                                            label.setText("Great job! You finished.");
                                            label.setHorizontalAlignment(SwingConstants.CENTER);
                                            JOptionPane.showMessageDialog(frame, label,"",JOptionPane.PLAIN_MESSAGE);
                                        }

                                    } else { //If tiles don't match wait 300 milliseconds and hide values for tiles
                                        Timer delayedRefresh = new Timer(300, new ActionListener() {
                                            public void actionPerformed(ActionEvent evt) {
                                                tiles[previousClick].setText("");
                                                tiles[currentClick].setText("");
                                                previousClick = currentClick = -1; //After 2 tiles have been selected and compared, set back to -1 (to show that nothing is selected anymore)
                                            }
                                        });
                                        delayedRefresh.setRepeats(false);  //Stops timer from repeating so that values get hidden (so it does it once per loop)
                                        delayedRefresh.start(); //start timer

                                        //MAX MISTAKES is 15. Cannot go over that.
                                        counter++; //counter for how many mistakes made (so pop up if too many mistakes made)
                                        numMistakesLeft.setText(""+(15-counter)+""); //update the number of mistakes left
                                        if(counter>=16) {                    //If too many wrong matches pop-up (max 15)
                                            numMistakesLeft.setText("---");          //set mistakes to fail
                                            timerClock = -1;                  //Stop the timer from running;

                                            //Add a pop-up which shows the failure message and add a label to center the message on it.
                                            JLabel labelWrong = new JLabel();  //Pop up showing failure message
                                            labelWrong.setText("You have failed. You cannot make more than 15 mistakes.");
                                            labelWrong.setHorizontalAlignment(SwingConstants.CENTER);
                                            JOptionPane.showMessageDialog(frame, labelWrong,"",JOptionPane.PLAIN_MESSAGE);
                                            timer.setText("Fail");

                                            //Disable the tiles so user must click the reset button
                                            for(int i=0; i < ((x*y)); i++) {
                                                tiles[i].setEnabled(false);
                                            }
                                            break;
                                        }
                                    }
                                }
                                break;
                            }

                        }
                    }
                });
            }
        }

        //Show a resetButton to start a new game (restart timer, reshuffle values)
        JButton resetButton = new JButton();
        resetButton.setFont(new java.awt.Font("Lao Sangam MN", 0, 20));
        resetButton.setText("RESET");
        setGridBagLayoutProperties (GridBagConstraints.NONE, 1,5,5,c, panel, resetButton);

        //Action performed when user clicks a tile it shows the value assigned to it
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option1.setEnabled(true); //Enable the radio buttons to switch icons now that the game restarted
                option2.setEnabled(true);

                numMistakesLeft.setText("15");

                //Store which 2 buttons were selected
                previousClick = -1;
                currentClick = -1;
                progress = 0;
                progressBar.setValue(0);
                shuffleValues();

                for(int i = 0; i < tiles.length; i++) {
                    tiles[i].setText("");
                    tiles[i].setEnabled(true);
                }
                //reset timer
                timerClock = -1;  //-1 means that the timer hasn't started (when you click first tile it begins)
                timer.setText("0 seconds");

                //reset error counter
                counter=0;
            }
        });

        //Defaults
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        add(panel);
        frame.setPreferredSize(new Dimension(450, 450));   //Set size of JFrame
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
            java.util.logging.Logger.getLogger(MatchingGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MatchingGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MatchingGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MatchingGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //Start
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MatchingGame().setVisible(true);
            }
        });

    }
}