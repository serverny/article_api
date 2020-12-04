package com.test.article_api.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    // For simplicity let's assume that "name" identifies unique user
    @NotBlank(message = "Name is mandatory")
    private String name;

}
