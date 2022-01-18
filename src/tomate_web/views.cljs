(ns tomate-web.views
  (:require
   [re-frame.core :as re-frame]
   [tomate-web.styles :as styles]
   [tomate-web.subs :as subs]
   [tomate-web.events :as events]
   [tomate-web.notifications :as notifications]
   [tomate-web.db :as db]
   [tomate-web.views.components.icons :as icons]))

(defn step-type-label [step-type]
  (cond  (= ::db/focus step-type) "Focus"
         (= ::db/short-break step-type) "Short break"
         :else "Long break"))

(defn idle-control-panel
  []
  [:div
   {:class (styles/commands)}
   [:button
    {:class (styles/button)
     :on-click  #(re-frame/dispatch [::events/start-timer])}
    "Start"]])

(defn running-control-panel
  []
  [:div
   {:class (styles/commands)}
   [:button
    {:class (styles/button)
     :on-click  #(re-frame/dispatch [::events/stop-timer])}
    "Stop"]
   [:button
    {:class (styles/secondary-button)
     :aria-label "Next"
     :on-click #(re-frame/dispatch [::events/next-step])}
    icons/forward]])

(defn time-display []
  (let [time (re-frame/subscribe [::subs/formatted-time])]
    [:p {:class (styles/timer)} @time]))

(defn control-panel []
  (let [running @(re-frame/subscribe [::subs/running])]
    (if running
      [running-control-panel]
      [idle-control-panel])))

(defn main-panel []
  (let [step-type @(re-frame/subscribe [::subs/step-type])
        notifications-activated? @(re-frame/subscribe [::subs/notifications])]
    [:<>
     [:header
      "Tomate"]


     [:div
      {:class (styles/container)}
      [:p {:class (styles/step-type)} (step-type-label step-type)]

      [time-display]
      [control-panel]

      [:div
       {:class (styles/settings)}

       [:label
        {:for "notifications-activated" :style {:position "relative"}}
        [:input
         {:id "notifications-activated" :name "notifications-activated" :type "checkbox" :checked notifications-activated?
          :style {:position "absolute" :opacity 0 :width "1em" :height "1em"}
          :on-change #(re-frame/dispatch [::notifications/toggle-notifications (not notifications-activated?)])}]
        (if notifications-activated?
          icons/active-notifications
          icons/inactive-notifications)]]]]))


