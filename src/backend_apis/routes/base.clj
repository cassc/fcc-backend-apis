(ns backend-apis.routes.base
  (:require
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
