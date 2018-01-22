#include <QCoreApplication>
#include <tcpserver.h>
#include <QObject>
#include <QThread>

int main(int argc, char *argv[])
{
    QCoreApplication a(argc, argv);

    tcpServer mTcpServer;

    return a.exec();
}
