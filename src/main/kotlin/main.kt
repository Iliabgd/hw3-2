abstract class CrudService<T : Item> {

    protected val items = mutableListOf<T>()

    fun add(elemT: T): T {
        items += elemT
        return items.last()
    }

    fun printItems() = println(items)

    fun update(elemT: T): Boolean {
        for ((index, existElemT) in items.withIndex()) {
            if (existElemT.id == elemT.id) {
                items[index] = elemT
                return true
            }
        }
        return false
    }

    fun delete(elemId: Int): Boolean {
        for (item in items) {
            if (item.id == elemId) {
                items.remove(item)
                return true
            }
        }
        return false
    }

    fun getList(elemOwnerId: Int) = items.filter { it.ownerId == elemOwnerId }

    fun getById(elemId: Int): T? {
        for (item in items) {
            if (item.id == elemId) {
                return item
            }
        }
        return null
    }
}

interface Item {
    val id: Int
    val ownerId: Int
}

data class Post(override val id: Int, override val ownerId: Int, val text: String) : Item

data class Note(override val id: Int, override val ownerId: Int, val noteText: String) : Item

data class Comment(val id: Int, val ownerId: Int,val commentText: String,var isDeleted: Boolean = false)

class PostNotFoundException (message: String) : RuntimeException(message)

object WallService : CrudService<Post>() {
    private var posts = emptyArray<Post>()
    private var lastPubId = 0

    fun clearWall() {
        posts = emptyArray()
        lastPubId = 0 // обнуляем счетчик id для постов
        println("Clearing the Wall")
    }
}

object NoteService : CrudService<Note>() {
    private var lastCommentId = 0
    private var comments = emptyArray<Comment>()

    fun createComment(noteId: Int, comment: Comment): Comment {
        for ((index, note) in items.withIndex()) {
            if (note.id == noteId) {
                comments += comment.copy(id = ++lastCommentId)
                return comments.last()
            }
        }
        return throw PostNotFoundException("Нет такой заметки с id $noteId, чтобы прокомментировать!")
    }

    fun deleteComment(commentId: Int, noteOwnerId: Int) : Boolean {
        for (comm in comments) {
            if (comm.id == commentId && !comm.isDeleted) {
                comm.isDeleted = true
                return true
            }
        }
        return false
    }
    fun updateComment(commentId: Int, noteOwnerId: Int, newTxt: String) : Boolean {
        for ((index, comm) in comments.withIndex()) {
                if (comm.id == commentId && !comm.isDeleted) {
                    comments[index] = comm.copy(id = commentId, ownerId = noteOwnerId, commentText = newTxt)
                    return true
                }
            }
            return false
    }

    fun getComments(noteId: Int) = comments.filter { it.id == noteId }

    fun restoreComment(removeCommId: Int): Boolean {
        for (comm in comments) {
            if (comm.id == removeCommId && comm.isDeleted) {
                comm.isDeleted = false
                return true
            }
        }
        return false
    }

    fun printComments() {
        for (comm in comments) {
            print(comm)
            println()
        }
        println()
    }
}

fun main() {
    //WallService.add(Post(1, 21, "Hello, Bunny"))
    //WallService.printItems()

    NoteService.add(Note(2, 22, "This is note to post about Bunny"))
    NoteService.add(Note(3, 22,"This is note to post about Bunny, but not Bunny"))
    NoteService.printItems()

    val comment1 = Comment(31, 22, "This is a comment about Bunny. He looks like...")
    val comment2 = Comment(32, 22, "This is another comment about Bunny. He looks like shit")

    NoteService.createComment(2, comment1)
    NoteService.updateComment(31,2, comment1.commentText)
    NoteService.createComment(3, comment2)
    NoteService.updateComment(32,3, comment2.commentText)
    NoteService.printComments()

    //NoteService.createComment(3, comment1)
   // NoteService.createComment(3, comment2)
    //NoteService.createComment(3, comment1)
   // NoteService.printItems()
    println()

    //NoteService.update(Note(2, 24,"BUNNY BUNNY BUNNY"))
    //NoteService.printItems()

}