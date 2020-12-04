package com.test.article_api.repo;

import com.test.article_api.model.Article;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleRepository extends ReactiveMongoRepository<Article, String> {

    Flux<Article> findByAuthor(String author);

}
