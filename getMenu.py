# Use Python 3.2.5
# @author Hyukchan Kwon (hyukchan.k@gmail.com)
# @author Thibault Rapin (thibault.rapin@gmail.com)

import time
import re
import mechanize

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

# Get login/pwd
fic = open('./pw.txt','r')
username = fic.readline();
password = fic.readline();
fic.close()

# Connect with mechanize
url = "https://cas.insa-rennes.fr/cas/login?service=http://intranet.insa-rennes.fr/"
br = mechanize.Browser()
br.set_handle_robots(False) #You may need to do this
br.open(url)
br.select_form(nr=0) #Choose the right form number. You can choose the form via the name attribute too select_form(name="YOUR_FORM_NAME")
br['username']=username
br['password']=password
br.method = "POST"
response = br.submit() #At this point you should see the html for the page that loads after login

fo = open(file_name, "w")
fo.write(br.open('http://intranet.insa-rennes.fr/fileadmin/ressources_intranet/Restaurant/menu'+str(week_number)+'.pdf').get_data())

fo.close()
