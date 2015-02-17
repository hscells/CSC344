#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

// pretty EOL
#define EOL '\n'

// stack to store the symbols
#define STACK_SIZE 1024

// total number of symbols we are looking for
#define NUM_SYMBOLS 3

typedef struct{
   char start_symbol;
   char end_symbol;
} t_symbol;

t_symbol *PAREN;
t_symbol *BRACE;
t_symbol *SQ_BRACE;

t_symbol SYMBOLS[NUM_SYMBOLS];

bool inside_comment = false;
bool inside_string = false;

int comment = 0;

int escaped_char = false;

char symbols_stack[STACK_SIZE];
int current_stack = 0;
int current_symbols = 0;

int dub_quotes = 0;
int sin_quotes = 0;

int identifiers = 0;
bool reading_identifier = false;
const char VALID_START_CHARS[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                                 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                                 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                                 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
                                 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                                 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
                                 'W', 'X', 'Y', 'Z', '_'};
const char VALID_CHARS[] = {'1', '2', '3', '4', '5', '6', '7', '8', '9'};

t_symbol *addSymbol(char s, char e){

   t_symbol *symbol = (t_symbol*) malloc(sizeof(t_symbol));
   symbol->start_symbol = s;
   symbol->end_symbol = e;
   SYMBOLS[current_symbols] = *symbol;
   current_symbols++ ;
   return symbol;

}

void addSymbolToStack(char c){

   if (current_stack >= STACK_SIZE){

      printf("Maximum depth has been reached.\n");
      exit(EXIT_FAILURE);

   } else {

      symbols_stack[current_stack] = c;
      current_stack++;

   }
}

char getTopSymbol(){

   return symbols_stack[current_stack-1];

}

void removeTopSymbol(){

   symbols_stack[current_stack] = '\0';
   current_stack--;

}

void error(int type, char symbol, int location,int line, int line_pos){

   if (type == 0){

      printf("%s%c%s%d%s%d%s%d","\nMissing end symbol (",symbol,") @ Line ",line,", position ",line_pos," #",location);

   } else if(type == 1) {

      printf("%s%c%s%d%s%d%s%d","\nMissing start symbol for matching (",symbol,") @ Line ",line,", position ",line_pos," #",location);

   } else if(type == 2) {

      printf("%s%c%s%d%s%d%s%d","\nMismatched ending symbol (",symbol,") @ Line ",line,", position ",line_pos," #",location);

   } else if(type == 3) {

      printf("%s%c%s%d%s%d%s%d","\nMismatched ",symbol," quote @ Line ",line,", position ",line_pos," #",location);

   } else if(type == 4) {

      printf("%s%c%s%d%s%d%s%d","\nMissing quote at symbol ",symbol," @ Line ",line,", position ",line_pos," #",location);

   }

}

int main(int argc, char const *argv[]) {

   if(argc < 2){
      printf("%s","No input specified.\n");
      exit(EXIT_FAILURE);
   }

   FILE *fp;
   // character currently being looked at
   char c;
   // previous character
   char prev_c = '\0';
   // loop iterator
   int i;

   // line counter
   int line = 1;
   // line position counter
   int line_pos = 0;
   // program counter
   int pc = 0;

   PAREN = addSymbol('(',')');
   BRACE = addSymbol('{','}');
   SQ_BRACE = addSymbol('[',']');

   // printf("%c",PAREN->start_symbol);
   if(!(fp = fopen(argv[1],"r"))){

      printf("%s","Specified input does not exist.\n");
      exit(EXIT_FAILURE);

   }

   while ((c = fgetc(fp)) != EOF)  {

      pc++; // increase the program counter
      line_pos++;


      if(escaped_char){

         ; // Do absolutley nothing. The character is escaped.


      /*
       * handle the pesky quotes. can't use the symbol structs because the start
       * and the end symbols are the same.
       */
      } else if (c == '\'' && !inside_comment && dub_quotes == 0){


         if (sin_quotes == 0) {

            sin_quotes++;
            inside_string = true;

         } else {

            sin_quotes = 0;
            inside_string = false;

         }

      } else if (c == '"' && !inside_comment && sin_quotes == 0){

         if (dub_quotes == 0){

            dub_quotes++;
            inside_string = true;

         }  else {

            dub_quotes = 0;
            inside_string = false;

         }

      /*
       * looking for identifiers. they must start with the valid characters
       * and we can't be looking at a comment, reading a string, or already
       * looking at an identifier.
       */
      } else if (!inside_comment && !inside_string && !reading_identifier && memchr(VALID_START_CHARS,c,sizeof(VALID_START_CHARS))){

         reading_identifier = true;

      } else if(reading_identifier){

         // if we hit a character that isn't in the list of valid characters
         if (!memchr(VALID_CHARS,c,sizeof(VALID_CHARS)) && !memchr(VALID_START_CHARS,c,sizeof(VALID_START_CHARS))){

            // we stop reading the identifier and increase the number
            reading_identifier = false;
            identifiers++;

         }

      }
      /*
       * Now we are free to look at the symbols that need matching.
       * When the pointer sees an opening symbol, it adds the closing symbol to
       * the stack. If the next closing symbol it sees is not the symbol on the
       * top of the stack, then we know there is an error.
       */
      if (!inside_comment && !inside_string && !reading_identifier) {

         for (int i = 0; i < NUM_SYMBOLS; i++){

            if(c == SYMBOLS[i].start_symbol){

               addSymbolToStack(SYMBOLS[i].end_symbol);

            } else if(c == SYMBOLS[i].end_symbol) {

               if (getTopSymbol() == c){

                  removeTopSymbol();

               } else if (getTopSymbol() != c){

                  error(2,c,pc,line,line_pos);

               } else if (current_stack == 0){

                  error(1,c,pc,line,line_pos);

               } else {

                  error(0,c,pc,line,line_pos);

               }

            }

         }

      }

      /*
       * Handle comments
       * Really nice and simple way to test if we are in a coment or not.
       * Just look at the current char and the previous one.
       */
      if (!inside_comment && c == '/' && prev_c == '/' && comment == 0) {

         inside_comment = true;
         comment = 1;

      }

      if (inside_comment && c == '\n' && comment == 1) {

         inside_comment = false;
         comment = 0;

      }

      if (!inside_comment && c == '*' && prev_c == '/' && comment == 0) {

         inside_comment = true;
         comment = 2;

      }

      if (inside_comment && c == '/' && prev_c == '*' && comment == 2) {

         inside_comment = false;
         comment = 0;

      }

      // uncomment to debug comments
      // printf("%d",comment);

      /*
       * If a quote is being escaped, this will handle that
       */
      if (!inside_comment && c == '\\' && !escaped_char){

         escaped_char = true;

      } else {

         escaped_char = false;

      }

      if (c == EOL && inside_string) {

         error(4,prev_c,pc,line,line_pos);

      } else if( c == EOL){

         line++;
         line_pos = 0;

      }

      // get the previous character
      prev_c = c;

   }

   // we have hit EOF, but there could still be some issues inside.

   // finally, look for any missing opening symbols
   if (current_stack > 0){

      error(0,getTopSymbol(),pc,line,line_pos);

   }

   // also need to check quotes
   if (sin_quotes > 0){

      error(0,'\'',pc,line,line_pos);

   }
   if (dub_quotes > 0){

      error(0,'"',pc,line,line_pos);

   }

   // print out the number of identifiers
   printf("\n%s %d %s","There are",identifiers,"identifiers.");

   printf("%s","\n");
   fclose(fp);

   free(PAREN);
   free(BRACE);
   free(SQ_BRACE);

   return 0;
}
