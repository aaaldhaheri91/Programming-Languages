/*
 * Author: Ahmed Aldhaheri
 * Date:   November 1, 2016
 * Course: COSC 341
 * Description: This is a Compiler for an XMicro language.
 * 				Compiler only includes a scanner and a top
 * 				down parser. The XMicro language program begins
 *				with word main followed by sequence of statements
 *				within {} block. The XMicro language only supports
 *				five statements which are assignment statement, 
 *				read statement, write statement, if-else statement,
 *				and while statement. 
*/

/* Include header files */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

/* Constants for true and false */
#define TRUE 1
#define FALSE 0

/* enumared types for token types */
typedef enum{
	MAIN, LPAREN, RPAREN, LCURLY, RCURLY,
	READ, WRITE, ASSIGNOP, PLUSOP, MINUSOP,
	MULTIPLYOP, DIVISIONOP, COMMA, SEMICOLON,
	IF, ELSE, WHILE, ID, INTLITERAL, LESSTHAN,
	GREATERTHAN, LESSTHANEQUAL, GREATERTHANEQUAL,
	EQUAL, NOTEQUAL, SCANEOF
}token;

/* scanner functions declarations */
token scanner();
void clear_buffer();
void buffer_char(char);
token check_reserved();
void lexical_error();
const char* translate(token);

/* parser functions declarations */
void parser();
void program();
void statement_list();
void statement();
void id_list();
void expression();
void term();
void add_sub_op();
void mult_div_op();
void match(token myTok);
void syntax_error();
void factor();
void boolean_expression();
void relational_operator();
void operand();

/* global variables */
FILE *fin;						/* source file */
token next_token;				/* next token in source file */
char token_buffer[100];			/* token buffer */
int token_ptr;					/* buffer pointer */
int line_num = 1;				/* line number in source file */
int error = FALSE;				/* flag to indicate error */

/* Main */
int main(){
	
	/* declare variables */
	int user_choice, i = 1;
	FILE *fout; 
	char source_file[21], output_file[21];
	const char *literal;			
	
	option_error:
	/* ask user for choice */
	printf("Choose one of the following options: ");
	printf("\n1. scan source code\n2. parse source code\n");
	scanf("%d", &user_choice);
	
	/* check user choice */
	file_error: 
	switch(user_choice){
		
		/* if choice is to scan and write tokens to output file */
		case 1:{ 
			
			/* ask user for input */
			printf("\nEnter source code file: ");
			scanf("%s", source_file);
			printf("\nEnter output file: ");
			scanf("%s", output_file);
			
			/* open source code and scan */
			fin = fopen(source_file, "r");
			if(fin == NULL){									/* check if file exist */
				printf("source file does not exist, please try again\n");
				goto file_error;
			}
			fout = fopen(output_file, "w");						/* create output file */
			
			token extracted_token = scanner();
			
			while(extracted_token != SCANEOF){						/* scan until end of program */
				i++;
				literal = translate(extracted_token);				/* will hold string value of token */
				fprintf(fout, "%s", literal);						/* write to token to output file */
				fprintf(fout, "%c", 32);							/* write space between tokens in output file */
				
				if(i % 5 == 0)										/* write only 5 tokens on every line in output file */
					fprintf(fout, "%c", 10);
				
				extracted_token = scanner();
				
			}
			break;
		}
			
		/* if choice is to parse source code */
		case 2:{
			
			/* ask user for input */
			printf("\nEnter source code file: ");
			scanf("%s", source_file);
			
			/* open source code and scan */
			fin = fopen(source_file, "r");
			if(fin == NULL){									/* check if file exist */
				printf("source file does not exist, please try again\n");
				goto file_error;
			}
			parser();
			
			/* check if there is any lexical or syntax errors */
			if(error)
				;
			else
				printf("parsing is successful\n");
			break;
		}
			
		default:
			printf("Wrong choice, please try again\n");
			goto option_error; 
			
					
	}
	
	
	return 0;
	
}

