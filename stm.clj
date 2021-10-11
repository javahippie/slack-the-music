#!/usr/bin/env bb

(require '[clojure.java.shell :refer [sh]])
(require '[babashka.curl :as curl])
(require '[cheshire.core :as json])
(require '[clojure.tools.cli :refer [parse-opts]])

(def cli-options
  ;; An option with a required argument
  [["-t" "--token TOKEN" "The Slack API token"]
   ["-h" "--help"]])


(def apple-script "tell application \"Music\"
	set songTitle to name of current track
	set artistTitle to artist of current track
	return songTitle & \" - \"  & artistTitle
end tell")

(defn get-current-track []
  (:out (sh "osascript" "-e" apple-script)))

(let [token (get-in (parse-opts *command-line-args* cli-options) [:options :token])
        current-track (get-current-track)
        emoji ":headphones:"
        expiration (.toEpochSecond (.plusMinutes (java.time.ZonedDateTime/now) 3))]
    (if (empty? current-track)
      "Apple Music not running, no track set"
      (do (curl/post "https://slack.com/api/users.profile.set"
                     {:headers {"Authorization" (str "Bearer " token)}
                      :query-params {"profile" (json/generate-string {:status_text current-track
                                                                      :status_emoji emoji
                                                                      :status_expiration expiration})}})
          (str "Set " current-track))))
