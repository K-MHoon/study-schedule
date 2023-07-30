package com.example.service.enums;

public enum RegisterState {

    NO_READ, // 아직 방장이 읽지 않음
    CANCEL, // 사용자가 취소함
    READ, // 읽음
    REJECT, // 거절
    PASS; // 가입 성공


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
