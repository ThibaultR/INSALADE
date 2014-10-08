# Use Python 3.2.5
# @author Hyukchan Kwon (hyukchan.k@gmail.com)
# @author Thibault Rapin (thibault.rapin@gmail.com)


import urllib.request
import time

# To use date without datetime.date
from datetime import date

currentYear = int(time.strftime('%Y'))
currentMonth = int(time.strftime('%m'))
# lstrip('0') to remove 0 at the beginning (i.e 07 -> 7)
currentDay = int(time.strftime('%d').lstrip('0'))

print(currentYear)
print(currentMonth)
print(currentDay)

week_number = date(currentYear, currentMonth, currentDay).isocalendar()[1]

print(week_number)

file_name = 'menu'+str(week_number)+'.pdf'
# Download file
# param : url of the file
# param : new local file name
urllib.request.urlretrieve('http://intranet.insa-rennes.fr/fileadmin/ressources_intranet/Restaurant/menu'+str(week_number)+'.pdf', file_name)