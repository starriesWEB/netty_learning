package com.starry.netty.server.handler;

import com.starry.netty.message.GroupChatRequestMessage;
import com.starry.netty.message.GroupChatResponseMessage;
import com.starry.netty.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/16 21:14
 * @Description
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName()).parallelStream().forEach(channel -> channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent())));
    }
}
