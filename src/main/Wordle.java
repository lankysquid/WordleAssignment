package main;

import java.io.*;
import java.util.*;
import java.text.Normalizer;


public class Wordle {

    // Declaring variables and arrayLists
    protected String chosenWordListFileName;
    protected String chosenWord;
    protected List<String> chosenWordList;
    protected List<String> greenLetters = new ArrayList<>();
    protected List<String> yellowLetters = new ArrayList<>();
    protected List<String> greyLetters = new ArrayList<>();
    protected List<String> userGuesses = new ArrayList<>();
    protected String result;
    protected String youWonMessage;
    protected String youLostMessage;

    // Declaring the background colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_GREY_BACKGROUND = "\u001B[100m";
    public static final String ANSI_CLEAR_SCREEN = "\033[H\033[2J";

    public Wordle() {
        chosenWordListFileName = "src/main/dictionary.txt";
        result = "Result: ";
        youWonMessage = "CONGRATULATIONS! YOU WON! :)";
        youLostMessage = "YOU LOST :( THE WORD CHOSEN BY THE GAME IS: ";
    }
    // METHODS

    // print instructions
    public void printInstructions() {
        System.out.println("The game has chosen a 5-letter word for you to guess.");
        System.out.println("You have 6 tries. In each guess, the game will confirm which letters the chosen word and the guessed word have in common:");
        System.out.println("- Letters highlighted in " + ANSI_GREEN_BACKGROUND + "green" + ANSI_RESET + " are in the correct place.");
        System.out.println("- Letters highlighted in " + ANSI_YELLOW_BACKGROUND + "yellow" + ANSI_RESET + " appear in the chosen word but in a different spot.");
        System.out.println("- Letters highlighted in " + ANSI_GREY_BACKGROUND + "grey" + ANSI_RESET + " do not appear in the chosen word.");
    }

    // ask the user for their first word
    public void askForFirstGuess() {
        System.out.println();
        System.out.println("Please write down your first guess:");
    }

    // verify the validity of the user word by length and check against available options
    public String obtainValidUserWord (List<String> wordList, int index) {
        Scanner myScanner = new Scanner(System.in); // Create a Scanner object
        String userWord = myScanner.nextLine();     // Read user input
        userWord = userWord.toLowerCase();          // covert to lowercase

        // check the length of the word and if it exists
        while ((userWord.length() != 5) || !(wordList.contains(userWord))) {
            if ((userWord.length() != 5)) {
                System.out.println("The word " + userWord + " does not have 5 letters.");
            } else if (noGreyLetters(greyLetters, userWord)) {
                System.out.println("The word " + userWord  + " contains letters not allowed.");
            }
            else {
                System.out.println("The word " + userWord + " is not in the word list.");
            }
            // Ask for a new word
            System.out.println("Please, submit a new 5-letter word.");
            System.out.print((index + 1) + ") ");
            userWord = myScanner.nextLine();
        }
        //myScanner.close();
        return userWord;
    }

    public boolean noGreyLetters(List<String> greyLetters, String word) {
        for (String c : greyLetters) {
            if (word.contains(c)) return false;
        }
        return true;
    }

    public void printColoredKeyboard(List<String> greenLetters, List<String> yellowLetters, List<String> greyLetters) {
        String qwerty = "Q W E R T Y U I O P\n" +
                        " A S D F G H J K L\n"  +
                        "  Z X C V B N M";
        String output = "";
        for (int i = 0; i < qwerty.length(); i++) {
            String letter = qwerty.substring(i,i +1);
            if (greenLetters.contains(letter)) {
                output += ANSI_GREEN_BACKGROUND + letter + ANSI_RESET;
            } else if (yellowLetters.contains(letter)) {
                output += ANSI_YELLOW_BACKGROUND + letter + ANSI_RESET;
            } else if (greyLetters.contains(letter)) {
                output += ANSI_GREY_BACKGROUND + letter + ANSI_RESET;
            } else {
                output += letter;
            }
        }
        System.out.println("\n\n===================");
        System.out.println(output);
        System.out.println("===================");
    }


