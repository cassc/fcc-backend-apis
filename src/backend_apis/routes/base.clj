(ns backend-apis.routes.base
  (:require
   [backend-apis.config :refer [props]]
   [hiccup.core :refer [html]]))


(defn make-page [body-element]
  (html
   [:html
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "referrer" :content "no-referrer"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:meta {:name "keywords" :content "demo backend apis with clojure"}]
     [:title "backend-apis"]
     [:link {:href "favicon.ico" :rel "shortcut icon" :type "image/x-icon"}]
     ;;[:script "var _hmt = _hmt || []; (function() {var hm = document.createElement('script'); hm.src = 'https://hm.baidu.com/hm.js?e20da0ff9c387f677eb71dd52db03801'; var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(hm, s);})();"]
     ]
    [:body
     body-element]]))

(defn make-link [link]
  [:div {:style "margin-left:20px;"} [:a {:href link} link]])

(defn main-page [& [{:keys [filesize filename]}]]
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
     [:ul
      [:li "Search for image with query term and page offset"]
      [:li "Recent searchs"]
      [:li "Note for local development: Google api key/cx is required"]]
     (make-link (str (props :base-url) "/img/search?term=cat&offset=10"))
     (make-link (str (props :base-url) "/img/latestsearch"))]
    [:div
     [:h3 "File Metadata Microservice"]
     [:p "Upload a file and check the file size"]
     [:form {:method "post" :action "/file" :enctype "multipart/form-data"}
      [:input {:type "file" :name "cfile" :value "cfile"}]
      [:input {:type "submit" :value "Upload"}]]
     (when filename
       [:div
        "Your uploaded file " [:span {:style "font-weight:bold;"} filename]
        " has file size: " [:span {:style "font-weight:bold;"} filesize] " bytes."])]]))

