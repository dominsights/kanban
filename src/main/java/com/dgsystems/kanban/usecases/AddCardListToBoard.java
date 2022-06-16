package com.dgsystems.kanban.usecases;

import com.dgsystems.kanban.boundary.BoardSession;
import com.dgsystems.kanban.entities.Board;
import com.dgsystems.kanban.entities.BoardMember;
import com.dgsystems.kanban.entities.CardList;
import com.jcabi.aspects.Loggable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

public class AddCardListToBoard {
    private final BoardRepository boardRepository;

    public AddCardListToBoard(BoardRepository repository) {
        boardRepository = repository;
    }

    @Loggable(prepend = true)
    public UUID execute(String boardName, String cardListTitle, Optional<BoardMember> boardMember) {
        Optional<Board> optional = boardRepository.getBoard(boardName);
        UUID id = UUID.randomUUID();
        optional.map(b -> {
            BoardSession boardSession = new BoardSession();
            Board updated = boardSession.addCardList(b, new CardList(id, cardListTitle, Collections.emptyList()));
            boardRepository.save(updated);
            return updated;
        }).orElseThrow();

        return id;
    }
}
