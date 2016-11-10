;Ahmed Aldhaheri
;COSC341
;Assignment #5
;Date: 11/10/2016


;counts the number of lists in a given list
(defun f1(L)

	(cond ((null L) 0)									;base case: if list is null return 0
		  ((listp (car L)) (+ 1 (f1 (cdr L)))) 			;recursive case: if its list add 1
		  (T (f1 (cdr L)))								;recursive case: else process tail of list
	)
)

;****************************************************************************************************************************

;decides whether a given list has an atom
(defun f2(L)
	(cond ((null L) nil)								;base case: if list is null return nil
		  ((atom (car L)) T)							;base case: if head of list is atom return true
		  (T (f2 (cdr L)))								;recursive case: else process tail of list
	)
)

;****************************************************************************************************************************

;takes a list of integers L and returns a list
;containing odd integers in L
(defun f3(L)	;;use oddp instead 
	(cond ((null L) nil)								;base case: if list is null return nil 
	      ((equal 1 (mod (car L) 2)) 					;recurisve case: if head is odd insert in list and process tail 
		  (cons (car L) (f3 (cdr L)))) 					
		  (T (f3 (cdr L)))								;recursive case: else process tail of list
	)
)

;****************************************************************************************************************************

;returns the last elment of a given list
(defun f4(L)
	(cond ((null L) nil)								;base case: if list is null return nil
		  ((equal nil (cdr L)) (car L))					;base case: if tail is nil return head 
		  (T (f4 (cdr L)))								;recursive case: process tail of list
	)
)

;****************************************************************************************************************************

;return the reverse of the given list
(defun f5(L)
	(cond ((null L) nil)								;base case: if list is null return nil
		  (T (append (f5 (cdr L)) (list (car L))))		;recursive case: append from stack once list reaches null 
	)													;will append LIFO from end to first
)

;****************************************************************************************************************************

;returns the first list of the list
(defun f6(L)
	(cond ((null L) nil)								;base case: if list is null return nil
		  ((listp (car L)) (car L))						;base case: if head of list contains a list return head 
		  (T (f6 (cdr L)))								;recurisve case: process tail of list
	)
)

;****************************************************************************************************************************

;returns a list containing the lists of a given list
(defun f7(L)
	(cond ((null L) nil)								;base case: if list is null return nil 
		  ((listp (car L)) (cons (car L)(f7 (cdr L))))	;recursive case: if head of list contains list, insert list and process tail 
		  (T (f7 (cdr L)))								;recursive case: else process tail of list
	)
)	

;****************************************************************************************************************************

;returns product of all integers in a given list
(defun f8(L)
	(cond ((null L) 1)											;base case: if list is null return 1
		  ((listp (car L)) (* (fHelp8 (car L)) (f8 (cdr L))))	;recursive case: if list contains list call f8 to process list
		  (T (* (car L) (f8 (cdr L))))							;recursive case:  else process tail of list
	)
)	


;helper function for f8, takes a list and 
;returns product of all integers in list
(defun fHelp8(L)
	(cond ((null L) 1)											;base case: if list is null return 1
		  ((listp (car L)) (* (f8 (car L)) (fHelp8 (cdr L))))	;recursive case: if list contains list call f8 to process list
		  (T (* (car L) (fHelp8 (cdr L))))						;recursive case:  else process tail of list
	)
)

;****************************************************************************************************************************

;removes duplicated from a given list
(defun f9(L)
	(cond ((null L) nil)								;base case: if list is null return nil 
		  ((and (listp (car L)) 						;recursive case: if head of list is a list call helper  
			(help9 (car L) (cdr L))) (f9 (cdr L)))		;function to check if there are any duplicates
		  ((not (member (car L) (cdr L))) 				;recursive case: if is not member of tail append to new list 
			(append (list (car L)) (f9 (cdr L))))
		  (T (f9 (cdr L)))								;recursive case: else process tail of list
	)
)

;helper function for f9, takes two list
;and searches for duplicates in given list
(defun help9(elem L)
	(cond ((null L) nil)						;base case: if list is null return not
		  ((equal elem (car L)) T)				;base case: if elem equal to head of list return true 
		  (T (help9 elem (cdr L)))				;recursive case: else process tail of list 
	)
)

;****************************************************************************************************************************

;returns the intersection of two lists 
(defun f10(L M)
	(cond ((null L) nil)								;base case: if list is null return nil 	
		  ((and (listp (car L))(help9 (car L) M))		;recursive case: if head of list L contains list call helper method
			(append (list (car L)) (f10 (cdr L) M)))	;to search for duplicate list in list M and if true append list
		  ((member (car L) M) (append (list (car L))	;recursive case: if head of list L is a member of list M then append  
			(f10 (cdr L) M)))							;and process tail of L and list M
		  (T (f10 (cdr L) M))							;recursive case: else process tail of list L and list M
	)
)

;****************************************************************************************************************************

