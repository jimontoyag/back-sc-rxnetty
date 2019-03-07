package com.jimg.segurocanguro.back_rx.controlador;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jimg.segurocanguro.back_rx.modelo.Usuario;
import com.jimg.segurocanguro.back_rx.servicio.AutenticacionServ;
import com.jimg.segurocanguro.back_rx.servicio.JwtServ;
import com.jimg.segurocanguro.back_rx.servicio.PostServ;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static rx.Observable.*;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import io.reactivex.netty.protocol.http.server.ResponseContentWriter;

public class PrincipalCont {
	
	public static ResponseContentWriter<ByteBuf> controlarPeticionesHttp(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp){
		if(req.getHeader(HttpHeaderNames.CONTENT_TYPE).equals(HttpHeaderValues.APPLICATION_JSON.toString())) {
			if(!req.getUri().startsWith("/public")) {
				if(req.getQueryParameters().get("sc-token") == null) {
					return resp.setStatus(HttpResponseStatus.UNAUTHORIZED);
				} 
				if(!JwtServ.getInstancia().validarToken(req.getQueryParameters().get("sc-token").get(0))) {
					return resp.setStatus(HttpResponseStatus.UNAUTHORIZED);
				}
			}
			switch (req.getHttpMethod().name()) {
			case "POST":
				return PrincipalCont.recibirPost(req, resp);
			case "GET":
				return PrincipalCont.recibirGet(req, resp);
			default:
				return resp.setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
			}
		} else {
			return resp.setStatus(HttpResponseStatus.BAD_REQUEST);
		}
		
	}
	
	private static ResponseContentWriter<ByteBuf> recibirGet(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp){
		if(req.getUri().startsWith("/saludar")) {
			Gson gson = new Gson();
			Usuario usr = JwtServ.getInstancia().usuarioToken(req.getQueryParameters().get("sc-token").get(0));
			return resp.writeString(just(gson.toJson(
					PostServ.saludar(usr))));
		} else {
			return resp.setStatus(HttpResponseStatus.NOT_FOUND);
		}
	}
	
	
	private static ResponseContentWriter<ByteBuf> recibirPost(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> resp){
		if (req.getUri().startsWith("/public/login")) {
			return resp.writeString(
					req.getContent().map(body -> {
								Gson gson = new Gson();
				    			Usuario usr = gson.fromJson(body.toString(CharsetUtil.UTF_8), Usuario.class);
				    			JsonObject ob = new JsonObject();
								if(!AutenticacionServ.validarUsuarioClave(usr.getUsuario(), usr.getClave())) {
									// TODO -- Http 401 usuario o clave incorrecto
									ob.addProperty("sc-token", "-1");
									ob.addProperty("mensaje", "credenciales incorrectas");
									resp.setStatus(HttpResponseStatus.UNAUTHORIZED);
									return gson.toJson(ob);			    				
								} else {
									ob.addProperty("sc-token", JwtServ.getInstancia().crearToken(usr.getUsuario(), usr.getNombre()));
									ob.addProperty("mensaje", "exito");
									return gson.toJson(ob);
								}
								
							}));
		}
		else {
			return resp.setStatus(HttpResponseStatus.NOT_FOUND);
		}
	
	}

}
