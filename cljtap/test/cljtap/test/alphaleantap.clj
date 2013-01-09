(ns cljtap.test.alphaleantap
  (:use [cljtap.alphaleantap]
    clojure.test :reload)
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :exclude [is] :as l]
    [clojure.core.logic.nominal :exclude [fresh hash] :as nom])
  (:require [clojure.pprint :as pp]))

(defmacro test-proveo [lvars formula expected-proof]
  `(let [actual-proof# (first (run 1 [q#] (nom/fresh ~lvars (proveo ~formula () () () q#))))]
     (is (= ~expected-proof actual-proof#))))

;; 1. '(<=> (=> p q) (=> (not q) (not p)))
(deftest pp-1
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


;; 18. (E y ,(A x (=> (f ,y) (f ,x))))
(deftest pp-18
  (test-proveo [y]
    `(~'forall
       ~(nom/tie y
          `(~'and
             (~'lit (~'pos (~'app ~'f ((~'var ~y)))))
             (~'lit (~'neg (~'app ~'f ((~'app ~'g ((~'var ~y))))))))))
    '(univ conj savefml savefml univ conj close)))

