import java.io.File;
import java.util.Scanner;


/*
* 1. Write a class to read the file named word, convert each word in it to lowercase, and
* store the word in data structure of your choice
*
* The data structure used to store words is: Trie
* The words are converted to lowercase in the trie's addWords function
*
 * */
public class ReadFile {

    // A method which takes the file name and a trie
    // Opens the file and saves the words in the trie
    static void readFile(String fileName, LexiconTrie trie) {
        File file = new File(fileName);
        Scanner scan = null;
        try {
            scan = new Scanner(file);
        } catch(Exception e) {
            e.printStackTrace();
        }
        if(scan != null) {
            while (scan.hasNextLine()) {
                trie.addWord(scan.nextLine());
            }
        }
    }
}
