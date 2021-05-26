import structure5.*;
import structure5.Set;
import structure5.Stack;
import structure5.Vector;

import java.util.*;

public class LexiconTrie {
    
    LexiconNode start = new LexiconNode('#', false);
    private int counter = 0;

    // Added for custom usage (BoggleWordFinder)
    LexiconNode _current = null; // Stores reference to current node.
    Stack<LexiconNode> _lastNode = new StackList<>(); // Stores references to the nodes we traversed, so we can get back later.

    // Method which moves the '_current' node to the child node containing character which is passed as parameter
    public void current_move(char a) {
        if(_current == null) {
            _current = start;
        }
        _lastNode.push(_current); // Push the last node to the stack of backwrd nodes
        _current = _current.getChild(a);
    }

    // Method which moves the current node to one back
    public void current_move_back() {
        _current = (_lastNode.empty()) ? null : _lastNode.pop();
    }

    // Method which tells if the provided character provisions a correct prefix or not.
    public boolean is_path(char a) {
        if(_current == null) {
            _current = start;
        }
        if(_current.hasChild(a)) return true;
        return false;
    }

    // Method which tells if the current node constitutes a word or not.
    public boolean is_word() {
        if(_current == null) {
            _current = start;
        }
        return _current.isWord();
    }

    public boolean addWord(String word) {

        //test null
        if(word.equals(null)) return false;
  
        //make word lowercase
        word = word.toLowerCase();
  
        //grab node object
        LexiconNode c = start;
        char ch;
  
        //iterate through word, create nodes as needed
        for (int x = 0; x < word.length(); x++){
          ch = word.charAt(x);
  
      //add child if not already present
          if (c.hasChild(ch) == false) c.addChild(new LexiconNode(ch, false));
  
      c = c.getChild(ch);
        }
  
        
        if (c.isWord() == true) return false;
        c.setIsWord(true);
        counter++;
        return true;
    }
      
      /*Adds words from an input file if words are seperated by newlines
       * @Pre: valid filename
       * @Post: Returns number of words added
       */
      public int addWordsFromFile(String filename) {
  
      int count = 0;
  
      try{
          //scanner to read in fstream
          Scanner in = new Scanner(new FileStream(filename));
  
          //string to hold lines
          String s;
  
          while(in.hasNextLine()){
          s = in.nextLine();
          if (addWord(s)) count++;
          }
  
          in.close();
  
      //cant assume file opened	
      } catch (Exception e){
          System.out.println("Invalid Filename. Couldn't add words to lexicon");
          }
      
      return count;
      }
      
      /*Removes word from lexicon
       * @Pre word is not null
       * @Returns true if word was removed from lexicion
       */ 
      public boolean removeWord(String word) {
  
      //test for null
      if(word.equals(null)) return false;
  
      //make lowercase
      word = word.toLowerCase();
  
      //check if word is alread present
      if(!containsWord(word)) return false;
  
      //stack is an excellent structure for deleting
      //a word. Allows us to easily iterate from
      //bottom of tree upwards so we don't delete
      //nodes that are part of other words
      StackList<LexiconNode> s = stackify(word);
  
      //set the bottom node word flag to false
      s.get().setIsWord(false);
  
      //decriment word counter
      counter--;
      int size = s.size();
  
      //remove word until either a node is reached that is part
      //of another word or we have reached to root of the trie
      for(int x = 1; x < size; x++){
          
          if(!s.get().isWord() && !s.get().hasChildren()){
          char l = s.pop().getLetter();
          s.get().removeChild(l);
          } else{
          break;
          }
      }
      
      return true; }
      
      //Creates a stack containing nodes leading up to a word in lexicon
      private StackList<LexiconNode> stackify(String word){
  
      StackList<LexiconNode> s = new StackList<LexiconNode>();
  
      LexiconNode c = start;
  
      s.push(c);
  
      for(int x = 0; x < word.length(); x++){
          c = c.getChild(word.charAt(x));
          s.push(c);
      }
      return s;
      }
      
      //Returns the number of words in the lexicon
      public int numWords() { return counter;}
      
      /*Checks if word is in the lexicon
       * @Pre: word is not null
       * @Post: returns true if word is in the lexicon
       */ 
      public boolean containsWord(String word){
  
      //check for null
      if(word.equals(null)) return false;
  
      //make word lowercase
      word = word.toLowerCase();
  
      LexiconNode c = start;
  
      char letter;
  
      //walk the trie letter by letter to see
      //if the word is contained
      for (int x = 0; x < word.length(); x++){
  
          letter = word.charAt(x);
          if (c.hasChild(letter) == false) return false;
          c = c.getChild(letter);
      }
  
      //check if the last node has the word flag
      if (c.isWord()) return true;
  
      return false;
      }
      
      /*Checks if prefix is in the lexicon
       * @Pre: word is not null
       * @Post: returns true if prefix is in the lexicon, even if the prefix is a word or if it has no children
       */ 
      public boolean containsPrefix(String prefix){
  
      
      prefix = prefix.toLowerCase();
      
      LexiconNode c = start;
      
      for (int x = 0; x < prefix.length(); x++){
          if (c.hasChild(prefix.charAt(x)) == false) return false;
          c = c.getChild(prefix.charAt(x));
      }
      
      return true;
      }
      
