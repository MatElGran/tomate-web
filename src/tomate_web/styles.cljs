(ns tomate-web.styles
  (:require
   [spade.core   :refer [defglobal defclass]]))

(defglobal vars
  [":root"
   {:theme/*color-base* :#999
    :theme/*color-light* :#ddd
    :theme/*color-dark* :#444}])

(defglobal body
  [:body
   {:font-family "sans-serif"
    :background-color :theme/*color-light*
    :color :theme/*color-dark*
    :min-height :100vh
    :margin 0}])

(defglobal header
  [:header
   {:font-size :1.5em
    :padding  "0.5em 1em"
    :background-color :theme/*color-dark*
    :color :theme/*color-light*
    :border-bottom-width :1px
    :border-bottom-color :theme/*color-base*
    :border-bottom-style :solid}])

(defclass container
  []
  {:background-color :theme/*color-base*
   :color :theme/*color-dark*
   :display :flex
   :flex-direction :column
   :align-items :center
   :text-align :center
   :max-width "80ch"
   :margin "2em auto"})


(defclass timer
  []
  {:font-family "'DM Mono', monospace"
   :font-size :4.5em
   :margin "3rem 0"})

(defclass level2
  []
  {:font-size :1.75em})

(defclass button
  []
  {:border-width 0
   :background-color :theme/*color-light*
   :color :theme/*color-dark*
   :min-width "15ch"
   :min-height "5ex"
   :border-radius "0.5em"
   :box-shadow "0.2em 0.2em 0.8em var(--theme--color-dark)"})

(defclass commands
  []
  {:display :flex
   :min-width "100%"
   :justify-content :space-evenly})