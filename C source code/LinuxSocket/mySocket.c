#include <stdio.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <unistd.h>
#include <netinet/in.h>
#include <string.h>

#define TRUE 1


void conn()
{
    // 定义一个服务端地址结构
    struct sockaddr_in my_addr;
    my_addr.sin_family = AF_INET;
    my_addr.sin_port = htons(8888);
    my_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    // 获取文件的地址
    int listen_fd = socket(AF_INET, SOCK_STREAM, 0);
    bind(listen_fd, (struct sockaddr *)&my_addr, sizeof(my_addr));

    listen(listen_fd, 5);
    printf("create server and listen");

    // 创建一个客户端地址结构
    struct sockaddr_in my_client;
    socklen_t client_len = sizeof(my_client);
    int client_fd = accept(listen_fd, (struct sockaddr *)&my_client, &client_len);
    printf("connection successfully! ...");

    char buffer[1024];

    while (TRUE)
    {
        memset(buffer, 0, sizeof(buffer));
        int read_len = read(client_fd, buffer, sizeof(buffer));
        printf("%s\n", buffer);
    }
    
}

int main()
{
    conn();
    return 0;
}