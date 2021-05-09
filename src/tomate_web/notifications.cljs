(ns tomate-web.notifications
  (:require [tomate-web.lib :as lib]
            [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]))

(re-frame/reg-cofx
 ::notification-permission
 (fn [coeffects _]
   (let [value (try
                 (.-permission js/Notification)
                 (catch js/Error e
                   (println e)
                   "denied"))]
     (assoc coeffects :notification-permission value))))

(re-frame/reg-cofx
 ::active-notifications
 (fn [coeffects _]
   (let [value (try
                 (.getItem (.-localStorage js/window) "active-notifications")
                 (catch js/Errror e
                   (println e)
                   false))]
     (assoc coeffects :active-notifications (= "true" value)))))

(lib/reg-event-db
 ::notification-permission-response
 (fn-traced [db [_ notification-permission]]
            (assoc db
                   :notification-permission notification-permission
                   :notifications (= "granted" notification-permission))))

(lib/reg-event-fx
 ::activate-notifications
 [(re-frame/inject-cofx ::notification-permission)]
 (fn-traced [cofx _]
            (if (= "granted" (:notification-permission cofx))
              {:db (assoc (:db cofx)
                          :notifications true
                          :notified true)
               :fx [[::activate-notifications]]}

              ;; FIXME We were there to activate in the first place
              {:fx [[::request-notification-permission
                     {:on-permission-change ::notification-permission-response}]]})))


(lib/reg-event-fx
 ::deactivate-notifications
 [(re-frame/inject-cofx ::notification-permission)]
 (fn-traced [cofx _]
            {:db (assoc (:db cofx) :notifications false)
             :fx [[::deactivate-notifications]]}))

(re-frame/reg-fx
 ::request-notification-permission
 (fn [{:keys [:on-permission-change]}]
   (if js/Notification
     (if (.then (.requestPermission js/Notification))
       (-> js/Notification
           .requestPermission
           (.then
            #(re-frame/dispatch [on-permission-change %])))

       (-> js/Notification
           (.requestPermission #(re-frame/dispatch [on-permission-change %]))))

     (println "No notification support"))))

(re-frame/reg-fx
 ::notify
 (fn [{:keys [:text :on-notification]}]
   (js/Notification. text)
   (re-frame/dispatch [on-notification])))

(re-frame/reg-fx
 ::activate-notifications
 (fn []
   (try
     (.setItem (.-localStorage js/window) "active-notifications" true)
     (catch js/Error e
       (println e)))))

(re-frame/reg-fx
 ::deactivate-notifications
 (fn []
   (try
     (.setItem (.-localStorage js/window) "active-notifications" false)
     (catch js/Error e
       (println e)))))