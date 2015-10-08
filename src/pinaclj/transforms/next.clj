(ns pinaclj.transforms.next
  (:require [pinaclj.page :as page]))

(defn- next-in-list [items item]
  (second (drop-while #(not= % item) items)))

(defn- next-url [page opts]
  (if (contains? page :next)
    (:next page)
    (next-in-list (page/retrieve-value page :pages opts)
                  (page/retrieve-value page :destination {}))))

(defn choose-next [page opts]
  (when-let [next-url (next-url page opts)]
    {:attrs {:href next-url}
     :content (:title (page/to-page next-url opts))}))

(def transform [:next choose-next])
