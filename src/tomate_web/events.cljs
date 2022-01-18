(ns tomate-web.events
  (:require
   [re-frame.core :as re-frame]
   [tomate-web.db :as db]
   [tomate-web.fx :as fx]
   [tomate-web.coeffects :as cofx]
   [tomate-web.lib :as lib]
   [tomate-web.notifications :as notifications]
   [day8.re-frame.tracing :refer-macros [fn-traced]]))

(lib/reg-event-fx
 ::initialize-db
 [(re-frame/inject-cofx ::notifications/notification-allowed?)
  (re-frame/inject-cofx ::notifications/notifications-active?)]
 (fn-traced
  [{:keys [notification-allowed? notifications-active?]} _]
            {:db (assoc db/default-db
              :notifications (and notification-allowed? notifications-active?))}))

(lib/reg-event-fx
 ::next-step
 [(re-frame/inject-cofx ::cofx/now)]
 (fn-traced
  [{:keys [db now]} _]
            (let [{:keys [session start-time]} db
                  step {:start-time start-time
                        :end-time now}]
              {:db (assoc db
                          :session (conj session step)
                          :start-time now
                          :elapsed-time 0
                          :notified false)
               ::fx/clear-interval {:id ::update-timer-interval}
               ::fx/dispatch-interval {:dispatch [::update-timer]
                                       :ms 1000
                                       :id ::update-timer-interval}})))

(defn overflow?
  [plan step-type elapsed-time]
  (let [step-duration (step-type (:durations plan))]
    (> elapsed-time step-duration)))

(defn notification-text
  [step-type]
  (if (= ::db/focus step-type)
    "Time for a break"
    "Time to focus"))

(lib/reg-event-fx
 ::update-timer
 [(re-frame/inject-cofx ::cofx/now)
  (re-frame/->interceptor :id ::check-overflow
                          :after (fn
                                   [{:keys [effects] :as context}]
                                   (let [{:keys [db fx] :or {fx []}} effects
                                         {:keys [plan notified notifications sound elapsed-time session]} db
                                         step-type (db/step-type session (:rounds plan))
                                         notify-fx [::notifications/notify {:text (notification-text step-type)
                                                                            :on-notification ::notified}]
                                         sound-fx [::notifications/play-sound {:on-notification ::notified}]]
                                     (if (and
                                          (not notified)
                                          (overflow? plan step-type elapsed-time))
                                       (re-frame/assoc-effect context
                                                              :fx (conj fx (when notifications notify-fx) (when sound sound-fx)))
                                       context))))]

 (fn
   [{:keys [now db]} _]
   (let [elapsed-time (int (/ (- now (:start-time db)) 1000))]
     {:db (assoc db :elapsed-time elapsed-time)})))


(lib/reg-event-db
 ::notified
 (fn-traced
  [db _]
            (assoc db
                   :notified true)))

(lib/reg-event-fx
 ::start-timer
 [(re-frame/inject-cofx ::cofx/now)]
 (fn-traced
  [cofx _]
            (let [current-time (:now cofx)]
              {:db (assoc (:db cofx)
                          :start-time current-time
                          :notified false
                          :running true)
               ::fx/dispatch-interval {:dispatch [::update-timer]
                                       :ms 1000
                                       :id ::update-timer-interval}})))


(lib/reg-event-db
 ::archive_session
 (fn-traced
  [{:keys [history] :as db} [_ session]]
  (let [updated-history (conj history session)]
              (assoc db :history updated-history))))


(lib/reg-event-fx
 ::stop-timer
 [(re-frame/inject-cofx ::cofx/now)]
 (fn-traced
  [{:keys [now db]} _]
  (let [{:keys [start-time session]} db
                  session-to-archive (conj
                                      session
                                      {:start-time start-time :end-time now})]
    {:db (assoc db
                          :start-time now
                          :elapsed-time 0
                          :running false
                          :notified false
                          :session [])
               :dispatch [::archive_session session-to-archive]
               ::fx/clear-interval {:id ::update-timer-interval}})))
