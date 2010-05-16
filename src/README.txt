INSTRUCCIONES
Se utiliza ActiveMQ como message broker, y este esta configurado de tal manera que corrar
con maven cuando este ejecuta las pruebas. Asi que para ver los ejemplos en acción es 
necesario correr las pruebas. Si se deseara hacerse por separado, se puede iniciar ActiveMQ
en su puerto por default(que es el que se usa aquí) y usar las clases de manera individual.
Las pruebas principales son
-examples.jms.sender.GameCoverServiceSenderTest: es un servicio que regresa la imagen de portada
	de algunos juegos. Manda un mensaje string con el SKU del juego y regresa un mensaje de bytes
	con la imagen. Este ejemplo demuestra el patrón request/response, reply to, message selectors 
	y uso de propiedades. 
-examples.jms.spring.GameCoverServiceSpringJmsTemplate: este ejemplo es una replica del anterior
	pero usando componentes de spring para su funcionamiento. La parte del servidor esta hecha
	con un listener y un container de spring para mensajes asincronos. Que hace un reply a la
	destinación indicada por el mensaje que recibe. Para el cliente se inject un JmsTemplate para
	mandar y recibir mensajes con selector y propiedades.
-examples.jms.pubsub.LoteriagameTest: este ejemplo de Topic es un simulador del juego de loteria
	donde un anunciador(publisher) dice en voz alta la carta, para que todos los jugadores
	(subscribers) la escuchen y la marque en sus tableros. Hay un topico solo para las cartas que
	se juegan y existe otro para determinar la condición de fin del juego cuando un usuariomarca
	todo su tablero. Para esto se manda el mensaje loteria junto con el nombre del jugador a todos
	los involucrados(anunciador y jugadores). De tal manera que todos cumplen doble función de ser
	publisher y subscriber.

Las del paquete examples.jms prueban componentes individuales sin interactuar a traves de JMS


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
