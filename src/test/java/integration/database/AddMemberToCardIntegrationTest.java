package integration.database;

import akka.actor.ActorSystem;
import com.dgsystems.kanban.Application;
import com.dgsystems.kanban.boundary.Context;
import com.dgsystems.kanban.entities.*;
import com.dgsystems.kanban.usecases.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@Transactional
@SpringBootTest(classes = Application.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AddMemberToCardIntegrationTest {
    private static final String USERNAME = UUID.randomUUID().toString();
    private static final String BOARD_NAME = UUID.randomUUID().toString();
    private static final String CARD_LIST_TITLE = UUID.randomUUID().toString();
    private static final String DO_THE_DISHES = "do the dishes";
    private static final UUID CARD_ID = UUID.randomUUID();

    @Resource
    BoardMemberRepository boardMemberRepository;
    @Resource
    BoardRepository boardRepository;

    @BeforeEach
    public void setup() {
        Context.actorSystem = ActorSystem.create();
    }

    @Test
    @DisplayName("Should add team member to card in integration with database")
    void shouldAddTeamMemberToCardInIntegrationWithDatabase() throws MemberNotInTeamException {
        BoardMember boardMember = new BoardMember(USERNAME);
        Card card = new Card(CARD_ID, DO_THE_DISHES, DO_THE_DISHES, Optional.empty());
        CreateBoard createBoard = new CreateBoard(boardRepository);
        AddTeamMember addTeamMember = new AddTeamMember(boardMemberRepository);
        AddMemberToBoard addMemberToBoard = new AddMemberToBoard(boardMemberRepository, boardRepository);
        AddCardListToBoard addCardListToBoard = new AddCardListToBoard(boardRepository);
        AddCardToCardList addCardToCardList = new AddCardToCardList(boardRepository);
        AddTeamMemberToCard addTeamMemberToCard = new AddTeamMemberToCard(boardMemberRepository, boardRepository);

        createBoard.execute(BOARD_NAME);
        addTeamMember.execute(new BoardMember(USERNAME));
        addMemberToBoard.execute(BOARD_NAME, boardMember);
        UUID cardListId = addCardListToBoard.execute(BOARD_NAME, CARD_LIST_TITLE);
        addCardToCardList.execute(BOARD_NAME, CARD_LIST_TITLE, card);
        addTeamMemberToCard.execute(BOARD_NAME, CARD_LIST_TITLE, card, boardMember);

        GetBoard getBoard = new GetBoard(boardRepository);
        Board board = getBoard.execute(BOARD_NAME).orElseThrow();

        Board expectedBoard = expectedBoard(boardMember, cardListId);
        assertThat(board).isEqualTo(expectedBoard);
    }

    private Board expectedBoard(BoardMember boardMember, UUID cardListId) {
        Card expectedCard = new Card(CARD_ID, DO_THE_DISHES, DO_THE_DISHES, Optional.of(boardMember));
        CardList expectedCardList = new CardList(cardListId, CARD_LIST_TITLE, List.of(expectedCard));
        return new Board(BOARD_NAME, List.of(expectedCardList), List.of(boardMember));
    }

    @Configuration
    @TestPropertySource("test.properties")
    @EnableTransactionManagement
    public class InMemoryTestConfig {
        @Autowired
        private Environment env;

        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
            dataSource.setUrl(env.getProperty("jdbc.url"));
            dataSource.setUsername(env.getProperty("jdbc.user"));
            dataSource.setPassword(env.getProperty("jdbc.pass"));

            return dataSource;
        }
    }
}