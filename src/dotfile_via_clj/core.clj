(ns dotfile-via-clj.core
  (:gen-class))

(require '[clojure.string :as str])
(require '[clojure.java.io :as io])
(require '[clojure.java.shell :as shell])
(require '[clojure.data :as diff])


; Utilizes clojure and terminal commands 'ls' and 'diff' to
; quickly verify if there are important diffs between
; dot files stored under the ./resources folder with
; that in the user home directory

(defn get-files
  "Retrive files from filepath"
  [filepath]
  (str/split (get (shell/sh "ls" "-a" filepath) :out) #"\n"))

(defn filter-files
  "Filter for only files given filepath and filenames"
  [filepath filenames]
  (filter (fn [x] (.isFile (io/file (str filepath "/" x)))) filenames))

(def home-dir
  "Home directory of current user"
  (System/getProperty "user.home"))

(defn commited-dot-files
  "Get commited dot files"
  []
  (let [filepath "./resources"]
    (filter-files filepath (get-files filepath))))

(defn current-dot-files
  "Get dot files from user home directory"
  []
  (filter-files home-dir (get-files home-dir)))

(def diff-dot-files
  "Returns set of files into commited, current and both"
  (diff/diff (set (commited-dot-files)) (set (current-dot-files))))

(defn print-diff-summary-report
  "Prints summary of files diffs between current and commited"
  []
  (let [commited-only (first diff-dot-files)
        current-only (second diff-dot-files)
        both (last diff-dot-files)]
    (println "Following dot files exist only in the home directory: ")
    (println (str/join "\n" current-only))
    (println)
    (println "Following dot files don't exist in the home directory: ")
    (println (str/join "\n" commited-only))
    (println)
    (println "Following dot files will be checked and diffed: ")
    (println (str/join "\n" both)))
  )

(defn diff-command
  "Construct diff command to diff dot files"
  [filename]
  (shell/sh "diff" (str home-dir "/" filename) (str "./resources/" filename)))

(defn is-diff?
  "Returns true if diff"
  [filename]
  (not (empty? (get (diff-command filename) :out))))

(defn print-diff-only-report
  "Print report on files which have diffs only"
  [filenames]
  (let [files-with-diff (map (fn [x] (if (is-diff? x) x nil)) filenames)]
    (println "Following files have differences:")
    (if (not (nil? files-with-diff))
      (println (str/join "\n" files-with-diff))
      (1))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (print-diff-summary-report)
  (print-diff-only-report (last diff-dot-files))
  (System/exit 0))