(ns backend-apis.routes.header-parser
  (:require
   [clojure.string :as s]
   [ring.util.response       :refer [response]]
   [backend-apis.routes.base :refer :all]
   [compojure.core           :refer :all]))

(defn whoami [{:keys [headers remote-addr]}]
  (response
   {:ipaddress (or (headers "x-real-ip") remote-addr)
    :language (-> (headers "accept-language")
                  (s/split #";")
                  first
                  (s/split #",")
                  first)
    :software (->> (headers "user-agent")
                   (re-seq #"\((.*?)\)")
                   first
                   second)}))

(defroutes header-parser-routes
  (GET "/whoami" req (whoami req)))
