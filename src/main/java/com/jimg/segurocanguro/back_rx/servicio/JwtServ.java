package com.jimg.segurocanguro.back_rx.servicio;

import java.security.Key;

import com.jimg.segurocanguro.back_rx.modelo.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

public class JwtServ {
	
	private static JwtServ instancia;
	
	private Key key;
	
	private JwtServ() {
		this.key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}
	
	public String crearToken(String usuario, String nombre) {
		return Jwts.builder()
			.setSubject(usuario)
			.claim("nombre", nombre)
				.signWith(this.key)
					.compact(); 
	}
	
	public boolean validarToken(String jwt) {
		try {
			Jwts.parser()
		    .setSigningKey(this.key) 
		    .parseClaimsJws(jwt);
		    return true;
		}   
		catch (JwtException ex) { 
		    return false;
		}
	}
	
	public Usuario usuarioToken(String jwt) {
		try {
			Jws<Claims> jws = Jwts.parser()
								.setSigningKey(this.key)
								.parseClaimsJws(jwt);
			Usuario usuario = new Usuario();
			usuario.setNombre((String) jws.getBody().get("nombre"));
			usuario.setUsuario(jws.getBody().getSubject());
			return usuario;
		    
		}   
		catch (JwtException ex) { 
		    return null;
		}
	}

	public static JwtServ getInstancia() {
		if(JwtServ.instancia == null) {
			JwtServ.instancia = new JwtServ();
		}
		return instancia;
	}

}
