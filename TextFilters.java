/*
 * TextFilters.java
 *
 * Group Members: Michael Lese, Yuxin David Huang
 * 
 * Project Breakup:
 *     Michael Lese -> Author of cat and diff
 *     Yuxin David Huang -> Author of grep, tr, and uniq
 * 
 * Final version: March 8, 2013
 */

import java.io.*;
import java.util.*;

/**
 * A class that simulates some simple filters
 * that exist in sh shells in Unix environments.
 *
 * @author Michael Lese, Yuxin David Huang, Vijay Ramachandran, Colgate University
 */
public class TextFilters {


	/**
	 * A method that simulates cat.
	 *
	 * If no argument is specified, will display stdin on stdout.
	 *
	 * If arguments are specified, then will display the contents of
	 * the files given as arguments, one after the other, on stdout.
	 
	 * If there is an error with any given file, then will print an error
	 * message to standard error (System.err, hereafter abbreviated
	 * "stderr") indicating that the file could not be opened,
	 * but will not terminate, and will continue on to the next file
	 * in the argument list.
	 *
	 * @param args argument list
	 */
    private static void cat(String[] args) {
	
		InputStream in = null;
		boolean file = false;
		String filename = "stdin";
		Scanner scan = null;

		if (args.length == 0) {
			scan = new Scanner(System.in);
			System.out.print(scan.nextLine());
		} else {
			for (int i = 0; i < args.length; i++) {
				filename = args[i];
				try {
					in = new FileInputStream(filename);
					file = true;
				} catch (FileNotFoundException e) {
					System.err.println("cat: file " + filename + " was not found. Exiting.");
					return;
				} catch (SecurityException e) {
					System.err.println("cat: file " + filename + " could not be opened. Access was denied.");
					return;
				}
				scan = new Scanner(in);

				while (scan.hasNextLine()) System.out.println(scan.nextLine());
			}
		}
		
		if (file) {
			try {
				in.close();
			} catch (IOException e) {
				System.err.println("cat: error closing file " + filename);
			}
		}
	} // end of cat(String[])
    

