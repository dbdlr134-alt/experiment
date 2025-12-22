package com.mnu.jpstudy.word;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WordRequestForm {

    private Long wordId;

    @NotBlank
    private String word;

    @NotBlank
    private String doc;

    @NotBlank
    private String korean;

    @NotBlank
    private String jlpt;

    @NotBlank
    private String requestedBy;
}