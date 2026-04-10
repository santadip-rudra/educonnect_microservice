package com.ctx.user_management_service.strategy.factory;

import com.ctx.user_management_service.strategy.RegisterAndUpdateStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegisterAndUpdateStrategyFactory {
    private  final List<RegisterAndUpdateStrategy> registerAndUpdateStrategyList;
    public RegisterAndUpdateStrategy getRegisterStrategy(String role){
        return registerAndUpdateStrategyList.stream().filter(
                registerStrategy -> registerStrategy.supports(role)
        ).toList().get(0);
    }
}
