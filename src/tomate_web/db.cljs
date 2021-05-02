(ns tomate-web.db
  (:require
   [cljs.spec.alpha :as s]))

(s/def ::work int?)
(s/def ::short-break int?)
(s/def ::long-break int?)
(s/def ::rounds int?)
(s/def ::running boolean?)
(s/def ::notified boolean?)
(s/def ::notification boolean?)
(s/def ::notification-permission #(or (= "granted" %) (= "default" %) (= "denied" %)))
(s/def ::archive-item (s/keys :req-un [::start-time
                                       ::end-time]))
(s/def ::session (s/coll-of ::archive-item))
(s/def ::history (s/coll-of ::session))
(s/def ::durations (s/keys :req [::work ::long-break ::short-break]))
(s/def ::plan (s/keys :req-un [::durations
                               ::rounds]))
(s/def ::schema (s/keys :req-un [::plan ::session ::history ::running ::notified ::notifications ::notification-permission]))


(def default-db
  {:session   []
   :history   []
   :running false
   :notified false
   :plan   {:durations {::work (* 25 60)
                        ::short-break (* 5 60)
                        ::long-break (* 15 60)}
            :rounds 4}})

(defn step-type [session rounds-count]
  (let [round-number (+ 1 (count session))
        steps-count-per-cycle (* 2 rounds-count)
        last-step? (= 0 (mod round-number steps-count-per-cycle))]
    (cond
      last-step? ::long-break
      (even? round-number) ::short-break
      :else ::work)))

(comment
  (let [session [1 2 3]
        rounds-count 4]
    (step-type session rounds-count)))

