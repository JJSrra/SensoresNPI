#include "connectiontounity.h"

connectionToUnity::connectionToUnity(QObject *parent) : QObject(parent)
{
    IP = "127.0.0.1";
    puerto = 6000;

}

void connectionToUnity::receiveMessageFromServer(QString data)
{
    qDebug() << "datos recibidos del servidor: " + data;
    socket->write( QByteArray(data.toUtf8()) );

}

void connectionToUnity::runConnection()
{
    socket = new QTcpSocket(this);

    qDebug() << "Conectando...";
    qDebug() << "IP: " << IP << " puerto: " << puerto;
    socket->connectToHost( IP, puerto);

    if(!socket->waitForConnected(1000)){
        qDebug() << "Error en unity: " << socket->errorString();
        socket->close();
    }else{
        qDebug() << "Conectado a unity";

    }

}
