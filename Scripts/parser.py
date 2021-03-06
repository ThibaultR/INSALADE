# We use Python 3.2.5
# Need to DL pdfMiner and install it with "python setup.py install" (DL : http://www.unixuser.org/~euske/python/pdfminer/#download)
# 
# @author Hyukchan Kwon (hyukchan.k@gmail.com)
# @author Thibault Rapin (thibault.rapin@gmail.com)
# 
# use script with "python3 parser.py X.pdf output.xml"

import os
import sys
import re
import Word
import Cell
import Menu

if(len(sys.argv) != 3):
    exit("Fatal Error: This script needs exactly 2 arguments: input file (.pdf) and output file (.xml).")


# PDF to extract
input = sys.argv[1]

# Extracted in html
tmpHtmlFile = "tmp.html"

# Export file (XML)
output = sys.argv[2]

# Extract pdf into html
#os.system(("/usr/local/bin/pdf2txt.py -t html %s > %s")%(input, tmpHtmlFile)) #Uncomment on server
os.system(("pdf2txt.py -t html %s > %s")%(input, tmpHtmlFile))

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
for line in open(tmpHtmlFile):
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

for line in open(tmpHtmlFile):
    if "</div><div" in line:
        mDiv = re.search("left:(.*?)px; top:(.*?)px;(.*)>(.*?)\s*$", line)
        tmpX = int(mDiv.group(1))
        tmpY = int(mDiv.group(2)) + 10
        currentWord = mDiv.group(4)
        selectCell(Word.Word(currentWord, tmpX, tmpY))
    elif "</span><span" in line:
        tmpY = tmpY + 12
        mSpan = re.search(".*>(.*?)\s*$", line)
        currentWord = mSpan.group(1)
        selectCell(Word.Word(currentWord, tmpX, tmpY))
    elif "<br>" in line:
        tmpY = tmpY + 12
        mBr = re.search("<br>(.*?)\s*$", line)
        currentWord = mBr.group(1)
        selectCell(Word.Word(currentWord, tmpX, tmpY))


menuTable =[[Menu.Menu() for j in range(0,7)] for i in range(0,2)]

CONST_LUNCH = 0
CONST_DINNER = 1
# Loop on the row of the matrix
for i in range(0,7):
    # set starter lunch
    menuTable[0][i].starter = cellTable[1][i].wordList

    # set mainCourse and dessert lunch
    j = 0
    print i;
    while j < len(cellTable[2][i].wordList) and len(cellTable[2][i].wordList)>0 and cellTable[2][i].wordList[j].wordStr.find("Yaourt") == -1:
        menuTable[0][i].mainCourse.append(cellTable[2][i].wordList[j])
        j = j + 1
    while j < len(cellTable[2][i].wordList):
        menuTable[0][i].dessert.append(cellTable[2][i].wordList[j])
        j = j + 1

    # set starter mainCourse dessert dinner
    if len(cellTable[3][i].wordList)>0:
        menuTable[1][i].starter.append(cellTable[3][i].wordList[0])
        j = 1
        while j < len(cellTable[3][i].wordList) and cellTable[3][i].wordList[j].wordStr.find("Yaourt") == -1:
            menuTable[1][i].mainCourse.append(cellTable[3][i].wordList[j])
            j = j + 1
        while j < len(cellTable[3][i].wordList):
            menuTable[1][i].dessert.append(cellTable[3][i].wordList[j])
            j = j + 1

    # set when
    menuTable[0][i].when = CONST_LUNCH
    menuTable[1][i].when = CONST_DINNER
    
    # set date
    menuTable[0][i].date = cellTable[0][i].wordList[0].wordStr
    menuTable[1][i].date = cellTable[0][i].wordList[0].wordStr

outputFile = open(output, "w")
outputFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>\n")
outputFile.write("<week>\n")
for y in range(0,7):
    for x in range(0,2):
        outputFile.write("\t<menu date=\""+menuTable[x][y].date+"\" when=\""+str(menuTable[x][y].when)+"\">\n")
        outputFile.write("\t\t<starter>\n")
        for w in menuTable[x][y].starter:
            outputFile.write("\t\t\t"+w.wordStr+"\n")
        outputFile.write("\t\t</starter>\n")

        outputFile.write("\t\t<maincourse>\n")
        for w in menuTable[x][y].mainCourse:
            outputFile.write("\t\t\t"+w.wordStr+"\n")
        outputFile.write("\t\t</maincourse>\n")
        
        outputFile.write("\t\t<dessert>\n")
        for w in menuTable[x][y].dessert:
            outputFile.write("\t\t\t"+w.wordStr+"\n")
        outputFile.write("\t\t</dessert>\n")
        outputFile.write("\t</menu>\n")
outputFile.write("</week>\n")
