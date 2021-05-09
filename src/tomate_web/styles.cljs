(ns tomate-web.styles
  (:require
   [spade.core   :refer [defglobal defclass]]))

(defglobal vars
  [":root"
   {:theme/*color-base* :#215876
    :theme/*color-light* :#aecbce
    :theme/*color-dark* :#224866
    :theme/*color-alternate* :#f9a058
    :theme/*font-size-xlarge* "clamp(4.5rem, 20vw, 8rem)"
    :theme/*font-size-base* "clamp(1rem, 3vw, 2rem)"
    :theme/*font-size-small* "clamp(1rem, 1vw, 2rem)"}])

(defglobal body
  [:body
   {:font-family "'Inter', sans-serif"
    :font-size :theme/*font-size-base*
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
   :border-radius "0.5em"
   :max-width "clamp(30ch, 65%, 60ch)"
   :margin "2em auto"}

  [:>*+* {:margin-top :2rem}])

(defclass timer
  []
  {:margin-bottom 0
   :font-family "'Courier Prime', monospace"
   :font-size :theme/*font-size-xlarge*})

(defclass step-type
  []
  {:margin 0})

(defclass tools
  []
  {:display :flex
   :width :100%})

(defclass commands
  []
  {:display :flex
   :justify-content :center
   :align-items :center
   :position :relative
   :width :100%})

(defclass button
  []
  {:border-width 0
   :background-color :theme/*color-alternate*
   :color :theme/*color-dark*
   :font-size :theme/*font-size-base*
   :font-weight 700
   :min-width :10ch
   :min-height :4ex
   :border-radius "0.5rem"
   :box-shadow "0.2rem 0.2rem 0.8rem var(--theme--color-dark)"})

(defclass secondary-button
  []
  {:display :flex
   :align-items :center
   :justify-content :center
   :position :absolute
   :right 0
   :border-width 0
   :background-color :theme/*color-base*
   :color :theme/*color-alternate*
   :box-shadow :initial}

  [:svg
   {:fill :theme/*color-alternate*
    :min-width :4ch}])


(defclass settings
  []
  {:font-size :theme/*font-size-small*})

(defclass active-status
  []  {:font-weight 700})

(defclass inactive-status
  [] {:cursor :pointer})