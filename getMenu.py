import urllib.request
file_name = 'test.pdf'
# Download file
# param : url of the file
# param : new local file name
urllib.request.urlretrieve('http://intranet.insa-rennes.fr/fileadmin/ressources_intranet/Restaurant/menu41.pdf', file_name)