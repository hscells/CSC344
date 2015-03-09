;;; CSC344 Assignment 2
;;; Harry Scells 2015

;; Functions with -x are my own versions of already implemented functions in the
;; common lisp library


;; Basic setup of things which will be used for testing
(setq p1 '(and x (or x (and y (not z)))))
(setq p2 '(and (and z nil) (or x 1)))
(setq p3 '(or 1 a))

;; Copy and paste of given functions that need testing
(defun andexp (e1 e2) (list 'and e1 e2))
(defun orexp  (e1 e2) (list 'or e1 e2))
(defun notexp (e1) (list 'not e1))

;; Re-implementation of the subst function
(defun subst-x (target replacement l)
   (cond
      ((null l)
         nil
      )
      ((listp l)
         (cons
            (subst target replacement (car l))
            (subst target replacement (cdr l)))
      )
      ((eq target (car l))
         (cons replacement (subst-x target replacement (cdr l)))
      )
      (t
         (cons (car l) (subst-x target replacement (cdr l))))))

;; Bind multiple values into an expression
(defun bind-values (exp bindings)
   (dolist (l bindings)
      (setf exp (subst-x (cadr l) (car l) exp)))exp)

;; Simplify a boolean expression
(defun simplify (exp)
   (cond
      ((null exp) nil)
      ((atom exp) exp)
      ((eq (car exp) 'or) (oreval (list (car exp) (simplify (cadr exp)) (simplify (caddr exp)))))
      ((eq (car exp) 'and) (andeval (list (car exp) (simplify (cadr exp)) (simplify (caddr exp)))))
      ((eq (car exp) 'not) (noteval (list (car exp) (simplify (cadr exp)) (simplify (caddr exp)))))))

(defun noteval (exp)
   (cond
      ((eq (second exp) nil) t)
      (t nil)))

(defun oreval (exp)
   (cond
      ((eq (or (second exp) (third exp)) nil) nil)
      (t (or (second exp) (third exp)))))

(defun andeval (exp)
   (cond
      ((eq (and (second exp) (third exp)) nil) nil)
      (t
         (cond
            ((eq (second exp) (third exp)) (third exp))
            ((not (eq (second exp) (third exp))) nil)))))

;; Evaluate an expression with the given bindings
(defun evalexp (exp bindings)
   (simplify (bind-values exp bindings )))
