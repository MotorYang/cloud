package io.github.motoryang.blog.settings.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteCheckResult {
    private Boolean status;
    private Integer articleCount;
    private String message;
}
