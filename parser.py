# Use Python 3.2.5
# @author Hyukchan Kwon (hyukchan.k@gmail.com)
# @author Thibault Rapin (thibault.rapin@gmail.com)
# Need to DL pdfMiner and install it with "python setup.py install"
# use with "python3 parser.py X.pdf PDFextract.html"

import os
import sys
import re
import Word
import Cell

if(len(sys.argv) != 3):
    exit("Fatal Error: This script needs exactly 2 arguments: input file and output file.")


# PDF to extract
input = sys.argv[1]
# Raw html
output = sys.argv[2]

# Extract pdf into html
os.system(("pdf2txt.py -t html %s > %s")%(input, output))

# List of x coordinate of each column
tabColumn = []
# List of y coordinate of each row
tabRow = []

# Matrix of Cells
cellTable =[[Cell.Cell() for j in range(0,7)] for i in range(0,4)]

# Coordinates of <div> containing the current word
tmpX = 0
tmpY = 0

# Place word in the appropriate Cell in Matrix
# @param Word word : word to place
def selectCell(word):
    # Avoid spaces and out of bound words
    if(len(word.wordStr) > 1 and word.posX > tabColumn[0] and word.posY > tabRow[0]):
        i = 0
        for x in tabColumn:
            if word.posX < x:
                break
            i = i + 1

        j = 0
        for y in tabRow:
            if word.posY < y:
                break
            j = j + 1
        cellTable[i-1][j-1].addW(word)

# Fill tabColumn and tabRow with appropriate coordinates
i = 0
for line in open(output):
    # Search the corners of the cells which are represented by points
    if "width:0px; height:0px;" in line:
        i = i + 1
        # The 6 first points are used for the first line of the table, 42 and 48 are duplicated points
        if i>6 and i!=42 and i!=48:
            if i<12:
                mColumn = re.search("left:(.*?)px", line)
                tabColumn.append(int(mColumn.group(1)))
            mRow = re.search("left:(.*?)px; top:(.*?)px;", line)
            if(int(mRow.group(1)) == tabColumn[0]):
                tabRow.append(int(mRow.group(2)))

for line in open(output):
    if "</div><div" in line:
        mDiv = re.search("left:(.*?)px; top:(.*?)px;(.*)>(.*?)\s*$", line)
        tmpX = int(mDiv.group(1))
        tmpY = int(mDiv.group(2))
        currentWord = mDiv.group(4)
        #addCurrentWord(tabWord, currentWord)
        selectCell(Word.Word(currentWord, tmpX, tmpY))
    elif "</span><span" in line:
        mSpan = re.search(".*>(.*?)\s*$", line)
        currentWord = mSpan.group(1)
        #addCurrentWord(tabWord, currentWord)
        selectCell(Word.Word(currentWord, tmpX, tmpY))
    elif "<br>" in line:
        mBr = re.search("<br>(.*?)\s*$", line)
        currentWord = mBr.group(1)
        #addCurrentWord(tabWord, currentWord)
        selectCell(Word.Word(currentWord, tmpX, tmpY))

#print(tabWord)
for w in cellTable[3][6].wordList:
    print(w.wordStr)
    print(w.posX)
    print(w.posY)
    
    
# print(cellTable[0][0].wordList[1].wordStr)