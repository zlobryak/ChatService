package ru.netology

import java.time.LocalDateTime

fun main() {

}

object ChatService {
    private var chats = mutableListOf<Chats>()
    private var ownerId = 1
    private var nextMessageId = 1


    fun startChat(userToChatWithId: Int?): Chats {
        chats.add(
            Chats(
                ownerId = ownerId,
                userToChatWithId = userToChatWithId,
            )
        )
        return chats.last()
    }

    fun deleteChat(userToChatWithId: Int) {
        if (!chats.removeIf { it.userToChatWithId == userToChatWithId }) {
            throw ChatNotFoundException(userToChatWithId)
        }
    }

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

    fun deleteMessage(userToChatWithId: Int, messageToDeleteId: Int) {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        if (!chat.messages.removeIf { it.messageId == messageToDeleteId }) {
            throw MessageNotFoundException(messageToDeleteId)
        }
    }

    fun editMessage(userToChatWithId: Int, messageToEditId: Int, text: String) {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        val message = chat.messages.find { it.messageId == messageToEditId }
            ?: throw MessageNotFoundException(messageToEditId)
        message.text = text
    }

    fun getLastMessages(userToChatWithId: Int): MutableList<String> {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        val listOfMessages = mutableListOf<String>()
        chat.messages.forEach { listOfMessages.add(it.text) }
        if (listOfMessages.isEmpty()) {
            println("Нет сообщений")
        }
        return listOfMessages
    }

    fun getListOfMessagesToRead(userToChatWithId: Int, messagesCount: Int): List<Message> {
        val chat = chats.find { it.userToChatWithId == userToChatWithId }
            ?: throw ChatNotFoundException(userToChatWithId)
        val chatsToReturn = chat.messages.takeLast(messagesCount)
        chatsToReturn.forEach { it.unread = false } //Храним список ссылок на оригинальные объекты
        return chatsToReturn
    }

    fun getUnreadChatCount(): Int {
        return chats.count { chat -> chat.messages.any { message -> message.unread } }
    }

    fun getChats(): MutableList<Chats> {
        return chats
    }

    fun clear() {
        chats.clear()
        nextMessageId = 1
    }

    fun chatsCount(): Int {
        return chats.size
    }

    fun messagesCount(userToChatWithId: Int): Int? {
        return chats.find { it.userToChatWithId == userToChatWithId }?.messages?.size
    }
}


data class Chats(
    val ownerId: Int? = null,
    val userToChatWithId: Int? = null,
    val messages: MutableList<Message> = mutableListOf()
)

data class Message(
    val messageId: Int? = null,
    val messageToReplayId: Int? = null,
    val ownerId: Int? = null,
    val date: LocalDateTime? = null,
    var text: String,
    var unread: Boolean = true
)

class ChatNotFoundException(chatId: Int) : RuntimeException("Чат с ID $chatId не найден")
class MessageNotFoundException(messageIdToDelete: Int) :
    RuntimeException("Сообщение с ID $messageIdToDelete не найдено")
