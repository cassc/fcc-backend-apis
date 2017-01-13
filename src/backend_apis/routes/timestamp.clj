(ns backend-apis.routes.timestamp
  (:require
   [clojure.string           :as s]
   [ring.util.response       :refer [response]]
   [backend-apis.routes.base :refer :all]
   [compojure.core           :refer :all])
  (:import
   [java.util Date Locale]
   [java.text SimpleDateFormat]))

(def format-patterns
  {#"^\d{8}$"                                          "yyyyMMdd"
   #"^\d{1,2}-\d{1,2}-\d{4}$"                          "dd-MM-yyyy"
   #"^\d{4}-\d{1,2}-\d{1,2}$"                          "yyyy-MM-dd"
   #"^\d{1,2}/\d{1,2}/\d{4}$"                          "MM/dd/yyyy"
   #"^\d{4}/\d{1,2}/\d{1,2}$"                          "yyyy/MM/dd"
   #"^\d{1,2}\s[a-z]{3}\s\d{4}$"                       "dd MMM yyyy"
   #"^\d{1,2}\s[a-z]{4,}\s\d{4}$"                      "dd MMMM yyyy"
   #"^\d{12}$"                                         "yyyyMMddHHmm"
   #"^\d{8}\s\d{4}$"                                   "yyyyMMdd HHmm"
   #"^\d{1,2}-\d{1,2}-\d{4}\s\d{1,2}:\d{2}$"           "dd-MM-yyyy HH:mm"
   #"^\d{4}-\d{1,2}-\d{1,2}\s\d{1,2}:\d{2}$"           "yyyy-MM-dd HH:mm"
   #"^\d{1,2}/\d{1,2}/\d{4}\s\d{1,2}:\d{2}$"           "MM/dd/yyyy HH:mm"
   #"^\d{4}/\d{1,2}/\d{1,2}\s\d{1,2}:\d{2}$"           "yyyy/MM/dd HH:mm"
   #"^\d{1,2}\s[a-z]{3}\s\d{4}\s\d{1,2}:\d{2}$"        "dd MMM yyyy HH:mm"
   #"^\d{1,2}\s[a-z]{4,}\s\d{4}\s\d{1,2}:\d{2}$"       "dd MMMM yyyy HH:mm"
   #"^\d{14}$"                                         "yyyyMMddHHmmss"
   #"^\d{8}\s\d{6}$"                                   "yyyyMMdd HHmmss"
   #"^\d{1,2}-\d{1,2}-\d{4}\s\d{1,2}:\d{2}:\d{2}$"     "dd-MM-yyyy HH:mm:ss"
   #"^\d{4}-\d{1,2}-\d{1,2}\s\d{1,2}:\d{2}:\d{2}$"     "yyyy-MM-dd HH:mm:ss"
   #"^\d{1,2}/\d{1,2}/\d{4}\s\d{1,2}:\d{2}:\d{2}$"     "MM/dd/yyyy HH:mm:ss"
   #"^\d{4}/\d{1,2}/\d{1,2}\s\d{1,2}:\d{2}:\d{2}$"     "yyyy/MM/dd HH:mm:ss"
   #"^\d{1,2}\s[a-z]{3}\s\d{4}\s\d{1,2}:\d{2}:\d{2}$"  "dd MMM yyyy HH:mm:ss"
   #"^\d{1,2}\s[a-z]{4,}\s\d{4}\s\d{1,2}:\d{2}:\d{2}$" "dd MMMM yyyy HH:mm:ss"
   #"^[A-Z][a-z]{4,}\s\d{1,2},\s\d{4}$"                "MMMMM dd, yyyy"})

(def us (Locale. "en-US"))

(defn format-date [d]
  (.format (SimpleDateFormat. "MMMMM dd, yyyy" us) d))

(defn try-as-num [nors]
  (try (Date. (* 1000 (Long/parseLong nors)))
       (catch Exception e)))

(defn try-as-date-string [nors]
  (try
    (when-let [pattern-key (some #(when (re-seq % nors) %) (keys format-patterns))]
      (.parse (SimpleDateFormat. (format-patterns pattern-key) us) nors))
    (catch Exception e
      (.printStackTrace e))))

(defn handle-timestamp-conversion [nors]
  (println nors)
  (response
   (if-let [d (or
               (try-as-num nors)
               (try-as-date-string nors))]
     {:unix (quot (.getTime d) 1000)
      :natural (format-date d)}
     {:unix nil
      :natural nil})))

(defn timestamp-doc []
  (make-page [:div "live doc"]))

(defroutes timestamp-routes
  ;; If we simply use GET "/timestamp/:nors", without the regex pattern,
  ;; `October 12, 2015` will not be captured
  (GET ["/timestamp/:nors" :nors #".*"] [nors] (handle-timestamp-conversion nors)))

