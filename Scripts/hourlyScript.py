# We use Python 3.2.5 (this script works with python 2)
#
# @author Hyukchan Kwon (hyukchan.k@gmail.com)
# @author Thibault Rapin (thibault.rapin@gmail.com)
#
# use script with "python hourlyScript.py"
# Our crontab (every hour) : 0 */1 * * * /usr/bin/python /root/INSALADE/hourlyScript.py > /tmp/mybackup.log 2>&1



import os

#os.chdir("/root/INSALADE/") #Uncomment on server

# Get available menu from intranet
os.system("python getMenu.py")

# Get the list of menu from getMenu.py
#menuList = os.listdir("/root/INSALADE/pdf/") #Uncomment on server
menuList = os.listdir("pdf")

# parse for each file
for menuFile in menuList:
    menu = os.path.splitext(menuFile)[0]
    #os.system(("python parser.py pdf/%s.pdf /var/www/INSALADE/XmlMenus/%s.xml")%(menu, menu)) #Uncomment on server
    os.system(("python parser.py pdf/%s.pdf xml/%s.xml")%(menu, menu))
