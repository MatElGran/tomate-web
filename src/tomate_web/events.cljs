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
 [(re-frame/inject-cofx ::notifications/notification-permission)
  (re-frame/inject-cofx ::notifications/active-notifications)]
 (fn-traced [{:keys [notification-permission active-notifications] :as cofx} _]
            (println cofx)
            {:db (assoc db/default-db
                        :notification-permission notification-permission
                        :notifications (and (= "granted" notification-permission) active-notifications))}))

(lib/reg-event-db
 ::notification-permission-response
 (fn-traced [db [_ notification-permission]]
            (assoc db
                   :notification-permission notification-permission
                   :notifications (= "granted" notification-permission))))

(lib/reg-event-fx
 ::next-step
 [(re-frame/inject-cofx ::cofx/now)]
 (fn-traced [{:keys [db now]} _]
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


(lib/reg-event-fx
 ::update-timer
 [(re-frame/inject-cofx ::cofx/now)
  (re-frame/->interceptor :id ::check-overflow
                          :after (fn [{:keys [effects] :as context}]
                                   (let [{:keys [db fx] :or {fx []}}  effects
                                         {:keys [plan notified notifications elapsed-time session]} db]
                                     (if (and notifications (not notified))
                                       (let [{:keys [durations rounds]} plan
                                             step-type (db/step-type session rounds)
                                             step-duration (step-type durations)
                                             overflow? (> elapsed-time step-duration)
                                             notification-text (cond (= ::db/focus step-type) "Time for a break"
                                                                     :else "Time to focus")

                                             notify [::notifications/notify {:text notification-text
                                                                             :on-notification ::notified}]]
                                         (if overflow?
                                           (re-frame/assoc-effect context :fx (conj fx notify))
                                           context))
                                       context))))]

 (fn [{:keys [now db]} _]
   (let [elapsed-time (int (/ (- now (:start-time db)) 1000))]
     {:db (assoc db :elapsed-time elapsed-time)})))


(lib/reg-event-db
 ::notified
 (fn-traced [db _]
            (assoc db
                   :notified true)))

(lib/reg-event-fx
 ::start-timer
 [(re-frame/inject-cofx ::cofx/now)]
 (fn-traced [cofx _]
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
 (fn-traced [db [_ session]]
            (let [history (:history  db)
                  updated-history (conj history session)]
              (assoc db :history updated-history))))


(lib/reg-event-fx
 ::stop-timer
 [(re-frame/inject-cofx ::cofx/now)]
 (fn-traced [cofx _]
            (let [{:keys [now db]} cofx
                  start-time (:start-time  db)
                  session (:session  db)
                  session-to-archive (conj
                                      session
                                      {:start-time start-time :end-time now})]
              {:db (assoc (:db cofx)
                          :start-time now
                          :elapsed-time 0
                          :running false
                          :notified false
                          :session [])
               :dispatch [::archive_session session-to-archive]
               ::fx/clear-interval {:id ::update-timer-interval}})))



(lib/reg-event-fx
 ::activate-notifications
 [(re-frame/inject-cofx ::notifications/notification-permission)]
 (fn-traced [cofx _]
            (if (= "granted" (:notification-permission cofx))
              {:db (assoc (:db cofx)
                          :notifications true
                          :notified true)
               :fx [[::notifications/activate-notifications]]}

              ;; FIXME We were there to activate in the first place
              {:fx [[::notifications/request-notification-permission
                     {:on-permission-change ::notification-permission-response}]]})))


(lib/reg-event-fx
 ::deactivate-notifications
 [(re-frame/inject-cofx ::notifications/notification-permission)]
 (fn-traced [cofx _]
            {:db (assoc (:db cofx) :notifications false)
             :fx [[::notifications/deactivate-notifications]]}))