/* returns next token from source file */
token scanner(){
	
	char c;								/* current character in source file */
	
	clear_buffer();						/* empty token buffer */
	
	while(TRUE){						/* loop reads and returns next token */
		
		c = getc(fin);					/* read a character from source file */
		
		if(c == EOF)					/* end of file */
			return SCANEOF;
		
		else if(isspace(c)){			/* skip white spaces and count line number */
			
			if(c == '\n')
				line_num++;
			
		}			
		
		else if(isalpha(c)){			/* identifier or reserved word */
			
			buffer_char(c);				/* buffer the first character */
			c = getc(fin);
			while(isalnum(c)){			/* read and buffer subsequent characters */
				
				buffer_char(c);
				c = getc(fin);
				
			}
			ungetc(c, fin);				/* put back the last character read */
			return check_reserved();	/* return identifier or reserved word */
			
		}
		
		else if(isdigit(c)){			/* integer literal */
			
			buffer_char(c);				/* buffer the first character */
			c = getc(fin);
			while(isdigit(c)){			/* read and buffer subsequent characters */
				
				buffer_char(c);
				c = getc(fin);
			}
			ungetc(c, fin);				/* put back the last character read */
			return INTLITERAL;
		}
		
		/* start relational operators check */
		else if(c == '<'){				/* check if less than or less than equal */
			
			c = getc(fin);
			
			if(c == '=')				/* check if equal follows relational operator */
				return LESSTHANEQUAL;
			else{						
				ungetc(c, fin);			/* if no equal follows, put back last character */
				return LESSTHAN;
			}
		}
		
		else if(c == '>'){				/* check if greater than or greater than equal */
			
			c = getc(fin);
			
			if(c == '=')					/* check if equal follows relational operator */
				return GREATERTHANEQUAL;
			else{
				ungetc(c, fin);			
				return GREATERTHAN;			/* if no equal follows, put back last character */
			}
		}	
		
		else if(c == '='){				/* check if equal relational operator */
			
			c = getc(fin);
			
			if(c == '=')				
				return EQUAL;
			else{
				ungetc(c, fin);			/* if not equal, put character back and throw lexical error */
				lexical_error();
			}
		}
		
		else if(c == '!'){				/* check for not equal operator */
			
			c = getc(fin);
			
			if(c == '=')
				return NOTEQUAL;
			else{
				ungetc(c, fin);			/* if not equal operator, put character back and throw lexical error */
				lexical_error();
			}
		}
		
		
		else if(c == '(')						/* left parantheses */
			return LPAREN;
		
		else if(c == ')')					/* rigth parantheses */
			return RPAREN;
		
		else if(c == '{')					/* left curly bracket */
			return LCURLY;		
		
		else if(c == '}')					/* right curly brackets */
			return RCURLY;
		
		else if(c == ',')					/* comma */
			return COMMA;
		
		else if(c == ';')					/* semicolon */
			return SEMICOLON;
		
		else if(c == '+')					/* plus operator */
			return PLUSOP;
		
		else if(c == '-')					/* minus operator */
			return MINUSOP;
		
		else if(c == '*')					/* product operator */
			return MULTIPLYOP;
			
		else if(c == '/'){					/* comment or division operator */
			
			c = getc(fin);
			if(c == '/'){					/* comment begins */
				
				do						/* read and discard until end of line */
					c = getc(fin);
				while(c != '\n');
				line_num++;
				
			}
			else{							/* division operator */
				
				ungetc(c, fin);
				return DIVISIONOP;
			}
			
		}
		
		else if(c == ':'){					/* possible assignment operator */
			
			c = getc(fin);
			if(c == '=')					/* assignment operator */
				return ASSIGNOP;
			
			else{							/* error due to : */
				
				ungetc(c, fin);
				lexical_error();
			}
		}
		
		else								/* invalid character */
			lexical_error();
	
			
	}/* end while loop */
	
	
}/* end scanner function */

/********************************************************************************************/

/* clears the buffer */
void clear_buffer(){
	
	token_ptr = 0;						/* reset tokent pointer */
	token_buffer[token_ptr] = '\0';		/* add null character */
}

/********************************************************************************************/

/* appends character to buffer 
 * @param c the character to appends 
 */
 void buffer_char(char c){
	 
	 token_buffer[token_ptr] = c;		/* append current character */
	 token_ptr++;						/* move token pointer */
	 token_buffer[token_ptr] = '\0';	/* move null character */
 }
 
