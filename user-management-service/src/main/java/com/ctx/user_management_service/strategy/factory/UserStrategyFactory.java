package com.ctx.user_management_service.strategy.factory;

import com.ctx.user_management_service.strategy.UserStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserStrategyFactory {
    private  final List<UserStrategy> userStrategyList;
    public UserStrategy getRegisterStrategy(String role){
        return userStrategyList.stream().filter(
                registerStrategy -> registerStrategy.supports(role)
        ).toList().get(0);
    }
}
