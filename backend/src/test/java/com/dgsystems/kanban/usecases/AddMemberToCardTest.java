package com.dgsystems.kanban.usecases;

import com.dgsystems.kanban.boundary.Context;
import com.dgsystems.kanban.entities.*;
import com.dgsystems.kanban.infrastructure.persistence.in_memory.InMemoryBoardRepository;
import com.dgsystems.kanban.infrastructure.persistence.in_memory.InMemoryMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class AddMemberToCardTest {
    public static final String BOARD_NAME = "work";
    public static final String LIST_TITLE = "todo";
    private Card card;
    private BoardRepository boardRepository;
    private MemberRepository teamMemberRepository;
    private Member owner;

    @Test
    @DisplayName("Should add team member to card")
    void shouldAddTeamMemberToCard() throws MemberNotInTeamException, OwnerDoesNotExistException {
        AddTeamMemberToCard addTeamMemberToCard = new AddTeamMemberToCard(teamMemberRepository, boardRepository);
        Member Member = new Member("username");
        addTeamMemberToCard.execute(BOARD_NAME, LIST_TITLE, card, Member, owner);
        Optional<Member> memberOptional = Optional.of(Member);
        Board newBoard = new GetBoard(boardRepository).execute(BOARD_NAME, memberOptional).orElseThrow();
        assertThat(firstCard(newBoard.cardLists()).teamMember()).isEqualTo(memberOptional);
    }

    @Test
    @DisplayName("Should not be able to add member to card when not in team")
    void shouldNotBeAbleToAddMemberToCardWhenNotInTeam() {
        AddTeamMemberToCard addTeamMemberToCard = new AddTeamMemberToCard(teamMemberRepository, boardRepository);
        Member Member = new Member("username");
        Member invalidUser = new Member("invalid_user");
        assertThrows(CompletionException.class, () -> addTeamMemberToCard.execute(BOARD_NAME, LIST_TITLE, card, Member, invalidUser));
        //TODO: assert member is not added to card
    }

    @BeforeEach
    public void setup() throws OwnerDoesNotExistException, MemberNotInTeamException {
        boardRepository = new InMemoryBoardRepository();
        teamMemberRepository = new InMemoryMemberRepository();

        Context.initialize(boardRepository);

        CreateBoard createBoard = new CreateBoard(boardRepository, teamMemberRepository);
        AddCardListToBoard addCardListToBoard = new AddCardListToBoard(boardRepository);
        AddCardToCardList addCardToCardList = new AddCardToCardList(boardRepository);
        owner = new Member("owner");
        teamMemberRepository.save(owner);
        createBoard.execute(BOARD_NAME, Optional.of(owner));
        addCardListToBoard.execute(BOARD_NAME, LIST_TITLE, Optional.of(owner));
        card = new Card(UUID.randomUUID(), "dishes", "do the dishes today", Optional.empty());
        addCardToCardList.execute(BOARD_NAME, LIST_TITLE, card, Optional.of(owner));
        AddMemberToBoard addMemberToBoard = new AddMemberToBoard(teamMemberRepository, boardRepository);
        addMemberToBoard.execute(BOARD_NAME, new Member("username"), owner);
    }

    private Card firstCard(List<CardList> cardListList) {
        return firstCardList(cardListList).cards().get(0);
    }

    private CardList firstCardList(List<CardList> cardListList) {
        return cardListList.get(0);
    }
}
