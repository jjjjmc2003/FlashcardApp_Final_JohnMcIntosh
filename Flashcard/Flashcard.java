
//Prime example of peak encapsulation use in Java, aka OOP in action, meaning I learned something
public class Flashcard {

    private String question;

    //Getters and setters for questions

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    private String answer;
    //Getters and setters for answers

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


//Constructor for questions and answers

    public Flashcard(String q, String a) {
        question = q;
        answer = a;
    }
}
