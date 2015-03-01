(setq p1 '(and x (or x (and y (not z)))))
(setq p2 '(and (and z nil) (or x 1)))
(setq p3 '(or 1 a))

(defun andexp (e1 e2) (list 'and e1 e2))
(defun orexp  (e1 e2) (list 'or e1 e2))
(defun notexp (e1) (list 'not e1))

(defun subst (target repalcement l)
   (cond
      ((null l)
         nil
      )
      ((listp l)
         (cons (subst target replacement (car l)) (subst target replacement (cdr l)))
      )
      ((eq target (car l))
         (cons replacement (subst target replacement (cdr l)))
      )
      (t
         (cons (car l) (subst target repalcement (cdr l)))
      )
   )
)

(defun bind-values (exp bindings)
   (lambda )
   )

(defun simplify (exp)
   nil)

(defun evalexp (exp bindings)
   (simplify (bind-values bindings exp)))
