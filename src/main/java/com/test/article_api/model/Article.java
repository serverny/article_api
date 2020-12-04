package com.test.article_api.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Article  extends BaseEntity {

    private Person author;

    private String text;

    private ArticleState state;

    private List<Comment> commentaries;

    public void addCommentary(Comment comment) {
        if (CollectionUtils.isEmpty(commentaries)) {
            commentaries = new ArrayList();
        }
        commentaries.add(comment);
    }

}
