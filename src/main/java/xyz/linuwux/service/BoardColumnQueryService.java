package xyz.linuwux.service;

import lombok.AllArgsConstructor;
import xyz.linuwux.persistence.dao.BoardColumnDAO;
import xyz.linuwux.persistence.entity.BoardColumnEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardColumnQueryService {
    private final Connection connection;

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        var dao = new BoardColumnDAO(connection);
        return dao.findById(id);
    }
}
