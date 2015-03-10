;;; CSC344 Assignment 2
;;; Harry Scells 2015

;; Functions with -x are my own versions of already implemented functions in the
;; common lisp library


;; Basic setup of things which will be used for testing
(setq p1 '(and x (or x (and y (not z)))))
(setq p2 '(and (and z nil) (or x t)))
(setq p3 '(or t a))

;; Copy and paste of given functions that need testing
(defun andexp (e1 e2) (list 'and e1 e2))
(defun orexp  (e1 e2) (list 'or e1 e2))
(defun notexp (e1) (list 'not e1))

;; Re-implementation of the subst function
(defun subst-x (target replacement l)
   (cond
      ((null l) nil)
      ((listp l)
         (cons
            (subst target replacement (car l))
            (subst target replacement (cdr l))))
      ((eq target (car l))
         (cons replacement (subst-x target replacement (cdr l))))
      (t
         (cons (car l) (subst-x target replacement (cdr l))))))

;; Bind multiple values into an expression
(defun bind-values (exp bindings)
   (dolist (l bindings)
      (setf exp (subst-x (cadr l) (car l) exp))) exp)

;; Simplify a boolean expression
(defun simplify (exp)
   (cond
      ((null exp) nil)
      ((atom exp) exp)
      ((eq (car exp) 'or)
         (cond
            ((and (listp (cadr exp)) (listp (caddr exp)))
               (oreval (list (car exp) (simplify (cadr exp)) (simplify (caddr exp)))))
            ((listp (cadr exp))
               (oreval (list (car exp) (simplify (cadr exp)) (caddr exp))))
            ((listp (caddr exp))
               (oreval (list (car exp) (cadr exp) (simplify (caddr exp)))))
            (t
               (oreval exp))))
      ((eq (car exp) 'and)
         (cond
            ((and (listp (cadr exp)) (listp (caddr exp)))
               (andeval (list (car exp) (simplify (cadr exp)) (simplify (caddr exp)))))
            ((listp (cadr exp))
               (andeval (list (car exp) (simplify (cadr exp)) (caddr exp))))
            ((listp (caddr exp))
               (andeval (list (car exp) (cadr exp) (simplify (caddr exp)))))
            (t
               (andeval exp))))
      ((eq (car exp) 'not)
         (noteval exp))))

(defun noteval (exp)
   (cond
      ((eq (second exp) nil) t)
      ((eq (second exp) t) nil)
      ((eq (second exp) 1) nil)
      ((listp (second exp))
         (cond
            ((eq (caadr exp) 'and)
               (return-from noteval
                  (simplify (list 'or (list 'not (cadr (cadr exp))) (list 'not (caddr (cadr exp)))))))
            ((eq (caadr exp) 'or)
               (return-from noteval
                  (simplify (list 'and (list 'not (cadr (cadr exp))) (list 'not (caddr (cadr exp)))))))))
      (t exp)))

(defun oreval (exp)
   (cond
      ((and (not (null (second exp))) (null (third exp))) (second exp))
      ((and (not (null (third exp))) (null (second exp))) (third exp))
      ((eq (second exp) t) t)
      ((eq (third exp) t) t)
      ((eq (second exp) 1) 1)
      ((eq (third exp) 1) 1)
      ((eq (second exp) (third exp)) (second exp))
      ((and (null (second exp)) (null (third exp))) nil)
      (t exp)))

(defun andeval (exp)
   (cond
      ((null (second exp)) nil)
      ((null (third exp)) nil)
      ((eq (second exp) (third exp)) (third exp))
      ((and (not (null (second exp))) (eq (third exp) t)) (second exp))
      ((and (not (null (third exp))) (eq (second exp) t)) (third exp))
      ((and (not (null (second exp))) (eq (third exp) 1)) (second exp))
      ((and (not (null (third exp))) (eq (second exp) 1)) (third exp))
      (t exp)))

;; Evaluate an expression with the given bindings
(defun evalexp (exp bindings)
   (simplify (bind-values exp bindings )))

(defun run-tests ()
   (format t "~S~%" (simplify '(or x nil)))
   (format t "~S~%" (simplify '(or nil x)))
   (format t "~S~%" (simplify '(or 1 x)))
   (format t "~S~%" (simplify '(or x 1)))
   (format t "~S~%" (simplify '(and x nil)))
   (format t "~S~%" (simplify '(and nil x)))
   (format t "~S~%" (simplify '(and x 1)))
   (format t "~S~%" (simplify '(and 1 x)))
   (format t "~S~%" (simplify '(not nil)))
   (format t "~S~%" (simplify '(not 1)))
   (format t "~S~%" (simplify '(not (and x y))))
   (format t "~S~%" (simplify '(not (or x y)))))
