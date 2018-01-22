#include "serverthread.h"

serverThread::serverThread(int ID, QObject *parent) : QThread(parent)
{
    socketDescriptor = ID;
}

void serverThread::run()
{
    qDebug() << socketDescriptor << " Starting thread";
    socket = new QTcpSocket();
    if(!socket->setSocketDescriptor(this->socketDescriptor))
    {
        emit error(socket->error());
        return;
    }

    connect(socket,SIGNAL(readyRead()),this,SLOT(readyRead()),Qt::DirectConnection);
    connect(socket,SIGNAL(disconnected()),this,SLOT(disconnected()),Qt::DirectConnection);

    qDebug() << socketDescriptor << " Client Connected";

    exec();
}

void serverThread::readyRead()
{
    QByteArray Data = socket->readLine();

    qDebug() << socketDescriptor << " Data in: " << Data;
    emit sendDataToServer(QString(Data));
}

void serverThread::disconnected()
{
    qDebug() << socketDescriptor << " Disconnected";

    socket->deleteLater();
    exit(0);
}
