package com.jimg.segurocanguro.back_rx.servicio;

public class AutenticacionServ {
	
	public static boolean validarUsuarioClave(String usuario, String clave) {
		if(usuario.equals("segurocanguro") && clave.equals("123456")) {
			return true;
		} else {
			return false;
		}
	}

}
