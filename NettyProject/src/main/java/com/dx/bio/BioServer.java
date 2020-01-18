package com.dx.bio;

import static com.dx.io.NetConstants.NET_BUFFER;
import static com.dx.io.NetConstants.SERVER_PORT;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Bio ��һ������ˡ�
 *
 * @author daixiao
 */
public class BioServer {

    /** ��־��¼���� */
    private static Log log = LogFactory.getLog(BioServer.class);

    public static void main(final String[] args) {
        // ����һ���̳߳�
        ThreadFactory factory = new ThreadFactoryBuilder()
                .setNameFormat("nio-pool-test-%d").build();
        ExecutorService pool = new ThreadPoolExecutor(5, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingDeque<Runnable>(NET_BUFFER), factory);
        // ����һ���߳���Ӧ�ͻ���
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            log.info(String.format("server is listening on port %d", SERVER_PORT));
            while (true) {
                // ��Խ������ӵĿͻ��˽��д���
                final Socket socket = serverSocket.accept();
                log.info(String.format("a client connect to server: %s:%d",
                        socket.getInetAddress(), socket.getLocalPort()));

                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        handleMsg(socket);
                    }
                });
            }
        } catch (IOException e) {
            log.warn("create server failed OR accept client failed", e);
        }

    }

    /**
     * ����ͻ�����Ϣ�ķ���
     *
     * @param socket �ͻ��˵� socket
     */
    public static void handleMsg(Socket socket) {
        log.info(String.format("current pid is %s, and thread name is %s",
                Thread.currentThread().getId(), Thread.currentThread().getName()));
        byte[] buffer = new byte[NET_BUFFER];
        try (InputStream inputStream = socket.getInputStream()) {
            while (true) {
                if (inputStream.read(buffer) != -1) {
                    log.info(new String(buffer, 0, buffer.length, StandardCharsets.UTF_8));
                } else {
                    break;
                }
            }
        } catch (IOException e) {
            log.warn(String.format("require input stream from client %s:%d failed!",
                    socket.getInetAddress(), socket.getLocalPort()), e);
        } finally {
            log.info(String.format("close client %s:%d socket!",
                    socket.getInetAddress(), socket.getLocalPort()));
            try {
                socket.close();
            } catch (IOException e) {
                log.warn(String.format("close client %s:%d socket failed!",
                        socket.getInetAddress(), socket.getLocalPort()), e);
            }
        }
    }
}
