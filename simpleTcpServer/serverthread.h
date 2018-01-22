#ifndef SERVERTHREAD_H
#define SERVERTHREAD_H

#include <QObject>
#include <QTcpSocket>
#include <QThread>

class serverThread : public QThread
{
    Q_OBJECT
public:
    explicit serverThread(int ID,QObject *parent = nullptr);
    void run();

signals:
    void error(QTcpSocket::SocketError socketerror);
    void sendDataToServer(QString message);

public slots:
    void readyRead();
    void disconnected();
private:
    QTcpSocket *socket;
    int socketDescriptor;
};

#endif // SERVERTHREAD_H
