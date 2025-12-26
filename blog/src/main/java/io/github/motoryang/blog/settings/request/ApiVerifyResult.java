package io.github.motoryang.blog.settings.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiVerifyResult {

    private Boolean status;
    private String message;

}
