package com.heima.wemedia.filters;

import com.heima.utils.common.AppJwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-1)
@Component
public class AuthTokenFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if(path.contains("/login/in")){
            return chain.filter(exchange);//放行请求
        }
        // ========================== 要鉴权
        ServerHttpResponse response = exchange.getResponse();
        String token = request.getHeaders().getFirst("token");
        if(StringUtils.isEmpty(token)){
            // 没有传token则终止
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//终止请求方法
        }
        Claims claimsBody = null;
        try {
            claimsBody  = AppJwtUtil.getClaimsBody(token);//获取token字符串中的载荷内容
        }catch (Exception e){
            e.printStackTrace();
        }
        if(claimsBody==null){//过期则直接返回
            // 没有传token则终止
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();//终止请求方法
        }

        //往请求头添加用户ID
        int result = AppJwtUtil.verifyToken(claimsBody);
        if(result==0||result==-1){
            // 转发用户信息
            String userId =claimsBody.get("id") + "";
            ServerHttpRequest serverHttpRequest = request.mutate()
                    .header("userId",userId)
                    .build();
            exchange = exchange.mutate().request(serverHttpRequest).build();
            return chain.filter(exchange);//放行请求
        }

        // 没有传token则终止
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();//终止请求方法
    }
}
