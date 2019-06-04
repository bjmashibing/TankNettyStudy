package com.mashibing.nettystudy.s08;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {
	public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
	
	public static void main(String[] args) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(2);
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			ChannelFuture f = b.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pl = ch.pipeline();
						pl.addLast(new ServerChildHandler());
					}
				})
				.bind(8888)
				.sync();
			
			System.out.println("server started!");
			
			f.channel().closeFuture().sync(); //close()->ChannelFuture
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
		
	}
}

class ServerChildHandler extends ChannelInboundHandlerAdapter { //SimpleChannleInboundHandler Codec
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Server.clients.add(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		ByteBuf buf = null;
		try {
			buf = (ByteBuf)msg;
			
			byte[] bytes = new byte[buf.readableBytes()];
			buf.getBytes(buf.readerIndex(), bytes);
			System.out.println(new String(bytes));
			
			Server.clients.writeAndFlush(msg);
			
			//System.out.println(buf);
			//System.out.println(buf.refCnt());
		} finally {
			//if(buf != null && buf) ReferenceCountUtil.release(buf);
			//System.out.println(buf.refCnt());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		//删除出现异常的客户端channle，并关闭连接
		Server.clients.remove(ctx.channel());
		ctx.close();
	}
	
	
}





