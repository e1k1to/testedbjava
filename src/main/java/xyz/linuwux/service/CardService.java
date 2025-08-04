package xyz.linuwux.service;

import lombok.AllArgsConstructor;
import xyz.linuwux.dto.BoardColumnInfoDTO;
import xyz.linuwux.exception.CardBlockedException;
import xyz.linuwux.exception.CardFinishedException;
import xyz.linuwux.exception.EntityNotFoundException;
import xyz.linuwux.persistence.dao.BlockDAO;
import xyz.linuwux.persistence.dao.CardDAO;
import xyz.linuwux.persistence.entity.BoardColumnKindEnum;
import xyz.linuwux.persistence.entity.CardEntity;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static xyz.linuwux.persistence.entity.BoardColumnKindEnum.CANCEL;
import static xyz.linuwux.persistence.entity.BoardColumnKindEnum.FINAL;

@AllArgsConstructor
public class CardService {
    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException{
        try {
            var dao = new CardDAO(connection);
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(() -> new EntityNotFoundException("Card de id %s nao foi encontrado.".formatted(cardId)));
            if (dto.blocked()) {
                throw new CardBlockedException("Card de id %s ja esta bloqueado. Desbloqueie-o para mover.".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst().
                    orElseThrow(() -> new IllegalStateException("Card informado pertence a outro board."));
            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("Card ja foi finalizado");
            }

            var nextColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card esta cancelado"));
            dao.moveToColumn(nextColumn.id(), cardId);
            connection.commit();

        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);

            var dto = optional.orElseThrow(() -> new EntityNotFoundException("Card de id %s nao foi encontrado.".formatted(cardId)));

            if (dto.blocked()) {
                throw new CardBlockedException("Card de id %s esta bloqueado. Desbloqueie-o para cancelar.".formatted(cardId));
            }
            var currentColumn = boardColumnsInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst().
                    orElseThrow(() -> new IllegalStateException("Card informado pertence a outro board."));

            if (currentColumn.kind().equals(FINAL)) {
                throw new CardFinishedException("Card ja foi finalizado");
            }
            boardColumnsInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst().orElseThrow(() -> new IllegalStateException("Card esta cancelado"));

            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();


        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);

            var dto = optional.orElseThrow(() -> new EntityNotFoundException("Card de id %s nao foi encontrado.".formatted(id)));

            if (dto.blocked()) {
                throw new CardBlockedException("Card de id %s ja esta bloqueado.".formatted(id));
            }

            var currentColumn = boardColumnsInfo.stream().filter(bc -> bc.id().equals(dto.columnId())).findFirst().orElseThrow();

            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
                throw new IllegalStateException("Card esta em uma coluna do tipo %s e nao pode ser bloqueado.".formatted(currentColumn.kind()));
            }

            var blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
    public void unblock(final Long id, final String reason) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            var optional = dao.findById(id);

            var dto = optional.orElseThrow(() -> new EntityNotFoundException("Card de id %s nao foi encontrado.".formatted(id)));

            if (!dto.blocked()) {
                throw new CardBlockedException("Card de id %s nao esta bloqueado.".formatted(id));
            }

            var blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}
