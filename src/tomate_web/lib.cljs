(ns tomate-web.lib
  (:require [cljs.spec.alpha :as s]
            [re-frame.core :as re-frame]
            [tomate-web.db :as db]))

(def debug? ^boolean goog.DEBUG)

(defn valid-schema?
  "validate the given db, writing any problems to console.error"
  [db]
  (when (not (s/valid? ::db/schema db))
    (let [res (s/explain-str ::db/schema db)]
      (.error js/console (str "schema problem: " res)))))

(def standard-interceptors
  [(when debug? re-frame/debug)
   (when debug? (re-frame/after valid-schema?))])

(def standard-interceptors-fx
  [(when debug?  re-frame/debug)
   (when debug? (re-frame/after #(when % (valid-schema? %))))])

(defn reg-event-db          ;; alternative to reg-event-db
  ([id handler-fn]
   (re-frame/reg-event-db id standard-interceptors handler-fn))
  ([id interceptors handler-fn]
   (re-frame/reg-event-db
    id
    [standard-interceptors interceptors]
    handler-fn)))

(defn reg-event-fx          ;; alternative to reg-event-fx
  ([id handler-fn]
   (re-frame/reg-event-fx id standard-interceptors-fx handler-fn))
  ([id interceptors handler-fn]
   (re-frame/reg-event-fx
    id
    [standard-interceptors-fx interceptors]
    handler-fn)))