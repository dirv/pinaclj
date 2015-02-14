(ns pinaclj.quote-transform)

(defn- convert-text-segment [text]
  (-> text
      (clojure.string/replace #"(^|\W)(')" "$1&lsquo;")
      (clojure.string/replace #"(\w|;)'" "$1&rsquo;")
      (clojure.string/replace #"(^|\W)(\")" "$1&ldquo;")
      (clojure.string/replace #"(\w|;)\"" "$1&rdquo;")))

(defn- blank? [ch]
  (or (nil? ch) (Character/isWhitespace ch) (= \> ch)))

(defn- replace-quote [next-char last-char]
  (cond
    (and (= \' next-char) (blank? last-char)) "&lsquo;"
    (= \' next-char) "&rsquo;"
    (and (= \" next-char) (blank? last-char)) "&ldquo;"
    :else "&rdquo;"))

(defn- quote-char? [ch]
  (or (= \' ch) (= \" ch)))

(defn- in-code-block? [current-tag]
  (and (not (nil? current-tag)) (.startsWith current-tag "code")))

(defn- set-last-char [next-char cur]
  (assoc cur :last-char next-char))

(defn- append-char [cur next-char]
  (assoc cur :result (str (:result cur) next-char)))

(defn- stream-convert [cur next-char]
  (set-last-char next-char
                 (let [{:keys [in-tag in-closing-tag current-tag result last-char]} cur]
                   (cond
                     (and (not in-tag) (not (in-code-block? current-tag)) (quote-char? next-char))
                      (assoc cur :result (str result (replace-quote next-char last-char)))
                     (= \< next-char)
                      (assoc (append-char cur next-char) :in-tag true :current-tag "")
                     (= \> next-char)
                      (assoc (append-char cur next-char) :in-tag false :in-closing-tag false :current-tag (if in-closing-tag  "" current-tag))
                     (and in-tag (= \/ next-char))
                      (assoc (append-char cur next-char) :in-closing-tag true)
                     in-tag
                      (assoc (append-char cur next-char) :current-tag (str current-tag next-char))
                     :else
                      (append-char cur next-char)))))

(defn convert [text]
  (:result (reduce stream-convert {} text)))
