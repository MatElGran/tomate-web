(ns tomate-web.views
  (:require
   [re-frame.core :as re-frame]
   [tomate-web.styles :as styles]
   [tomate-web.subs :as subs]
   [tomate-web.events :as events]
   [tomate-web.db :as db]))

(defn step-type-label [step-type]
  (cond  (= ::db/focus step-type) "Focus"
         (= ::db/short-break step-type) "Short break"
         :else "Long break"))

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
    {:class (styles/secondary-button)
     :on-click next-command}
    [:svg
     {:viewBox "0 0 24 24"}
     [:path
      {:d "M7.58 16.89l5.77-4.07c.56-.4.56-1.24 0-1.63L7.58 7.11C6.91 6.65 6 7.12 6 7.93v8.14c0 .81.91 1.28 1.58.82zM16 7v10c0 .55.45 1 1 1s1-.45 1-1V7c0-.55-.45-1-1-1s-1 .45-1 1z"}]]]])

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
      [:p {:class (styles/step-type)} (step-type-label step-type)]

      [timer time]


      (if running

        [running-control-pane
         #(re-frame/dispatch [::events/stop-timer])
         #(re-frame/dispatch [::events/next-step])]

        [idle-control-pane
         #(re-frame/dispatch [::events/start-timer])])
      [:p
       {:class (styles/settings)}
       "notifications: "
       [:span {:class (if notifications
                        (styles/active-status)
                        (styles/inactive-status))
               :on-click  (when (not notifications)
                            #(re-frame/dispatch [::events/activate-notifications]))}
        "on"]
       " / "
       [:span {:class (if notifications
                        (styles/inactive-status)
                        (styles/active-status))
               :on-click  (when notifications
                            #(re-frame/dispatch [::events/deactivate-notifications]))}
        "off"]]]]))

