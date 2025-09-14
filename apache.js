<VirtualHost *:80>

    ServerName condologix.com
    ServerAlias www.condologix.com

    ProxyPreserveHost On
    ProxyPass / http://localhost:3000/
    ProxyPassReverse / http://localhost:3000/

</VirtualHost>

<VirtualHost *:80>

    ServerName pageinpublic.com
    ServerAlias www.pageinpublic.com

    ProxyPreserveHost On
    ProxyPass / http://localhost:4000/
    ProxyPassReverse / http://localhost:4000/

</VirtualHost>
