package com.dgsystems.kanban.usecases;

import com.dgsystems.kanban.boundary.Context;
import com.dgsystems.kanban.entities.Board;
import com.dgsystems.kanban.entities.Member;
import com.dgsystems.kanban.entities.OwnerDoesNotExistException;
import com.dgsystems.kanban.infrastructure.persistence.in_memory.InMemoryMemberRepository;
import com.dgsystems.kanban.infrastructure.persistence.in_memory.InMemoryBoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class CreateBoardTest {
    public BoardRepository boardRepository;
    private MemberRepository MemberRepository;

    @BeforeEach
    void setup() {
        boardRepository = new InMemoryBoardRepository();
        MemberRepository = new InMemoryMemberRepository();
        MemberRepository.save(new Member("owner"));
        Context.initialize(boardRepository);
    }

    @Test
    @DisplayName("Should create empty board")
    void shouldCreateEmptyBoard() throws OwnerDoesNotExistException {
        CreateBoard createBoard = new CreateBoard(boardRepository, MemberRepository);
        Member owner = new Member("owner");
        Board expected = new Board(MoveCardBetweenListsTest.BOARD_NAME, Collections.emptyList(), Collections.singletonList(owner), owner);
        createBoard.execute(MoveCardBetweenListsTest.BOARD_NAME, Optional.of(owner));
        Optional<Board> board = boardRepository.getBoard(MoveCardBetweenListsTest.BOARD_NAME);

        assertThat(board).isEqualTo(Optional.of(expected));
    }

    @Test
    @DisplayName("Should not create board when owner does not exist")
    void shouldNotCreateBoardWhenOwnerDoesNotExist() {
        CreateBoard createBoard = new CreateBoard(boardRepository, new InMemoryMemberRepository());
        Member owner = new Member("owner");

        assertThatExceptionOfType(OwnerDoesNotExistException.class)
                .isThrownBy(() -> {
                    createBoard.execute(MoveCardBetweenListsTest.BOARD_NAME, Optional.of(owner));
                });
        assertThat(boardRepository.getBoard(MoveCardBetweenListsTest.BOARD_NAME)).isEqualTo(Optional.empty());
    }
}