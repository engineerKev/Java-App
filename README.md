java program that stores system info(number of active threads) using Sigar API and stores it in a mySQL database

EXTERNALS FOR JAVA APP

1) sigar.jar 2)log4.jar 3)mysql connector jar

RUNNING JAVA APP

1) open in eclipse or netbeans, and add the above jars to the build path 

2) right click on the project go to BUILDPATH, select CONFIGURE BUILDPATH, select the libraries tab, 
click on add external jars, and locate the jars. 

3) Once the jars are loaded, if you don't have a database called TEST in your mySQL server, change that 
line(19) in the code to the name of the database you'd like the program to open upon execution 

4) Also remember to change the password, and user name 5) finally run the app and if everything is 
well you should see output messages showing the latest number of active threads that was stored in the database.
