package ru.netology

import java.time.LocalDateTime

fun main() {
    // Добавьте несколько сообщений, чтобы было что отлаживать
    ChatService.addMessage(userToChatWithId = 123, text = "Привет")
    ChatService.addMessage(userToChatWithId = 123, text = "Как дела?")

    // Вызов функции с последовательностью
    ChatService.getLastMessages(userToChatWithId = 123)

}

object ChatService {
    private var chats = mutableListOf<Chats>()
    private var ownerId = 1
    private var nextMessageId = 1

    // Создаёт новый чат с указанным собеседником и добавляет его в список чатов.
    // Возвращает созданный чат.
    fun startChat(userToChatWithId: Int?): Chats {
        chats.add(
            Chats(
                ownerId = ownerId,
                userToChatWithId = userToChatWithId,
            )
        )
        return chats.last()
    }

    // Удаляет чат по идентификатору собеседника.
    // Если чат не найден, выбрасывает исключение ChatNotFoundException.
    fun deleteChat(userToChatWithId: Int) {
        if (!chats.removeIf { it.userToChatWithId == userToChatWithId }) {
            throw ChatNotFoundException(userToChatWithId)
        }
    }

    // Добавляет новое сообщение в чат с указанным собеседником.
    // Если чат не существует, создаёт его автоматически.
    // Сообщение получает уникальный ID и помечается как непрочитанное.
    fun addMessage(userToChatWithId: Int, text: String) {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: startChat(userToChatWithId)
        chat.messages.add(
            Message(
                messageId = nextMessageId++,
                ownerId = ownerId,
                text = text
            )
        )
    }

    // Удаляет сообщение из чата по ID сообщения и ID собеседника.
    // Если чат не найден — выбрасывает ChatNotFoundException.
    // Если сообщение не найдено — выбрасывает MessageNotFoundException.
    fun deleteMessage(userToChatWithId: Int, messageToDeleteId: Int) {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        if (!chat.messages.removeIf { it.messageId == messageToDeleteId }) {
            throw MessageNotFoundException(messageToDeleteId)
        }
    }

    // Редактирует текст существующего сообщения в чате.
    // Если чат или сообщение не найдены — выбрасывает соответствующее исключение.
    fun editMessage(userToChatWithId: Int, messageToEditId: Int, text: String) {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        val message = chat.messages.find { it.messageId == messageToEditId }
            ?: throw MessageNotFoundException(messageToEditId)
        message.text = text
    }

    // Возвращает список текстов всех сообщений в чате с указанным собеседником.
    // Если сообщений нет, выводит в консоль "Нет сообщений" и возвращает пустой список.
    // Если чат не найден — выбрасывает исключение.
    fun getLastMessages(userToChatWithId: Int): MutableList<String> {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        val listOfMessages = chat.messages.asSequence()
            .map { (it.text) }
            .toMutableList()
        if (listOfMessages.isEmpty()) {
            println("Нет сообщений")
        }
        return listOfMessages
    }

    // Возвращает указанное количество последних сообщений из чата и помечает их как прочитанные.
    // Если чат не найден — выбрасывает исключение.
    // Возвращается список оригинальных объектов Message (с возможностью изменения состояния).
    fun getListOfMessagesToRead(userToChatWithId: Int, messagesCount: Int): List<Message> {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        return chat.messages
            .takeLast(messagesCount)
            .asSequence()
            .onEach { it.unread = false }
            .toList()
    }

    // Подсчитывает количество чатов, в которых есть хотя бы одно непрочитанное сообщение.
    fun getUnreadChatCount(): Int {
        return chats.count { chat -> chat.messages.any { message -> message.unread } }
    }

    // Возвращает копию списка всех чатов (для внешнего доступа).
    fun getChats(): MutableList<Chats> {
        return chats
    }

    // Очищает все чаты и сбрасывает счётчик ID сообщений (используется в тестах).
    fun clear() {
        chats.clear()
        nextMessageId = 1
    }

    // Возвращает общее количество чатов.
    fun chatsCount(): Int {
        return chats.size
    }

    // Возвращает количество сообщений в чате с указанным собеседником.
    // Если чат не найден, возвращает null.
    fun messagesCount(userToChatWithId: Int): Int? {
        return chats.find { it.userToChatWithId == userToChatWithId }?.messages?.size
    }
}

// Данные чата: владелец, собеседник и список сообщений.
data class Chats(
    val ownerId: Int? = null,
    val userToChatWithId: Int? = null,
    val messages: MutableList<Message> = mutableListOf()
)

// Данные сообщения: ID, ID ответа (если есть), автор, дата, текст и статус прочтения.
data class Message(
    val messageId: Int? = null,
    val messageToReplayId: Int? = null,
    val ownerId: Int? = null,
    val date: LocalDateTime? = null,
    var text: String,
    var unread: Boolean = true
)

// Исключение, выбрасываемое при отсутствии чата с указанным ID собеседника.
class ChatNotFoundException(chatId: Int) : RuntimeException("Чат с ID $chatId не найден")

// Исключение, выбрасываемое при отсутствии сообщения с указанным ID.
class MessageNotFoundException(messageIdToDelete: Int) :
    RuntimeException("Сообщение с ID $messageIdToDelete не найдено")