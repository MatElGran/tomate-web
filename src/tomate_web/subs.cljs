(ns tomate-web.subs
  (:require
   [re-frame.core :as re-frame]
   [tomate-web.db :as db]))

(re-frame/reg-sub
 ::plan
 (fn [db]
   (:plan db)))

(re-frame/reg-sub
 ::notifications
 (fn [db]
   (:notifications db)))
(re-frame/reg-sub
 ::session
 (fn [db]
   (:session db)))

(re-frame/reg-sub
 ::start-time
 (fn [db]
   (:start-time db)))

(re-frame/reg-sub
 ::elapsed-time
 (fn [db]
   (:elapsed-time db)))

(re-frame/reg-sub
 ::running
 (fn [db]
   (:running db)))

(re-frame/reg-sub
 ::rounds-count
 :<- [::plan]
 (fn [plan _]
   (:rounds plan)))

(re-frame/reg-sub
 ::focus-duration
 :<- [::plan]
 (fn [plan _]
   (get-in plan [:durations ::db/focus])))

(defn left-pad [n]
  (let [abs-n (.abs js/Math n)]
    (if (< abs-n 10)
      (str  "0" abs-n)
      (str abs-n))))

(defn format-time
  [duration]
  (let [minutes (int (/ duration (* 60)))
        seconds (int (- duration (* minutes 60)))]

    (str (when (> 0 duration) "-") (left-pad minutes) ":" (left-pad seconds))))

(comment
  (let [duration (* 60 25)]
    (format-time duration)))

(comment
  (let [duration (- (* 60 25))]
    (format-time duration)))

(comment
  (let [duration (- (* 60 8))]
    (format-time duration)))

(comment
  (let [duration (- 8)]
    (format-time duration)))

(re-frame/reg-sub
 ::step-type
 :<- [::session]
 :<- [::rounds-count]
 (fn [[session rounds-count] _]
   (db/step-type session rounds-count)))

(re-frame/reg-sub
 ::formatted-time

 :<- [::elapsed-time]
 :<- [::step-type]
 :<- [::focus-duration]

 (fn [[elapsed-time step-type, focus-duration] _]
   (format-time
    (if (= step-type ::db/focus)
      (- focus-duration elapsed-time)
      elapsed-time))))
