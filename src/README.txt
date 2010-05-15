
TODO Queue sender/receiver
+Bajar el active mq y ver el config.xml
+Configurar el active mq en maven
+Queue que mande sku de los juegos y regrese las imagenes en un byteMessage 
+con reply queue 
+que se haga filtering por el correlation id en el cliente
+message listener en el lado que regresa las imagenes
+loggear los metadados de la conexion
++hacer browsing de una cola
+Con message filtering por consola :D en el servidor de imagenes: no estoy procesando los de xbox :D

TODO Topic publish/suscribe
+Crear un juego de loteria con tableros aleatorios
Crear un publisher que diga que carta se escoge
Crear 10 suscribers que representend jugadores y que se suscriban al topico con MessageListener
Cuando un jugador gane, mande la señal de loteria a un reply Destination

+con queue que haga replay a otro queue
+loggear connectionMetadata con connetcion.getMetadata
+usar QueueBrowser de session.createBrowser
mandar mensajes como una transaccion a una cola
leear mensajes como una transaccion en un replayTo queue
+configurar el activemq con maven
-configurar persistent suscribers con activemq
+Hacer el lockup con jndi.properties
Mandar mensajes con spring jmstemplate poniendo properties o headers
Recibir mensajes convertidos con jmstemplate
configurar una sessionfactory con jndi
configurar una sessionfactory directamente con objectos de activemq
hacer un mdp con messagelistener
hacer mpd con messagelisteneradapter
