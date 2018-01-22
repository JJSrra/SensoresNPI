#include "tcpserver.h"

tcpServer::tcpServer(QObject *parent) : QTcpServer(parent)
{

    mThreadUnity = new QThread(this);
    mConnection = new connectionToUnity();

    mConnection->moveToThread(mThreadUnity);
    connect(this,SIGNAL(sendDataToUnityConnection(QString)), mConnection,SLOT(receiveMessageFromServer(QString)));
    connect(mThreadUnity,SIGNAL(started()),mConnection,SLOT(runConnection()));
    mThreadUnity->start();

    if(!this->listen(QHostAddress::Any,serverPort))
    {
        qDebug() << "Could not start server";
    }
    else
    {
        qDebug() << "Listening...";
    }

}

void tcpServer::incomingConnection(int socketDescriptor)
{
    qDebug() << socketDescriptor << " Connecting...";
    serverThread *thread = new serverThread(socketDescriptor,this);
    connect(thread, SIGNAL(finished()),thread, SLOT(deleteLater()));
    connect(thread,SIGNAL(sendDataToServer(QString)),this, SLOT(receiveFromThread(QString) ));
    thread->start();
}

void tcpServer::receiveFromThread(QString message){
    qDebug() << "servidor: mensaje recibido de la conexión: " + message +
                "enviando a unity";
    emit sendDataToUnityConnection(message);
}
