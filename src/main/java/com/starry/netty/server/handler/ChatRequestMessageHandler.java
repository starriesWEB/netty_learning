package com.starry.netty.server.handler;

import com.starry.netty.message.ChatRequestMessage;
import com.starry.netty.message.ChatResponseMessage;
import com.starry.netty.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/16 21:16
 * @Description
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {

        // 接收人
        String to = msg.getTo();
        // 发送人
        String from = msg.getFrom();
        // 消息内容
        String content = msg.getContent();

        // 通过用户名获取对应的 channel
        Channel channel = SessionFactory.getSession().getChannel(to);
        if (channel == null) {
            // 返回给发送人
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户名不存在或者不在线"));
        } else {
            // 发送给接收人
            channel.writeAndFlush(new ChatResponseMessage(from, content));
            // 返回给发送人
            ctx.writeAndFlush(new ChatResponseMessage(true, "发送成功"));
        }

    }
}
