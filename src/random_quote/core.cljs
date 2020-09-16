(ns random-quote.core
  (:require [ajax.core :refer [GET]]
   [reagent.core :as reagent :refer [atom]]))

;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Quote!"
                          :author "Costin Dragoi"}))

(defn fetch-link! [state]
  (GET "https://quote-garden.herokuapp.com/api/v2/quotes/random"
       {:handler (fn [data]
                   (println (get-in data ["quote" "quoteAuthor"]))

                   (swap! state assoc :text (get-in data ["quote" "quoteText"]))
                   (swap! state assoc :author (get-in data ["quote" "quoteAuthor"])))
        :error-handler (fn [{:keys [status status-text]}]
                         (js/console.log status status-text))}))

(defn tweet-link [state]
  (str "https://twitter.com/intent/tweet?hashtags=quote&text="
       (:text @state) " - " (:author @state)))

(defn quote [state]
  [:div
   [:h1 "Random Quote Generator"]
   [:div.quote
    [:div.quote-container
     [:p.quote-content (:text @state)]
     [:p.quote-author (:author @state)]]
    [:div.quote-buttons
     [:a {:href (tweet-link state)
          :target "_blank"} "tweet"]
     [:button {:on-click #(fetch-link! state)} "next quote"]]]]
  )

(defn start []
  (reagent/render-component [quote app-state]
                            (. js/document (getElementById "app"))))

(defn ^:export init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (start))

(defn stop []
  ;; stop is called before any code is reloaded
  ;; this is controlled by :before-load in the config
  (js/console.log "stop"))
