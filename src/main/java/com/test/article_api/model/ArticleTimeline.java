package com.test.article_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Document
public class ArticleTimeline extends BaseEntity {

    @NotBlank
    private String articleId;

    @NotNull
    private Person author;

    private List<Article> articleSnapshots;

    public void addArticle(Article article) {
        if (CollectionUtils.isEmpty(articleSnapshots)) {
            articleSnapshots = new ArrayList<>();
        }
        articleSnapshots.add(article);
    }
}
