package com.example.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RegisterState {

    NO_READ("방장이 읽지 않음"),
    CANCEL("사용자가 취소함"),
    READ("방장이 읽음"),
    REJECT("방장이 거절함"),
    PASS("가입 성공");

    private final String desc;

    public static RegisterState convertStringToRegisterState(String value) {
        assert value != null;
        for (RegisterState registerState : RegisterState.values()) {
            if(registerState.name().toLowerCase().equals(value)) {
                return registerState;
            }
        }
        return NO_READ;
    }
}
