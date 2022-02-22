package com.adobe.prj.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.adobe.prj.entity.MyUserDetails;
import com.adobe.prj.service.MyUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private MyUserDetailsService userDetailsService;
    private JWTTokenHelper jwtTokenHelper;

    public JWTAuthenticationFilter(MyUserDetailsService userDetailsService,JWTTokenHelper jwtTokenHelper) {
        this.userDetailsService=userDetailsService;
        this.jwtTokenHelper=jwtTokenHelper;

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


        String authToken=jwtTokenHelper.getToken(request);
        if(authToken!=null) {

                String userName = jwtTokenHelper.getUsernameFromToken(authToken);

                if (null != userName) {

                    MyUserDetails userDetails = (MyUserDetails) userDetailsService.loadUserByUsername(userName);

                    if (jwtTokenHelper.validateToken(authToken, userDetails)) {

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetails(request));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    }
                } else {
                    try {
                        SecurityContextHolder.clearContext();
                        response.setContentType("application/json");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().println("expired or invalid JWT token ");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    return;
                }
        } else if (!request.getRequestURI().equals("/login")){
            try {
                SecurityContextHolder.clearContext();
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().println("expired or invalid JWT token ");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            return;
        }

        filterChain.doFilter(request, response);

    }

}