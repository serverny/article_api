package com.test.article_api.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArticleTimeline extends BaseEntity {

    @NotNull
    private String articleId;

    private List<Article> articleVersions;

    public void addArticle(Article article) {
        if (CollectionUtils.isEmpty(articleVersions)) {
            articleVersions = new ArrayList<>();
        }
        articleVersions.add(article);
    }
}
