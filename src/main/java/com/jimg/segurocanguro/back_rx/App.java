package com.jimg.segurocanguro.back_rx;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerInterceptorChain;
import io.reactivex.netty.protocol.http.server.HttpServerInterceptorChain.Interceptor;

import com.google.gson.Gson;
import com.jimg.segurocanguro.back_rx.modelo.Peticion;
import com.jimg.segurocanguro.back_rx.modelo.Respuesta;

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
                        .next(addHeader())
                        .end((req, resp) -> {                        	
                        	return resp.writeString(req.getContent().compose( content -> {
                        		return content.map( body -> {
                        			Gson gson = new Gson();
                        			Peticion pet = gson.fromJson(body.toString(CharsetUtil.UTF_8), Peticion.class);
                        			Respuesta rel = new Respuesta();
                        			rel.setMensaje("Hola "+pet.getNombre());
                        			
                        			return gson.toJson(rel);
                        		});
                        	}));
                        	
                        }));
                                          
        System.out.println(server.getServerPort());
        
        server.awaitShutdown();
    }
    
    private static Interceptor<ByteBuf, ByteBuf> addHeader() {
        return handler -> (request, response) -> {
            return handler.handle(request ,
                                  response.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON));
        };
    }
}
