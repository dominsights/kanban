package com.dgsystems.kanban.usecases;

import com.dgsystems.kanban.boundary.Context;
import com.dgsystems.kanban.entities.Board;
import com.dgsystems.kanban.entities.Member;
import com.dgsystems.kanban.entities.BoardsDoNotBelongToOwnerException;
import com.dgsystems.kanban.entities.OwnerDoesNotExistException;
import com.dgsystems.kanban.infrastructure.persistence.in_memory.InMemoryMemberRepository;
import com.dgsystems.kanban.infrastructure.persistence.in_memory.InMemoryBoardRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class GetAllBoardsTest {
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
    @DisplayName("Should return only boards created by user")
    void shouldReturnOnlyBoardsCreatedByUser() throws BoardsDoNotBelongToOwnerException, OwnerDoesNotExistException {
        Member user1 = new Member("user1");
        Member user2 = new Member("user2");
        MemberRepository.save(user1);
        MemberRepository.save(user2);

        Board user1Board = new Board("user1Board", Collections.emptyList(), singletonList(user1), user1);
        Board user2Board = new Board("user2Board", Collections.emptyList(), singletonList(user2), user2);

        CreateBoard createBoard = new CreateBoard(boardRepository, MemberRepository);
        createBoard.execute("user1Board", Optional.of(user1));
        createBoard.execute("user2Board", Optional.of(user2));

        GetAllBoards getAllBoards = new GetAllBoards(boardRepository);
        List<Board> boardsUser1 = getAllBoards.execute(Optional.of(user1));
        List<Board> boardsUser2 = getAllBoards.execute(Optional.of(user2));

        assertThat(boardsUser1).isEqualTo(singletonList(user1Board));
        assertThat(boardsUser2).isEqualTo(singletonList(user2Board));
    }

    @Test
    @DisplayName("Should get all boards")
    void shouldGetAllBoards() throws BoardsDoNotBelongToOwnerException, OwnerDoesNotExistException {
        CreateBoard createBoard = new CreateBoard(boardRepository, MemberRepository);
        GetAllBoards getAllBoards = new GetAllBoards(boardRepository);
        Member owner = new Member("owner");

        createBoard.execute("work", Optional.of(owner));
        createBoard.execute("hobby", Optional.of(owner));
        List<Board> response = getAllBoards.execute(Optional.of(new Member("owner")));

        Assertions.assertThat(response).hasSize(2);
        Assertions.assertThat(response.get(0).title()).isEqualTo("work");
        Assertions.assertThat(response.get(1).title()).isEqualTo("hobby");
    }
}