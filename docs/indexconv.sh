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
# Can generate Table Of Content (TOC) in the output *.md file by specifying `-t` option
# `> ./indexconv.sh -t`
#
# To generate TOC, will use the "technote-space/toc-generator" as described at
# https://qiita.com/technote-space/items/59520dfa47504c558805

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

find . -iname "*.adoc" -maxdepth 1 -type f  -not -name "_*.adoc" | while read fname; do
    target=${fname//adoc/md}
    xml=${fname//adoc/xml}
    echo "converting $fname into $target"
    # converting a *.adoc into a docbook
    asciidoctor -b docbook -a leveloffset=+1 -o - "$fname" > "$xml"
    cat "$xml" | pandoc --markdown-headings=atx --wrap=preserve -t markdown_strict -f docbook - > "$target"
    if [ $requireTOC = true ]; then
      # Insert the placeholder for TOC into the Markdown document to locate TOC
      echo -e "<!-- START doctoc -->\n<!-- END doctoc -->\n" > TOC_placeholder
      cat TOC_placeholder "$target" > with_TOC
      cat with_TOC > "$target"
      rm TOC_placeholder
      rm with_TOC
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