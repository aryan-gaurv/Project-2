package com.example.authify.filter;

import com.example.authify.service.AppUserDetailsService;
import com.example.authify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    final AppUserDetailsService appUserDetailsService;

    final JwtUtil jwtUtil;

    List<String> PUBLIC_URLS=List.of("/api/v1.0/login","/api/v1.0/register","/api/v1.0/send-reset-otp","/api/v1.0/reset-password","/api/v1.0/logout");
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String servletPath = request.getServletPath();
        if(PUBLIC_URLS.contains(servletPath)){
            filterChain.doFilter(request,response);
            return;
        }
        String jwt=null;
        String email=null;
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            jwt = authorization.substring(7);

        }
        if (jwt==null){
            Cookie[] cookies=request.getCookies();
            if (cookies!=null){
                for (Cookie cookie : cookies) {
                    if("jwt".equals(cookie.getName())){
                        jwt = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (jwt!=null){
            email=jwtUtil.extractEmail(jwt);
            if (email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);
                if(jwtUtil.validateToken(jwt,userDetails)){
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
        filterChain.doFilter(request,response);
    }
}
