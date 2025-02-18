package com.ermnvldmr.w.form;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class WritePostForm {
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @NotBlank
    @Size(min = 1, max = 10000)
    @Lob
    private String text;

    private List<MultipartFile> media;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public List<MultipartFile> getMedia() {
        return media;
    }

    public void setImage(List<MultipartFile> media) {
        this.media = media;
    }
}
