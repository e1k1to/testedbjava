package xyz.linuwux.ui;

import lombok.AllArgsConstructor;
import xyz.linuwux.persistence.entity.BoardEntity;

import java.util.Scanner;

@AllArgsConstructor
public class BoardMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final BoardEntity entity;

    public void execute() {
        System.out.printf("Bem vindo ao board %s, selecione a operacao desejada:", entity.getId());
        System.out.println("Boas vindas ao gerenciador de boards! Escolha a opcao desejada:");
        var option = -1;
        while(option != 9) {
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
    }

    private void createCard() {
    }

    private void moveCardToNextColumn() {
    }

    private void blockCard() {
    }

    private void unblockCard() {
    }

    private void cancelCard() {
    }

    private void showBoard() {
    }

    private void showColumn() {
    }

    private void showCard() {
    }
}
