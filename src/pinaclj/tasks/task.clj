(ns pinaclj.tasks.task
  )

(defn info [msg]
  {:type :info :msg msg})

(defn success [msg]
  {:type :success :msg msg})

(defn error [msg]
  {:type :error :msg msg})
