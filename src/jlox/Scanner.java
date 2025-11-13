package jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
	private final String source;
	private final List<Token> tokens = new ArrayList<>();
	private int start = 0;
	private int current = 0;
	private int line = 1;
	private static final Map<String, TokenType> keywords;
	
	static {
		keywords = new HashMap<>();
		keywords.put("and", TokenType.AND);
		keywords.put("class", TokenType.CLASS);
		keywords.put("else", TokenType.ELSE);
		keywords.put("false", TokenType.FALSE);
		keywords.put("for", TokenType.FOR);
		keywords.put("fun", TokenType.FUN);
		keywords.put("if", TokenType.IF);
		keywords.put("nil", TokenType.NIL);
		keywords.put("or", TokenType.OR);
		keywords.put("print", TokenType.PRINT);
		keywords.put("return", TokenType.RETURN);
		keywords.put("super", TokenType.SUPER);
		keywords.put("this", TokenType.THIS);
		keywords.put("true", TokenType.TRUE);
		keywords.put("var", TokenType.VAR);
		keywords.put("while", TokenType.WHILE);
	}

	Scanner(String source) {
		this.source = source;
	}

	List<Token> scanTokens() {
		while (!isAtEnd()) {
			start = current;
			scanToken();
		}

		tokens.add(new Token(TokenType.EOF, "", null, line));
		return tokens;
	}

	private boolean isAtEnd() {
		return current >= source.length();
	}

	private void scanToken() {
		char c = advance();
		switch (c) {
		case '(':
			addToken(TokenType.LEFT_PAREN);
			break;
		case ')':
			addToken(TokenType.RIGHT_PAREN);
			break;
		case '{':
			addToken(TokenType.LEFT_BRACE);
			break;
		case '}':
			addToken(TokenType.RIGHT_BRACE);
			break;
		case ',':
			addToken(TokenType.COMMA);
			break;
		case '.':
			addToken(TokenType.DOT);
			break;
		case '-':
			addToken(TokenType.MINUS);
			break;
		case '+':
			addToken(TokenType.PLUS);
			break;
		case ';':
			addToken(TokenType.SEMICOLON);
			break;
		case '*':
			addToken(TokenType.STAR);
			break;
		case '!':
			addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
			break;
		case '=':
			addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
			break;
		case '<':
			addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
			break;
		case '>':
			addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
			break;
		case '/':
			if(match('/')) {
				parseSingleLineComment();
			} else if(match('*')) {
				parseMultiLineComment();
			} else {
				addToken(TokenType.SLASH);
			}
			break;
		case ' ':
		case '\r':
		case '\t':
			break;
		case '\n':
			line++;
			break;
		case '"':
			parseString();
			break;
		default:
			if(isDigit(c)) {
				parseNumber();
			} else if (isAlpha(c)){
				parseIdentifier();
			} else {
				Lox.error(line, "Unexpected character.");
			}
			break;
		}
	}

	char advance() {
		return source.charAt(current++);
	}
	
	private boolean match(char expected) {
		if(isAtEnd()) {
			return false;
		}
		
		if(source.charAt(current) != expected) {
			return false;
		}
		
		current++;
		return true;
	}
	
	
	char peek() {
		if(isAtEnd()) {
			return '\0';
		}
		
		return source.charAt(current);
	}
	
	char peekNext() {
		if(current + 1 >= source.length()) {
			return '\0';
		}
		
		return source.charAt(current + 1);
	}
	
	void parseSingleLineComment() {
		while(peek() != '\n' && !isAtEnd()) {
			advance();
		}
	}
	
	void parseMultiLineComment() {
		while(peek() != '*' && !isAtEnd()) {
			advance();
		}
		
		advance();
		
		if(peek() == '/') {
			advance();
		} else {
			Lox.error(line, "Unexpected character.");
		}
	}
	
	void parseString() {
		while(peek() != '"' && !isAtEnd()) {
			if(peek() == '\n') {
				line++;
			}
			advance();
		}
		
		if(isAtEnd()) {
			Lox.error(line, "Unterminated string.");
			return;
		}
		
		advance(); // Consume "
		
		String value = source.substring(start + 1, current - 1);
		addToken(TokenType.STRING, value);
	}
	
	boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	private void parseNumber() {
		while(isDigit(peek())) {
			advance();
		}
		
		if(peek() == '.' && isDigit(peekNext())) {
			advance();
			while(isDigit(peek())) {
				advance();
			}
		}
		
		addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
	}
	
	private boolean isAlpha(char c) {
		return (c >= 'a' && c <= 'z') ||
			   (c >= 'A' && c <= 'Z') ||
				c == '_';
	}
	
	private boolean isAlphaNumeric(char c) {
		return isDigit(c) || isAlpha(c);
	}
	
	private void parseIdentifier() {
		while(isAlphaNumeric(peek())) {
			advance();
		}
		
		String text = source.substring(start, current);
		TokenType type = keywords.get(text);
		if(type == null) {
			type = TokenType.IDENTIFIER;
		}
		
		addToken(type);
	}
	
	void addToken(TokenType type) {
		addToken(type, null);
	}
	
	void addToken(TokenType type, Object literal) {
		String lexeme = source.substring(start, current);
		tokens.add(new Token(type, lexeme, literal, line));
	}
}
