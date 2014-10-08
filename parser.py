# Need to DL pdfMiner and install it with "python setup.py install"
# use with "python parser.py menu41.pdf PDFextract.html"

import os
import sys

# PDF to extract
input = sys.argv[1]
# Raw html
output = sys.argv[2]

# Extract pdf into html
os.system(("pdf2txt.py -t html %s > %s")%(input, output))
i=0
for line in open(output):
	# We search the points of the table which have a width and a height of 0px
	if "width:0px; height:0px;" in line:
  		i = i + 1
		# The 6 first point are used for the line of the table, they are useless
		if i>6 and i!=42 and i!=48:
			print line
		print i
