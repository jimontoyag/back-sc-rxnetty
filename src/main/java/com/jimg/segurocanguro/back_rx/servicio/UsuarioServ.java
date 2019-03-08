package com.jimg.segurocanguro.back_rx.servicio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsuarioServ {
	
	private static UsuarioServ instancia;
	
	private Map<String,String> usuarios;
	
	private UsuarioServ() {
		this.usuarios = new HashMap<String, String>(1);
		this.usuarios.put("segurocanguro", "159753");
	}
	
	private static UsuarioServ getInstancia() {
		if(instancia == null) {
			instancia = new UsuarioServ();
		}
		return instancia;
	}
	
	public static List<String> obtenerUsuarios(String usuario) {
		return getInstancia().usuarios.keySet().stream()
				.filter(usr -> usr.toLowerCase().contains(usuario))
				.collect(Collectors.toList());
	}
	
	public static void crearUsuario(String usuario, String clave){
		getInstancia().usuarios.put(usuario, clave);
	}
	
	public static boolean validaClave(String usuario, String clave) {
		return getInstancia().usuarios.get(usuario).equals(clave);
	}
}
