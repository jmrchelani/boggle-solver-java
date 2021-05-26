/* This is where you'll write your code to use a canonical graph-traversal algorithm to
 * solve a problem that at first may not seem like it's a graph problem at all.
 *
 * A note: This really is a fun one. If it gets to feel frustrating instead of fun, or if you feel
 * like you're  completely stuck, step back a bit and ask some questions.
 *
 * Spend a lot of time sketching out a plan, and figuring out which data structures might be
 * best for the various tasks, before you write any code at all.
 *
 * If you're not sure about how to approach this as a graph problem, feel free to ask questions.
 * (I won't give you all the answer, but...)
 * -Ben
 *
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BoggleWordFinder {

    // some useful constants
    public static final String WORD_LIST = "words";
    public static final int ROWS = 100;
    public static final int COLUMNS = 100;
    public static final int SEED = 137;

    // This is a Trie, which contains the valid words extracted from word file.
    // This trie is taken from "https://github.com/TaylorBeebe/Trie_Dictionary" which uses structure5 library ("http://www.cs.williams.edu/~bailey/JavaStructures/Software.html")
    // The necessary "structure5" can be found in the directory with file "bailey.jar"
    // There are 5 more functions added for our personal usages (is_word, is_path, current_move, current_move_back, reset_current)
    public static LexiconTrie trie = new LexiconTrie();

    // A simple counter to keep track of number of valid words found.
    static int counter = 0;

    // A TreeSet to store answers (valid words found) in a sorted manner
    static TreeSet<String> answer = new TreeSet<>();

    public static void main(String[] args) throws IOException {

        // Initializing boggle board
        BoggleBoard board = new BoggleBoard(ROWS, COLUMNS, SEED);

        // A function which reads the words.txt file and saves those words into trie
        ReadFile.readFile(WORD_LIST, trie);

        // Output file generating
        String outFileName = "output_optimized_large_board";
        FileOutputStream outStream = new FileOutputStream(outFileName);

        // Capturing starting time
        double startTime = System.currentTimeMillis();
        //Solver function which solves the board
        solver(board);
        // Capturing end time
        double endTime = System.currentTimeMillis();

        // Traversing the board to output the board's letters
        for(int i = 0; i < board.getRows(); i++) {
            for(int j = 0; j < board.getColumns(); j++) {
                System.out.print(Character.toUpperCase(board.getCharAt(i,j)));
                outStream.write(Character.toUpperCase(board.getCharAt(i,j)));
            }
            System.out.println();
            outStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }

        // Outputting all the valid words found from the board
        for(String word : answer) {
            System.out.println(word);
            outStream.write(word.getBytes(StandardCharsets.UTF_8));
            outStream.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
        }

        String temp = "Found "+counter+" words found in "+(endTime - startTime)+" milliseconds";
        System.out.println(temp);

        outStream.write(temp.getBytes(StandardCharsets.UTF_8));

    }


    // The main solver function which takes BoggleBoard as parameter and returns nothing
    static void solver(BoggleBoard board) {
        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {

                // trie's added function, which resets the temporary positional node to start/root
                trie.reset_current();

                // Helper function which is used recursively
                processSolver(board, i, j, ""+board.getCharAt(i,j));
            }
        }
    }

    // Helper recursive function which takes BoggleBoard, x and y indices and word.
    private static void processSolver(BoggleBoard board, int x, int y, String word) {

        // trie's added function which tells if the temporary positional node
        // which we are at, makes a valid path (prefix) or not.
        if(!trie.is_path(board.getCharAt(x,y))) {
            return;
        }

        // trie's added function which moves the temporary positional node
        // to the child node, with letter present at x,y indices of board, of the current node
        trie.current_move(board.getCharAt(x,y));

        // trie's added function which tells if the node which we are at constitutes a word or not
        if(trie.is_word() && !answer.contains(word)) {
            answer.add(word);
            counter++;
        }


        // Continue checking the neighbours of the x,y index.

        // Upper left
        if (0 <= x - 1 && 0 <= y - 1 && !board.isVisited(x - 1,y - 1)){
            if(trie.is_path(board.getCharAt(x-1,y-1)))
            {
                processSolver(board, x-1, y-1, word+board.getCharAt(x-1,y-1));

                // trie's added function which pushes the current positional node to one step back.
                trie.current_move_back();
            }
        }

        // Up
        if (0 <= y - 1 && !board.isVisited(x,y - 1)){
            if(trie.is_path(board.getCharAt(x,y-1)))
            {
                processSolver(board, x, y-1, word + board.getCharAt(x,y-1));
                trie.current_move_back();
            }
        }

        // Upper right
        if (x + 1 < board.getRows() && 0 <= y - 1 && !board.isVisited(x + 1,y - 1)){
            if(trie.is_path(board.getCharAt(x+1,y-1)))
            {
                processSolver(board, x+1, y-1, word+board.getCharAt(x+1,y-1));
                trie.current_move_back();
            }
        }

        // Right
        if (x + 1 < board.getRows() && !board.isVisited(x + 1,y)){
            if(trie.is_path(board.getCharAt(x+1,y)))
            {
                processSolver(board, x+1, y, word + board.getCharAt(x+1,y));
                trie.current_move_back();
            }

        }

        // Lower right
        if (x+1 < board.getRows() && y+1 < board.getColumns() && !board.isVisited(x+1,y+1)){
            if(trie.is_path(board.getCharAt(x+1,y+1)))
            {
                processSolver(board, x+1, y+1, word+board.getCharAt(x+1,y+1));
                trie.current_move_back();
            }
        }

        // Down
        if (y + 1 < board.getColumns() && !board.isVisited(x,y + 1)){
            if(trie.is_path(board.getCharAt(x,y+1)))
            {
                processSolver(board, x, y+1, word + board.getCharAt(x,y+1));
                trie.current_move_back();
            }
        }

        // Lower left
        if (0 <= x - 1 && y + 1 < board.getColumns() && !board.isVisited(x - 1,y + 1)) {
            if (trie.is_path(board.getCharAt(x - 1, y + 1)))
            {
                processSolver(board, x-1, y+1, word+board.getCharAt(x-1,y+1));
                trie.current_move_back();
            }
        }

        // Left
        if (0 <= x - 1 && !board.isVisited(x - 1,y)){
            if(trie.is_path(board.getCharAt(x-1,y)))
            {
                processSolver(board, x-1, y, word + board.getCharAt(x-1,y));
                trie.current_move_back();
            }
        }


    }

}
