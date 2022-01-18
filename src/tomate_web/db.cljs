(ns tomate-web.db
  (:require
   [cljs.spec.alpha :as s]))

(s/def ::start-time inst?)
(s/def ::end-time inst?)
(s/def ::elapsed-time int?)
(s/def ::focus int?)
(s/def ::short-break int?)
(s/def ::long-break int?)
(s/def ::rounds int?)
(s/def ::running boolean?)
(s/def ::notified boolean?)
(s/def ::notification boolean?)
(s/def ::notification-allowed? boolean?)
(s/def ::archive-item (s/keys :req-un [::start-time
                                       ::end-time]))
(s/def ::session (s/coll-of ::archive-item))
(s/def ::history (s/coll-of ::session))
(s/def ::durations (s/keys :req [::focus ::long-break ::short-break]))
(s/def ::plan (s/keys :req-un [::durations
                               ::rounds]))
(s/def ::schema (s/keys :req-un [::plan ::session ::history ::running ::notified ::notifications ::elapsed-time]
                        :opt-un [::start-time ::end-time]))


(def default-db
  {:session   []
   :history   []
   :running false
   :notified false
   :elapsed-time 0
   :plan   {:durations {::focus (* 25 60)
                        ::short-break (* 5 60)
                        ::long-break (* 15 60)}
            :rounds 4}})

(defn step-type
  "Determine the current step type from history and configuration"
  [session rounds-count]
  (let [current-round-number (+ 1 (count session))
        steps-count-per-cycle (* 2 rounds-count)
        last-step? (= 0 (mod current-round-number steps-count-per-cycle))]
    (cond
      last-step? ::long-break
      (even? current-round-number) ::short-break
      :else ::focus)))

(comment
  (let [session [1 2 3]
        rounds-count 4]
    (step-type session rounds-count)))

