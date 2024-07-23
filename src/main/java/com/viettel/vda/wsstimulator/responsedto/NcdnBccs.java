package com.viettel.vda.wsstimulator.responsedto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NcdnBccs {
    private String errorCode;

    public NcdnBccs(String errorCode) {
        this.errorCode = errorCode;
    }
}
