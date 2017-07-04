How to install

 apt-get update 
 apt-get upgrade 
 
apt-get install openjdk-8-jdk
apt-get install mongodb
apt-get install apache2
apt-get install vim
apt-get install wiringpi
apt-get install git*git config --global user.email "clementsoullard@yahoo.fr"
git config --global user.name "Clement Soullard"


git clone https://github.com/clementsoullard/background-scheduler-service.git -buse_lcd_lib

a2enmod proxy_ajp
a2enmod proxy_ssl
a2ensite default-ssl
a2enmod ssl
 vi /etc/apache2/conf-enabled/java.conf

Check the configuration by going to with browser on the HTTPS

The take the info of the certificate export it and replace the file rapberry.crt in the Android project

  apt-get install tinyproxy
 
 
 vi /etc/tinyproxy.conf
 
 Allow 192.168.1.0/16
 
 service tinyproxy start
<Location /tvscheduler>
ProxyPass  ajp://localhost:8009/tvscheduler
ProxyPassReverse http://www.cesarsuperstar.com/tvscheduler
allow from all
</Location>

service apache2 restart


update-rc.d ssh enable 2 3 4 5

vi .ssh/authorized_keys

ssh-rsa AAAAB3NzaC1yc2EAAAABJQAAAQEAgtNOvkPB4QHGGLw+hixQggvlcr7G7LrkJsBZ5f5hl+ekJsgAwV/3ODqjiQaEZ7vRFq0siWvMwLXuNX5vevplKLg7wvyf+R/tp/u2b88XPBwyEYUVMsexHLlN0hAuMNqAxw2SMa02WZXsYZVHJd1daamcieXSdKHpJ77u7dtPohPnS8a3LBHEFhIMtA8/tgnibkyEqepqjicQUKy8gCpLx7j7wVp9dV2QN5xxghHyNAoTbmfxmzXfDxNyt2ub4VEDVUE0VakjCNsqtHrsRLRlwjjVsEjSlR1/ktFpLOfagmKom+QmfUxnUU8/lLkPSu9oJSAy2muBXVHlHsZLNjCiIw== imported-openssh-key

unzip apache-tomcat-*.zip
mv apache-tomcat-* tomcat-8
cd tomcat-8
chmow +x bin/*.sh

mkdir /home/clement/tomcat-8/conf-ext
Create file 
 /home/clement/tomcat-8/conf-ext/application-prod.properties
 edit file tomcat-8/bin/setenv.sh
 chmod +x  tomcat-8/bin/setenv.sh
 
 It contains
 
 #!/bin/sh
echo Sourcing setenv.sh
CLASSPATH=$CLASSPATH:$CATALINA_HOME/conf-ext
JAVA_OPTS="$JAVA_OPTS -Dspring.profiles.active=prod"
 
vi conf-ext/application-prod.properties
 It contains 
 
 # To switch of the http port.
server.port=8080
spring.data.mongodb.uri=mongodb://192.168.1.21:27017/test
scheduler.work.path=/home/clement/scheduler/work
livebox.urlPrefix=http://192.168.1.12:8080
production.mode=false

# Configuration du fichier de log


vi conf-ext/logback.xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <layout class="ch.qos.logback.classic.PatternLayout">
  <!--    <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>  -->
  <Pattern>%d{HH:mm:ss} %msg%n</Pattern> 
    </layout>
  </appender>
  
  <logger name="com.clement" level="DEBUG"/>

  <logger name="org.springframework.data.mongodb.core" level="DEBUG"/>

  <root level="INFO">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>


mkdir -p /home/clement/scheduler/work/



Importthe channel from the channels file in data
mongoimport --db test --collection channel --type json --file channels.json --jsonArray

Export the certicate from the https serer and copy it in the asset folder of the android project

Disable empty password
vi /etc/sshd/sshd_config

# Change to no to disable tunnelled clear text passwords
PasswordAuthentication no

Configurer la box comme dans les PNG du repertoire doc

Configurer IIS pour accepter tous les hostname
(Voir screen shot)
Ouvrir le firewall windows pour accepter les connexion sur le port 81

Ajouter le DNS pour le decodeur dans la configuration de la box
decodeur 192.168.1.12

 ln -sf /usr/share/zoneinfo/Europe/Paris /etc/localtime
 
 mkdir /mnt/nas
 mount -tcifs //192.168.1.23/backup /mnt/nas -o"username=clement,password=Cl3m3nt-00"
 
mongorestore -d tvscheduler dumpmongo/tvscheduler/



vi /etc/cron.daily/mongodump

#!/bin/bash
cd /tmp/
mongodump -dtvscheduler -omongodump
tar -cvzf dump-mongo-`date +%Y%m%d`-day.tgz mongodump/
cp  dump-mongo-`date +%Y%m%d`-day.tgz /mnt/nas/backup-mongo/
rm -f dump-mongo-*
rm -rf mongodump/
find /mnt/nas/backup-mongo/ -name *day* -mtime +7 -exec rm -f {} \;

chmod +x /etc/cron.daily/mongodump


vi /etc/cron.weekly/mongodump-week

#!/bin/bash
cd /tmp/
mongodump -dtvscheduler -omongodump
tar -cvzf dump-mongo-`date +%Y%m%d`-week.tgz mongodump/
cp  dump-mongo-`date +%Y%m%d`-week.tgz /mnt/nas/backup-mongo/
rm -f dump-mongo-*
rm -rf mongodump/
find /mnt/nas/backup-mongo/ -name *week* -mtime +31 -exec rm -f {} \;

chmod +x /etc/cron.weekly/mongodump-week

vi /etc/cron.monthly/mongodump-month

#!/bin/bash
cd /tmp/
mongodump -dtvscheduler -omongodump
tar -cvzf dump-mongo-`date +%Y%m%d`-month.tgz mongodump/
cp  dump-mongo-`date +%Y%m%d`-month.tgz /mnt/nas/backup-mongo/
rm -f dump-mongo-*
rm -rf mongodump/
find /mnt/nas/backup-mongo/ -name *month* -mtime +365 -exec rm -f {} \;

chmod +x /etc/cron.monthly/mongodump-month

service cron reload

vi /etc/cron.daily/backup-tomcat

#!/bin/sh
SCOPE=day
tar -cvzf /mnt/nas/backup-tomcat/tomcat-`date +%Y%m%d`-$SCOPE.tgz -C/home/clement tomcat-8
find /mnt/nas/backup-tomcat/ -name \*$SCOPE\* -mtime +7 -exec rm -f {} \;

 
 
 