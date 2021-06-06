package files

import filesystem.FileSystemException

import scala.annotation.tailrec

class Directory(override val parentPath: String, override val name: String, val contents: List[DirectoryEntry])
  extends DirectoryEntry(parentPath, name) {
  def hasEntry(name: String): Boolean = findEntry(name) != null

  def getAllFoldersInPath: List[String] =
    path.substring(1).split(Directory.SEPARATOR).toList.filter(x => x.nonEmpty)

  def findDescendant(path: List[String]): Directory = {
    if (path.isEmpty) this
    else findEntry(path.head).toDirectory.findDescendant(path.tail)
  }

  def addEntry(newEntry: DirectoryEntry): Directory = new Directory(parentPath, name, contents :+ newEntry)

  def findEntry(entryName: String): DirectoryEntry = {
    @tailrec
    def findEntryHelper(name: String, contentList: List[DirectoryEntry]): DirectoryEntry = {
      if (contentList.isEmpty) null
      else if (contentList.head.name.equals(name)) contentList.head
      else findEntryHelper(name, contentList.tail)
    }
    findEntryHelper(entryName, contents)
  }

  def replaceEntry(entryName: String, newEntry: DirectoryEntry): Directory =
    new Directory(parentPath, name, contents.filter(x => !x.name.equals(entryName)) :+ newEntry)

  override def toDirectory: Directory = this

  override def toFile: File = throw new FileSystemException("A directory cannot be converted to a file")

  override def getType: String = "Directory"
}

object Directory {
  val ROOT_PATH = "/"
  val SEPARATOR = "/"
  def ROOT: Directory = Directory.empty("", "");

  def empty(parentPath: String, name: String) = new Directory(parentPath, name, List())
}