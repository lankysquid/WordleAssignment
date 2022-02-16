package main;

import java.io.*;
import java.util.*;
public class Wordle {
    // Declaring variables and arrayLists
    protected String secretWordListFileName;
    protected String secretWord;
    protected List<String> secretWordList;
    protected List<String> greenLetters = new ArrayList<>();
    protected List<String> yellowLetters = new ArrayList<>();
    protected List<String> greyLetters = new ArrayList<>();
    protected List<String> userGuesses = new ArrayList<>();
    protected String youWonMessage;
    protected String youLostMessage;

    // Declaring the background colors
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    public static final String ANSI_GREY_BACKGROUND = "\u001B[100m";
    public static final String ANSI_CLEAR_SCREEN = "\033[H\033[2J";

    /**
     * Default constructor that sets simple local variables
     */
    public Wordle() {
        secretWordListFileName = "src/main/dictionary.txt"; // DON'T CHANGE THIS
        youWonMessage = "CONGRATULATIONS! YOU WON! :)"; // You can change this
        youLostMessage = "YOU LOST :( THE WORD CHOSEN BY THE GAME IS: "; // you can change this
    }

    /**
     * Prints basic instructions for the user once at the beginning of the game.
     */
    public void printInstructions() {
        System.out.println("The game has chosen a 5-letter word for you to guess.");
        System.out.println("You have 6 tries. In each guess, the game will confirm which letters the chosen word and the guessed word have in common:");
        System.out.println("- Letters highlighted in " + ANSI_GREEN_BACKGROUND + "green" + ANSI_RESET + " are in the correct place.");
        System.out.println("- Letters highlighted in " + ANSI_YELLOW_BACKGROUND + "yellow" + ANSI_RESET + " appear in the chosen word but in a different spot.");
        System.out.println("- Letters highlighted in " + ANSI_GREY_BACKGROUND + "grey" + ANSI_RESET + " do not appear in the chosen word.");
    }

    // ask the user for their first guess
    public void askForFirstGuess() {
        System.out.println();
        System.out.println("Please write down your first guess:");
    }

    /**
     * Obtains the users guess and validates it against 3 criteria
     * 1) The user's guess has exactly 5 characters
     * 2) The user's guess is within the wordList provided
     * 3) The user's guess does not contain already guessed grey letters
     * For each failure scenario, this method prints a unique error message
     * and prompts the user to input another guess.
     * @param wordList the list of words that the user's word must be in
     * @param index the guess index of the user's input
     * @return a valid user guess
     */
    public String obtainValidUserWord (List<String> wordList, int index) {
        Scanner myScanner = new Scanner(System.in); // Create a Scanner object
        String userWord = myScanner.nextLine().toUpperCase();  // Read user input as UpperCase

        // check the length of the word and if it exists or if it has illegal char
        while ((userWord.length() != 5)
                || !(wordList.contains(userWord.toLowerCase()))
                || containsGreyLetters(greyLetters, userWord)
        ) {
            if ((userWord.length() != 5)) {
                System.out.println("The word " + userWord + " does not have 5 letters.");
            } else if (containsGreyLetters(greyLetters, userWord)) {
                System.out.println("The word " + userWord  + " contains letters not allowed.");
            }
            else {
                System.out.println("The word " + userWord + " is not in the word list.");
            }
            // Ask for a new word
            System.out.println("Please, submit a new 5-letter word.");
            System.out.print((index + 1) + ") ");
            userWord = myScanner.nextLine().toUpperCase();
        }
        return userWord;
    }

    /**
     * Confirms the existence of an already greyed out letter within a string
     * @param greyLetters list of grey letters as strings (you can use arrayList methods)
     * @param word the word to be checked
     * @return true if there are no grey letter in word, otherwise false
     */
    public boolean containsGreyLetters(List<String> greyLetters, String word) {
        for (String c : greyLetters) {
            if (word.contains(c)) return true;
        }
        return false;
    }

