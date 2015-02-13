(ns pinaclj.quote-transform)

(defn convert-quote-text [text]
  (-> text
      (clojure.string/replace #"(^|\W)(')" "$1&lsquo;")
      (clojure.string/replace #"(\w|;)'" "$1&rsquo;")
      (clojure.string/replace #"(^|\W)(\")" "$1&ldquo;")
      (clojure.string/replace #"(\w|;)\"" "$1&rdquo;")))
