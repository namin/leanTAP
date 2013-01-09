(ns cljtap.test.alphaleantap
  (:use [cljtap.alphaleantap]
        [cljtap.nnf]
    clojure.test :reload)
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :exclude [is] :as l]
    [clojure.core.logic.nominal :exclude [fresh hash] :as nom])
  (:require [clojure.pprint :as pp]))

(defmacro test-pp [n axioms theorem]
  `(do
     (println "Pelletier Problem" ~n)
     (time (do-prove-th ~axioms ~theorem))
     (println)))

(deftest test-pelletier-problems
;; 1 - 5
;; Micah Linnemeier

(test-pp 1 () '(<=> (=> p q) (=> (not q) (not p))))
(test-pp 2 () '(=>  (not (not p)) p))
(test-pp 3 () '(=> (not (=> p q)) (=> q p)))
(test-pp 4 () '(<=> (=> (not p) q) (=> (not q) p)))
(test-pp 5 () '(=> (=> (or p q) (or p r)) (or p (=> q r))))

;; 6 - 10
;; Adam Hinz

(test-pp 6 () '(or p (not p)))
(test-pp 7 () '(or p (not (not (not p)))))
(test-pp 8 () '(=> (=> (=> p q) p) p))

(test-pp 9 ()
    '(=> (and (or p q)
	      (and
               (or (not p) q)
               (or p (not q))))
	 (not (or (not p) (not q)))))

(test-pp 10
    '((=> q r)
      (=> r (and p q))
      (=> p (or q r)))
    '(<=> p q))

;; 11 - 15
;; Joe Near

(test-pp 11 () '(<=> p p))
(test-pp 12 () '(<=> (<=> (<=> p q) r) (<=> p (<=> q r))))
(test-pp 13 () '(<=> (or p (and q r)) (and (or p q) (or p r))))
(test-pp 14 () '(<=> (<=> p q) (and (or q (not p)) (or (not q) p))))
(test-pp 15 () '(<=> (=> p q) (or (not p) q)))

;; 16 - 20
;; Micah Linnemeier

(test-pp 16 () '(or (=> p q) (=> q p)))

(test-pp 17 ()
    '(<=> (=> (and p (=> q r)) s)
          (and (or (not p) (or q s)) (or (not p) (or (not r) s)))))

(test-pp 18 () (E y (A x `(~'=> (~'f ~y) (~'f ~x)))))

(test-pp 19 ()
    (E x (A y (A z `(~'=>
                      (~'=> (~'p ~y) (~'q ~z))
                      (~'=> (~'p ~x) (~'q ~x)))))))

(test-pp 20 ()
    (A x (A y (E z (A w
                      `(~'=>
                         (~'=> (~'and (~'p ~x) (~'q ~y))
                               (~'and (~'r ~z) (~'s ~w)))
                         ~(E x (E y
                                 `(~'=>
                                    (~'and (~'p ~x) (~'q ~y))
                                    ~(E z
                                       `(~'r ~z)))))))))))

;; 21 - 30
;; Micah Linnemeier

(test-pp 21
    `(~(E x `(~'=> ~'p (~'f ~x))) ~(E x `(~'=> (~'f ~x) ~'p)))
    (E x `(~'<=> ~'p (~'f ~x))))

(test-pp 22 () `(~'=> ~(A x `(~'<=> ~'p (~'f ~x))) (~'<=> ~'p ~(A x `(~'f ~x)))))
(test-pp 23 () `(~'<=> ~(A x `(~'or ~'p (~'f ~x))) (~'or ~'p ~(A x `(~'f ~x)))))

(test-pp 24
    `((~'not ~(E x `(~'and (~'s ~x) (~'q ~x))))
      ~(A x `(~'=> (~'p ~x) (~'or (~'q ~x) (~'r ~x))))
      (~'not (~'=> ~(E x `(~'p ~x)) ~(E x `(~'q ~x))))
      ~(A x `(~'=> (~'or (~'q ~x) (~'r ~x)) (~'s ~x))))
    (E x `(~'and (~'p ~x) (~'r ~x))))

(test-pp 25
    `(~(E x `(~'p ~x))
      ~(A x `(~'=> (~'f ~x) (~'and (~'not (~'g ~x)) (~'r ~x))))
      ~(A x `(~'=> (~'p ~x) (~'and (~'g ~x) (~'f ~x))))
      (~'or ~(A x `(~'=> (~'p ~x) (~'r ~x))) ~(E x `(~'and (~'p ~x) (~'r ~x)))))
     (E x `(~'and (~'p ~x) (~'r ~x))))

(test-pp 26
    `((~'<=> ~(E x `(~'p ~x)) ~(E x `(~'q ~x)))
      ~(A x (A y `(~'=> (~'and (~'p ~x) (~'q ~y)) (~'<=> (~'r ~x) (~'s ~y))))))
    `(~'<=> ~(A x `(~'=> (~'p ~x) (~'r ~x))) ~(A x `(~'=> (~'q ~x) (~'s ~x)))))

(test-pp 27
    `(~(E x `(~'and (~'f ~x) (~'not (~'g ~x))))
      ~(A x `(~'=> (~'f ~x) (~'h ~x)))
      ~(A x `(~'=> (~'and (~'j ~x) (~'i ~x)) (~'f ~x)))
      (~'=> ~(E x `(~'and (~'h ~x) (~'not (~'g ~x))))
            ~(A x `(~'=> (~'i ~x) (~'not (~'h ~x))))))
    (A x `(~'=> (~'j ~x) (~'not (~'i ~x)))))

(test-pp 28
    `(~(A x `(~'=> (~'p ~x) ~(A x `(~'q ~x))))
      (~'=> ~(A x `(~'or (~'q ~x) (~'r ~x))) ~(E x `(~'and (~'q ~x) (~'s ~x))))
      (~'=> ~(E x `(~'s ~x)) ~(A x `(~'=> (~'f ~x) (~'g ~x)))))
    (A x `(~'=> (~'and (~'p ~x) (~'f ~x)) (~'g ~x))))

(test-pp 29
    `((~'and ~(E x `(~'f ~x)) ~(E x `(~'g ~x))))
    `(~'<=>
      (~'and
       ~(A x `(~'=> (~'f ~x) (~'h ~x)))
       ~(A x `(~'=> (~'g ~x) (~'j ~x))))
       ~(A x (A y `(~'=> (~'and (~'f ~x) (~'g ~y)) (~'and (~'h ~x) (~'j ~y)))))))

(test-pp 30
    `(~(A x `(~'=> (~'or (~'f ~x) (~'g ~x)) (~'not (~'h ~x))))
      ~(A x `(~'=> (~'=> (~'g ~x) (~'not (~'i ~x))) (~'and (~'f ~x) (~'h ~x)))))
    (A x `(~'i ~x)))


;; 31 - 40
;; Adam Hinz

(test-pp 31
    `((~'not ~(E x `(~'and (~'f ~x) (~'or (~'g ~x) (~'h ~x)))))
      ~(E x `(~'and (~'i ~x) (~'f ~x)))
      ~(A x `(~'=> (~'not (~'h ~x)) (~'j ~x))))
    (E x `(~'and (~'i ~x) (~'j ~x))))

(test-pp 32
    `(~(A x `(~'=> (~'and (~'f ~x) (~'or (~'g ~x) (~'h ~x))) (~'i ~x)))
      ~(A x `(~'=> (~'and (~'i ~x) (~'h ~x)) (~'j ~x)))
      ~(A x `(~'=> (~'k ~x) (~'h ~x))))
    (A x `(~'=> (~'and (~'f ~x) (~'k ~x)) (~'j ~x))))

(test-pp 33
    ()
    `(~'<=> ~(A x `(~'=> (~'and (~'p a) (~'=> (~'p ~x) (~'p b))) (~'p c)))
            ~(A x `(~'and (~'or (~'not (~'p a)) (~'or (~'p ~x) (~'p c)))
                          (~'or (~'not (~'p a)) (~'or (~'not (~'p b)) (~'p c)))))))

(test-pp 34
    ()
    `(~'<=>
      (~'<=> ~(E x (A y `(~'<=> (~'p ~x) (~'p ~y))))
             (~'<=> ~(E x `(~'q ~x)) ~(A y `(~'q ~y))))
      (~'<=> ~(E x (A y `(~'<=> (~'q ~x) (~'q ~y))))
             (~'<=> ~(E x `(~'p ~x)) ~(A y `(~'p ~y))))))

(test-pp 35
    ()
    (E x (E y `(~'=> (~'p ~x ~y) ~(A x (A y `(~'p ~x ~y)))))))

(test-pp 36
    `(~(A x (E y `(~'f ~x ~y)))
      ~(A x (E y `(~'g ~x ~y)))
      ~(A x (A y `(~'=> (~'or (~'f ~x ~y) (~'g ~x ~y))
                        ~(A z `(~'=> (~'or (~'f ~y ~z) (~'g ~y ~z)) (~'h ~x ~z)))))))
    (A x (E y `(~'h ~x ~y))))


(test-pp 37
    `(~(A z (E w (A x (E y `(~'and (~'=> (~'p ~x ~z) (~'p ~y ~w))
                                   (~'and (~'p ~y ~z)
                                          (~'=> (~'p ~y ~w) ~(E u `(~'q ~u ~w)))))))))
      ~(A x (A z `(~'=> (~'not (~'p ~x ~z)) ~(E y `(~'q ~y ~z)))))
      (~'=> ~(E x (E y `(~'q ~x ~y))) ~(A x `(~'r ~x ~x))))
    (A x (E y `(~'r ~x ~y))))

(test-pp 38
    ()
    `(~'<=> ~(A x `(~'=> (~'and (~'p a) (~'=> (~'p ~x) ~(E y `(~'and (~'p ~y) (~'r ~x ~y)))))
                    ~(E z (E w `(~'and (~'p ~z) (~'and (~'r ~x ~w) (~'r ~w ~z)))))))
            ~(A x `(~'and
                     (~'or (~'not (~'p a)) (~'or (~'p ~x) ~(E z (E w `(~'and (~'p ~z)
                                                                        (~'and (~'r ~x ~w)
                                                                          (~'r ~w ~z)))))))
                     (~'or (~'not (~'p a))
                       (~'or (~'not ~(E y `(~'and (~'p ~y) (~'r ~x ~y))))
                         ~(E z (E w `(~'and (~'p ~z)
                                       (~'and (~'r ~x ~w)
                                         (~'r ~w ~z)))))))))))

(test-pp 39
    ()
    `(~'not ~(E x (A y `(~'<=> (~'f ~y ~x) (~'not (~'f ~y ~y)))))))

(test-pp 40
    ()
    `(~'=> ~(E y (A x `(~'<=> (~'f ~x ~y) (~'f ~x ~x))))
         (~'not ~(A x (E y (A z `(~'<=> (~'f ~z ~y) (~'not (~'f ~z ~x)))))))))



;; 41 - 50
;; (Assigned to Joe Near)

(test-pp 41
    `(~(A z (E y (A x `(~'<=> (~'f ~x ~y) (~'and (~'f ~x ~z) (~'not (~'f ~x ~x))))))))
    `(~'not ~(E z (A x `(~'f ~x ~z)))))

(test-pp 42
    ()
    `(~'not ~(E y (A x `(~'<=> (~'f ~x ~y) (~'not ~(E z `(~'and (~'f ~x ~z) (~'f ~z ~x)))))))))

(test-pp 43
    `(~(A x (A y `(~'<=> (~'q ~x ~y) ~(A z `(~'<=> (~'f ~z ~x) (~'f ~z ~y)))))))
    `~(A x (A y `(~'<=> (~'q ~x ~y) (~'q ~y ~x)))))

(test-pp 44
    `(~(A x `(~'and (~'=> (~'f ~x) ~(E y `(~'and (~'g ~y) (~'h ~x ~y))))
                    ~(E y `(~'and (~'g ~y) (~'not (~'h ~x ~y))))))
      ~(E x `(~'and (~'j ~x) ~(A y `(~'=> (~'g ~y) (~'h ~x ~y))))))
    (E x `(~'and (~'j ~x) (~'not (~'f ~x)))))

(test-pp 45
    `(~(A x `(~'and (~'f ~x) ~(A y `(~'=> (~'and (~'g ~y) (~'=> (~'h ~x ~y) (~'j ~x ~y)))
                                          ~(A y `(~'and (~'g ~y) (~'=> (~'h ~x ~y) (~'k ~y))))))))
      (~'not ~(E y `(~'and (~'l ~y) (~'k ~y))))
      ~(E x `(~'and (~'and (~'f ~x) ~(A y `(~'=> (~'h ~x ~y) (~'l ~y))))
                 ~(A y `(~'and (~'g ~y) (~'=> (~'h ~x ~y) (~'j ~x ~y)))))))
    (E x `(~'and (~'f ~x) (~'not ~(E y `(~'and (~'g ~y) (~'h ~x ~y)))))))

(test-pp 46
    `(~(A x `(~'=> (~'and (~'f ~x) ~(A y `(~'=> (~'and (~'f ~y) (~'h ~y ~x))
                                                (~'g ~y))))
                   (~'g ~x)))
      (~'=> ~(E x `(~'and (~'f ~x) (~'not (~'g ~x))))
            ~(E x `(~'and (~'f ~x) (~'and (~'not (~'g ~x))
                                   ~(A y `(~'=> (~'and (~'f ~y) (~'not (~'g ~y))) (~'j ~x ~y)))))))
      ~(A x (A y `(~'=> (~'and (~'f ~x) (~'and (~'f ~y) (~'h ~x ~y))) (~'not (~'j ~y ~x))))))
    (A x `(~'=> (~'f ~x) (~'g ~x)))))

(defmacro test-proveo [lvars formula expected-proof]
  `(let [actual-proof# (first (run 1 [q#] (nom/fresh ~lvars (proveo ~formula () () () q#))))]
     (is (= ~expected-proof actual-proof#))))

(deftest test-proveo-1
  (test-proveo []
    '(or
       (and
         (and (lit (pos (sym p))) (lit (neg (sym q))))
         (or (lit (pos (sym q))) (lit (neg (sym p)))))
       (and
         (or (lit (neg (sym p))) (lit (pos (sym q))))
         (and (lit (neg (sym q))) (lit (pos (sym p))))))
    '(split
       (conj conj savefml savefml split (close) (close))
       (conj
         split
         (savefml conj savefml close)
         (savefml conj close)))))


(deftest test-proveo-18
  (test-proveo [y]
    `(~'forall
       ~(nom/tie y
          `(~'and
             (~'lit (~'pos (~'app ~'f (~'var ~y))))
             (~'lit (~'neg (~'app ~'f (~'app ~'g (~'var ~y))))))))
    '(univ conj savefml savefml univ conj close)))
