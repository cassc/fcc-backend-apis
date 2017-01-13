(ns backend-apis.routes.base
  (:require
   [backend-apis.config :refer [props]]
   [hiccup.core :refer [html]]))


(defn make-page [body-element]
  (str (html
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
          body-element]])
       "<a href='https://github.com/cassc/fcc-backend-apis' target='_blank'><img style='position: absolute; top: 0; right: 0; border: 0;' src='https://camo.githubusercontent.com/38ef81f8aca64bb9a64448d0d70f1308ef5341ab/68747470733a2f2f73332e616d617a6f6e6177732e636f6d2f6769746875622f726962626f6e732f666f726b6d655f72696768745f6461726b626c75655f3132313632312e706e67' alt='Fork me on GitHub' data-canonical-src='https://s3.amazonaws.com/github/ribbons/forkme_right_darkblue_121621.png'></a>"))

(defn make-link [link]
  [:div {:style "margin-left:20px;"} [:a {:href link} link]])

(defn main-page [& [{:keys [filesize filename]}]]
  (make-page
   [:div
    [:div
     [:h3 [:a {:href "https://www.freecodecamp.com/challenges/timestamp-microservice"} "Timestamp Microservice"]]
     [:ul
      [:li "Only " [:span {:style "font-weight:bold;"} "us-locale"] " is supported"]
      [:li "Input can be digits (unix timestamp) or a natual date string."]]
     (make-link (str (props :base-url) "/timestamp/1444579200"))
     (make-link (str (props :base-url) "/timestamp/December%2015,%202015"))]
    [:div
     [:h3 [:a {:href "https://www.freecodecamp.com/challenges/request-header-parser-microservice"} "Request Header Parser Microservice"]]
     [:p
      "Returns the client computuer information by reading the request header"]
     (make-link (str (props :base-url) "/whoami"))]
    [:div
     [:h3 [:a {:href "https://www.freecodecamp.com/challenges/url-shortener-microservice"} "URL Shortener Microservice"]]
     [:p
      [:ul
       [:li "Shorten a url if a full url is provided. Pass " [:span {:style "font-family:monospace;"} "allow=true"] " to all invalid url"]
       [:li "Pass a shortened url and redirect the original url"]]]
     (make-link (str (props :base-url) "/short/https://www.baidu.com"))
     (make-link (str (props :base-url) "/short/dala?allow=true"))
     (make-link (str (props :base-url) "/s/swsa"))]
    [:div
     [:h3 [:a {:href "https://www.freecodecamp.com/challenges/image-search-abstraction-layer"} "Image Search Abstraction Layer"]]
     [:ul
      [:li "Search for image with query term and page offset"]
      [:li "Recent searchs"]
      [:li "Note for local development: Google api key/cx is required"]]
     (make-link (str (props :base-url) "/img/search?term=cat&offset=10"))
     (make-link (str (props :base-url) "/img/latestsearch"))]
    [:div
     [:h3 [:a {:href "https://www.freecodecamp.com/challenges/file-metadata-microservice"} "File Metadata Microservice"]]
     [:p "Upload a file and check the file size"]
     [:form {:method "post" :action "/file" :enctype "multipart/form-data"}
      [:input {:type "file" :name "cfile" :value "cfile"}]
      [:input {:type "submit" :value "Upload"}]]
     (when filename
       [:div
        "Your uploaded file " [:span {:style "font-weight:bold;"} filename]
        " has file size: " [:span {:style "font-weight:bold;"} filesize] " bytes."])]]))

