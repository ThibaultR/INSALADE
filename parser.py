# Need to DL pdfMiner and install it with "python setup.py install"
# use with "python parser.py menu41.pdf PDFextract.html"

import os
import sys
import re

if(len(sys.argv) != 3) :
    exit("Fatal Error: This script needs exactly 2 arguments: input file and output file.")

# PDF to extract
input = sys.argv[1]
# Raw html
output = sys.argv[2]

# Extract pdf into html
os.system(("pdf2txt.py -t html %s > %s")%(input, output))
i=0
tabColumn = []
tabRow = []

tabWord = []

tmpX = 0
tmpY = 0

def addCurrentWord(tabWord, currentWord):
    if(len(currentWord) > 1):
        tabWord.append(currentWord)

for line in open(output):
    # We search the points of the table which have a width and a height of 0px
    if "width:0px; height:0px;" in line:
        i = i + 1
        # The 6 first point are used for the line of the table, they are useless
        if i>6 and i!=42 and i!=48:
            if i<12:
                mColumn = re.search("left:(.*?)px", line)
                tabColumn.append(int(mColumn.group(1)))
            mRow = re.search("left:(.*?)px; top:(.*?)px;", line)
            if(int(mRow.group(1)) == tabColumn[0]):
                tabRow.append(int(mRow.group(2)))
    if "</div><div" in line:
        mDiv = re.search("left:(.*?)px; top:(.*?)px;(.*)>(.*?)\s*$", line)
        tmpX = mDiv.group(1)
        tmpY = mDiv.group(2)
        currentWord = mDiv.group(4)
        addCurrentWord(tabWord, currentWord)
    else:
        if "</span><span" in line:
            mSpan = re.search(".*>(.*?)\s*$", line)
            currentWord = mSpan.group(1)
            addCurrentWord(tabWord, currentWord)
        else:
            if "<br>" in line:
                mBr = re.search("<br>(.*?)\s*$", line)
                currentWord = mBr.group(1)
                addCurrentWord(tabWord, currentWord)