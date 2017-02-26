(ns backend-apis.routes.comment
  (:require
   [backend-apis.config :refer [props]]
   [clojure.java.jdbc :as j]
   [clojure.java.io :as io]
   [clojure.string :as s]
   [ring.util.response       :refer [response redirect]]
   [backend-apis.routes.base :refer :all]
   [compojure.core           :refer :all]))

(defn host-key-valid? [key]
  (and key (props [:keys key] {:default nil})))

(defn email-valid? [email]
  (boolean (and email (string? email) (re-seq (props [:valid-email-regex]) email))))

(defn- sqlite-db []
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     (props [:sqlite :db])
   :test-query "select 1"})

(defn save-comment [{:keys [email content nickname ip uri] :as params}]
  {:pre [(email-valid? email) content nickname ip uri]}
  (j/insert! (sqlite-db) :comment params))

(defn get-comment [{:keys [host uri]}]
  {:pre [host uri]}
  (j/query (sqlite-db) ["select * from comment where host=? and uri=?" host uri]))

(defn handle-post-comment [{:keys [params headers] :as req}]
  (if-let [host (host-key-valid? (:key params))]
    (let [ip (or (get-in req [:headers "x-real-ip"]) (:remote-addr req))]
      (save-comment (-> params
                        (select-keys [:email :content :nickname :uri])
                        (assoc :ip ip :host host)))
      (response {:code "ok"}))
    (response {:code "err" :msg "invalid key"})))

(defn handle-get-comment [{:keys [params headers]}]
  (if-let [host (host-key-valid? (:key params))]
    (response {:code "ok" :data (get-comment {:host host :uri (:uri params)})})
    (response {:code "err" :msg "invalid key"})))

(defroutes public-comment-routes
  (GET "/post/comment" req (handle-post-comment req))
  (GET "/get/comment" req (handle-get-comment req)))