    public void printDefinitionLink (String randomChosenWord) { // prints the link to the dictionary definition of the chosen word
        System.out.println("The word's definition: https://www.merriam-webster.com/dictionary/" + randomChosenWord);
    }



    // Read the dictionary and assemble the dictionary arrayList from which to choose the random chosen word
    public List<String> readDictionary() {
        List<String> wordList = new ArrayList<>();
        try {
            File file = new File(chosenWordListFileName);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                wordList.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading in the dictionary.");
            e.printStackTrace();
        }
        return wordList;

    }

    // get a random word from the dictionary arraylist
    public String getRandomWord(List<String> wordList) {
        Random rand = new Random(); //instance of random class
        int upperbound = wordList.size();
        //generate random values from 0 to arrayList size
        int int_random = rand.nextInt(upperbound);
        return wordList.get(int_random);
    }

    // method that replaces a char in a string at a specific index

    public ArrayList<String> wordToList(String word) {
        ArrayList<String> characterList = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            characterList.add(word.substring(i,i+1));
        }
        return characterList;
    }

    public String analyzeUserGuess(String chosenWord, String userGuess) {
        String reColoredWord = "";
        ArrayList<String> chosenList = wordToList(chosenWord);
        ArrayList<String> userList = wordToList(userGuess);

        for (int i = 0; i < userList.size(); i++) {
            String letter = userList.get(i);
            int letterIndex = chosenList.indexOf(letter);
            if(letterIndex >= 0) {
                if (letterIndex == i) {
                    reColoredWord += ANSI_GREEN_BACKGROUND + letter + ANSI_RESET;
                    greenLetters.add(letter.toUpperCase());
                    yellowLetters.removeIf(s -> greenLetters.contains(s));
                    chosenList.set(letterIndex, "");
                } else {
                    reColoredWord += ANSI_YELLOW_BACKGROUND + letter + ANSI_RESET;
                    yellowLetters.add(letter.toUpperCase());
                    chosenList.set(letterIndex, "");
                }

            } else {
                reColoredWord += ANSI_GREY_BACKGROUND + letter + ANSI_RESET;
                greyLetters.add(letter.toUpperCase());
            }
        }
        return reColoredWord;
    }
    public void printGuesses() {
        System.out.println(ANSI_CLEAR_SCREEN);
        System.out.flush();
        int i = 1;
        for (String guess : userGuesses) {
            System.out.println( i++ + ") " + guess);
        }
    }

    public void loopThroughSixGuesses(List<String> wordList) {

        for (int j = 0; j < 6; j++) {
            System.out.print((j + 1) + ") ");
            String userWord = obtainValidUserWord(wordList, j);

            // check if the user won: the userWord is the same as chosenWord
            if (userWord.equals(chosenWord)) {
                System.out.println((result + ANSI_GREEN_BACKGROUND + userWord.toUpperCase() + ANSI_RESET));
                System.out.println();
                System.out.println(youWonMessage);
                System.out.println();
                printDefinitionLink(chosenWord);
                System.exit(0);
            } else {
                String analyzedWord = analyzeUserGuess(chosenWord, userWord);
                userGuesses.add(analyzedWord);

                printColoredKeyboard(greenLetters, yellowLetters, greyLetters);
                printGuesses();
            }
        }
        System.out.println();
        System.out.println(youLostMessage + chosenWord.toUpperCase() + ".");
        System.out.println();
        printDefinitionLink(chosenWord);
    }

    // play method that calls on all other methods.
    public void play () {
        chosenWordList =  readDictionary();
        // Selecting a random word from the dictionary
        chosenWord = getRandomWord(chosenWordList);

        // Instructions to the game
        this.printInstructions();

        // ask the user for the first guess
        this.askForFirstGuess();

        this.loopThroughSixGuesses(chosenWordList);

    }

}

