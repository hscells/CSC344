#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

// stack to store the symbols
#define STACK_SIZE 1024

// total number of symbols we are looking for
#define NUM_SYMBOLS 5

typedef struct{
   char start_symbol;
   char end_symbol;
} t_symbol;

t_symbol *PAREN;
t_symbol *BRACE;
t_symbol *SQ_BRACE;

t_symbol SYMBOLS[NUM_SYMBOLS];

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
                                 'W', 'X', 'Y', 'Z'};
const char VALID_CHARS[] = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '_'};

t_symbol *addSymbol(char s, char e){

   t_symbol *symbol = (t_symbol*) malloc(sizeof(t_symbol));
   symbol->start_symbol = s;
   symbol->end_symbol = e;
   SYMBOLS[current_symbols] = *symbol;
   current_symbols++ ;
   return symbol;

}

void addSymbolToStack(char c){

   symbols_stack[current_stack] = c;
   current_stack++;

}

char getTopSymbol(){

   return symbols_stack[current_stack-1];

}

void removeTopSymbol(){

   symbols_stack[current_stack] = '\0';
   current_stack--;

}

void error(int type, char symbol, int location){

   if (type == 0){

      printf("%s%c%s%d","\nMissing end symbol (",symbol,") at location: ",location);

   } else if(type == 1) {

      printf("%s%c%s%d","\nMissing start symbol for matching (",symbol,") at location: ",location);

   } else if(type == 2) {

      printf("%s%c%s%d","\nMismatched ending symbol (",symbol,") at location: ",location);

   }

}

int main(int argc, char const *argv[]) {

   if(argc < 2){
      printf("%s","No input specified.\n");
      exit(EXIT_FAILURE);
   }

   FILE *fp;
   int c;
   int i;
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

      pc++;

      // handle the pesky quotes. can't use the symbol structs because the start
      // and the end symbols are the same
      if (c == '\''){

         if (sin_quotes == 0)
            sin_quotes++;
         else
            sin_quotes = 0;

      } else if (c == '"'){

         if (dub_quotes == 0)
            dub_quotes++;
         else
            dub_quotes = 0;

      // looking for identifiers. they must start with the valid characters
      } else if (!reading_identifier && memchr(VALID_START_CHARS,c,sizeof(VALID_START_CHARS))){

         reading_identifier = true;

      } else if(reading_identifier){

         // if we hit a character that isn't in the list of valid characters
         if (!memchr(VALID_CHARS,c,sizeof(VALID_CHARS)) && !memchr(VALID_START_CHARS,c,sizeof(VALID_START_CHARS))){

            // we stop reading the identifier and increase the number
            reading_identifier = false;
            identifiers++;

         }

      }
      // now we are free to look at the symbols that need matching
      if (!reading_identifier) {

         for (int i = 0; i < NUM_SYMBOLS; i++){

            if(c == SYMBOLS[i].start_symbol){

               addSymbolToStack(SYMBOLS[i].end_symbol);

            } else if(c == SYMBOLS[i].end_symbol) {

               if (getTopSymbol() == c){

                  removeTopSymbol();

               } else if (getTopSymbol() != c){

                  error(2,c,pc);

               } else if (current_stack == 0){

                  error(1,c,pc);

               } else {

                  error(0,c,pc);

               }

            }

         }

      }

   }

   // finally, look for any missing opening symbols
   if (current_stack > 0){

      error(0,getTopSymbol(),pc);

   }

   // also need to check quotes
   if (sin_quotes > 0){

      error(0,'\'',pc);

   }
   if (dub_quotes > 0){

      error(0,'"',pc);

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
