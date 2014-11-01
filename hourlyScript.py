# We use Python 3.2.5 (this script works with python 2)
#
# @author Hyukchan Kwon (hyukchan.k@gmail.com)
# @author Thibault Rapin (thibault.rapin@gmail.com)
#
# use script with "python hourlyScript.py"
# Our crontab (every hour) : 0 */1 * * * python /root/INSALADE/hourlyScript.py


import os


# Get available menu from intranet
os.system("python getMenu.py")

# Get the list of menu from getMenu.py
menuList = os.listdir("pdf")

# parse for each file
for menuFile in menuList:
        menu = os.path.splitext(menuFile)[0]
		# in server use /var/www/INSALADE/XmlMenus/ instead of xml/
        os.system(("python parser.py pdf/%s.pdf xml/%s.xml")%(menu, menu))
