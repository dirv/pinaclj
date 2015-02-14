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

(defn- in-code-block? [current-tag]
  (= "code" current-tag))

(defn- stream-convert [orig]
  (:result (reduce
    (fn [{result :result last-char :last-char in-tag :in-tag in-closing-tag :in-closing-tag current-tag :current-tag} next-char]
        (cond
          (and (not in-tag) ( not (in-code-block? current-tag)) (quote-char? next-char)) {:result (str result (replace-char next-char last-char)) :last-char next-char :in-tag in-tag :current-tag current-tag}
          (= \< next-char) {:result (str result next-char) :last-char next-char :in-tag true :current-tag "" }
          (= \> next-char)
            (if in-closing-tag
                {:result (str result next-char) :last-char next-char :in-tag false :in-closing-tag false :current-tag ""}
                {:result (str result next-char) :last-char next-char :in-tag false :in-closing-tag false :current-tag current-tag})
          (and in-tag (= \/ next-char)) { :result (str result next-char) :last-char next-char :in-tag in-tag :in-closing-tag true :current-tag current-tag}
          in-tag {:result (str result next-char) :last-char next-char :in-tag in-tag :current-tag (str current-tag next-char)}
          :else {:result (str result next-char) :last-char next-char :in-tag in-tag :current-tag current-tag }))
    {:result "" :last-char nil :in-tag false :current-tag ""}
    orig)))

(defn convert [text]
  (stream-convert text))
