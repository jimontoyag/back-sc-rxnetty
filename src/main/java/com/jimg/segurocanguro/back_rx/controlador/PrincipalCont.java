package com.jimg.segurocanguro.back_rx.controlador;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jimg.segurocanguro.back_rx.modelo.Usuario;
import com.jimg.segurocanguro.back_rx.servicio.JwtServ;
import com.jimg.segurocanguro.back_rx.servicio.UsuarioServ;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

import static rx.Observable.*;

import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import rx.Observable;

public class PrincipalCont {
	
	public static Observable<Void> controlarPeticionesHttp(HttpServerRequest<ByteBuf> req, HttpServerResponse<ByteBuf> rel){
		String path = req.getDecodedPath();
		path = path.replaceAll("/$", "");
		if(!path.startsWith("/public")) {
			if(req.getQueryParameters().get("sc_token") == null) {
				return rel.setStatus(HttpResponseStatus.UNAUTHORIZED);
			} 
			if(!JwtServ.getInstancia().validarToken(req.getQueryParameters().get("sc_token").get(0))) {
				return rel.setStatus(HttpResponseStatus.UNAUTHORIZED);
			}
		}
		HttpMethod method = req.getHttpMethod();
    	if(path.equals("/public/login")) {
    		if(method.compareTo(HttpMethod.POST) == 0) {
    			return req.getContent().map(content -> {
    				Gson gson = new Gson();
    				Usuario usr = new Usuario();
    				usr = gson.fromJson(content.toString(CharsetUtil.UTF_8), Usuario.class);
    				JsonObject ob = new JsonObject();
    				if(!UsuarioServ.validaClave(usr.getUsuario(), usr.getClave())) {
    					ob.addProperty("sc_token", "-1");
    					ob.addProperty("mensaje", "Error nombre de usuario o contraseña");
    					return rel.setStatus(HttpResponseStatus.UNAUTHORIZED).writeString(just(gson.toJson(ob)));			    				
    				} else {
    					ob.addProperty("sc_token", JwtServ.getInstancia().crearToken(usr.getUsuario()));
    					ob.addProperty("mensaje", "exito");
    					return rel.writeString(just(gson.toJson(ob)));
    				}
    			}).flatMap(v -> v.first());
    		}else return rel.setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
    		
    	} else if (path.equals("/usuario")) {
    		if(method.compareTo(HttpMethod.GET) == 0) {
    			Usuario usr = JwtServ.getInstancia().usuarioToken(req.getQueryParameters().get("sc_token").get(0));
    			return rel.writeString(just(mensajeSimple("Hola "+usr.getUsuario())));
    		} else if(method.compareTo(HttpMethod.POST) == 0) {
    			return req.getContent().map(content -> {
    				Gson gson = new Gson();
    				Usuario usr = new Usuario();
    				usr = gson.fromJson(content.toString(CharsetUtil.UTF_8), Usuario.class);
    				UsuarioServ.crearUsuario(usr.getUsuario(), usr.getClave());
    				return rel.writeString(just(mensajeSimple("creado con exito "+usr.getUsuario())));
    			}).flatMap(v -> v.first());
    		
    		}    		
    		else return rel.setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
    		
    	} else if (path.equals("/usuarios")) {
    		if(method.compareTo(HttpMethod.GET) == 0) {
    			String busqueda = req.getQueryParameters().get("busqueda") == null || req.getQueryParameters().get("busqueda").size() == 0? 
    					"" : req.getQueryParameters().get("busqueda").get(0);
    			JsonArray arr = new JsonArray();
    			UsuarioServ.obtenerUsuarios(busqueda).forEach(usr -> arr.add(usr));
    			JsonObject ob = new JsonObject();
    			ob.add("usuarios", arr);
    			ob.addProperty("busqueda", busqueda);
    			return rel.writeString(just(ob.toString()));
    		} else return rel.setStatus(HttpResponseStatus.METHOD_NOT_ALLOWED);
    	}
    	
    	else return rel.setStatus(HttpResponseStatus.NOT_FOUND);
    }
	
	private static String mensajeSimple(String mensaje) {
		JsonObject ob = new JsonObject();
		ob.addProperty("mensaje", mensaje);
		return ob.toString();
	}

}
