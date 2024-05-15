import javax.swing.*; //All the different imports we have swing which I'm using to make the GUI
import java.awt.event.ActionListener; //Listener for the buttons in the GUI
import java.awt.event.ActionEvent; //Handles the events in the GUI
import java.io.BufferedWriter; //Used to write to the file
import java.io.FileWriter; //Used to write to the file
import java.util.ArrayList; //Used to store the flashcards
import java.awt.Font; //Used to set the font, boldness, size, etc.
import java.awt.BorderLayout; //Used to set the layout, ie how big the frame is
import java.util.Iterator; //Used to iterate through the flashcards
import java.io.File; //Used to read the file we want to save or upload

public class FlashcardApp { //Class representing the app
    private JTextArea questions; //Text area for the questions, use this to have a place to type in the questions
    private JTextArea answers; //Text area for the answers, this is where the user will be able to see the answers
    private ArrayList<Flashcard> cardList; //List of flashcards, used array list because we can add and remove easily
    // and since I chose to have the user add the flashcards themselves and then save it as a file, they can handle the
    // uploading process themselves, so we can just store the flashcards in an array list since once we access the list it
    // will not change and we can just iterate through it. Had I given the user the ability to edit the flashcards after
    // they are created, I would probably have used a linked list instead of an array list
    private JFrame frame; //Frame for the app, used to create the layout

    public FlashcardApp() {
        // Setting up the UI for the app
        frame = new JFrame("Flash Card");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        // Using JPanel to create a layout

        // Setting the font to Times New Roman and bold
        // because high school teachers swore that's all we could use in college,
        // also made it bold to make it stand out
        Font greatFont = new Font("Times New Roman", Font.BOLD, 20);
        questions = new JTextArea(6, 20);
        questions.setLineWrap(true); // This makes the text wrap, so it does not run off the screen
        questions.setWrapStyleWord(true); // This makes the text wrap when there is a space
        questions.setFont(greatFont); // Setting the font


        // Questions area + scroll bar
        JScrollPane questionsJScrollPane = new JScrollPane(questions); // Scroll pane so we can scroll the text if the card has a lot of words
        questionsJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); // Scroll bar allows you to scroll vertically
        questionsJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // no need to scroll horizontally since the text is wrapped


        //Answers are + scroll bar, set up same way as questions
        answers = new JTextArea(6, 20);
        answers.setLineWrap(true);
        answers.setWrapStyleWord(true);
        answers.setFont(greatFont);

        // JScrollPane so we can scroll the text if the card has a lot of words only vertically same as questions
        JScrollPane answerJScrollPane = new JScrollPane(answers);
        answerJScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        answerJScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        // Button to go to the next card to keep studying
        JButton nextButton = new JButton("Next Card");

        cardList = new ArrayList<>(); // Create an array list to store the flashcards

        // Labels, gotta have those
        JLabel questionsLabel = new JLabel("Question");
        JLabel answersLabel = new JLabel("Answer");

        // Adding the buttons and those labels the main panel
        mainPanel.add(questionsLabel);
        mainPanel.add(questionsJScrollPane);
        mainPanel.add(answersLabel);
        mainPanel.add(answerJScrollPane);
        mainPanel.add(nextButton);
        nextButton.addActionListener(new NextCardListener());

        // Menu Bar, this is where I am storing the option to save or create a new deck of flashcards
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem saveMenuItem = new JMenuItem("Save");


        // Adding the menu items
        fileMenu.add(newMenuItem);
        fileMenu.add(saveMenuItem);

        menuBar.add(fileMenu);

        // Add event listeners, so the user can save or create a new deck
        newMenuItem.addActionListener(new NewMenuItemListener());
        saveMenuItem.addActionListener(new SaveMenuListener());

        frame.setJMenuBar(menuBar);

        // Add the main panel to the frame
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel); // put frame in the center
        frame.setSize(500, 600); // size of frame
        frame.setVisible(true); // show the frame
    }

    public static void main(String[] args) {  // run this thing, just a heads up... it works
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FlashcardApp();
            }
        });
    }

    // Listener for the Next Card button this class is used to have the user create a flashcard ie question and answer, which is
    // what the getText method does, and then add it to our array... I know really complicated, then I added a little print statement
    // to make sure that it actually was adding the cards and the array list was growing like a weed, which it does so that's good
    // and then capped it all off with clear card so that when we slam that button it clears the question and answer areas
    class NextCardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Flashcard card = new Flashcard(questions.getText(), answers.getText());
            cardList.add(card);
            System.out.println("Size of cardList: " + cardList.size());
            clearCard();
        }
    }

    // Listener for the New menu item, again added this to create a new deck of flashcards used print statements to make sure it worked
    class NewMenuItemListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("New button is pressed");
        }
    }

    // Listener for the Save menu item, so above we saved the flashcards to an array, this class is used to then save that
    // array to a file so we can then reaccess it later
    class SaveMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Flashcard card = new Flashcard(questions.getText(), answers.getText());
            cardList.add(card);

            // this the part where we save the file
            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(frame);
            saveFile(fileSave.getSelectedFile());
        }
    }

    // Clear the question and answer fields, used in the next card listener class
    private void clearCard() {
        questions.setText("");
        answers.setText("");
        questions.requestFocus();
    }

    // So this is the actual logic behind the saveFile method, that I explained above, here I use the BufferedWriter
    // to write to the characters from the cards to the file we are saving it to, then iterate through the Flashcard array using
    // the iterator, then we write the question and answer to the file using the write method and separate the question and answer
    // with a / so we can store them uniquely, this will be important when we upload the file, then we can close the writer since the
    // q and a's are uploaded to the file. I also added some exception handling so if we have an error we can see it, and then I would
    // know where the error is so I could fix it
    private void saveFile(File selectedFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));

            Iterator<Flashcard> cardIterator = cardList.iterator();
            while (cardIterator.hasNext()) {
                Flashcard card = cardIterator.next();
                writer.write(card.getQuestion() + "/");
                writer.write(card.getAnswer() + "\n");
            }
            writer.close();
        } catch (Exception e) {
            System.out.println("File Error");
            e.printStackTrace();
        }
    }
}