/********************************************************************************************/

/* check whether buffer is reserved word or identifier */
token check_reserved(){
	
	if(strcmp(token_buffer, "main") == 0)		/* 6 reserved words */
		return MAIN;
	else if(strcmp(token_buffer, "read") == 0)
		return READ;
	else if(strcmp(token_buffer, "write") == 0)
		return WRITE;
	else if(strcmp(token_buffer, "if") == 0)
		return IF;
	else if(strcmp(token_buffer, "else") == 0)
		return ELSE;
	else if(strcmp(token_buffer, "while") == 0)
		return WHILE;
	else
		return ID;								/* identifier */
} 

/********************************************************************************************/

/* reports lexical error and sets error flag */
void lexical_error(){
	
	printf("lexical error in line %d\n", line_num);
	error = TRUE;
}

/********************************************************************************************/

/* Translates token values into strings and returns it */
const char* translate(token tok){
	
	switch(tok){
		case MAIN: return "MAIN";
		case LPAREN: return "LPAREN";
		case RPAREN: return "RPAREN";
		case LCURLY: return "LCURLY";
		case RCURLY: return "RCURLY";
		case READ: return "READ";
		case WRITE: return "WRITE";
		case ASSIGNOP: return "ASSIGNOP";
		case PLUSOP: return "PLUSOP";
		case MINUSOP: return "MINUSOP";
		case MULTIPLYOP: return "MULTIPLYOP";
		case DIVISIONOP: return "DIVISIONOP";
		case COMMA: return "COMMA";
		case SEMICOLON: return "SEMICOLON";
		case IF: return "IF";
		case ELSE: return "ELSE";
		case WHILE: return "WHILE";
		case ID: return "ID";
		case INTLITERAL: return "INTLITERAL";
		case LESSTHAN: return "LESSTHAN";
		case GREATERTHAN: return "GREATERTHAN";
		case LESSTHANEQUAL: return "LESSTHANEQUAL";
		case GREATERTHANEQUAL: return "GREATERTHANEQUAL";
		case EQUAL: return "EQUAL";
		case NOTEQUAL: return "NOTEQUAL";
		case SCANEOF: return "SCANEOF";
		default: break;
					
	}
	
}

/********************************************************************************************/

/* parses source file */
void parser(){
	
	next_token = scanner();				/* read the first token */
	program();							/* parse the program */
	match(SCANEOF);						/* check end of file */
}

/********************************************************************************************/

/* parses a program */
/* <program> --> main <leftCurly> <stmtlist> <rightCurly> */
void program(){
	
	match(MAIN);						/* main */
	match(LCURLY);						/* left curly brakets */
	statement_list();					/* list of statements */
	match(RCURLY);						/* right curly brackets, end of program */
	
}

/********************************************************************************************/

/* parses list of statements */
/* <stmtlist> --> <stmt> {<stmt>} */
void statement_list(){
	
	statement();						/* first statement */
	while(TRUE){
		
		if(next_token == ID || next_token == READ || next_token == WRITE ||
			next_token == IF || next_token == WHILE)
			statement();				/* subsequent statements */
		else
			break;
	}
}

/********************************************************************************************/

/* parses one statement */
/* <stmt> --> id := <expr>;
   <stmt> --> read <leftP> <idlist> <rightP>;
   <stmt> --> write <leftP> <idlist> <rigthP>;
   <stmt> --> if <leftP> <booleanexpr> <rightP> <leftCurly>
			  <stmtlist> <rigthCurly> 
			  [else <leftCurly> <stmtlist> <rightCurly>] 
   <stmt> --> while <leftP> <booleanexpr> <rightP> <leftCurly>
			  <stmtlist> <rightCurly>
*/
void statement(){
	
	if(next_token == ID){				/* assignment statement */
		
		match(ID);
		match(ASSIGNOP);
		expression();
		match(SEMICOLON);
		
	}
	
	else if(next_token == READ){		/* read statement */
		
		match(READ);
		match(LPAREN);
		id_list();
		match(RPAREN);
		match(SEMICOLON);
		
	}
		
	else if(next_token == WRITE){		/* write statement */
		
		match(WRITE);
		match(LPAREN);
		id_list();
		match(RPAREN);
		match(SEMICOLON);
		
	}
	
	else if(next_token == IF){			/* if statement */
		
		match(IF);
		match(LPAREN);
		boolean_expression();
		match(RPAREN);
		match(LCURLY);
		statement_list();
		match(RCURLY);
		
		if(next_token == ELSE){			/* else statement */
			
		match(ELSE);
		match(LCURLY);
		statement_list();
		match(RCURLY);
			
		}
		
	}
	
	else if(next_token == WHILE){			/* while statement */
		
		match(WHILE);
		match(LPAREN);
		boolean_expression();
		match(RPAREN);
		match(LCURLY);
		statement_list();
		match(RCURLY);
		
	}
	
	else
		syntax_error();						/* invalid begining of statement */
}

