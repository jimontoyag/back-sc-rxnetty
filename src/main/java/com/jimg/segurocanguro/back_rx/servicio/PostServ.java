package com.jimg.segurocanguro.back_rx.servicio;

import com.jimg.segurocanguro.back_rx.modelo.Respuesta;
import com.jimg.segurocanguro.back_rx.modelo.Usuario;

public class PostServ {
	
	public static Respuesta saludar(Usuario peticion) {
		Respuesta respuesta = new Respuesta();
		respuesta.setMensaje("Hola "+peticion.getNombre());
		return respuesta;
	}

}
