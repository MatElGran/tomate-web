(ns tomate-web.styles
  (:require
   [spade.core   :refer [defglobal defclass]]))

(defglobal vars
  [":root"
   {:theme/*color-base* :#215876
    :theme/*color-light* :#aecbce
    :theme/*color-dark* :#224866
    :theme/*color-alternate* :#eae37e}])

(defglobal body
  [:body
   {:font-family "'Inter', sans-serif"
    :background-color :theme/*color-dark*
    :color :theme/*color-light*
    :min-height :100vh
    :margin 0}])

(defglobal header
  [:header
   {:font-size :1.5em
    :padding  "0.5em 1em"
    :border-bottom-width :1px
    :border-bottom-color :theme/*light*
    :border-bottom-style :solid}])

(defclass container
  []
  {:background-color :theme/*color-base*
   :color :theme/*color-light*

   :display :flex
   :flex-direction :column
   :align-items :center
   :text-align :center

   :border-radius "0.5em"
  ;;  FIXME régler la taille de la police sur la taille du container
   :max-width "clamp(60vw, 65%, 60ch)"
   :margin "2em auto"})


(defclass timer
  []
  {:font-family "'Courier Prime', monospace"
   :font-size "clamp(3.5em, 8vw, 6.5em)"
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