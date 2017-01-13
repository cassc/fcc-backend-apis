(ns backend-apis.routes.file-meta
  (:require
   [backend-apis.config :refer [props]]
   [clojure.java.io :as io]
   [clojure.string :as s]
   [ring.util.response       :refer [response redirect]]
   [backend-apis.routes.base :refer :all]
   [compojure.core           :refer :all]))

(defn handle-put-file [req]
  (let [f (-> req :params :cfile :tempfile)
        filename (-> req :params :cfile :filename)]
    (main-page {:filename filename
                :filesize (.length f)})))

(defroutes file-meta-routes
  (POST "/file" req (handle-put-file req)))
