#!/usr/bin/env bash

# Convert all the files with name ending with `*.adoc` into `*.md`.
# `*.adoc` is an Asciidoc document file, `*.md` is a Mardown document file.
# E.g, `index_.adoc` will be converted into `index_.md`
# Except ones with `_` as prefix.
# E.g, `_index.adoc` is NOT processed by this script, will be left unprocessed.
#
# How to active this: in the command line, just type
# `> ./indexconv.sh`
#
# Can generate Table of content in the output *.md file by specifying `-t` option
# `> ./indexconv.sh -t`

requireTOC=false

optstring="t"
while getopts ${optstring} arg; do
    case ${arg} in
        t)
            requireTOC=true
            ;;
        ?)
            ;;
    esac
done

find . -iname "*.adoc" -type f -maxdepth 1 -not -name "_*.adoc" | while read fname; do
    target=${fname//adoc/md}
    xml=${fname//adoc/xml}
    echo "converting $fname into $target"
    # converting a *.adoc into a docbook
    asciidoctor -b docbook -a leveloffset=+1 -o - "$fname" > "$xml"
    if [ $requireTOC = true ]; then
      # generate a Markdown file with Table of contents
      cat "$xml" | pandoc --standalone --toc --markdown-headings=atx --wrap=preserve -t markdown_strict -f docbook - > "$target"
    else
      # without TOC
      cat "$xml" | pandoc --markdown-headings=atx --wrap=preserve -t markdown_strict -f docbook - > "$target"
    fi
    echo deleting $xml
    rm -f "$xml"
done

# if we find a index*.md (or index*.md),
# we rename all of them to a single index.md while overwriting,
# effectively the last wins.
# E.g, if we have `index_.md`, it will be overwritten into `index.md`
find . -iname "index*.md" -not -name "index.md" -type f -maxdepth 1 | while read fname; do
    echo Renaming $fname to index.md
    mv $fname index.md
done