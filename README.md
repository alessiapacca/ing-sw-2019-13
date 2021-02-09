# Progetto di Ingegneria del Software 2019
Implementazione del gioco in scatola Adrenaline per il progetto di ingegneria del software 2019 al Politecnico di Milano. 

<p float="left">
  <img src="https://github.com/alessiapacca/ing-sw-2019-13/blob/master/adrenaline.jpg" width="450">
  <img src="https://github.com/alessiapacca/ing-sw-2019-13/blob/master/carte.jpg" width="470">
</p>



Membri del gruppo  | 
------------- | 
Matteo Pacciani  |
Tommaso Pegolotti  | 
Alessia Paccagnella | 

## Funzionalità sviluppate:
- [x] Regole complete
- [x] Socket
- [x] RMI
- [x] Gui
- [x] CLI
- [x] Partite Multiple

## Come generare il jar:

    1)Posizionarsi nella cartella del progetto
    2)eseguire il comando : mvn clean
    3)eseguire il comando : mvn package

## Come iniziare il gioco:

    Da intellij :
        1)Start server
        2)Far partire almeno 3 client sia UpdaterGui che UpdaterCli

    Jar :
        1) spostarsi nella cartella del jar (/ing-sw-2019-13/target)
        2) eseguire il comando : java -jar ing-sw-2019-13-1.0-SNAPSHOT-jar-with-dependencies.jar server
           per far partire il server
        3) eseguire il comando : java -jar ing-sw-2019-13-1.0-SNAPSHOT-jar-with-dependencies.jar gui
           per far partire un client con la gui oppure
           eseguire il comando : java -jar ing-sw-2019-13-1.0-SNAPSHOT-jar-with-dependencies.jar cli
           per far partire un client con la cli.

Per usare la cli basta riempire i campi richiesti, per far parire la gui bisogna
riempire i campi presenti nella schermata iniziale, il programma chiederà le informazioni
relative alla partita se necessario.

NB: è necessario installare javaFX per il proprio sistema operativo.
