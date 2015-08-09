(ns pinaclj.tasks.task
  )

(defn success [msg]
  {:type :success :msg msg})

(defn error [msg]
  {:type :error :msg msg})

