package com.test.article_api.repo;

import com.test.article_api.model.ArticleTimeline;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ArticleTimelineRepository extends ReactiveMongoRepository<ArticleTimeline, String> {
}
