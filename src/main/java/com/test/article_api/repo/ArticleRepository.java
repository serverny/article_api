package com.test.article_api.repo;

import com.test.article_api.model.Article;
import com.test.article_api.model.Person;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import javax.validation.constraints.NotBlank;

@Repository
public interface ArticleRepository extends ReactiveMongoRepository<Article, String> {

    Flux<Article> findByAuthor(String author);

}