/********************************************************************************************/

/* parses list of identifiers */
/* <idlist> --> id {, id} */
void id_list(){
	
	match(ID);							/* first identifier */
	while(next_token == COMMA){
		
		match(COMMA);					/* subsequent identifier */
		match(ID);
	}
}

/********************************************************************************************/

/* parses expressions */
/* <expr> --> <term> {<addSubOP> <term>} */	
void expression(){
	
	term();								/* first term */
	while(next_token == PLUSOP || next_token == MINUSOP){
		
		add_sub_op();					/* add or minus operators */
		term();
		
	}
}		

/* parses terms */
/* <term> --> <factor> {<multDivOP> <factor>} */
void term(){
	
	factor(); 							/* first factor */
	while(next_token == MULTIPLYOP || next_token == DIVISIONOP){
		
		mult_div_op();					/* multiply or division operator */
		factor();
		
	}
}	

/********************************************************************************************/

/* parses factor */
/* <factor> --> id | integer | <leftP> <expr> <rightP> */
void factor(){
	
	if(next_token == ID)				/* identifier */
		match(ID);
	
	else if(next_token == INTLITERAL)	/* integer literal */
		match(INTLITERAL);
	
	else if(next_token == LPAREN){		/* expression inside parantheses */
		
		match(LPAREN);
		expression();
		match(RPAREN);
		
	}
	
	else								/* invalid term */
		syntax_error();
		
}

/********************************************************************************************/

/* parses plus or minus operator */
/* <addSubOP> --> + | - */
void add_sub_op(){
	
	if(next_token == PLUSOP || next_token == MINUSOP)
		match(next_token);
	else
		syntax_error();
	
}

/********************************************************************************************/

/* parses multiplication or division operator */
/* <multDivOP> --> * | / */
void mult_div_op(){
	
	if(next_token == MULTIPLYOP || next_token == DIVISIONOP)
		match(next_token);
	else
		syntax_error();
	
}

/********************************************************************************************/

/* parses boolean expression */
/* <booleanexpr> --> <operand> <relOP> <operand> */
void boolean_expression(){
	
	operand();							/* left operand */
	relational_operator();				/* relational operator */
	operand();							/* right operand */
	
}

/********************************************************************************************/

/* parses operand */
/* <operand> --> id | integer */
void operand(){
	
	if(next_token == ID || next_token == INTLITERAL)
		match(next_token);
	
	else
		syntax_error();
		
}

/********************************************************************************************/

/* parses relational operator */
/* <relOP> --> < | > | <= | >= | == | != */
void relational_operator(){
	
	if(next_token == LESSTHAN || next_token == GREATERTHAN || next_token == LESSTHANEQUAL
	   || next_token == GREATERTHANEQUAL || next_token == EQUAL || next_token == NOTEQUAL)
	   match(next_token);
	
	else
		syntax_error();
		
}

/********************************************************************************************/

/* checks whether the expected token and the actual token match,
   and also reads the next token from source file */
void match(token myTok){
	
	if(myTok == next_token)					/* expected token and actual token match */
		;
	else
		syntax_error();						/* expected token and actual token do not match */
	next_token = scanner();
	
}

/********************************************************************************************/

/* reports syntax error */
void syntax_error(){
	
	printf("syntax error in line %d\n", line_num);
	error = TRUE;
	
}