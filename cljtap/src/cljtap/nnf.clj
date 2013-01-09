(ns cljtap.nnf
  (:refer-clojure :exclude [==])
  (:use [clojure.core.logic :exclude [is] :as l]
        [clojure.core.logic.nominal :exclude [fresh hash] :as nom])
  (:use [clojure.core.match :only [match]]))

;; TODO(namin): make-nom should be in core.logic.nominal
(defn make-nom [x]
  (nom (lvar x)))

(defmacro A [var body]
  `(list '~'forall '~var (fn [~var] ~body)))

(defmacro E [var body]
  `(list '~'ex '~var (fn [~var] ~body)))

(defn third [x] (second (rest x)))

(defn show-formula [fml]
  (cond
    (not (seq? fml)) fml
    (= (first fml) 'var) fml
    (= (first fml) 'forall) (let [var (second fml)]
                              `(~'A ~var ~(show-formula ((third fml) var))))
    (= (first fml) 'ex) (let [var (second fml)]
                          `(~'E ~var ~(show-formula ((third fml) var))))
    :else (cons (first fml) (map show-formula (rest fml)))))

(defn fvt [fml]
  (match [fml]
    [(['var (x :guard nom?)] :seq)]
    #{`(~'var ~x)}
    [([f & args] :seq)]
    (apply clojure.set/union (map fvt args))
    :else #{}))

(defn fv [fml]
  (match [fml]
    [(['var (x :guard nom?)] :seq)]
    #{`(~'var ~x)}
    [(['not x] :seq)]
    (fv x)
    [([(:or 'and 'or '=> '<=>) x y] :seq)]
    (clojure.set/union (fv x) (fv y))
    [([(:or 'forall 'ex) x t] :seq)]
    (clojure.set/difference (fv t) #{x})
    [([f & args] :seq)]
    (apply clojure.set/union (map fvt args))
    :else #{}))

(declare handle-lit)

(defn handle-lit [lit]
  (match [lit]
    [(['var (x :guard nom?)] :seq)]
    `(~'var ~x)
    [(x :guard symbol?)]
    `(~'sym ~x)
    [([(f :guard symbol?) & d] :seq)]
    `(~'app ~f ~@(map handle-lit d))))

(defn nnf [fml]
  (match [fml]

    ; trivial re-writing using the standard tautologies
    [(['not (['not a] :seq)] :seq)]
    (nnf a)
    [(['not (['forall var gfml] :seq)] :seq)]
    (nnf `(~'ex ~var ~(fn [x] `(~'not ~(gfml x)))))
    [(['not (['ex var gfml] :seq)] :seq)]
    (nnf `(~'forall ~var ~(fn [x] `(~'not ~(gfml x)))))
    [(['not (['and & fmls] :seq)] :seq)]
    (nnf `(~'or ~@(map (fn [x] `(~'not ~x)) fmls)))
    [(['not (['or & fmls] :seq)] :seq)]
    (nnf `(~'and ~@(map (fn [x] `(~'not ~x)) fmls)))
    [(['=> a b] :seq)]
    (nnf `(~'or (~'not ~a) ~b))
    [(['not (['=> a b] :seq)] :seq)]
    (nnf `(~'and ~a (~'not ~b)))
    [(['<=> a b] :seq)]
    (nnf `(~'or (~'and ~a ~b) (~'and (~'not ~a) (~'not ~b))))
    [(['not (['<=> a b] :seq)] :seq)]
    (nnf `(~'or (~'and (~'not ~a) ~b) (~'and ~a (~'not ~b))))

    ; propagate inside
    [(['forall x gfml] :seq)]
    (let [v (make-nom x)]
      `(~'forall ~(nom/tie v (nnf (gfml `(~'var ~v))))))
    [(['and & fmls] :seq)]
    `(~'and ~@(map nnf fmls))
    [(['or & fmls] :seq)]
    `(~'or ~@(map nnf fmls))

    ; skolemization
    [(['ex v gfml] :seq)]
    (let [fvars (seq (fv (show-formula `(~'ex ~v ~gfml))))
          fml-ex (cons (gensym) fvars)
          fml-sk (gfml fml-ex)]
      (nnf fml-sk))


    ; handle literals
    [(['not l] :seq)]
    `(~'lit (~'neg ~(handle-lit l)))
    :else `(~'lit (~'pos ~(handle-lit fml)))))

(defn build-and [ax]
  (cond
    (empty? ax) ()
    (empty? (rest ax)) (first ax)
    :else `(~'and ~(first ax) ~(build-and (rest ax)))))

(defn prepare [axioms theorem]
  (let [neg-formula (if (empty? axioms)
                     `(~'not ~theorem)
                     (build-and (cons `(~'not ~theorem) axioms)))
        nf (nnf neg-formula)]
    nf))
