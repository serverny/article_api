package com.test.article_api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment extends BaseEntity {

    private String editorsComment;

    // can use collection of Comments here, depends on business requirements
    private String authorsReply;
}
