#ifndef CONNETIONTOUNITY_H
#define CONNETIONTOUNITY_H

#include <QObject>
#include <Qstring>
#include <QDebug>
#include <QTcpSocket>
#include <QThread>

class connectionToUnity : public QObject
{
    Q_OBJECT
public:
    explicit connectionToUnity(QObject *parent = nullptr);
    void connectToUnity();
    void sendMessageToUnity();

signals:

public slots:
    void receiveMessageFromServer(QString data);
    void runConnection();
private:
    QTcpSocket *socket;
    QString IP;
    int puerto;

};

#endif // CONNETIONTOUNITY_H
