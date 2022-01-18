(ns tomate-web.notifications
  (:require [tomate-web.lib :as lib]
            [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]))

(re-frame/reg-cofx
 ::notification-allowed?
 (fn
   [cofx _]
   (let [value (try
                 (.-permission js/Notification)
                 (catch js/Error e
                   ; FIXME
                   (println e)
                   "denied"))]
     (assoc cofx :notification-allowed? (= "granted" value)))))

(re-frame/reg-cofx
 ::notifications-active?
 (fn
   [cofx _]
   (let [value (try
                 (.getItem (.-localStorage js/window) "active-notifications")
                 (catch js/Errror e
                   ; FIXME
                   (println e)
                   false))]
     (assoc cofx :notifications-active? (= "true" value)))))

(lib/reg-event-db
 ; FIXME rename
 ::notification-permission-response
 (fn-traced
  [db [_ notification-allowed?]]
  (let [notifications-active? (= "granted" notification-allowed?)]
            (assoc db
           :notifications notifications-active?))))

(lib/reg-event-fx
 ::toggle-notifications
 [(re-frame/inject-cofx ::notification-allowed?)]
 (fn-traced
  [{:keys [db notification-allowed?]} [_ active?]]

  (if (and (not notification-allowed?) active?)
    ; FIXME We were there to activate in the first place
    {:fx [[::request-notification-allowed?
           {:on-permission-change ::notification-permission-response}]]}

    {:db (assoc db
                :notifications active?
                          :notified true)
     :fx [[::persist-to-local-storage {:key "active-notifications" :value active?}]]})))

(re-frame/reg-fx
 ::request-notification-allowed?
 (fn
   [{:keys [:on-permission-change]}]
   (if js/Notification
     (if (.then (.requestPermission js/Notification))
       (-> js/Notification
           .requestPermission
           (.then
            #(re-frame/dispatch [on-permission-change %])))

       (-> js/Notification
           (.requestPermission #(re-frame/dispatch [on-permission-change %]))))
     ; FIXME
     (println "No notification support"))))

(re-frame/reg-fx
 ::notify
 (fn
   [{:keys [:text :on-notification]}]
   ; FIXME test when permission has been denied since activation
   (js/Notification. text)
   (re-frame/dispatch [on-notification])))

(re-frame/reg-fx
 ::persist-to-local-storage
 (fn
   [{:keys [key value]}]
   (try
     (.setItem (.-localStorage js/window) key value)
     (catch js/Error e
       ; FIXME remonter l'erreur?
       (println e)))))
