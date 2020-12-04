package com.test.article_api.controller;

import com.oblac.nomen.Casing;
import com.oblac.nomen.Nomen;
import com.test.article_api.model.*;
import com.test.article_api.repo.ArticleRepository;
import com.test.article_api.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api")
// В реальном приложении наверное стоило бы разнести разных акторов по разным контроллерам.
public class UniversalController {

//    @Autowired
    ArticleService articleService;

//    @Autowired
    ArticleRepository articleRepository;

    /*
        В реальном приложении для разных акторов с разными экдпоинтами скорее всего должны отдаваться разные DTO.
        Я пока оставил идентичную реализацию, просто чтобы показать как я понял работу с разными акторами.
        TODO: Роли акторов по идее надо брать из креденшиалов (для этого прикрутить webflux security?)
    */
    @PostMapping("/author/createArticle")
    @Operation(summary = "Create new article")
    public Mono<Article> createArticle(
            @Parameter(description = "New article. Cannot be null or empty.", required = true, schema = @Schema(implementation = Article.class))
            @Valid @RequestBody Article article) {
        return articleService.createArticle(article); // TODO: разобраться почему не работает валидация на аннотациях. Надо писать свой валидатор?
    }

    @Operation(summary = "Get article by Id for Author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found article",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Article.class))}),
            @ApiResponse(responseCode = "404", description = "Article not found",
                    content = @Content)})
    @GetMapping("/author/articles/{id}")
    public Mono<Article> findArticleForAuthor(@Parameter(description = "id of article you want to open", required = true)
                                              @PathVariable("id") String id) {
        return articleService.findArticle(id);
    }

    @Operation(summary = "Get all articles written by Author for Editor")
    @GetMapping("/author/articles")
    public Flux<Article> findAllArticlesForAuthor(
            @Parameter(description = "Current author", required = true)
            @RequestBody  String author) {
        return articleService.findArticlesByAuthor(author);
    }

    @PostMapping("/author/updateArticle")
    @Operation(summary = "Update existing article")
    public Mono<Article> updateArticle(
            @Parameter(description = "Update article. ID cannot be null or empty.", required = true, schema = @Schema(implementation = Article.class))
            @Valid @RequestBody Article article) {
        return articleService.updateArticle(article);
    }

    @PostMapping("/author/sendToEditor")
    @Operation(summary = "Send article to editor")
    public Mono<Article> sendToEditor(
            @Parameter(description = "Send article to editor. ID cannot be null or empty.", required = true)
            @Valid @RequestBody String id) {
        return articleService.sendArticleToEditor(id);
    }

/*
    @Operation(summary = "Get all articles written by Author for Editor")
    @GetMapping("/author/rejectedArticles")
    public Flux<Article> findRejectedArticlesForAuthor(
            @Parameter(description = "Name of current author", required = true)
            @RequestBody  String author) {
        return articleService.findRejectedArticlesForAuthor(author);
    }

    @Operation(summary = "Get timelines for all articles written by Author")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found timeline(s)",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ArticleTimeline.class))}),
            @ApiResponse(responseCode = "404", description = "No timelines exist",
                    content = @Content)})
    @GetMapping("/author/timelines")
    public Flux<ArticleTimeline> findTimelinesForAuthor(
            @Parameter(description = "Name of current author", required = true)
            @RequestBody  String author) {
        return articleService.findTimelinesForAuthor(author);
    }
*/


    @Operation(summary = "Get article by Id for Editor")
    @GetMapping("/editor/articles/{id}")
    public Mono<Article> findArticleForEditor(@PathVariable("id") String id) {
        return articleService.findArticle(id);
    }

/*
    @Operation(summary = "Get all articles written by Author for Editor")
    @GetMapping("/editor/article/{author}")
    public Flux<Article> findAllAuthorArticlesForAuthor(@PathVariable("author") String author) {
        return articleService.findArticlesByAuthor(author);
    }
*/




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
                comment.setCreatedDate(new Date()); // выставляем дату вручную т.к. для вложенной сущности аудит не работает. Подумать как автоматизировать и стоит ли.
                article.addCommentary(comment);
            }
            articles.add(article);
        }
        articleRepository.saveAll(articles).subscribe();
        return HttpStatus.OK.getReasonPhrase();
    }
}


