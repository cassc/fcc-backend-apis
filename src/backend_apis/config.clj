(ns backend-apis.config
  (:require
   [clojure.java.io :as io]
   [clj-props.core :refer [defconfig]]))

(def  cfg
  (or (.get (System/getenv) "BEND_CONFIG") "config.edn"))

(defconfig props (io/file cfg) {:secure false})

