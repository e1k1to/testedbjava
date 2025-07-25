package xyz.linuwux.service;

import lombok.AllArgsConstructor;
import xyz.linuwux.persistence.dao.BoardColumnDAO;
import xyz.linuwux.persistence.dao.BoardDAO;
import xyz.linuwux.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class BoardQueryService {
    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        var boardColumnDAO = new BoardColumnDAO(connection);
        var optional = dao.findById(id);
        if(optional.isPresent()) {
            var entity = optional.get();

            entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));

            return Optional.of(entity);
        }
        return Optional.empty();
    }
}
