/**
 * typecheck.prolog
 * Harry Scells 2015
 * CSC344 SUNY Oswego
 */

% make some predicates to type check
string(X):- forall(member(Y,X),number(Y)) | X = string.
boolean(X):- X = true | X = false.
int(X):- integer(X) | X = int.
char(X):- atom(X) | X = char.
complex(X):- float(X) | X = float.
class(X):- X = class.

% Bind some variables
bind(x,int).
bind(y,3).
bind(h,string).
bind(w,string).
bind(hw,"Hello World!").
bind(animal,class).
bind(aString,string).

% Also bind some variables that are inherited
bind(animal,hunger,0).
bind(animal,eatFood,int).
bind(aString,length,int).

% Returns the parent of the method of a class
parent(X,P,T):- bind(P,X,T).

% Infer the values of single types
infer(X,T):- bind(X,Y), int(Y), T = int, !.
infer(X,T):- bind(X,Y), string(Y), T = string, !.
infer(X,T):- bind(X,Y), char(Y), T = char, !.
infer(X,T):- bind(X,Y), complex(Y), T = float, !.
infer(X,T):- bind(X,Y), boolean(Y), T = bool, !.
infer(X,T):- bind(X,Y), class(Y), T = class, !.

% Infer the values of inherited types
infer(X,Y,T):- bind(X,Y,Z), int(Z), T = int, !.
infer(X,Y,T):- bind(X,Y,Z), string(Z), T = string, !.
infer(X,Y,T):- bind(X,Y,Z), char(Z), T = char, !.
infer(X,Y,T):- bind(X,Y,Z), complex(Z), T = float, !.
infer(X,Y,T):- bind(X,Y,Z), boolean(Z), T = bool, !.
infer(X,Y,T):- bind(X,Y,Z), class(Z), T = class, !.
