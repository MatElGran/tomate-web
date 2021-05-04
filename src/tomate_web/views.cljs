(ns tomate-web.views
  (:require
   [re-frame.core :as re-frame]
   [tomate-web.styles :as styles]
   [tomate-web.subs :as subs]
   [tomate-web.events :as events]))


(defn idle-control-pane [command]
  [:div
   {:class (styles/commands)}
   [:button
    {:class (styles/button)
     :on-click  command}
    "Start"]])

(defn running-control-pane [stop-command next-command]
  [:div
   {:class (styles/commands)}
   [:button
    {:class (styles/button)
     :on-click  stop-command}
    "Stop"]
   [:button
    {:class (styles/button)
     :on-click next-command}
    "Next"]])

(defn timer
  [time]
  [:p {:class (styles/timer)} @time])

(defn main-panel []
  (let [step-type @(re-frame/subscribe [::subs/step-type])
        time (re-frame/subscribe [::subs/formatted-time])
        running @(re-frame/subscribe [::subs/running])
        notifications @(re-frame/subscribe [::subs/notifications])]

    [:<>
     [:header
      "Tomate"]


     [:div
      {:class (styles/container)}
      ;; [:button {:class (styles/button)
      ;;           :on-click (if notifications
      ;;                       #(re-frame/dispatch [::events/deactivate-notifications])
      ;;                       #(re-frame/dispatch [::events/activate-notifications]))}
      ;;  (if notifications
      ;;    "Deactivate notifications"
      ;;    "Activate notifications")]
      [timer time]
      (if running

        [running-control-pane
         #(re-frame/dispatch [::events/stop-timer])
         #(re-frame/dispatch [::events/next-step])]

        [idle-control-pane
         #(re-frame/dispatch [::events/start-timer])])
      [:p {:class (styles/level2)} step-type]]]))

