package com.test.article_api.repo;

import com.test.article_api.model.ArticleTimeline;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

public interface ArticleTimelineRepository extends ReactiveMongoRepository<ArticleTimeline, String> {
}
