(ns tomate-web.fx
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]))

(s/def ::dispatch sequential?)
(s/def ::ms int?)
(s/def ::id any?)
(s/def ::dispatch-interval-args (s/keys :req-un [::dispatch ::ms ::id]))

(def registered-keys (atom nil))

(re-frame/reg-fx
 ::dispatch-interval
 (fn [{:keys [:dispatch :ms :id] :as config}]
   (s/assert ::dispatch-interval-args config)
   (let [interval-id (js/setInterval #(re-frame/dispatch dispatch) ms)]
     (swap! registered-keys assoc id interval-id))))

(re-frame/reg-fx
 ::clear-interval
 (fn [{:keys [:id]}]
   (when-let [interval-id (get @registered-keys id)]
     (js/clearInterval interval-id)
     (swap! registered-keys dissoc id))))

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