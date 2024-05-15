import javax.swing.*; // imports again
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

//Importing the FlashCardPlayer class, aka Inheritance, so that we can use the FlashCardPlayer to make
// building the quiz easier
public class FlashCardQuiz extends FlashCardPlayer {

    private JButton startQuizButton; //add in the buttons, and area I need specifically for the quiz
    private JButton checkAnswerButton;
    private JPanel mainPanel;
    private JTextArea answerTextArea;

    private int score; // and score

    //Here we have the constructor of the quiz and call the superclass constructor
    // then I set up the start quiz button, add it to the frame, then set size location etc,
    // of the frame
    public FlashCardQuiz() {
        super();
        mainPanel = new JPanel();
        startQuizButton = new JButton("Start Quiz");
        startQuizButton.addActionListener(new StartQuizListener());
        mainPanel.add(startQuizButton);
        frame.getContentPane().add(mainPanel, BorderLayout.SOUTH);
        frame.setSize(500, 250);
        answerTextArea = new JTextArea(2, 20);
        mainPanel.add(answerTextArea);

        //add in the check answer button, use this to wait for it check the answer
        checkAnswerButton = new JButton("Check Answer");
        checkAnswerButton.addActionListener(new CheckAnswerListener());
        mainPanel.add(checkAnswerButton);
        checkAnswerButton.setEnabled(false);
    }

    //Listener for the start quiz button
    private class StartQuizListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            startQuiz(); // call the start quiz method, logic below
        }
    }

    //Listener for the check answer button
    private class CheckAnswerListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            checkAnswer(); // call the check answer method, logic below
        }
    }

    //This is the logic for the start quiz button, and something I am paticularly proud of
    // so in a similar way to how I loaded in the cards in FlashCardApp, here we load it in
    // again, the quiz specifically, shuffle the cards, iterate through them, display the questions
    // then allow the check answer button to be pressed so the user can check the answer
    private void startQuiz() {
        cardList = new ArrayList<>();
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadFlashcards(selectedFile);
            Collections.shuffle(cardList);
            cardIterator = cardList.iterator();
            score = 0;
            nextQuestion();
            startQuizButton.setEnabled(false);
            checkAnswerButton.setEnabled(true);
        }
    }

    // Load questions and answers from the specified file similar style to FlashCardApp and FlashCardPlayer
    protected void loadFlashcards(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file)); //used buffer reader again to read the file
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("/");
                if (parts.length >= 2) {
                    cardList.add(new Flashcard(parts[0], parts[1])); // Add question and answer to cardList
                }
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error loading quiz questions: " + e.getMessage()); // another error handling exception spot
            e.printStackTrace();
        }
    }

    protected void nextQuestion() { // This is the logic for the next question button, and the check answer button
        if (cardIterator.hasNext()) { // basically just iterates through the cards and displays the next question after checking answer
            currentCard = cardIterator.next();
            display.setText(currentCard.getQuestion());
            answerTextArea.setText("");
        } else {
            JOptionPane.showMessageDialog(frame, "Quiz Finished!\nYour Score: " + score + "/" + cardList.size()); //this is logic for the pop up displaying score
            startQuizButton.setEnabled(true); // allows the use of the start quiz button again
            checkAnswerButton.setEnabled(false); // disable the check answer button
        }
    }

    private void checkAnswer() { // check answer button logic reads answer checks it based off of the answer loaded in the area, and increments the score
        String userAnswer = answerTextArea.getText().trim(); // get the user answer
        if (userAnswer.equalsIgnoreCase(currentCard.getAnswer())) { // if the answer is correct
            score++; // increment the score
        }
        nextQuestion();
    }

    public static void main(String[] args) { // RUN THIS THING
        SwingUtilities.invokeLater(FlashCardQuiz::new);
    }
}