	/**
	 * A method that simulates diff.
	 *
	 * diff takes 2 arguments. If less than 2 arguments
	 * were specified, diff will quit, and print an error
	 * message.
	 *
	 * When the arguments are given, diff will compare the 2
	 * files given. It will print the lines that are in the first
	 * file in the argument list but not in the second preceded
	 * by a "-"; it will print the lines vice versa preceded by
	 * a "+";
	 *
	 * @param args argument list
	 */
    private static void diff(String[] args) {
	
		String file1 = null;
		String file2 = null;
		InputStream in1 =  null;
		InputStream in2 = null;
		boolean f1 = false;
		boolean f2 = false;

		if (args.length <= 0) {
			System.err.println("diff: missing argument");
			return;
		} else {
		
			if (args.length == 1) {
				System.err.println("diff: need filename of second file");
				return;
			} else if (args.length > 2) {
				System.err.println("diff: too many arguments");
				return;
			} else {
				file1 = args[0];
				file2 = args[1];
			
				try {
					in1 = new FileInputStream(file1); 
					f1 = true;
				} catch (FileNotFoundException e) {
					System.err.println("diff error: file " + file1 + " could not be found. Exiting.");
					return;
				} catch (SecurityException e) {
					System.err.println("diff error: file " + file1 + " could not be opened. Access was denied.");
					return;
				}
			
				try {
					in2 = new FileInputStream(file2);
				} catch (FileNotFoundException e) {
					System.err.println("diff: file " + file2 + " could not be found. Exiting.");
					return;
				} catch (SecurityException e) {
					System.err.println("diff: file " + file2 + " could not be opened. Access was denied.");
					return;
				}
			}
			
			
		}

		Scanner scan1 = new Scanner(in1);
		Scanner scan2 = new Scanner(in2);
		boolean keep1 = false;
		boolean keep2 = false;
		String currLine1 = ""; 
		String currLine2 = "";

		while (scan1.hasNext() && scan2.hasNext()) {

			if (!keep1) currLine1 = scan1.nextLine();
			if (!keep2) currLine2 = scan2.nextLine();

			if (currLine1.compareTo(currLine2) < 0)  {
				System.out.println("- " + currLine1);
				keep1 = false; 
				keep2 = true;
			} else if (currLine1.compareTo(currLine2) > 0) {
				System.out.println("+ " + currLine2);
				keep2 = false; 
				keep1 = true;
			} else {
				keep1 = false;
				keep2 = false;
			}
			
		}

		if (!scan1.hasNextLine()) {
			if (!currLine1.equals(currLine2)) {
				System.out.println("+ " + currLine2);
			}
			while (scan2.hasNextLine()) System.out.println("+ " + scan2.nextLine());
		} else if (!scan2.hasNextLine()) {
			if (!currLine1.equals(currLine2)) {
				System.out.println("+ " + currLine1);
			}
			while (scan1.hasNextLine()) System.out.println("- " + scan1.nextLine());
		}



		if (f1 || f2) {
			try {
				in1.close();
			} catch (IOException e) {
				System.err.println("diff: error closing file " + file1);
			}
		}
    } // end of diff(String[])
    
    
	/**
	 * A method that simulates grep.
	 *
	 * grep takes more than 1 arguments. If no arguments
	 * are given then grep will print an error message 
	 * and quit.
	 *
	 * grep will print the lines in the file (specified by
	 * the second argument) that contains the string
	 * specified in the first argument. If no second argument
	 * is given, then it will take input from stdin.
	 *
	 * @param args argument list
	 */
    private static void grep(String[] args) {
    
        InputStream in = null;      // input stream
        
        boolean file = false;       // used for error-msg printing
        String filename = null;     // default - no filename
        
        String target = null;       // default - no target to search for 
        boolean countLine = false;  // default - do not count line numbers
        
        if (args.length > 0) {
            
            // check if the first argument is "-l" or the string to search for
            if ("-l".equals(args[0])) {
                countLine = true;
                if (args.length < 2) { 
					System.err.println("grep: Not enough arguments"); 
					return;	
				}
            }
            
            if (countLine) {
                
                if (args.length > 1) {
                    target = args[1];
                }
                
                // use file instead of stdin if filename is provided
                if (args.length > 2) {
                    filename = args[2];
                    try {
                        in = new FileInputStream(filename);
                        file = true;
                    } catch (FileNotFoundException e) {
                        System.err.print("grep: error, file ");
                        System.err.print(filename);
                        System.err.println(" not found or could not be opened");
                        return;
                    } catch (SecurityException e) {
                        System.err.print("grep: error, access to file ");
                        System.err.print(filename);
                        System.err.println(" was denied");
                        return;
                    }
                }
                
                if (args.length > 3) {
					System.err.println("grep: Too many arguments");
					return;
                }
				
            } else {
                target = args[0];
                
                // use file instead of stdin if filename is provided
                if (args.length > 1) {
                    filename = args[1];
                    try {
                        in = new FileInputStream(filename);
                        file = true;
                    } catch (FileNotFoundException e) {
                        System.err.print("grep: error, file ");
                        System.err.print(filename);
                        System.err.println(" not found or could not be opened");
                        return;
                    } catch (SecurityException e) {
                        System.err.print("grep: error, access to file ");
                        System.err.print(filename);
                        System.err.println(" was denied");
                        return;
                    }
                }
                
                if (args.length > 2) {
					System.err.println("grep: Too many arguments");
					return;
				}
            }
            
            Scanner sc;
            if (file) {
                sc = new Scanner(in);
            } else {
                sc = new Scanner(System.in);
            }
            
            int lineNum = 0;
            while (sc.hasNextLine()) {
                
                String currLine = sc.nextLine();
                if (currLine.contains(target)) {
                    if (countLine) {
                        System.out.println("{" + lineNum + "} " + currLine);
                    } else {
                        System.out.println(currLine);
                    }
                }
                
                lineNum++;
            }
            
            // close the input stream
            if (file) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.print("grep: error closing file ");
                    System.err.println(filename);
					return;
                }
            }
            
        } else {
			System.err.println("grep: Not enough arguments");
			return;
		}
    
    } // end of grep(String[])
    
    
	/**
	 * A method that simulates tr.
	 *
	 * tr does not need an argument. If no arguments
	 * are given, then input will be taken from stdin.
	 *
	 * tr prints every word on every line on individual
	 * lines.
	 *
	 * @param args argument list
	 */
    private static void tr(String[] args) {
        
        InputStream in = null;      // input stream
        boolean file = false;       // used for error-msg printing
        String filename = null;     // default - no filename
		Scanner sc = null;
        
        if (args.length <= 0) {
			in = System.in;
			sc = new Scanner(in);
			while (sc.hasNextLine()) {
				String currLine = sc.nextLine().trim();
				String[] currWords = currLine.split(" ");
				for (String word : currWords) {
					System.out.println(word);
				}
			}
		} else {
			for (int i = 0; i < args.length; i++) {
				filename = args[i];
				try {
					in = new FileInputStream(filename);
					file = true;
				} catch (FileNotFoundException e) {
					System.err.println("tr: file " + filename + " was not found. Exiting.");
					return;
				} catch (SecurityException e) {
					System.err.println("tr: file " + filename + " could not be opened. Access was denied.");
					return;
				}
				scan = new Scanner(in);

				while (sc.hasNextLine()) {
					String currLine = sc.nextLine().trim();
					String[] currWords = currLine.split(" ");
					for (String word : currWords) {
						System.out.println(word);
					}
				}	
			}
        }
        
        
    
    } // end of tr(String[])
    
    
	/**
	 * A method that simulates uniq.
	 *
	 * tr does not need an argument. If no arguments
	 * are given, then input will be taken from stdin.
	 *
	 * uniq will print the input with repeated lines
	 * only once.
	 *
	 * @param args argument list
	 */
    private static void uniq(String[] args) {
    
		InputStream in = null;			// input stream
		boolean file = false;			// read from file?
		String filename = null;			// default - no file name
		
		if (args.length > 0) {
			if (args.length > 1) {
				System.err.println("uniq: Too many arguments");
				return;
			}
		
			filename = args[0];
			try {
				file = true;
				in = new FileInputStream(filename);
			} catch (FileNotFoundException fife) {
				System.err.println("uniq: cannot open file " + filename);
				return;
			} catch (SecurityException se) {
				System.err.println("uniq: do not have permission to open " + filename);
				return;
			}
		} else {
			in = System.in;
		}
		
		Scanner sc = new Scanner(in);
		String prevLine = "";
		if (sc.hasNextLine()) {
			prevLine = sc.nextLine();
			System.out.println(prevLine);
		}
		
		while (sc.hasNextLine()) {
			String currLine = sc.nextLine();
			if (!currLine.equals(prevLine)) {
				System.out.println(currLine);
				prevLine = currLine;
				
			}
		}
		
		if (file) {
			try {
				in.close();
			} catch (IOException ioe) {
				System.err.println("uniq: error closing file " + filename);
			}
		}
		
    } // end of uniq(String[])
    
    
    /**
     * Sorts a file or input from stdin in lexical order.
     *
     * @param args arguments given from the command line
     */
    private static void sort(String[] args) {
    
        // This code was taken from Sort.java
    
        InputStream in = null;          // the input stream
        boolean file = false;           // used for error-msg printing
        String filename = "stdin";      // default - no filename
        
        if (args.length > 0) {
            // there's a filename, so try to open a file
            filename = args[0];
            try {
                in = new FileInputStream(filename);
                file = true;
            } catch (FileNotFoundException e) {
                System.err.print("sort: error, file ");
                System.err.print(filename);
                System.err.println(" not found or could not be opened");
                return;
            } catch (SecurityException e) {
                System.err.print("sort: error, access to file ");
                System.err.print(filename);
                System.err.println(" was denied");
                return;
            }
        } else {
            // no filename -- use stdin instead
            in = System.in;
        }
        
        Scanner scan = new Scanner(in);    // open scanner on input stream
        
        /* unlike some of the other filters to implement above,
           sort requires storing all the lines of the file
           in order to print them in order.
         
           do not use unnecessary storage in your own implementations!
        */
        ArrayList<String> list = new ArrayList<String>();
        
        // add lines to the list
        while (scan.hasNextLine())
            list.add(scan.nextLine());
        
        // sort the list (by default -- alphabetically
        Collections.sort(list);
        
        // print each line in the sorted list
        for (String s : list)
            System.out.println(s);
        
        // close the input stream
        if (file) {
            try {
                in.close();
            } catch (IOException e) {
                System.err.print("sort: error closing file ");
                System.err.println(filename);
            }
        }
    
    } // end of sort(String[] args)
    
    
    /*************
     This code provides the main() program that simulates
     piping among the filters so you can invoke multiple
     filters from the Dr. Java Interactions pane
     *************/
    
    private static enum Filter {
        CAT, DIFF, GREP, SORT, TR, UNIQ
    } // end of Filter
            
    public static void main(String[] args) {
    
        PrintStream stdout = System.out;
        InputStream stdin = System.in;
    
        ArrayList<ArrayList<String>> commands = new ArrayList<ArrayList<String>>();
        
        ArrayList<String> group = new ArrayList<String>();

        for (int pos = 0; pos < args.length; pos++) {
            if (args[pos].equals("---")) {
                commands.add(group);
                group = new ArrayList<String>();
            }
            else
                group.add(args[pos]);
        }

        commands.add(group);
        
        ByteArrayOutputStream baos = null;
        
        for (int pos = 0; pos < commands.size(); pos++) {
            ArrayList<String> g = commands.get(pos);
            if (g.size() <= 0) {
                System.err.println("TextFilters: error, missing command in pipe");
                return;
            }
                
            Filter f;
            String cmd = g.get(0);
            
            if (cmd.equals("cat"))
                f = Filter.CAT;
            else if (cmd.equals("diff"))
                f = Filter.DIFF;
            else if (cmd.equals("grep"))
                f = Filter.GREP;
            else if (cmd.equals("sort"))
                f = Filter.SORT;
            else if (cmd.equals("tr"))
                f = Filter.TR;
            else if (cmd.equals("uniq"))
                f = Filter.UNIQ;
            else {
                System.err.print("TextFilters: error, cannot recognize command ");
                System.err.println(cmd);
                return;
            }
                
            String[] arr = new String[g.size()-1];
            for (int i = 1; i < g.size(); i++)
                arr[i-1] = g.get(i);
                
            if (pos > 0)
                System.setIn(new ByteArrayInputStream(baos.toByteArray()));
            else
                System.setIn(stdin);
                
            if (pos == commands.size()-1)
                System.setOut(stdout);
            else {
                baos = new ByteArrayOutputStream();
                System.setOut(new PrintStream(baos));
            }
            
            switch(f) {
            
                case CAT:
                    cat(arr);
                    break;
            
                case DIFF:
                    diff(arr);
                    break;
                    
                case GREP:
                    grep(arr);
                    break;
                    
                case SORT:
                    sort(arr);
                    break;
                    
                case TR:
                    tr(arr);
                    break;
                    
                case UNIQ:
                    uniq(arr);
                    break;
                    
                default:
                    System.err.println("TextFilters: unexpected error");
                    return;
            }
        }
        System.setIn(stdin);
		
    } // end of main(String[])
	
} // end of TextFilters
