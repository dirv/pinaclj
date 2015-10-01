(ns pinaclj.transforms.prev
  (:require [pinaclj.page :as page]))

(defn- prev-in-list [items item]
  (last (take-while #(not= % item) items)))

(defn prev-url [page opts]
  (or (:prev page)
      (prev-in-list (page/retrieve-value page :pages opts)
                    (page/retrieve-value page :destination opts))))

(defn choose-prev [page opts]
  (when-let [prev-url (prev-url page opts)]
    {:attrs {:href prev-url}
     :content (:title (page/to-page prev-url opts))}))

(def transform [:prev choose-prev])
