/*
 * Hengmen
 * The Hengmen game coded in java
 * Author: Michael Muehlberger
 * Last Change: 01.02.2023
 */

import java.io.*;
import java.util.*;

public class Hangmen {

    public static void main(String[] args) {

        String filename = "";

        ArrayList<String> textfileList = new ArrayList();
        ArrayList<Character> wordLetters = new ArrayList();
        ArrayList<Character> hiddenLetters = new ArrayList();
        ArrayList<Character> missesChar = new ArrayList();

        Set<Character> lettersAll = new HashSet<Character>();
        Set<Character> letters = new HashSet<Character>();

        Scanner scanner = new Scanner(System.in);
        int wordlist_size = 0;
        int wordCount = 1;
        int misses = 0;
        int wordLength = 0;
        String word = "";
        String guess = "";
        char guessChar = ' ';
        boolean errorMsg = false;
        int wins = 0;

        resetLetters(lettersAll);

        try {
            filename = args[0];
        } catch (ArrayIndexOutOfBoundsException | NoSuchElementException e) {
            System.out.println("Error: No file name given!");
            errorMsg = true;
        }

        if (errorMsg == false) {

            try (BufferedReader inBuffer = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = inBuffer.readLine()) != null) {

                    if (line.equals("") == false) {
                        textfileList.add(line);
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error: File not found!");
                errorMsg = true;
            } catch (IOException | IndexOutOfBoundsException e) {
                System.out.println("Error: Could not read file!");
                errorMsg = true;
            }
        }

        if (errorMsg == false) {
            errorMsg = checkFile(textfileList, lettersAll);
        }

        if (errorMsg == false) {
            wordlist_size = textfileList.size();
            printline('b');
            System.out.printf("HANGMEN (%d Word(s))\n", wordlist_size);
            printline('t');
            Collections.shuffle(textfileList);

        }

        while ((wordCount <= wordlist_size) && errorMsg == false) {

            resetLetters(letters);
            wordLetters.clear();
            hiddenLetters.clear();
            missesChar.clear();

            misses = 0;

            System.out.printf("Word #%d:\n\n\n", wordCount);

            word = textfileList.get(wordCount - 1);
            wordLength = word.length();

            for (int i = 0; i < wordLength; i++) {

                wordLetters.add(i, word.charAt(i));
                hiddenLetters.add(i, '_');

            }


            while (errorMsg == false) {

                printMenu(misses, hiddenLetters, missesChar);
                System.out.print("Next guess: ");

                try {
                    guess = scanner.nextLine();
                } catch (NoSuchElementException e) {
                    errorMsg = true;
                    break;
                }

                System.out.println("");

                if ((guess.length() == 1) && (lettersAll.contains(guess.charAt(0)) == true && errorMsg == false)) {
                    guessChar = guess.charAt(0);

                    if (letters.contains(guessChar)) {
                        removeChar(letters, guessChar);

                        if (guessLetter(guessChar, wordLetters, hiddenLetters) == false) {
                            missesChar.add(guessChar);
                            misses++;
                            printHangmen(misses);

                            if (misses == 11) {
                                printMenu(misses, hiddenLetters, missesChar);

                                System.out.println("\nYOU LOSE!");
                                wordCount++;

                                break;
                            }
                        } else {
                            printHangmen(misses);

                            if (wordLetters.equals(hiddenLetters)) {
                                printMenu(misses, hiddenLetters, missesChar);

                                System.out.println("\nYOU WIN!");
                                wordCount++;
                                wins++;

                                break;
                            }

                        }

                    } else {
                        System.out.println("Character already guessed!\n");
                        printHangmen(misses);
                    }


                } else {
                    if (guess.length() == 1) {
                        System.out.println("Invalid character!\n");
                        printHangmen(misses);

                    } else {
                        System.out.println("Invalid input!\n");
                        printHangmen(misses);
                    }
                }

            }

            if (errorMsg == false && (wordCount > wordlist_size)) {
                printline('b');
                System.out.printf("WINS: %d/%d\n", wins, wordlist_size);
            } else {
                printline('t');
            }

        }
    }

    //checks if textfile is empty of corrupted
    static boolean checkFile(ArrayList<String> textfile, Set<Character> allowedLetters) {

        int wordAmount = textfile.size();
        int CharacterAmount = 0;
        String inspectWord = "";

        if (wordAmount == 0) {
            System.out.println("Error: Empty file!");
            return true;
        } else {
            for (int i = 0; i < wordAmount; i++) {

                inspectWord = textfile.get(i);
                CharacterAmount = inspectWord.length();

                for (int m = 0; m < CharacterAmount; m++) {

                    if (allowedLetters.contains(inspectWord.charAt(m)) == false) {

                        System.out.println("Error: Corrupt file!");
                        return true;
                    }
                }
            }
        }

        return false;

    }

