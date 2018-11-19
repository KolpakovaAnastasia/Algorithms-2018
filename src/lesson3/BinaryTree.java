package lesson3;
import kotlin.NotImplementedError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Attention: comparable supported but comparator is not
@SuppressWarnings("WeakerAccess")
public class BinaryTree<T extends Comparable<T>> extends AbstractSet<T> implements CheckableSortedSet<T> {

    private static class Node<T> {
        final T value;

        Node<T> left = null;

        Node<T> right = null;

        Node(T value) {
            this.value = value;
        }
    }

    private Node<T> root = null;
    private boolean lastChild;
    private int size = 0;

    @Override
    public boolean add(T t) {
        Node<T> closest = find(t);
        int comparison = closest == null ? -1 : t.compareTo(closest.value);
        if (comparison == 0) {
            return false;
        }
        Node<T> newNode = new Node<>(t);
        if (closest == null) {
            root = newNode;
        }
        else if (comparison < 0) {
            assert closest.left == null;
            closest.left = newNode;
        }
        else {
            assert closest.right == null;
            closest.right = newNode;
        }
        size++;
        return true;
    }

    public boolean checkInvariant() {
        return root == null || checkInvariant(root);
    }

    private boolean checkInvariant(Node<T> node) {
        Node<T> left = node.left;
        if (left != null && (left.value.compareTo(node.value) >= 0 || !checkInvariant(left))) return false;
        Node<T> right = node.right;
        return right == null || right.value.compareTo(node.value) > 0 && checkInvariant(right);
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     */
    //данная программа не работает, но оставила в ней то, что написала, чтобы работало остальное
    @Override
    public boolean remove(Object o) {
        Node<T> actual = root;
        lastChild = true;
        int match;
        @SuppressWarnings("") T clue = (T) o;
        while ((match = actual.value.compareTo(clue)) != 0) {
            if (match > 0) {
                lastChild = true;
                actual = actual.left;
            } else {
                lastChild = false;
                actual = actual.right;
            }
            if (actual == null)
                return false;
        }
        return true;
    }
    @Override
    public boolean contains(Object o) {
        @SuppressWarnings("unchecked")
        T t = (T) o;
        Node<T> closest = find(t);
        return closest != null && t.compareTo(closest.value) == 0;
    }

    private Node<T> find(T value) {
        if (root == null) return null;
        return find(root, value);
    }

    private Node<T> find(Node<T> start, T value) {
        int comparison = value.compareTo(start.value);
        if (comparison == 0) {
            return start;
        }
        else if (comparison < 0) {
            if (start.left == null) return start;
            return find(start.left, value);
        }
        else {
            if (start.right == null) return start;
            return find(start.right, value);
        }
    }
    private void delete(Node<T> actual, Node<T> parent) {
        if (actual.left == null && actual.right == null) {
            if (actual == root) {
                root = null;
            } else if (lastChild) {
                parent.left = null;
            } else
                parent.right = null;

        } else if (actual.right == null) {
            if (actual == root) {
                root = actual.left;
            } else if (lastChild) {
                parent.left = actual.left;
            } else
                parent.right = actual.left;

        } else if (actual.left == null) {
            if (actual == root) {
                root = actual.right;
            } else if (lastChild) {
                parent.left = actual.right;
            } else
                parent.right = actual.right;
        }
     else {
        Node substitution = getSubstitution(actual);
        if (actual == root) {
            root = substitution;
        } else if (lastChild) {
            parent.left = substitution;
        } else
            parent.right = substitution;
        substitution.left = actual.left;
    }
}
    private Node<T> getSubstitution(Node<T> changeNode) {
        Node<T> subParent = changeNode;
        Node<T> substitution = changeNode;
        Node<T> actualNode = changeNode.right;
        while (actualNode != null) {
            subParent = substitution;
            substitution = actualNode;
            actualNode = actualNode.left;
        }
        if (substitution != changeNode.right) {
            subParent.left = substitution.right;
            substitution.right = changeNode.right;
        }
        return substitution;
    }


    public class BinaryTreeIterator implements Iterator<T> {
        private LinkedList<Node<T>> list;
        private Node<T> actual = null;

        private BinaryTreeIterator() {}

        /**
         * Поиск следующего элемента
         * Средняя
         */
        private Node<T> findNext() {
            Node<T> next = list.getFirst();
            list.removeFirst();
            return next;
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        @Override
        public T next() {
            actual = findNext();
            while (actual.right != null) {
                list.addFirst(actual.right);
                actual.right = actual.right.left;
            }
            return actual.value;
        }


        /**
         * Удаление следующего элемента
         * Сложная
         */
        @Override
        public void remove() {
            if (actual != null) {
                Node<T> parent;
                if (hasNext()) {
                    parent = findNext();
                } else {
                    parent = find(last());
                }
                delete(actual, parent);
                size--;
            }
        }
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new BinaryTreeIterator();
    }

    @Override
    public int size() {
        return size;
    }


    @Nullable
    @Override
    public Comparator<? super T> comparator() {
        return null;
    }

    /**
     * Для этой задачи нет тестов (есть только заготовка subSetTest), но её тоже можно решить и их написать
     * Очень сложная
     */
    @NotNull
    @Override
    public SortedSet<T> subSet(T fromElement, T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> headSet(T toElement) {
        // TODO
        throw new NotImplementedError();
    }

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    @NotNull
    @Override
    public SortedSet<T> tailSet(T fromElement) {
        // TODO
        throw new NotImplementedError();
    }

    @Override
    public T first() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.left != null) {
            current = current.left;
        }
        return current.value;
    }

    @Override
    public T last() {
        if (root == null) throw new NoSuchElementException();
        Node<T> current = root;
        while (current.right != null) {
            current = current.right;
        }
        return current.value;
    }
}
