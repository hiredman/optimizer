(ns com.manigfeald.optimizer)

(defprotocol Range
  (sample [r]))

(defrecord IntRange [max min]
  Range
  (sample [r]
    (+ min (rand-int (- max min)))))

(defrecord FloatRange [max min]
  Range
  (sample [r]
    (+ min (rand (- max min)))))

(defprotocol ScoredParameter
  (update! [p score value]))

(deftype RandomParameterOptimizer [exp data range]
  clojure.lang.IDeref
  (deref [_]
    (if (> exp (rand))
      (sample range)
      (:value (deref data))))
  ScoredParameter
  (update! [_ score value]
    (swap! data (fn [m]
                  (if (> score (:score m))
                    {:score score :value value}
                    m)))
    nil))

(defn parameter
  "given an initial value, some thing that satisfies the Range
  protocol, and a percentage in the form of a float between 0 and 1 of
  the time to run random tests, returns a parameter you can deref to
  get a value. once you get a value and use it for something, you
  should update! the parameter with the score for the used value, the
  higher the score, the better."
  ([range]
     (parameter (sample range) range 0.10))
  ([init-value range experiments]
     (RandomParameterOptimizer.
      experiments
      (atom {:value init-value
             :score Long/MIN_VALUE})
      range)))
