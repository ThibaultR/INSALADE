# Use Python 3.2.5
# @author Hyukchan Kwon (hyukchan.k@gmail.com)
# @author Thibault Rapin (thibault.rapin@gmail.com)


import Word

# Each Cell of the table will be represented by the object Cell which contains a list of Word
class Cell:
    def __init__(self):
        self.wordList = []
        
    def addW(self, word):
        
        i = 0
        for w in self.wordList:
            if word.posY < w.posY and i < len(self.wordList):
                self.wordList.insert(i,word)
                break
            i = i + 1

        # case if word.posY is the biggest
        if i == len(self.wordList):
            self.wordList.insert(i,word)
        
        
"""
#Test Cell and Word classes
c = Cell()
w1 = Word.Word("coucou1", 88, 69)
w2 = Word.Word("coucou2", 88, 70)
w3 = Word.Word("coucou3", 88, 51)
w4 = Word.Word("coucou4", 56, 69)
print(c.wordList)
c.addW(w1)
c.addW(w2)
c.addW(w3)
c.addW(w4)
print(c.wordList[0].posY)
print(c.wordList[1].posY)
print(c.wordList[2].posY)
print(c.wordList[3].posY)

cellTable =[[Cell() for j in range(0,3)] for i in range(0,6)]
Table[0][0].addW(w1)
Table[0][0].addW(w2)
Table[0][1].addW(w2)
Table[0][1].addW(w3)
print(Table)
print(Table[0][0].wordList[0].posY)
print(Table[0][1].wordList[0].posY)
print(Table[0][0].wordList[1].posY)
print(Table[0][1].wordList[1].posY)
"""





