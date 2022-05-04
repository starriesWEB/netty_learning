package com.starry.netty.server.handler;

import com.starry.netty.message.LoginRequestMessage;
import com.starry.netty.message.LoginResponseMessage;
import com.starry.netty.message.Message;
import com.starry.netty.server.service.UserServiceFactory;
import com.starry.netty.server.session.SessionFactory;
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
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUsername();
        String password = msg.getPassword();
        boolean login = UserServiceFactory.getUserService().login(username, password);
        Message message;
        if (login) {
            // 间用户和对应的 channel 进行绑定
            SessionFactory.getSession().bind(ctx.channel(), username);
            message = new LoginResponseMessage(true, "登录成功");
        } else {
            message = new LoginResponseMessage(false, "用户名或密码错误");
        }
        ctx.writeAndFlush(message);
    }
}
