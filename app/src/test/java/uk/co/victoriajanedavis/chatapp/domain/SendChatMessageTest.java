package uk.co.victoriajanedavis.chatapp.domain;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;
import java.util.UUID;

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import uk.co.victoriajanedavis.chatapp.data.model.db.ChatDbModel;
import uk.co.victoriajanedavis.chatapp.data.model.network.MessageNwModel;
import uk.co.victoriajanedavis.chatapp.data.repositories.MessageRepository;
import uk.co.victoriajanedavis.chatapp.data.repositories.cache.RecentMessagesCache;
import uk.co.victoriajanedavis.chatapp.data.repositories.cache.MessageCache;
import uk.co.victoriajanedavis.chatapp.data.repositories.store.BaseReactiveStore;
import uk.co.victoriajanedavis.chatapp.data.repositories.store.MessageReactiveStore;
import uk.co.victoriajanedavis.chatapp.data.room.ChatAppDatabase;
import uk.co.victoriajanedavis.chatapp.data.services.ChatAppService;
import uk.co.victoriajanedavis.chatapp.domain.entities.ChatEntity;
import uk.co.victoriajanedavis.chatapp.domain.entities.MessageEntity;
import uk.co.victoriajanedavis.chatapp.domain.interactors.SendChatMessage;
import uk.co.victoriajanedavis.chatapp.domain.interactors.SendChatMessage.MessageParams;
import uk.co.victoriajanedavis.chatapp.common.BaseTest;
import uk.co.victoriajanedavis.chatapp.common.ModelGenerationUtil;


public class SendChatMessageTest extends BaseTest {

    private ChatAppDatabase database;
    private BaseReactiveStore<ChatDbModel> chatStore;
    private MessageReactiveStore messageStore;

    private MessageRepository repository;
    private SendChatMessage interactor;

    @Mock
    private ChatAppService service;


    /*
    @Before
    public void setUp() {
        database = Room.inMemoryDatabaseBuilder(
                InstrumentationRegistry.getContext(),
                ChatAppDatabase.class)
                .allowMainThreadQueries()
                .build();

        Cache.DiskCache<UUID, ChatDbModel> chatCache = new RecentMessagesCache(database);
        chatStore = new BaseReactiveStore<>(chatCache);

        MessageCache messageCache = new MessageCache(database);
        messageStore = new MessageReactiveStore(messageCache);

        repository = new MessageRepository(messageStore, service);

        interactor = new SendChatMessage(repository);
    }

    @After
    public void closeDb() {
        database.close();
    }


    @Test
    public void sendingMessageReturnsMessageFromNetworkAndEmitsToGetAllStream() {
        ChatDbModel chatDbModel = ModelGenerationUtil.INSTANCE.createChatMembershipDbModel();
        MessageNwModel messageNwModel = ModelGenerationUtil.INSTANCE.createMessageNwModel(chatDbModel.getUuid());
        messageNwModel.setText("hello");

        new ArrangeBuilder().withNewlySentMessageFromService(messageNwModel);

        chatStore.storeSingular(chatDbModel).subscribe();

        TestObserver<List<MessageEntity>> getAllObserver = repository.getAllMessagesInChat(chatDbModel.getUuid()).test();
        TestObserver<MessageEntity> sendObserver = interactor.getSingle(new MessageParams(messageNwModel.getChatUuid(), messageNwModel.getText())).test();

        getAllObserver.assertValueAt(0, List::isEmpty);
        getAllObserver.assertValueAt(1, list -> list.size() == 1);
        getAllObserver.assertValueAt(1, list -> list.get(0).getChatUuid().equals(messageNwModel.getChatUuid()));
        getAllObserver.assertValueAt(1, list -> list.get(0).getText().equals("hello"));
        getAllObserver.assertValueCount(2);

        sendObserver.assertValueAt(0, messageEntity -> messageEntity.getChatUuid().equals(messageNwModel.getChatUuid()));
        sendObserver.assertValueCount(1);
        sendObserver.assertComplete();
    }

    @Test
    public void sendMessageSingleEmitsErrorWhenNetworkServiceErrors() {
        ChatEntity chatEntity = new ChatEntity(UUID.randomUUID());
        MessageNwModel messageNwModel = ModelGenerationUtil.INSTANCE.createMessageNwModel(chatEntity.getUuid());
        messageNwModel.setText("hello");

        Throwable throwable = Mockito.mock(Throwable.class);
        new ArrangeBuilder().withErrorInSendChatMessageFromService(throwable);

        TestObserver<MessageEntity> sendObserver = interactor.getSingle(new MessageParams(messageNwModel.getChatUuid(), messageNwModel.getText())).test();

        sendObserver.assertError(throwable);
    }
    */


    /****************************************************/
    /****************** Helper methods ******************/
    /****************************************************/

    private class ArrangeBuilder {

        private ArrangeBuilder withNewlySentMessageFromService(MessageNwModel messageNwModel) {
            Mockito.when(service.postMessageToChat(messageNwModel.getChatUuid().toString(), messageNwModel.getText()))
                    .thenReturn(Single.just(messageNwModel));
            return this;
        }

        private ArrangeBuilder withErrorInSendChatMessageFromService(Throwable error) {
            Mockito.when(service.postMessageToChat(Mockito.anyString(), Mockito.anyString()))
                    .thenReturn(Single.error(error));
            return this;
        }

    }
}
