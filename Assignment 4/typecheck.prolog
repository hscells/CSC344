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
bind(hw,string).
bind(animal,class).
bind(eatFood,int).

inherit(animal,eatFood).

infer(X):- bind(X,Y), int(Y), write(int).
infer(X):- bind(X,Y), string(Y), write(string).
infer(X):- bind(X,Y), char(Y), write(char).
infer(X):- bind(X,Y), complex(Y), write(float).
infer(X):- bind(X,Y), boolean(Y), write(bool).
infer(X):- bind(X,Y), class(Y), write(class).
