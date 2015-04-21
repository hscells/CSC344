/**
 * typecheck.prolog
 * Harry Scells 2015
 * CSC344 SUNY Oswego
 */

% make some predicates to type check
boolean(X):- X = true, ! | X = false, !.
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
   !.

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

inherited(C,P):- bind(C,P), bind(P,X), X = class.

% Infer the values of single types
infer(X,T):- bind(X,Y), type(Y,T), !.
% Infer the values of inherited types
infer(X,Y,T):- bind(X,Y,Z), bind(X,class), type(Z,T), !.
infer(X,Y,T):- inherited(X,P), bind(P,Y,Z), type(Z,T), !.
