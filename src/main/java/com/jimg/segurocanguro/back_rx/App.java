package com.jimg.segurocanguro.back_rx;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;

import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerInterceptorChain;
import io.reactivex.netty.protocol.http.server.HttpServerInterceptorChain.Interceptor;

import com.jimg.segurocanguro.back_rx.controlador.PrincipalCont;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	HttpServer<ByteBuf, ByteBuf> server;
    	
        server = HttpServer.newServer(8081)
        		.start(HttpServerInterceptorChain.startRaw()
                        .next(addJsonHeader())
                        .end(PrincipalCont::controlarPeticionesHttp));
                                          
        server.awaitShutdown();
    }
    
    private static Interceptor<ByteBuf, ByteBuf> addJsonHeader() {
        return handler -> (request, response) -> {
            return handler.handle(request ,
                                  response.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON));
        };
    }
}
