(ns backend-apis.core
  (:gen-class)
  (:require
   [hiccup.core                       :refer [html]]
   [compojure.core                    :refer :all]
   [environ.core                      :refer [env]]
   [backend-apis.config               :refer [props]]
   [compojure.core                    :refer [defroutes routes]]
   [ring.middleware.defaults          :refer :all]
   [ring.middleware.json              :refer [wrap-json-response wrap-json-params]]
   [ring.util.response                :refer [redirect]]
   [ring.middleware.reload            :refer [wrap-reload]]
   [backend-apis.routes.base          :refer :all]
   [backend-apis.routes.timestamp     :refer [timestamp-routes]]
   [backend-apis.routes.header-parser :refer [header-parser-routes]]
   [backend-apis.routes.url-shorten   :refer [url-shorten-routes]]
   [backend-apis.routes.image-search  :refer [img-search-routes]]
   [backend-apis.routes.file-meta     :refer [file-meta-routes]]
   [compojure.route                   :as route]
   [org.httpkit.server                :refer [run-server]]))


(defonce bend-server (atom nil))

(defn wrap-logging [handler]
  (fn [{:keys [uri session params request-method] :as req}]
    (let [start (System/currentTimeMillis)
          ua      (get-in req [:headers "user-agent"])
          address (or (get-in req [:headers "x-real-ip"]) (:remote-addr req))]
      (try
        (handler req)
        (catch Throwable e
          (.printStackTrace e))
        (finally
          (println "Req:" uri
                   " UA:" ua
                   " User:" (:identity session)
                   " From:" address
                   " Method:" request-method
                   " Params:" params
                   " Time:" (- (System/currentTimeMillis) start)))))))

(defroutes public-routes
  (ANY "*" [] (main-page)))

(defn start-server []
  (let [dev? (env :dev)
        my-routes [timestamp-routes header-parser-routes url-shorten-routes img-search-routes file-meta-routes public-routes] 
        app (-> (apply routes my-routes)
                (wrap-logging)
                (wrap-json-response)
                (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
                (wrap-json-params))
        options (props :server-options)]
    (println "Dev mode? " (if dev? "true" "false"))
    (reset! bend-server (run-server
                       (if dev?
                         (wrap-reload app {:dirs ["src" "resources"]})
                         app)
                       options))
    (println "Server start success with options" options)))

(defn stop-server []
  (when @bend-server
    (@bend-server)
    (reset! bend-server nil)))

(defn -main []
  (start-server))



