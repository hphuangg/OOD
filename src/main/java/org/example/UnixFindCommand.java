package org.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * class diagram
 *
 *                AbstractFile
 *                 /       \
 *           File            Directory
 *                      (contains children files)
 *
 *                 Filter (Interface)
 *                     |
 *    SizeFilter | ExtensionFilter | RegexFilter
 */

public class UnixFindCommand {

    public static void main(String[] args) {
        Directory root = new Directory("", "/", 0);

        File file1 = new File("/", "abc.xml", 0);
        root.addChild(file1);

        for (Entity child : root.getChildren()) {
            System.out.println(child.getName());
        }
    }

    public static List<File> searchFiles(String directory, List<Filter> filters) {
        List<Entity> entities = FileUtils.listAllUnderDirectory(directory);

        List<File> res = new ArrayList<>();

        if (entities.isEmpty()) return res;

        for (Entity entity : entities) {
            if (entity.isFile()) {
                File file = (File) entity;
                boolean isMatch = filters.stream().allMatch(filter -> filter.isValid(file)); // Predicate
                if (isMatch) res.add(file);
            } else {
                Directory dir = (Directory) entity;
                List<File> matchedFiles = searchFiles(dir.getLocation() + dir.getName(), filters);
                res.addAll(matchedFiles);
            }
        }
        return res;
    }
}

class FileUtils {
    static List<Entity> listAllUnderDirectory(String directory) {
        return new ArrayList<>();
    }
}

interface Filter {
    boolean isValid(File file);
}

@AllArgsConstructor
class ExtensionFilter implements Filter {

    ExtensionType extensionType;

    @Override
    public boolean isValid(File file) {
        return file.getExtensionType().equals(extensionType);
    }
}

@AllArgsConstructor
class SizeFilter implements Filter {

    int size;

    @Override
    public boolean isValid(File file) {
        return file.getSize() < size;
    }
}


@AllArgsConstructor
@Getter
abstract class Entity { // or AbstractFile
    String location;
    String name;
    long size;

    public abstract boolean isDirectory();
    public abstract boolean isFile();
}


@Getter
class File extends Entity {

    ExtensionType extensionType;

    String content;

    public File(String directory, String name, long size) {
        super(directory, name, size);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isFile() {
        return true;
    }

}

@Getter
class Directory extends Entity {

    private final List<Entity> children;

    public Directory(String directory, String name, long size) {
        super(directory, name, size);
        this.children = new ArrayList<>();
    }

    public void addChild(Entity entity) {
        this.children.add(entity);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

}

enum ExtensionType {
    doc,
    exe,
    xml
}

