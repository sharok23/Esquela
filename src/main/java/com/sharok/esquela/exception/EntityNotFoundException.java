package com.sharok.esquela.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {
    public final String entity;
    public final Long id;

    public EntityNotFoundException(String entity, Long id) {
        super("Entity " + entity + " not found with Id: " + id);
        this.id = id;
        this.entity = entity;
    }
}
