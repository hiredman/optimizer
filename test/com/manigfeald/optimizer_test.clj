(ns com.manigfeald.optimizer-test
  (:require [clojure.test :refer :all]
            [com.manigfeald.optimizer :refer :all]))

(deftest a-test
  (let [n 5
        a (vec (repeatedly n #(rand-int 100)))
        ap (vec (repeatedly n #(parameter (->IntRange 0 100))))]
    (is (loop [i 0]
          (let [v (vec (map deref ap))]
            (if (not= v a)
              (do (dotimes [i (count v)]
                    (update! (nth ap i)
                             (- (Math/pow (- (nth a i) (nth v i)) 2))
                             (nth v i)))
                  (recur (inc i)))
              i))))))
