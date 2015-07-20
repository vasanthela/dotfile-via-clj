# dotfile-via-clj

Simple dot file management using clojure utility

## Installation

Requires leiningen

## Usage

Place all canoncial dot files in the ./resources folder

    $ lein run
    
## Sample Output

    $ Following dot files exist only in the home directory:
    .gemrc
    .bash_history

    $ Following dot files don't exist in the home directory:
    .tmux.conf

    $ Following dot files will be checked and diffed:
    .ackrc
    .vimrc
    .zshrc

    $ Following files have differences:
    .vimrc
