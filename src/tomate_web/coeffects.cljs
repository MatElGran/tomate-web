(ns tomate-web.coeffects
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-cofx
 ::now
 (fn [coeffects _]
   (assoc coeffects :now (js/Date.))))

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