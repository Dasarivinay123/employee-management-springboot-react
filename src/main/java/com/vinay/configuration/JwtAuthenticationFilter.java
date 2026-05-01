package com.vinay.configuration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.vinay.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtService jwtService;
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		final String authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
		    filterChain.doFilter(request, response);
		    return;
		}
		final String jwt = authHeader.substring(7);
		final String username = jwtService.extractUserName(jwt);
		
		Authentication authentication = SecurityContextHolder
							.getContext().getAuthentication();
		
		if (username != null && authentication == null) {
		    // do authentication
			UserDetails userDetails = userDetailsService
					.loadUserByUsername(username);
			//Check token is valid or not based on user details
			if (jwtService.isTokenValid(jwt, userDetails)) {
			    // authenticated
				UsernamePasswordAuthenticationToken authenticationToken =
					    new UsernamePasswordAuthenticationToken(
					        userDetails,
					        null,
					        userDetails.getAuthorities()
					    );
				//set the session Id
				authenticationToken.setDetails(
					    new WebAuthenticationDetailsSource()
					    .buildDetails(request)
					);

				SecurityContextHolder.getContext()
			    .setAuthentication(authenticationToken);
			}
	    

		}
		filterChain.doFilter(request, response);
	}

	
	
}
