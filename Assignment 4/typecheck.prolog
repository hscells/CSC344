/**
 * typecheck.prolog
 * Harry Scells 2015
 * CSC344 SUNY Oswego
 */
 cdr([_|T],T).
 car([H|_],H).

% make some predicates to type check
boolean(X):- X = true, ! | X = fals | X = boolean, !.
int(X):- integer(X), ! | X = int, !.
complex(X):- float(X), ! | X = float, !.
class(X):- inherited(X,P), bind(P,class), ! | X = class, ! | bind(X,Y), class(Y).
char(X):- atom(X), ! | X = char, !.
string(X):- forall(member(Y,X),number(Y)), ! | X = string, !.

type(X,T):-
   boolean(X), T = boolean, ! |
   int(X), T = int, ! |
   complex(X), T = complex, ! |
   class(X), T = class, ! |
   char(X), T = char, ! |
   not(string(X)), T = undefined, ! |
   string(X), T = string, !,
   T = undefined, !.

% Bind some variables
bind(x,int).
bind(y,3).
bind(b,true).
bind(h,string).
bind(w,string).
bind(hw,"Hello World!").
bind(animal,class).
bind(cat,animal).
bind(dog,animal).
bind(aString,string).

% Also bind some variables that are inherited
bind(animal,hunger,0).
bind(animal,eatFood,1).
bind(aString,length,int).

def(add,[int,int]).

inherited(C,P):- bind(C,P), bind(P,X), X = class.

% Infer the values of single types
infer(X,T):- bind(X,Y), type(Y,T), !.
% Infer if the arguments in a function are correct
infer(X,A):-
   % while the length of the args is > 0
   length(A,Q), Q > 0,
   % get the bound argument list
   def(X,L),
   infer(X,A,L).

infer(A,L):-
   % while the length of the args is > 0
   length(A,Q), Q > 0,
   car(A,A1), type(A1,T),
   car(L,L1), type(L1,R),

   write(T = R),

   cdr(A,A2), cdr(L,L2),
   infer(A2,L2).

% Infer the values of inherited types
infer(X,Y,T):- bind(X,Y,Z), bind(X,class), type(Z,T), !.
infer(X,Y,T):- inherited(X,P), bind(P,Y,Z), type(Z,T), !.
