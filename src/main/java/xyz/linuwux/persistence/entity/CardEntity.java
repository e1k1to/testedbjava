package xyz.linuwux.persistence.entity;

import lombok.Data;

import static xyz.linuwux.persistence.entity.BoardColumnKindEnum.INITIAL;

@Data
public class CardEntity {
    private Long id;
    private String title;
    private String description;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
}
