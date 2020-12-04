package com.test.article_api.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class Article  extends BaseEntity {

    @NotNull(message = "Author is mandatory")
    private Person author;

    @NotBlank(message = "Text is mandatory")
    private String text;

    private ArticleState state;

    private List<Comment> commentaries;

    public void addCommentary(Comment comment) {
        if (CollectionUtils.isEmpty(commentaries)) {
            commentaries = new ArrayList<>();
        }
        commentaries.add(comment);
    }

}
