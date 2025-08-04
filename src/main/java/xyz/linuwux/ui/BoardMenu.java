package xyz.linuwux.ui;

import lombok.AllArgsConstructor;
import xyz.linuwux.dto.BoardColumnInfoDTO;
import xyz.linuwux.persistence.entity.BoardColumnEntity;
import xyz.linuwux.persistence.entity.BoardEntity;
import xyz.linuwux.persistence.entity.CardEntity;
import xyz.linuwux.service.BoardColumnQueryService;
import xyz.linuwux.service.BoardQueryService;
import xyz.linuwux.service.CardQueryService;
import xyz.linuwux.service.CardService;

import java.sql.SQLException;
import java.util.Scanner;

import static xyz.linuwux.persistence.config.ConnectionConfig.getConnection;
import static xyz.linuwux.persistence.entity.BoardColumnKindEnum.INITIAL;

@AllArgsConstructor
public class BoardMenu {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.printf("Bem vindo ao board %s, selecione a operacao desejada:", entity.getId());
            System.out.println("Boas vindas ao gerenciador de boards! Escolha a opcao desejada:");
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - Mover card");
                System.out.println("3 - Bloquear Card");
                System.out.println("4 - Desbloquear Card");
                System.out.println("5 - Cancelar Card");
                System.out.println("6 - Visualizar board");
                System.out.println("7 - Visualizar coluna com cards");
                System.out.println("8 - Visualizar card");
                System.out.println("9 - Voltar para menu anterior");
                System.out.println("10 - Sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Voltando para o menu anterior");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Digite uma opcao valida!");
                }
            }
        }catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void createCard() throws SQLException{
        var card = new CardEntity();
        System.out.println("Informe o titulo do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descricao do card");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try (var connection = getConnection()) {
            new CardService(connection).insert(card);
        }

    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Informe id do card que sera movido para proxima coluna:");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()) {
            new CardService(connection).moveToNextColumn(cardId, boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Informe id do card que sera bloqueado:");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do bloqueio do card");
        var reason = scanner.next();

        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();

        try(var connection = getConnection()) {
            new CardService(connection).block(cardId, reason, boardColumnsInfo);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Informe id do card que sera desbloqueado:");
        var cardId = scanner.nextLong();
        System.out.println("Informe o motivo do desbloqueio do card");
        var reason = scanner.next();
        try(var connection = getConnection()) {
            new CardService(connection).unblock(cardId, reason);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }

    private void cancelCard() throws SQLException {
        System.out.println("Informe id do card que sera cancelado:");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();

        try(var connection = getConnection()) {
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }


    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board: [%s,%s]\n", b.id(), b.name());
                b.columns().forEach( c -> {
                    System.out.printf("Coluna [%s] tipo [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount());
                });
            });
        }
    }

    private void showColumn() throws SQLException {
        var columnIds = entity.getBoardColumns().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumn = -1L;
        while(!columnIds.contains(selectedColumn)) {
            System.out.printf("Escolha uma coluna do board %s\n", entity.getName());
            entity.getBoardColumns().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumn = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s, tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s.\nDescricao: %s\n",ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Informe o id do card que deseha visualizar:");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()) {
            new CardQueryService(connection).findById(selectedCardId)
                    .ifPresentOrElse(c -> {
                        System.out.printf("Card %s - %s.\n", c.id(), c.title());
                        System.out.printf("Descricao: %s.\n", c.description());
                        System.out.println(c.blocked() ? "Esta bloqueado. Motivo: " + c.blockReason() : "Nao esta bloqueado");
                        System.out.printf("Ja foi bloqueado %s vezes\n", c.blocksAmount());
                        System.out.printf("Esta na coluna %s - %s no momento\n", c.id(), c.columnName());
                    }, () -> System.out.printf("Nao existe card com id %s\n", selectedCardId));
        }
    }
}
