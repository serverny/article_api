package com.test.article_api.service;

import com.test.article_api.model.Article;
import com.test.article_api.model.ArticleState;
import com.test.article_api.repo.ArticleRepository;
import com.test.article_api.repo.ArticleTimelineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ArticleService {

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ArticleTimelineRepository articleTimelineRepository;

    public Mono<Article> findArticle(String id) {
        return articleRepository.findById(id).switchIfEmpty(Mono.error(new Exception("No Article found with Id: " + id)));
    }

    public Flux<Article> findArticlesByAuthor(String author) {
        return articleRepository.findByAuthor(author).switchIfEmpty(Mono.error(new Exception("No Articles found for Author: " + author)));
    }

    public Mono<Article> createArticle(Article article) {
        findArticle(article.getId())
                .flatMap(a -> Mono.error(new Exception("Illegal usage - article with this id already exists")));
        return articleRepository.save(article);
    }

    public Mono<Article> updateArticle(Article article) {
        return findArticle(article.getId())
                .map(a -> article)
                .flatMap(articleRepository::save)
                .switchIfEmpty(Mono.error(new Exception("Article you are trying to update not found.")));
    }

    public Mono<Article> sendArticleToEditor(String articleId) {
        return findArticle(articleId)
                .filter(article -> ArticleState.NEW.equals(article.getState())
                        || ArticleState.REJECTED.equals(article.getState()))
                .map(article -> {
                    article.setState(ArticleState.IN_REVIEW);
                    return article;
                })
                .flatMap(articleRepository::save)
                .switchIfEmpty(Mono.error(new Exception("Article you are trying to send to Editor not found.")));
    }

    public Flux<Article> findRejectedArticlesForAuthor(String author) {
        return findArticlesByAuthor(author)
                .filter(article -> ArticleState.REJECTED.equals(article.getState()));
    }

}
