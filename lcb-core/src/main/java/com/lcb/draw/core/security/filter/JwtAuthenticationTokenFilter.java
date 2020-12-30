package com.lcb.draw.core.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.lcb.common.core.domain.model.LoginUser;
import com.lcb.common.utils.SecurityUtils;
import com.lcb.common.utils.StringUtils;
import com.lcb.draw.core.web.service.TokenService;

/**
 * token过滤器 验证token有效性
 *
 * @author lcb
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
  @Autowired
  private TokenService tokenService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    LoginUser loginUser = tokenService.getLoginUser(request);
    if (StringUtils.isNotNull(loginUser) && StringUtils.isNull(SecurityUtils.getAuthentication())) {
      tokenService.verifyToken(loginUser);
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null,
          loginUser.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }
    chain.doFilter(request, response);
  }
}
