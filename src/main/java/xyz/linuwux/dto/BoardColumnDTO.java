package xyz.linuwux.dto;

import xyz.linuwux.persistence.entity.BoardColumnKindEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnKindEnum kind, int cardsAmount) {

}
