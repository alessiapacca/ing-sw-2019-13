# Progetto di Ingegneria del Software 2019
Implementazione del gioco in scatola Adrenaline per il progetto di ingegneria del software 2019 al Politecnico di Milano. 

<p float="left">
  <img src="https://github.com/alessiapacca/ing-sw-2019-13/blob/master/adrenaline.jpg" width="400">
  <img src="https://github.com/alessiapacca/ing-sw-2019-13/blob/master/carte.jpg" width="415">
</p>



Devs  | 
------------- | 
Matteo Pacciani  |
Tommaso Pegolotti  | 
Alessia Paccagnella | 

## Developed features:
- [x] Complete game rules 
- [x] Socket connection 
- [x] RMI connection
- [x] Gui (graphical user interface)
- [x] CLI (command line interface)
- [x] Multi player

## How to generate the jar:

    In the project folder
    1) mvn clean
    2) mvn package

## How to start the game:

    From intellij :
        1)Start server
        2)Start at least 3 clients (both UpdaterGui and UpdaterCli)

    Jar :
        In the jar folder (/ing-sw-2019-13/target)
        1) java -jar ing-sw-2019-13-1.0-SNAPSHOT-jar-with-dependencies.jar server
           to start the server
        2) java -jar ing-sw-2019-13-1.0-SNAPSHOT-jar-with-dependencies.jar gui
           to start a client with the gui
           java -jar ing-sw-2019-13-1.0-SNAPSHOT-jar-with-dependencies.jar cli
           to start a client with the cli 

Note: the installation of javaFX is required