    /**
     * Prints a facsimile keyboard with the letters appropriately colored based on user guesses
     * @param greenLetters list of green letters
     * @param yellowLetters list of yellow letters
     * @param greyLetters list of grey letters
     */
    public void printColoredKeyboard(List<String> greenLetters, List<String> yellowLetters, List<String> greyLetters) {
        String qwerty = """
                Q W E R T Y U I O P
                 A S D F G H J K L
                  Z X C V B N M""";
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < qwerty.length(); i++) {
            String letter = qwerty.substring(i,i +1);
            if (greenLetters.contains(letter)) {
                output.append(ANSI_GREEN_BACKGROUND).append(letter).append(ANSI_RESET);
            } else if (yellowLetters.contains(letter)) {
                output.append(ANSI_YELLOW_BACKGROUND).append(letter).append(ANSI_RESET);
            } else if (greyLetters.contains(letter)) {
                output.append(ANSI_GREY_BACKGROUND).append(letter).append(ANSI_RESET);
            } else {
                output.append(letter);
            }
        }
        System.out.println("\n\n===================");
        System.out.println(output);
        System.out.println("===================");
    }

    /**
     * Prints ths dictionary.com link to the definition of the random secrete word
     * at the end of the round
     * @param randomChosenWord the secret word
     */
    public void printDefinitionLink (String randomChosenWord) { // prints the link to the dictionary definition of the chosen word
        System.out.println("The word's definition: https://www.merriam-webster.com/dictionary/" + randomChosenWord);
    }

    /**
     * Reads the dictionary.txt file and adds all the words to a list
     * @return a list of words that the answer could come from
     */
    public List<String> readDictionary() {
        List<String> wordList = new ArrayList<>();
        try {
            File file = new File(secretWordListFileName);
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

    /**
     * Retrieves a random word from the word list
     * @param wordList the list of possible answer words
     * @return a random string from the list
     */
    public String getRandomWord(List<String> wordList) {
        return wordList.get(new Random().nextInt(wordList.size()));
    }

    /**
     * Converts a String to an ArrayList of single character Strings
     * @param word word to be converted
     * @return an ArrayList with each character from word as it's own
     *          String in the list
     */
    public ArrayList<String> wordToList(String word) {
        ArrayList<String> characterList = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            characterList.add(word.substring(i,i+1));
        }
        return characterList;
    }

    /**
     * Converts the user's guessed word into a word with appropriate colors,
     * and adds the letters to the appropriate world list
     * @param secretWord secret word to compare against
     * @param userGuess the user's word to be analyed
     * @return the user's word with the correct collors in the background
     */
    public String analyzeUserGuess(String secretWord, String userGuess) {
        String reColoredWord = "";
        ArrayList<String> secretWordAsList = wordToList(secretWord);
        ArrayList<String> userWordAsList = wordToList(userGuess);
        for (int i = 0; i < userWordAsList.size(); i++) {
            String letter = userWordAsList.get(i);
            int letterIndex = secretWordAsList.indexOf(letter.toLowerCase());
            if(letterIndex >= 0) {
                if (letterIndex == i) {
                    reColoredWord += ANSI_GREEN_BACKGROUND + letter + ANSI_RESET;
                    greenLetters.add(letter.toUpperCase());
                    yellowLetters.removeIf(s -> greenLetters.contains(s));
                    secretWordAsList.set(letterIndex, "");
                } else {
                    reColoredWord += ANSI_YELLOW_BACKGROUND + letter + ANSI_RESET;
                    yellowLetters.add(letter.toUpperCase());
                    secretWordAsList.set(letterIndex, "");
                }

            } else {
                reColoredWord += ANSI_GREY_BACKGROUND + letter + ANSI_RESET;
                greyLetters.add(letter.toUpperCase());
            }
        }
        return reColoredWord;
    }

    /**
     * Recolors the user's guess according to the wrd list
     */
    public String recolorWord(String word, List<String> greyLetters, List<String> greenLetters, List<String> yellowLetters) {
        StringBuilder recoloredWord = new StringBuilder();
        List<String> wordAsList = wordToList(word);
        for (String letter : wordAsList) {
            if (greenLetters.contains(letter)) {
                recoloredWord.append(ANSI_GREEN_BACKGROUND).append(letter).append(ANSI_RESET);
            } else if (yellowLetters.contains(letter)) {
                recoloredWord.append(ANSI_YELLOW_BACKGROUND).append(letter).append(ANSI_RESET);
            } else {
                recoloredWord.append(ANSI_GREY_BACKGROUND).append(letter).append(ANSI_RESET);
            }
        }
        return recoloredWord.toString();
    }

    /**
     * Prints the list of recolored user guesses to the screen
     */
    public void printGuesses() {
        System.out.println(ANSI_CLEAR_SCREEN);
        System.out.flush();
        int i = 1;
        for (String guess : userGuesses) {
            System.out.println( i++ + ") " + guess);
        }
    }

    /**
     * Main gameplay loop that asks for the six guesses from the user
     * @param wordList the list of words with the possible answer
     */
    public void loopThroughSixGuesses(List<String> wordList) {
        for (int j = 0; j < 6; j++) {
            System.out.print((j + 1) + ") ");
            String userWord = obtainValidUserWord(wordList, j);

            // check if the user won: the userWord is the same as chosenWord
            if (userWord.equals(secretWord)) {
                System.out.println((ANSI_GREEN_BACKGROUND + userWord.toUpperCase() + ANSI_RESET));
                System.out.println();
                System.out.println(youWonMessage);
                System.out.println();
                printDefinitionLink(secretWord);
                System.exit(0);
            } else {
                String analyzedWord = analyzeUserGuess(secretWord, userWord);
                userGuesses.add(analyzedWord);

                printColoredKeyboard(greenLetters, yellowLetters, greyLetters);
                printGuesses();
            }
        }
        System.out.println();
        System.out.println(youLostMessage + secretWord.toUpperCase() + ".");
        System.out.println();
        printDefinitionLink(secretWord);
    }

    /**
     * Gameplay method that controls the flow of the game.
     */
    public void play () {
        secretWordList =  readDictionary();
        // Selecting a random word from the dictionary
        secretWord = getRandomWord(secretWordList);
        // Instructions to the game
        this.printInstructions();
        // ask the user for the first guess
        this.askForFirstGuess();
        // main gameplay loop
        this.loopThroughSixGuesses(secretWordList);
    }
}