    //checks if guessed letter is part of the secret word + replaces hidden word with letters
    static boolean guessLetter(char guess, ArrayList<Character> wordLetters, ArrayList<Character> hiddenLetters) {

        int wordLength = wordLetters.size();

        String guessString = String.valueOf(guess);

        char guessUpC = guessString.toUpperCase().charAt(0);
        char guessLoC = guessString.toLowerCase().charAt(0);

        if (wordLetters.contains(guessLoC) || wordLetters.contains(guessUpC)) {

            for (int i = 0; i < wordLength; i++) {

                if (wordLetters.get(i).equals(guessLoC)) {
                    hiddenLetters.set(i, wordLetters.get(i));
                }

                if (wordLetters.get(i).equals(guessUpC)) {
                    hiddenLetters.set(i, wordLetters.get(i));
                }
            }

            return true;

        } else {

            return false;

        }
    }

    //removes chars from letter list to see whether letter was already guessed
    static void removeChar(Set<Character> letters, char guessChar) {

        String removeChar = String.valueOf(guessChar);

        Character a = removeChar.toUpperCase().charAt(0);
        Character b = removeChar.toLowerCase().charAt(0);

        letters.remove(a);
        letters.remove(b);
    }

    //prints the menu
    static void printMenu(int misses, ArrayList<Character> hiddenLetters, ArrayList<Character> missedChars) {

        int wordLenth = hiddenLetters.size();

        System.out.print("Word:");

        for (int i = 0; i < wordLenth; i++) {
            System.out.printf(" %c", hiddenLetters.get(i));
        }

        System.out.println("");
        System.out.printf("Misses (%d/11)", misses);

        for (int j = 0; j < missedChars.size(); j++) {

            String charS = missedChars.get(j).toString().toUpperCase();

            if (j == 0) {

                System.out.printf(": %s", charS);

            } else if (j > 0) {
                System.out.printf(", %s", charS);
            }
        }

        System.out.println();


    }

    //resets the letter sets by putting all letters back inside
    static void resetLetters(Set<Character> letters) {

        letters.clear();

        for (char j = 'a'; j <= 'z'; j++) {
            letters.add(j);
        }

        for (char j = 'A'; j <= 'Z'; j++) {
            letters.add(j);
        }

    }

    //prints a bold or thick line
    static void printline(char line) {

        for (int i = 1; i <= 80; i++) {

            if (line == 'b') { //bold line
                System.out.print("=");

            } else if (line == 't') // thin line
            {
                System.out.print("-");
            }
        }
        System.out.println("");

    }

    //prints the hangmen
    static void printHangmen(int num) {

        switch (num) {

            case 0:
                System.out.println("");
                break;

            case 1:
                System.out.println(

                        "===\n"
                );
                break;

            case 2:
                System.out.println(

                        " |\n" +
                                " |\n" +
                                " |\n" +
                                " |\n" +
                                "===\n"
                );
                break;
            case 3:
                System.out.println(

                        "  ____\n" +
                                " |\n" +
                                " |\n" +
                                " |\n" +
                                " |\n" +
                                "===\n"
                );
                break;
            case 4:
                System.out.println(

                        "  ____\n" +
                                " |/\n" +
                                " |\n" +
                                " |\n" +
                                " |\n" +
                                "===\n"
                );
                break;
            case 5:
                System.out.println(

                        "  ____\n" +
                                " |/   |\n" +
                                " |\n" +
                                " |\n" +
                                " |\n" +
                                "===\n"
                );
                break;
            case 6:
                System.out.println(

                        "  ____\n" +
                                " |/   |\n" +
                                " |    O\n" +
                                " |\n" +
                                " |\n" +
                                "===\n"
                );
                break;
            case 7:
                System.out.println(

                        "  ____\n" +
                                " |/   |\n" +
                                " |    O\n" +
                                " |    |\n" +
                                " |\n" +
                                "===\n"
                );
                break;
            case 8:
                System.out.println(

                        "  ____\n" +
                                " |/   |\n" +
                                " |    O\n" +
                                " |    |\n" +
                                " |   /\n" +
                                "===\n"
                );

                break;
            case 9:
                System.out.println(

                        "  ____\n" +
                                " |/   |\n" +
                                " |    O\n" +
                                " |    |\n" +
                                " |   / \\\n" +
                                "===\n"
                );

                break;
            case 10:
                System.out.println(

                        "  ____\n" +
                                " |/   |\n" +
                                " |    O\n" +
                                " |   /|\n" +
                                " |   / \\\n" +
                                "===\n"
                );
                break;
            case 11:
                System.out.println(

                        "  ____\n" +
                                " |/   |\n" +
                                " |    O\n" +
                                " |   /|\\\n" +
                                " |   / \\\n" +
                                "===\n"
                );
                break;
        }
    }
}