      //Returns an iterator that iterates through a vector containing all the words within the lexicon
      public Iterator<String> iterator() {
  
      Vector<String> words = new Vector<String>(counter);
      iteratorHelper(new String(""), start, words);
      return words.iterator();
      }
  
      
      //Helper function for iterator
      private void iteratorHelper(String s, LexiconNode n, Vector<String> words){
      if(!n.equals(start)) s += n.getLetter();
      
      if (n.isWord()) words.add(s);
      
      if(n.hasChildren()){
          Iterator<LexiconNode> i = n.iterator();
          while(i.hasNext()){
          iteratorHelper(s, i.next(), words);
          }
      }
      }
      
      /* Recursively creates a set containing possible corrections 
       * with the given parameters
       * @Pre: target is not null, maxDistance is not zero
       * @Post: returns a set containing possible corrections
       */
      public Set<String> suggestCorrections(String target, int maxDistance) {
  
      //set list containing possible corrections to mispelled word
      Set<String> corrections = new SetList<String>();
  
      //base case
      if (target.equals(null) || maxDistance == 0) return corrections;
  
      target = target.toLowerCase();
      
      LexiconNode n;
  
      Iterator<LexiconNode> i = start.iterator();
      
      while(i.hasNext()){
          n = i.next();
          suggestCorrectionsHelper(target, new String("" + n.getLetter()), maxDistance, n, corrections);
      }
      return corrections;
      }
      
      //Helper for corrections suggestion
      private void suggestCorrectionsHelper(String target, String running, int maxDistance, LexiconNode n, Set<String> corrections) {
  
      //test base case
      if(n.isWord() && running.length() == target.length() && maxDistance > 0) corrections.add(running);
  
      if(running.length() < target.length() && maxDistance >= 0){
          Iterator<LexiconNode> it = n.iterator();
          while(it.hasNext()){
          n = it.next();
          if(n.getLetter() == target.charAt(running.length())) suggestCorrectionsHelper(target, running
                                   + n.getLetter(), maxDistance, n, corrections);
          else suggestCorrectionsHelper(target, running + n.getLetter(), maxDistance - 1, n, corrections);
          }
      }
      }
      
      /*Creates a set containing strings that match a given regular expression
       * @Pre:Pattern is not null
       * @Post: Returns a set containing strings that match a regular expression
       */ 
      public Set<String> matchRegex(String pattern){
  
      Set<String> regex = new SetList<String>();
  
      if(pattern.equals(null)) return regex;
  
      pattern = pattern.toLowerCase();
  
      matchRegexHelper(pattern, new String(""), start, regex);
  
      return regex;
      }
     
      //Helper function for regular expression interpreter
      private void matchRegexHelper(String expression, String running, LexiconNode n, Set<String> regex){
  
      //Base case: if the expression is an empty string and the running string is a word
      if ((expression.length() == 0 && n.isWord())) regex.add(running);
  
      //Make sure the expression isn't empty and the node has children
      if(expression.length() > 0 && n.hasChildren()){
  
          //Get the character at index zero (it makes the code easier to read)
          char l = expression.charAt(0);
  
          //See if  the first character is a ? or *
          if (l == '?' || l == '*'){
  
          //create an iterator for the children of this node
          Iterator<LexiconNode> i = n.iterator();
  
          //for both ? and *, remove the first letter of the expression and don't advance the node
          matchRegexHelper(expression.substring(1), running, n, regex);
          while (i.hasNext()){
              n = i.next();
  
              //if it is a *, dont change the expression, add this node's letter to the running
              //string, and call itself with new node
              if (l == '*') matchRegexHelper(expression, running + n.getLetter(), n, regex);
  
              //if it is a ?, remove the first letter of the expression, add this node's letter to the
              //running string, and call itself with new node
              else matchRegexHelper(expression.substring(1), running + n.getLetter(), n, regex);
          }
          //otherwise, it must be a letter/other character so see if the child is in this node's
          //children. If not, it is a dead end	
          } else if (n.hasChild(l)) matchRegexHelper(expression.substring(1), running
                                 + n.getChild(l).getLetter(), n.getChild(l), regex);
          }
      }
    
    public static void main(String[] args) {
        System.out.println("EZZPZ");
    }

    public void reset_current() {
          _current = start;
    }
}

class LexiconNode implements Comparable<LexiconNode> {

    char letter;
    boolean isWord;
    OrderedVector<LexiconNode> v = new OrderedVector<LexiconNode>();

    LexiconNode(char a, boolean b) {
        letter = a;
        isWord = b;
    }

    @Override
    public int compareTo(LexiconNode o) {
        if(o.equals(null)) return 0;
        return this.letter - o.getLetter();
    }
    public char getLetter() {
        return letter;
    }
    public void addChild(LexiconNode ln) {
        if(!ln.equals(null)) v.add(ln);
        }
        
        //Get LexiconNode child for 'ch' out of child data structure
        public LexiconNode getChild(char ch) {
        for(LexiconNode n : v){
            if (n.getLetter() == ch) return n;
        }
        return null;
        }
        
        //Returns true if child is contained in children
        public boolean hasChild(char ch){
        for(LexiconNode n : v){
            if (n.getLetter() == ch) return true;
        }
        return false;
        }
        
        //Remove LexiconNode child for 'ch' from child data structure
        public void removeChild(char ch) {
        for(LexiconNode n : v){
            if (n.getLetter() == ch) v.remove(n);
        }
        }
        
        //returns an iterator for the children of this node
        public Iterator<LexiconNode> iterator() {
        return v.iterator();
        }
        //returns true if the children vector has children
        public boolean hasChildren(){return v.size() > 0;}
        
        //changes the isWord Flag
        protected boolean setIsWord(boolean b){
        this.isWord = b;
        return true;
        }
        
        //returns the value of isWord
        protected boolean isWord() {return isWord;}
}