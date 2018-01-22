#ifndef TCPSERVER_H
#define TCPSERVER_H

#include <QObject>
#include <QTcpServer>
#include <QDebug>
#include <serverthread.h>
#include <connectiontounity.h>
#include <QThread>

class tcpServer : public QTcpServer
{
    Q_OBJECT
public:
    explicit tcpServer(QObject *parent = nullptr);
    void StartServer();

signals:
    void sendDataToUnityConnection(QString dataToSend);

public slots:
    void incomingConnection(int socketDescriptor);
    void receiveFromThread(QString message);
private:
    QTcpServer * mServer;
    int serverPort = 5000;
    connectionToUnity *mConnection;
    QThread *mThreadUnity;


};

#endif // TCPSERVER_H
