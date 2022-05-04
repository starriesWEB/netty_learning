package com.starry.netty.server.handler;

import com.starry.netty.message.GroupCreateRequestMessage;
import com.starry.netty.message.GroupCreateResponseMessage;
import com.starry.netty.server.session.Group;
import com.starry.netty.server.session.GroupSession;
import com.starry.netty.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Set;

/**
 * @author starry
 * @version 1.0
 * @date 2022/2/16 21:34
 * @Description
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        String groupName = msg.getGroupName();
        Set<String> members = msg.getMembers();
        GroupSession groupSession = GroupSessionFactory.getGroupSession();

        Group group = groupSession.createGroup(groupName, members);
        if (group != null) {
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, groupName + "已存在"));

        } else {
            // 通知被拉入的人员
            List<Channel> channelList = groupSession.getMembersChannel(groupName);
            channelList.parallelStream().forEach(channel -> channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入" + groupName)));

            ctx.writeAndFlush(new GroupCreateResponseMessage(true, groupName + "创建成功"));
        }
    }
}
