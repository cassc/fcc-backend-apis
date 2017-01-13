(ns backend-apis.core
  (:gen-class)
  (:require
   [hiccup.core                             :refer [html]]
   [compojure.core           :refer :all]
   [environ.core             :refer [env]]
   [backend-apis.config           :refer [props]]
   [compojure.core           :refer [defroutes routes]]
   [ring.middleware.defaults :refer :all]
   [ring.middleware.json     :refer [wrap-json-response wrap-json-params]]
   [ring.util.response       :refer [redirect]]
   [ring.middleware.reload   :refer [wrap-reload]]
   [backend-apis.routes.base :refer :all]
   [backend-apis.routes.timestamp :refer [timestamp-routes]]
   [backend-apis.routes.header-parser :refer [header-parser-routes]]
   [backend-apis.routes.url-shorten :refer [url-shorten-routes]]
   [backend-apis.routes.image-search :refer [img-search-routes]]
   [compojure.route          :as route]
   [org.httpkit.server       :refer [run-server]]))


(defonce bend-server (atom nil))

(defn wrap-logging [handler]
  (fn [{:keys [uri session params] :as req}]
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
                   " Params:" params
                   " Time:" (- (System/currentTimeMillis) start)))))))

(defn on-error [req val]
  (redirect "/login"))

(defn make-link [link]
  [:div {:style "margin-left:20px;"} [:a {:href link} link]])

(defn doc-page []
  (make-page
   [:div
    [:div
     [:h3 "Timestamp Microservice"]
     [:ul
      [:li "Only " [:span {:style "font-weight:bold;"} "us-locale"] " is supported"]
      [:li "Input can be digits (unix timestamp) or a natual date string."]]
     (make-link (str (props :base-url) "/timestamp/1444579200"))
     (make-link (str (props :base-url) "/timestamp/December%2015,%202015"))]
    [:div
     [:h3 "Request Header Parser Microservice"]
     [:p
      "Returns the client computuer information by reading the request header"]
     (make-link (str (props :base-url) "/whoami"))]
    [:div
     [:h3 "URL Shortener Microservice"]
     [:p
      [:ul
       [:li "Shorten a url if a full url is provided. Pass " [:span {:style "font-family:monospace;"} "allow=true"] " to all invalid url"]
       [:li "Pass a shortened url and redirect the original url"]]]
     (make-link (str (props :base-url) "/short/https://www.baidu.com"))
     (make-link (str (props :base-url) "/short/dala?allow=true"))
     (make-link (str (props :base-url) "/s/swsa"))]
    [:div
     [:h3 "Image Search Abstraction Layer"]
     [:p
      [:li "Search for image with query term and page offset"]
      [:li "Recent searchs"]]
     (make-link (str (props :base-url) "/img/search?term=cat&offset=10"))
     (make-link (str (props :base-url) "/img/latestsearch"))
     ]]))

(defroutes public-routes
  (GET "/" [] (doc-page)))

(defn start-server []
  (let [dev? (env :dev)
        my-routes [public-routes timestamp-routes header-parser-routes url-shorten-routes img-search-routes] 
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



