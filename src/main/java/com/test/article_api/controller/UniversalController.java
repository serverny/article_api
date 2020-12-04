package com.test.article_api.controller;

import com.oblac.nomen.Casing;
import com.oblac.nomen.Nomen;
import com.test.article_api.model.Article;
import com.test.article_api.repo.ArticleRepository;
import com.test.article_api.model.Comment;
import com.test.article_api.model.Person;
import com.test.article_api.service.ArticleService;
import com.test.article_api.model.ArticleState;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
// В реальном приложении наверное стоило бы разнести разных акторов по разным контроллерам.
public class UniversalController {

    @Autowired
    ArticleService articleService;

    @Autowired
    ArticleRepository articleRepository;

    /*
        В реальном приложении для разных акторов с разными экдпоинтами скорее всего должны отдаваться разные DTO.
        Я пока оставил идентичную реализацию, просто чтобы показать как я понял работу с разными акторами.
        Там где функциональной разницы между акторами нет эндпоинты с полностью идентичным функционалом дублировать не стал.
    */
    @Operation(summary = "Get article by Id for Author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found article",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Article.class))}),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content)})
    @GetMapping("/author/articles/{id}")
    public Mono<Article> findArticleForAuthor(@Parameter(description = "id of article you want to open")
                                              @PathVariable("id") String id) {
        return articleService.findArticle(id);
    }

    @Operation(summary = "Get article by Id for Editor")
    @GetMapping("/editor/articles/{id}")
    public Mono<Article> findArticleForEditor(@PathVariable("id") String id) {
        return articleService.findArticle(id);
    }

    @Operation(summary = "Get all articles written by Author for Editor")
    @GetMapping("/editor/articles/{author}")
    public Flux<Article> findAllAuthorArticlesForAuthor(@PathVariable("author") String author) {
        return articleService.findArticlesByAuthor(author);
    }

    @PostMapping("author/save")



    /*

    @GetMapping("/author/articles/{name}")
    public Flux<ResponseEntity<Article>> getArticle(@PathVariable String name) {
        return Flux.just(ResponseEntity.ok(articleRepository.findArticlesByAuthor(name)));
    }
*/

    // Populate DB for testing
    private String beautifySentence(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1) + ".";
    }

    @GetMapping("/dev/generateArticles")
    @Operation(summary = "For testing only, generate randomized articles")
    public String testing() {
        List<Article> articles = new ArrayList<>();
        List<Person> authors = new ArrayList<>(10);
        for (Integer index = 0; index < 5; index++) {
            authors.add(new Person(Nomen.est().person().withCasing(Casing.CAPITALIZE).get()));
        }
        for (Integer index = 0; index < 10; index++) {
            Article article = new Article();
            article.setAuthor(authors.get(index % 5));
            String randomText = Nomen.est().withSeparator(" ").superb().pokemon().adjective().color().animal().noun().get();
            article.setText(beautifySentence(randomText));
            article.setState(index % 3 == 0 ? ArticleState.IN_REVIEW : ArticleState.NEW);
            if (index % 5 == 0) {
                article.setState(ArticleState.REJECTED);
                Comment comment = new Comment();
                randomText = Nomen.est().withSeparator(" ").superb().color().noun().person().adjective().get();
                comment.setEditorsComment(beautifySentence(randomText));
                comment.setCreatedDate(LocalDate.now()); // выставляем дату вручную т.к. для вложенной сущности аудит не работает. Подумать как автоматизировать и стоит ли.
                article.addCommentary(comment);
            }
            articles.add(article);
        }
        articleRepository.saveAll(articles).subscribe();
        return HttpStatus.OK.getReasonPhrase();
    }
}


