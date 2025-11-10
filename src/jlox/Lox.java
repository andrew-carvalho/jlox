package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
	private static boolean hadError = false;
	public static final int EXIT_USAGE_ERROR = 64;
	public static final int EXIT_DATA_ERROR = 65;

	public static void main(String[] args) throws IOException {
		if(args.length > 1) {
			System.out.println("usage: jlox [script]");
			System.exit(EXIT_USAGE_ERROR);
		}
		
		if(args.length == 1) {
			runFile(args[0]);
			return;
		}
		
		runPrompt();
	}
	
	public static void runFile(String path) throws IOException {
		byte[] bytes = Files.readAllBytes(Paths.get(path));
		run(new String(bytes, Charset.defaultCharset()));
		if(hadError) {
			System.exit(EXIT_DATA_ERROR);
		}
	}
	
	public static void runPrompt() throws IOException {
		InputStreamReader input = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input);
		
		while(true) {
			System.out.print("> ");
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			run(line);
			hadError = false;
		}
	}
	
	public static void run(String source) {
	    Scanner scanner = new Scanner(source);
	    List<Token> tokens = scanner.scanTokens();
	    
	    for(Token token : tokens) {
	    	System.out.println(token);
	    }
	}
	
	public static void error(int line, String message) {
		report(line, "", message);
	}
	
	public static void report(int line, String where, String message) {
		System.err.println("[line " + line + "] Error" + where + ": " + message);
		hadError = true;
	}
}
