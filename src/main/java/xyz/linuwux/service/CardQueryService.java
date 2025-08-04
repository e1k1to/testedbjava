package xyz.linuwux.service;

import lombok.AllArgsConstructor;
import xyz.linuwux.dto.CardDetailsDTO;
import xyz.linuwux.persistence.dao.CardDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {
    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var dao = new CardDAO(connection);
        return dao.findById(id);
    }
}
