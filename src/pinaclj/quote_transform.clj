(ns pinaclj.quote-transform)

(defn- convert-text-segment [text]
  (-> text
      (clojure.string/replace #"(^|\W)(')" "$1&lsquo;")
      (clojure.string/replace #"(\w|;)'" "$1&rsquo;")
      (clojure.string/replace #"(^|\W)(\")" "$1&ldquo;")
      (clojure.string/replace #"(\w|;)\"" "$1&rdquo;")))

(defn- blank? [ch]
  (or (nil? ch) (Character/isWhitespace ch) (= \> ch)))

(defn- replace-char [next-char last-char]
  (cond
    (and (= \' next-char) (blank? last-char)) "&lsquo;"
    (= \' next-char) "&rsquo;"
    (and (= \" next-char) (blank? last-char)) "&ldquo;"
    :else "&rdquo;"))

(defn- quote-char? [ch]
  (or (= \' ch) (= \" ch)))

(defn- stream-convert [orig]
  (:result (reduce
    (fn [{result :result last-char :last-char} next-char]
        (if (quote-char? next-char)
            {:result (str result (replace-char next-char last-char)) :last-char next-char}
            {:result (str result next-char) :last-char next-char}))
    {:result "" :last-char nil}
    orig)))

(defn convert [text]
  (stream-convert text))
