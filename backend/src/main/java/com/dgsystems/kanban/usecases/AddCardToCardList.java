package com.dgsystems.kanban.usecases;

import com.dgsystems.kanban.boundary.BoardSession;
import com.dgsystems.kanban.entities.Board;
import com.dgsystems.kanban.entities.BoardMember;
import com.dgsystems.kanban.entities.Card;
import com.dgsystems.kanban.entities.MemberNotInTeamException;
import com.jcabi.aspects.Loggable;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;

import java.util.Optional;

public record AddCardToCardList(BoardRepository boardRepository) {
    @Loggable(prepend = true)
    public void execute(String boardName, String cardListTitle, Card card, Optional<BoardMember> boardMember) throws MemberNotInTeamException {
        if (boardMember.isEmpty()) throw new MemberNotInTeamException("");
        Optional<Board> optional = boardRepository.getBoard(boardName);
        BoardSession boardSession = new BoardSession();
        Board board = null;

        if (optional.isPresent()) {
            Either<MemberNotInTeamException, Board> either = boardSession.addCardToCardList(optional.get(), cardListTitle, card, boardMember.get());

            if (either instanceof Right r) {
                board = (Board) r.value();
            } else if (either instanceof Left l) {
                throw (MemberNotInTeamException) l.value();
            } else {
                throw new IllegalStateException();
            }
        }

        boardRepository.save(board);
    }
}