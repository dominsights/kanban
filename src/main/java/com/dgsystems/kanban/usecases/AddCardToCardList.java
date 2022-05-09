package com.dgsystems.kanban.usecases;

import com.dgsystems.kanban.boundary.BoardManager;
import com.dgsystems.kanban.entities.Board;
import com.dgsystems.kanban.entities.Card;
import com.jcabi.aspects.Loggable;

import java.util.Optional;

public record AddCardToCardList(BoardRepository boardRepository) {
    @Loggable(prepend = true)
    public void execute(String boardName, String cardListTitle, Card card) {
        Optional<Board> optional = boardRepository.getBoard(boardName);
        BoardManager boardManager = new BoardManager();
        Board board = optional
                .map(b -> boardManager.addCardToCardList(b, cardListTitle, card))
                .orElseThrow();

        boardRepository.save(board);
    }
}
