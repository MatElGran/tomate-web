(ns tomate-web.styles
  (:require
   [spade.core   :refer [defglobal defclass]]))


(defglobal vars
  [":root"
   {:theme/*color-base* :#215876
    :theme/*color-light* :#aecbce
    :theme/*color-dark* :#224866
    :theme/*color-alternate* :#f9a058
    :theme/*font-size-xlarge* "clamp(3.5rem, 8vw, 6.5rem)"
    :theme/*font-size-large* "clamp(1.5rem, 4vw, 3.5rem)"
    :theme/*font-size-base* "clamp(1rem, 1vw, 3rem)"}])

(defglobal body
  [:body
   {:font-family "'Inter', sans-serif"
    :background-color :theme/*color-dark*
    :color :theme/*color-alternate*
    :min-height :100vh
    :margin 0}])

(defglobal header
  [:header
   {:font-size :1.5rem
    :padding  "0.5em 1em"
    :border-bottom-width :1px
    :border-bottom-color :theme/*light*
    :border-bottom-style :solid}])

(defclass container
  []

  {:background-color :theme/*color-base*
   :display :flex
   :flex-direction :column
   :align-items :center
   :text-align :center
   :padding "2em 0"
   :font-size :theme/*font-size-base*
   :border-radius "0.5em"
   :max-width "clamp(60vw, 65%, 60ch)"
   :margin "2em auto"})

(defclass timer
  []
  {:margin 0
   :font-family "'Courier Prime', monospace"
   :font-size :theme/*font-size-xlarge*})

(defclass level2
  []
  {:margin 0
   :font-size :theme/*font-size-large*})

(defclass button
  []
  {:border-width 0
   :background-color :theme/*color-alternate*
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