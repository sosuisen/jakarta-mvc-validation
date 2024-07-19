package com.example.model;

import jakarta.mvc.binding.MvcBinding;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.FormParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message{
        private int id;
        private String name;

        @MvcBinding
        @NotBlank(message = "{title.NotBlank}")
        @Size(max = 70, message = "{title.Size}")
        @FormParam("title")
        private String title;
        
        @MvcBinding
        @NotBlank(message = "{message.NotBlank}")
        @Size(max = 400, message = "{message.Size}")
        @FormParam("message")
        private String message;
}