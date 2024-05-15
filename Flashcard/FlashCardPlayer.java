// so this is now where we can start doing the cool stuff as in actually use the flashcard, the Flashcard App
// was pretty much centered around just retrieving the questions and answers the user wanted to study and then giving htem the
// ability to save them to a file. Here we then give the user the ability to access the file they saved upload it here, and then
// study in an old-fashioned style. In this portion I use a lot of polyphormism to make the code look cleaner and more readable

import javax.swing.*; // imports again
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

public class FlashCardPlayer { // This is where the magic happens

    public JTextArea display; // Displays the text
    public JButton showAnswer; //Button to show answer
    public JFrame frame; // The frame for the player
    public Iterator<Flashcard> cardIterator; //Reads the cards
    public Flashcard currentCard; // Reads one card at a time

    private boolean osShowAnswer; // Toggles the answer

    ArrayList<Flashcard> cardList = new ArrayList<>(); // The list of cards


    //Below is the class for the player, up top we pretty much just create the frame and edit it
    // to make it look nice
    public FlashCardPlayer() {
        frame = new JFrame("Flash Card Player");  // creates the frame
        JPanel mainPanel = new JPanel();               // creates the panel
        Font mfont = new Font("Times New Roman", Font.BOLD, 30);  // sets the font, again TNR

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // closes the frame when exited

        display = new JTextArea(10, 20);  // creates the text area, and size
        display.setFont(mfont);                         // we display our font

       //Create the menu bar
        JScrollPane questionJScrollPane = new JScrollPane(display);
        questionJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        questionJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Show Answer button creation + listener + add it to panel
        showAnswer = new JButton("Show Answer");
        showAnswer.addActionListener(new NextCardListener());


        mainPanel.add(questionJScrollPane);
        mainPanel.add(showAnswer);


        // Create the menu bar and offer button to load the card set
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem loadMenuItem = new JMenuItem("Load card set");
        loadMenuItem.addActionListener(new OpenMenuListener());

        // Add the load card set to the file menu
        fileMenu.add(loadMenuItem);
        menuBar.add(fileMenu);

        // Add the menu bar to the frame, which adds file and load card set, and set the frame
        // size
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(1000, 500);
        frame.setVisible(true);
    }
//This line here essentially makes everything run smoothly by creating a thread and making sure
// it runs on the swing thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlashCardPlayer::new);
    }

    // listener for next card/show answer, here I just broke down what each line means in basic english
    // this is probably the most important segment of code because this is how it runs from card
    // to card, so basically I have a bunch of if else statements that tell the program what to do
    // based on whether an answer or question are showing, and if there are more cards to stop you
    // from clicking next question infinitely
    // Oh quick side note the next card and show answer button are the same button rather than
    // making two I made it harder on myself by changing the text of the button itself depending
    // on the status of the card, like if an answer was showing the text had to output "Next Card"
    // and if the question was showing it had to output "Show Answer", again hindsight probably would
    // have just made two separate buttons and disabled them when the other one was clicked,but I feel
    // like it looks cleaner, this is also my use of polyphormism because I implement the Action Listener
    // and then create the action performed method to tie in all the different buttons that I have to ensure
    // that I can go from question to question with ease
    class NextCardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!osShowAnswer) { // If it's not showing the answer
                display.setText(currentCard.getAnswer()); // Show the answer
                showAnswer.setText("Next Question"); // Change button text to next question
                osShowAnswer = true; // Toggle osShowAnswer
            } else { // If it's showing the answer
                if (cardIterator.hasNext()) { // If there is a next card
                    showNextCard(); // Move to the next card
                    display.setText(currentCard.getQuestion()); // Show the next question
                    showAnswer.setText("Show Answer"); // Change button text
                    osShowAnswer = false; // Toggle osShowAnswer
                } else { // If there are no more cards
                    showAnswer.setEnabled(false); // Disable button
                    display.setText("That was the last card, consider \nthe study session over\n\nGo Take a Nap"); // Display the message
                }
            }
        }
    }

    // Listener for the open menu, allows user to open a file ie their flashcard set
    class OpenMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            JFileChooser fileOpen = new JFileChooser();
            fileOpen.showOpenDialog(frame);
            loadFile(fileOpen.getSelectedFile());
            osShowAnswer = false;
        }
    }

    // this is where the file is loaded into the card list, so basically its the same as in FlashCard
    // App, except we do the opposite. Rather than load the card into a file, we are loading the info
    // in the file onto the cards
    private void loadFile(File selectedFile) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile)); // Reads the file
            String line = null; // Sets the line to null

            while ((line = reader.readLine()) != null) { // Reads each line
                makeCard(line); // Makes the card by adding the lines in the file to the card list
            }
            reader.close(); // Closes the reader
        } catch (Exception e) { // set up a catch to try and catch any errors and handle them properly
            System.out.println("File Error");
            e.printStackTrace();
        }
        cardIterator = cardList.iterator(); // Set the iterator to the card list
        showNextCard(); // Shows the card, more logic on this below
    }

    //So here is how we make the individual cards, remember when I said the "/" would be important
    // in FlashcardApp class, well here it is. Here I use a string tokenizer to break the string up
    // into the question and answer by using / as the divider or delimiter, so when it iterates through
    // the string it will break up the string into the question and answer, read it and create a card
    private void makeCard(String lineToParse) {
        StringTokenizer result = new StringTokenizer(lineToParse, "/"); // Differentiates question from answer in file since we use / to differentiate between Q&A
        if (result.countTokens() >= 2) { // Ensure there are at least two tokens as in question/answer
            Flashcard card = new Flashcard(result.nextToken(), result.nextToken()); // Create the card using those two tokens
            cardList.add(card);
            System.out.println("Made a card"); // used this to make sure that the card was made when I uploaded a file... it does, so if you make 5 cards you will see this message 5 times
        }
    }


    // This is where the next card is displayed, basically just an if statement that
    // checks if the iterator has a next card, if it does it will move to the next card
    private void showNextCard() {
        if (cardIterator.hasNext()) {
            currentCard = cardIterator.next();
            display.setText(currentCard.getQuestion());
            showAnswer.setText("Show Answer");
            osShowAnswer = false; // Show the question first
        } else {
            display.setText("That was the last card");
            showAnswer.setEnabled(false);
        }
    }
}
