/**
 * copyright@daixiao
 * file encoding: utf-8
 */
package com.dx.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.dx.netty.handler.MyNettyClientHandler;

import static com.dx.io.NetConstants.SERVER_PORT;

/**
 * netty 的一个客户端
 *
 * @author mica
 */
public class SimpleNettyClient {

    private static Log log = LogFactory.getLog(SimpleNettyClient.class);


    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MyNettyClientHandler());
                        }
                    });

            log.info("connect to server");
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", SERVER_PORT).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.warn("", e);
        } finally {
           group.shutdownGracefully();
        }
    }
}
