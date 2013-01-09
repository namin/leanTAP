(ns cljtap.alphaleantap
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :exclude [is] :as l]
    [clojure.core.logic.nominal :exclude [fresh hash] :as nom]))

(declare subst subst-fmlo subst-tm* lookupo negateo proveo)

(defn subst [fml env out]
  (conde
    [(fresh [l r]
       (== `(~'pos ~l) fml)
       (== `(~'pos ~r) out)
       (subst-fmlo l env r))]
    [(fresh [l r]
       (== `(~'neg ~l) fml)
       (== `(~'neg ~r) out)
       (subst-fmlo l env r))]))

(defn subst-fmlo [fml env out]
  (conde
    [(fresh [a]
       (== `(~'var ~a) fml)
       (lookupo a env out))]
    [(fresh [a]
       (== `(~'sym ~a) fml)
       (== fml out))]
    [(fresh [f d r]
       (conso 'app (lcons f d) fml)
       (conso 'app (lcons f r) out)
       (subst-tm* d env r))]))

(defn subst-tm* [tm* env out]
  (conde
    [(== () tm*) (== () out)]
    [(fresh [a d r1 r2]
       (conso a d tm*)
       (conso r1 r2 out)
       (subst-fmlo a env r1)
       (subst-tm* d env r2))]))

(defn lookupo [x env out]
  (fresh [a d va vd]
    (conde
      [(conso [x out] d env)]
      [(conso a d env)
       (lookupo x d out)])))

(defn negateo [fml neg]
  (fresh [lit]
    (conde
      [(== `(~'pos ~lit) fml)
       (== `(~'neg ~lit) neg)]
      [(== `(~'neg ~lit) fml)
       (== `(~'pos ~lit) neg)])))

(defn proveo [fml unexp lits env proof]
  (conde
    [(fresh [a b p1]
       (== `(~'and ~a ~b) fml)
       (conso 'conj p1 proof)
       (proveo a (lcons b unexp) lits env p1))]
    [(fresh [a b p1 p2]
       (== `(~'or ~a ~b) fml)
       (== `(~'split ~p1 ~p2) proof)
       (proveo a unexp lits env p1)
       (proveo b unexp lits env p2))]
    [(nom/fresh [v]
       (fresh [x1 b unexp1 p1]
         (== `(~'forall ~(nom/tie v b)) fml)
         (conso 'univ p1 proof)
         (appendo unexp (list fml) unexp1)
         (proveo b unexp1 lits (lcons [v x1] env) p1)))]
    [(fresh [lit new-lit]
       (== `(~'lit ~lit) fml)
       (subst lit env new-lit)
       (conde
         [(fresh [l rest neg p1]
            (conso l rest lits)
            (== `(~'close) proof)
            (negateo new-lit neg)
            (membero neg lits))]
         [(fresh [next unexp1 p1]
            (conso next unexp1 unexp)
            (conso 'savefml p1 proof)
            (proveo next unexp1 (lcons new-lit lits) env p1))]))]))
