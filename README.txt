How to install
apt-get install openjdk-8-jdk
 apt-get install mongodb
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

a2enmod proxy_ajp
a2enmod proxy_ssl
a2ensite default-ssl
 
<Location /tvscheduler>
ProxyPass  ajp://localhost:8009/tvscheduler
ProxyPassReverse http://www.cesarsuperstar.com/tvscheduler
allow from all
</Location>


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
 
 mount -tcifs //192.168.1.23/backup /mnt/nas -o"username=clement,password=Cl3m3nt-00"
 



 
 
 