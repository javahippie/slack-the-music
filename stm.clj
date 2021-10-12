#!/usr/bin/env bb

(require '[clojure.java.shell :refer [sh]])
(require '[clojure.edn :refer [read-string]])
(require '[babashka.curl :as curl])
(require '[cheshire.core :as json])
(require '[clojure.tools.cli :refer [parse-opts]])

;; The script needs the Slack API Token as a parameter
(def cli-options
  [["-t" "--token TOKEN" "The Slack API token"]])

(def apple-script (slurp "music.script"))

(defn get-current-track
  "Retrieves the current track from the Apple Music app.
  Returns a map with title, artist and player state if successful,
  nil if Apple Music is not running"
  []
  (->> apple-script
       (sh "osascript" "-e")
       (:out)
       (read-string)))

(defn response-ok?
  "Slack always sends a HTTP 200, even if the authorization was not successful.
  Thank you, Slack!"
  [response]
  (-> response
      :body
      (json/decode)
      (get "ok")))

(defn get-emoji [genre]
  (case genre
    "Metal" ":the_horns:"
    "Jazz" ":trumpet:"
    "Alternative" ":broccoli:"
    ":headphones"))

(defn set-slack-status
  "Publishes track and artist in your Slack status.
  Returns true if successful, false else.
  The status expires automatically after 3 minutes.
  Increase the expiry limit to 20 minutes, if you are into progressive rock."
  [title artist album genre]
  (let [token (get-in (parse-opts *command-line-args* cli-options) [:options :token])]
    (response-ok?
     (curl/post "https://slack.com/api/users.profile.set"
                {:headers {"Authorization" (str "Bearer " token)}
                 :query-params {"profile" (json/generate-string {:status_text (str title " - " artist " (" album ")")
                                                                 :status_emoji (get-emoji genre)
                                                                 :status_expiration (.toEpochSecond (.plusMinutes (java.time.ZonedDateTime/now) 3))})}}))))

;; This runs when the namespace is loaded
(let [{:keys [title artist album genre state]} (get-current-track)]
  (if (= :playing state)
    (if (set-slack-status title artist album genre)
      (format "%s by %s published to Slack" title artist)
      "Could not connect to Slack. Is your token correct?")
    "Apple Music not running, no track set"))
